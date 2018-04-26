/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import org.ciedayap.cincamimis.Cincamimis;
import org.ciedayap.cincamimis.Context;
import org.ciedayap.cincamimis.Measurement;
import org.ciedayap.cincamimis.MeasurementItem;
import org.ciedayap.cincamimis.MeasurementItemSet;
import org.ciedayap.cincamimis.Quantitative;

/**
 * It is responsible for the implementation of the tabular translator
 * @author Mario
 */
public class TabularMode extends CincamimisTranslator{
    /**
     * This flag indicates the behavior once a CINCAMI/MIS was processed.
     * TRUE indicates that the processor will continue supervising the queue waiting
     * for new messages. FALSE indicates that once the message was processed, the thread
     * will be finished.
     */
    private boolean autoSense;
    /**
     * It contains the last processed window
     */
    private final ArrayBlockingQueue<CincamimisWindow> processedWindows;
    
    /**
     * It supervises the CINCAMI/MIS queue in autosense mode. Each time the 
     * Cincami/MIS is translated, the trasnlated object is incorporated in the
     * array named "processedWindows" until the capacity is full. When the capacity
     * is full, the oldest object is discarded and this is replaced by the new translation.
     * @param queue The queue to be consumed
     * @throws QueueException It is raised when the queue passed as parameter is null
     */
    public TabularMode(CincamimisQueue queue) throws QueueException {
        super(queue);
        autoSense=true;
        processedWindows=new ArrayBlockingQueue(10);
    }

     /**
     * It supervises the CINCAMI/MIS queue in autosense mode
     * @param queue The queue to be consumed
     * @param capacity The capacity for the queue related to the translated objects. At least 10 should be given.
     * @param autoSense TRUE indicates that the thread will continue supervising the queue
     * waiting for a new message. FALSE indicates that once the processing is finished, the thread
     * will be finished.
     * @throws QueueException It is raised when the queue passed as parameter is null
     */
    public TabularMode(CincamimisQueue queue,int capacity,boolean autoSense) throws QueueException {
        super(queue);
        this.autoSense=autoSense;
        processedWindows=new ArrayBlockingQueue((capacity<10)?10:capacity);
    }

    @Override
    public void run() {
        while(autoSense)
        {
         if(!queue.isEmpty())
         {
             Cincamimis currentMessage;
             try {
                 currentMessage=queue.firstAvailableandRemove();
             } catch (QueueException ex) {
                 currentMessage=null;
             }
             
             //Generate the Tabular View by translation
             if(currentMessage!=null)
             {
                 //Make the translation
                CincamimisWindow translated=translate(currentMessage);
                
                //Check the queue size. Discard the oldest object when the capacity is full for freeing space. Next, the new element will be incorporated
                if(addKeepingLast(translated)) System.out.println("Window added");
                else System.out.println("Window NOT added");
             }                         
         }                          
        }
    }

    /**
     * @return the autoSense
     */
    public boolean isAutoSense() {
        return autoSense;
    }

    /**
     * @param autoSense the autoSense to set
     */
    public void setAutoSense(boolean autoSense) {
        this.autoSense = autoSense;
    }
    
    /**
     * It is responsible for adding the translated window at the end of the queue.
     * If the capacity is full, the head of the queue is removed for incorporating
     * the new element.
     * @param translated The new element to be incorporated
     * @return TRUE if the new element could be incorporated, FALDSE otherwise.
     */
    protected synchronized boolean addKeepingLast(CincamimisWindow translated)
    {
        if(processedWindows==null) return false;
        
        if(processedWindows.remainingCapacity()==0) processedWindows.poll();//Remove the first element from the queue
        
        return processedWindows.offer(translated); 
    }
    
    /**
     * It verifies the availability of CincamimisWindow in the queue
     * @return TRUE if at least there exists available instances.
     */
    public synchronized boolean isEmptyProcessedWindows()
    {
        if(processedWindows==null) return false;
        
        return processedWindows.isEmpty();
    }
    
    /**
     * It removes and returns the oldest window in the queue (the first).
     * @return The oldest window (the first), null in case of the queue is empty.
     */
    public synchronized CincamimisWindow getOldestWindow()
    {
        if(processedWindows==null) return null;
        
        return processedWindows.poll();
    }
    /**
     * It is responsible for translating from the CINCAMI/MIS object model to tabular view.
     * The CincamimisWindow incorporates the implemmentation of TableView interface, which
     * make easy the tabular access to the data.
     * @param message The message to be converted
     * @return The Window incorporating all the measures coming from the Cincami/MIS message under the object model
     */
    public static CincamimisWindow translate(Cincamimis message)
    {
        if(message==null) return null;
     
        MeasurementItemSet mis=message.getMeasurements();
        if(mis==null) return null;
        
        ArrayList<MeasurementItem> lista=mis.getMeasurementItems();
        if(lista==null || lista.isEmpty()) return null;
     
        //There are measurements
        Attributes myAttributes=new Attributes();
        HashMap<Integer,ArrayList<Quantitative>> data=new HashMap();        
        Long myOrder=1L;
        
        Attribute dt=new Attribute();  
        ColumnFamily datetimefamily=ColumnFamily.create("datetime");
        ColumnFamily measurefamily=ColumnFamily.create("measurement");
        ColumnFamily contextfamily=ColumnFamily.create("context");
        ColumnFamily traceabilityfamily=ColumnFamily.create("traceability");
        
        if(datetimefamily==null || measurefamily==null || contextfamily==null || traceabilityfamily==null) return null;
        
        dt.setColumnFamily(datetimefamily);
        dt.setName("date");
        dt.setOrder(myOrder);
        myOrder++;
        dt.setKind(Integer.class);
        myAttributes.add(dt);
        String dateFormat="yyyyMMdd";
        
        dt=new Attribute();
        dt.setColumnFamily(datetimefamily);
        dt.setName("time");
        dt.setOrder(myOrder);
        myOrder++;
        dt.setKind(BigDecimal.class);
        myAttributes.add(dt);      
        String timeFormat="HHmmss.SSS";

        dt=new Attribute();
        dt.setColumnFamily(datetimefamily);
        dt.setName("timezone");
        dt.setOrder(myOrder);
        myOrder++;
        dt.setKind(String.class);
        dt.setQuantitative(false);
        myAttributes.add(dt);      
        String zoneFormat="VV";
        
        dt=new Attribute();
        dt.setColumnFamily(traceabilityfamily);
        dt.setName("madapter");
        dt.setOrder(myOrder);
        myOrder++;
        dt.setKind(String.class);
        dt.setQuantitative(false);
        myAttributes.add(dt);      

        dt=new Attribute();
        dt.setColumnFamily(traceabilityfamily);
        dt.setName("datasource");
        dt.setOrder(myOrder);
        myOrder++;
        dt.setKind(String.class);
        dt.setQuantitative(false);
        myAttributes.add(dt);      

        dt=new Attribute();
        dt.setColumnFamily(traceabilityfamily);
        dt.setName("entity");
        dt.setOrder(myOrder);
        myOrder++;
        dt.setKind(String.class);
        dt.setQuantitative(false);
        myAttributes.add(dt);      
        
        
        //Building the attributes
        for(MeasurementItem item:lista)
        {//It walks along each measurementItem in the list. Each iteration will be a Tuple            
            //Verifying the Context
            Context myContext=item.getContext();
            if(myContext!=null)
            {//There is defined context
                for(Measurement myMeasurement:myContext.getMeasurements())
                {
                    if(myMeasurement!=null)
                    {                                                                       
                        dt=new Attribute();
                        dt.setColumnFamily(contextfamily);
                        dt.setName(myMeasurement.getIdMetric());
                        dt.setOrder(myOrder);
                        dt.setKind(BigDecimal.class);
                        
                        if(!myAttributes.isPresent(dt))
                        {
                            myAttributes.add(dt);
                            myOrder++;
                        }                                                                      
                    }
                }
            }
            
            //Verifying the measurement itself
            Measurement myMeasurement= item.getMeasurement();
            if(myMeasurement!=null)
            {                                                                       
                dt=new Attribute();
                dt.setColumnFamily(measurefamily);
                dt.setName(myMeasurement.getIdMetric());
                dt.setOrder(myOrder);
                dt.setKind(BigDecimal.class);

                if(!myAttributes.isPresent(dt))
                {
                    myAttributes.add(dt);
                    myOrder++; 
                }                                                                      
            }            
        }
                
        //Building the Window
        CincamimisWindow win;
        try {
            win=new CincamimisWindow(myAttributes);
        } catch (AttributeException | WindowException ex) {
            return null;
        }
        win.changeStateTo(CincamimisWindow.CREATING);
        
        //Building the Tuples
        Optional<Attribute> myDate=myAttributes.getAttributeByName("date");
        Optional<Attribute> myTime=myAttributes.getAttributeByName("time");
        Optional<Attribute> myZone=myAttributes.getAttributeByName("timezone");
        
        if(myDate==null || myTime==null || myZone==null || !myDate.isPresent() || !myTime.isPresent() || !myZone.isPresent())
            return null;
        
        for(MeasurementItem item:lista)
        {//It walks along each measurementItem in the list. Each iteration will be a Tuple            
            Tuple row=new Tuple(myAttributes);
            
            Optional<Attribute> dsid=row.getAttributesByName("datasource");
            if(dsid!=null && dsid.isPresent())
                row.update(dsid.get(), item.getDataSourceID());

            Optional<Attribute> _entity=row.getAttributesByName("entity");
            if(_entity!=null && _entity.isPresent())
                row.update(_entity.get(), item.getIdEntity());

            Optional<Attribute> _ma=row.getAttributesByName("madapter");
            if(_ma!=null && _ma.isPresent())
                row.update(_ma.get(), message.getDsAdapterID());
            
            //Verifying the Context
            Context myContext=item.getContext();
            if(myContext!=null)
            {//There is defined context
                for(Measurement myMeasurement:myContext.getMeasurements())
                {
                    if(myMeasurement!=null)
                    {           
                        Optional<Attribute> ats=row.getAttributesByName(myMeasurement.getIdMetric());
                                                
                        if(ats!=null && ats.isPresent())
                        {
                            ZonedDateTime zdt=myMeasurement.getDatetime();
                            Quantitative value=myMeasurement.getMeasure().getQuantitative();
                            
                            if(value!=null)
                            {//Measure
                                row.update(ats.get(), value);
                            }
                            
                            if(zdt!=null)
                            {//Instant
                                Quantitative dateValue=Quantitative.factoryDeterministicQuantitativeMeasure(new BigDecimal(DateTimeFormatter.ofPattern(dateFormat).format(zdt)));
                                Quantitative timeValue=Quantitative.factoryDeterministicQuantitativeMeasure(new BigDecimal(DateTimeFormatter.ofPattern(timeFormat).format(zdt)));
                                String zoneValue=DateTimeFormatter.ofPattern(zoneFormat).format(zdt);
                                row.update(myDate.get(), dateValue);
                                row.update(myTime.get(), timeValue);
                                row.update(myZone.get(), zoneValue);
                            }
                        }                                                                      
                    }
                }
            }
            
            //Verifying the measurement itself
            Measurement myMeasurement= item.getMeasurement();
            if(myMeasurement!=null)
            {                                                                       
                Optional<Attribute> ats=row.getAttributesByName(myMeasurement.getIdMetric());

                if(ats!=null && ats.isPresent())
                {
                    ZonedDateTime zdt=myMeasurement.getDatetime();
                    Quantitative value=myMeasurement.getMeasure().getQuantitative();

                    if(value!=null)
                    {//Measure
                        row.update(ats.get(), value);
                    }

                    if(zdt!=null)
                    {//Instant
                        Quantitative dateValue=Quantitative.factoryDeterministicQuantitativeMeasure(new BigDecimal(DateTimeFormatter.ofPattern(dateFormat).format(zdt)));
                        Quantitative timeValue=Quantitative.factoryDeterministicQuantitativeMeasure(new BigDecimal(DateTimeFormatter.ofPattern(timeFormat).format(zdt)));
                        String zoneValue=DateTimeFormatter.ofPattern(zoneFormat).format(zdt);
                        row.update(myDate.get(), dateValue);
                        row.update(myTime.get(), timeValue);
                        row.update(myZone.get(), zoneValue);
                    }
                }                                                                      
            }

            //Incorporating the row in the window
            win.addTuple(row);
        }
        
        win.changeStateTo(CincamimisWindow.CREATED);
        return win;
    }  
}
