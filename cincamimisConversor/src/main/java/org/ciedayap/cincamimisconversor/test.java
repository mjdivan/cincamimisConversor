/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import org.ciedayap.cincamimisconversor.arff.ArffConverter;
import org.ciedayap.cincamimis.Cincamimis;
import org.ciedayap.cincamimis.Context;
import org.ciedayap.cincamimis.LikelihoodDistribution;
import org.ciedayap.cincamimis.LikelihoodDistributionException;
import org.ciedayap.cincamimis.MeasurementItem;
import org.ciedayap.cincamimis.MeasurementItemSet;
import org.ciedayap.cincamimis.Quantitative;
import org.ciedayap.utils.NullableBigDecimal;
import org.ciedayap.utils.QuantitativeUtils;
import org.ciedayap.utils.StringUtils;
import org.ciedayap.utils.TranslateJSON;
import org.ciedayap.utils.TranslateXML;

/**
 * It is a test class for testing the behavior of the queue
 * @author Mario
 */
public class test {
    public static void main(String args[]) throws LikelihoodDistributionException, NoSuchAlgorithmException, QueueException, InterruptedException, IOException
    {
       //Runing 1. conversion times
        //for(int i=100;i<500;i+=100)
          //  simula(i,false);
       
        //Runing 2. ColumnarData with and without nulls by metric or attribute
        //simula(12,true);
        
        //Runing 3. Testing the queue
        /*CincamimisQueue myQueue=new CincamimisQueue(10,true);
        int nmetric=3;
        for(int i=10;i<100;i+=10)
        {
            myQueue.addKeepingLast(getMessage(i,nmetric));
            nmetric++;
        }
            
        TabularMode tm=new TabularMode(myQueue);
        Thread hilo=new Thread(tm,"tabular");
        hilo.start();
        Thread.sleep(500);
        while(!tm.isEmptyProcessedWindows())
        {
            CincamimisWindow cw=tm.getOldestWindow();
            System.out.println("Attributes: "+cw.columnCount()+" Records: "+cw.getRowCount());
        }
        tm.setAutoSense(false);
        */
        
        
        //Running 4. Testing the queue with multiple readers
      /*  CincamimisQueue myQueue=new CincamimisQueue(10,true);
        int nmetric=3;
        for(int i=10;i<100;i+=10)
        {
            myQueue.addKeepingLast(getMessage(i,nmetric));
            nmetric++;
        }
        
        System.out.println("Initialized Queue: "+myQueue.size());
            
        TabularMode tm[]=new TabularMode[5];
        Thread hilo[]=new Thread[5];
        for(int i=0;i<5;i++)
        {
            tm[i]=new TabularMode(myQueue);
            hilo[i]=new Thread(tm[i],"tabular"+i);
            hilo[i].start();
        }
        
        //Thread.sleep(500);
        while(true)
        {
            for(int i=0;i<5;i++)
            {
                System.out.println();
                System.out.println("TabularMode: "+i);

                while(!tm[i].isEmptyProcessedWindows())
                {
                    CincamimisWindow cw=tm[i].getOldestWindow();
                    System.out.println("Attributes: "+cw.columnCount()+" Records: "+cw.getRowCount());
                }
            }
            Thread.sleep(100);
            myQueue.addKeepingLast(getMessage(20,4));
        }
        */      
       
        
       // simula(1000,false);
        
        //ZonedDateTime zdt=ZonedDateTime.now();
        //System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(zdt));
        //ZonedDateTime zdt=ZonedDateTime.now();
        //String s=DateTimeFormatter.ofPattern("VV").format(zdt);
        //System.out.println(s);
        
       //Runing 5. conversion times for ARFF data format
       
       for(int i=100;i<=5000;i+=100)
            simulaArffConverter(i,-1);
       
       //descriptive();
        
    }

    public static Cincamimis getMessage(int nmeasurements,int nmetrics) throws NoSuchAlgorithmException, LikelihoodDistributionException
    {
        LikelihoodDistribution ld;
        ld = LikelihoodDistribution.factoryRandomDistributionEqualLikelihood(3L, 5L);        
        Context myContext=Context.factoryEstimatedValuesWithoutCD("idMetricContextProperty", ld);
        Random r=new Random();
        
        ArrayList tabla=new ArrayList();
        ArrayList registro;


          registro=new ArrayList();
          Cincamimis flujo=new Cincamimis();
          flujo.setDsAdapterID("dsAdapter1");
          MeasurementItemSet mis=new MeasurementItemSet();
          for(int j=0;j<nmeasurements;j++)
          {//Genero desde 1 hasta i (multiplo de salto) mensajes hastya llegar a volMax
              MeasurementItem mi=MeasurementItem.factory("idEntity1", "dataSource1", "myFormat", "idMetric"+(j%nmetrics), 
                      BigDecimal.TEN.multiply(BigDecimal.valueOf(r.nextGaussian())));
              if((j%2)==0) mi.setContext(myContext);
              mis.add(mi);
          }
        
        flujo.setMeasurements(mis);        
        return flujo;
    }    
    
    /**
     * It is responsible for simulating the associated time in relation to the translation operation
     * @param volMax Maximum number of measurements in the Cincami/MIS object model
     * @param show TRUE indicates that the table should be showed by console, FALSE implies that the table will not be showed.
     * @throws LikelihoodDistributionException It is raised if the likelihood distribution is not correctly defined.
     * @throws NoSuchAlgorithmException It is fired when MD5 is not implemmented on the platform 
     */
    public static void simula(int volMax,boolean show) throws LikelihoodDistributionException, NoSuchAlgorithmException
    {
        LikelihoodDistribution ld;
        ld = LikelihoodDistribution.factoryRandomDistributionEqualLikelihood(3L, 5L);        
        Context myContext=Context.factoryEstimatedValuesWithoutCD("idMetricContextProperty", ld);
        Random r=new Random();
        
        ArrayList tabla=new ArrayList();
        ArrayList registro;


          registro=new ArrayList();
          Cincamimis flujo=new Cincamimis();
          flujo.setDsAdapterID("dsAdapter1");
          MeasurementItemSet mis=new MeasurementItemSet();
          for(int j=0;j<volMax;j++)
          {//Genero desde 1 hasta i (multiplo de salto) mensajes hastya llegar a volMax
              MeasurementItem mi=MeasurementItem.factory("idEntity1", "dataSource1", "myFormat", "idMetric"+(j%3), 
                      BigDecimal.TEN.multiply(BigDecimal.valueOf(r.nextGaussian())));
              if((j%2)==0) mi.setContext(myContext);
              mis.add(mi);
          }
        
        flujo.setMeasurements(mis);
        long before=System.nanoTime();        
        CincamimisWindow tm=TabularMode.translate(flujo);      
        long after=System.nanoTime();
        
        System.out.println("Records: "+tm.getRowCount()+" Tiempo total: "+(after-before));
                
        for(int i=0;show && i<tm.getRowCount();i++)
        {
          if(i==0)
          {
            System.out.println();
            for(int j=0;j<tm.columnCount();j++)
            {
                System.out.print(tm.getColumnMetadata(j).getName()+"\t");
            }
            System.out.println();
          }
          
          Tuple t=tm.getRowAt(i);
          for(int j=0;j<tm.columnCount();j++)
          {
              if(tm.getColumnMetadata(j).isQuantitative())
              {
                Quantitative value=(Quantitative)t.getValue(tm.getColumnMetadata(j));

                if(value!=null && QuantitativeUtils.isConsistent(value))
                {
                    if(QuantitativeUtils.isDeterministic(value))
                    {
                        System.out.print(value.getDeterministicValue()+"\t");
                    }
                    else
                    {
                        System.out.print(QuantitativeUtils.mathematicalExpectation(value)+" (e)\t");
                    }
                }
                else System.out.print("<null>\t");
              }
              else
              {
                String value=(String)t.getValue(tm.getColumnMetadata(j));

                if(!StringUtils.isNull(value))
                {
                    System.out.print((value)+"\t");
                }
                else System.out.print("<null>\t");
                  
              }
          }
          System.out.println();           
        }
    
        if(show)
        {
            ColumnarData cd=tm.getColumnData(2);
            System.out.println(cd.getColumnName().getName()+": ");
            System.out.println();
            for(Optional bdn:cd.getValues())
            {
                System.out.print((bdn.isPresent()?"- ":(bdn.get()+" ")));
            }
            
            System.out.println();
            System.out.println(cd.getColumnName().getName()+" (Without nulls): ");            
            for(Object bdn:cd.getValuesWithoutNull())
            {
                System.out.print(bdn+" ");
            }
            
        }
    }
    
    /**
     * It is responsible for simulating the associated time in relation to the translation operation
     * @param volMax Maximum number of measurements in the Cincami/MIS object model
     * @param threshold Maximum number of measurements to be informed as result
     * @throws LikelihoodDistributionException It is raised if the likelihood distribution is not correctly defined.
     * @throws NoSuchAlgorithmException It is fired when MD5 is not implemmented on the platform 
     */
    public static void simulaArffConverter(int volMax,long threshold) throws LikelihoodDistributionException, NoSuchAlgorithmException, FileNotFoundException, IOException
    {
        LikelihoodDistribution ld;
        ld = LikelihoodDistribution.factoryRandomDistributionEqualLikelihood(3L, 5L);        
        Context myContext=Context.factoryEstimatedValuesWithoutCD("idMetricContextProperty", ld);
        Random r=new Random();
        
        ArrayList tabla=new ArrayList();
        ArrayList registro;


          registro=new ArrayList();
          Cincamimis flujo=new Cincamimis();
          flujo.setDsAdapterID("dsAdapter1");
          MeasurementItemSet mis=new MeasurementItemSet();
          for(int j=0;j<volMax;j++)
          {//Genero desde 1 hasta i (multiplo de salto) mensajes hastya llegar a volMax
              MeasurementItem mi=MeasurementItem.factory("idEntity1", "dataSource1", "myFormat", "idMetric"+(j%3), 
                      BigDecimal.TEN.multiply(BigDecimal.valueOf(r.nextGaussian())));
              if((j%2)==0) mi.setContext(myContext);
              mis.add(mi);
          }
        
        flujo.setMeasurements(mis);
        long before, after,stages[]=new long[2];
        CincamimisWindow tm=TabularMode.translate(flujo);      
        
        before=System.nanoTime();        
        String ret=ArffConverter.fromWindow(tm,"myRelation",threshold);
        System.out.println(ret);
        after=System.nanoTime();
        
        
        if(!StringUtils.isEmpty(ret) && ret.indexOf("@DATA")>0)
            stages[0]=(after-before);
            
        ZonedDateTime zdt=ZonedDateTime.now();
        StringBuilder sb=new StringBuilder();
        sb.append("/Users/mjdivan/weka/archivo").append(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS").format(zdt)).append(".arff");
        before=System.nanoTime();
        FileOutputStream fos=new FileOutputStream(sb.toString(),true);
        ArffConverter.fromWindowToFile(tm, "myRelation", fos, "UTF-8");
        after=System.nanoTime();
        stages[1]=(after-before);
        sb.delete(0, sb.length());
               
        System.out.println("Records: "+tm.getRowCount()+" Conversion: "+stages[0]+" FlushFile: "+stages[1]);
    }
    
    public static void descriptive() throws NoSuchAlgorithmException, LikelihoodDistributionException, FileNotFoundException, IOException
    {
        LikelihoodDistribution ld;
        ld = LikelihoodDistribution.factoryRandomDistributionEqualLikelihood(3L, 5L);        
        Context myContext=Context.factoryEstimatedValuesWithoutCD("idMetricContextProperty", ld);
        Random r=new Random();
        
        ArrayList tabla=new ArrayList();
        ArrayList registro;

          registro=new ArrayList();
          Cincamimis flujo=new Cincamimis();
          flujo.setDsAdapterID("dsAdapter1");
          MeasurementItemSet mis=new MeasurementItemSet();
          for(int j=0;j<15;j++)
          {//Genero desde 1 hasta i (multiplo de salto) mensajes hastya llegar a volMax
              MeasurementItem mi=MeasurementItem.factory("idEntity1", "dataSource1", "myFormat", "idMetric"+(j%3), 
                      BigDecimal.TEN.multiply(BigDecimal.valueOf(r.nextGaussian())));
              if((j%2)==0) mi.setContext(myContext);
              mis.add(mi);
          }
        
        flujo.setMeasurements(mis);
     
        System.out.println(TranslateXML.toXml(flujo));
        System.out.println();
        System.out.println(TranslateJSON.toJSON(flujo));
        System.out.println();
        
        CincamimisWindow tm=TabularMode.translate(flujo);

        for(int i=0;i<tm.getRowCount();i++)
        {
          if(i==0)
          {
            System.out.println();
            for(int j=0;j<tm.columnCount();j++)
            {
                System.out.print(tm.getColumnMetadata(j).getName()+"\t");
            }
            System.out.println();
          }
          
          Tuple t=tm.getRowAt(i);
          DecimalFormatSymbols decSymbol = new DecimalFormatSymbols();
          decSymbol.setDecimalSeparator('.');
          DecimalFormat df=new DecimalFormat("#######0.000");
          
          for(int j=0;j<tm.columnCount();j++)
          {
              if(tm.getColumnMetadata(j).isQuantitative())
              {
                Quantitative value=(Quantitative)t.getValue(tm.getColumnMetadata(j));

                if(value!=null && QuantitativeUtils.isConsistent(value))
                {
                    if(QuantitativeUtils.isDeterministic(value))
                    {
                        System.out.print(df.format(value.getDeterministicValue())+"\t");
                    }
                    else
                    {
                        System.out.print(df.format(QuantitativeUtils.mathematicalExpectation(value))+" (e)\t");
                    }
                }
                else System.out.print("*\t");
              }
              else
              {
                String value=(String)t.getValue(tm.getColumnMetadata(j));

                if(!StringUtils.isNull(value))
                {
                    System.out.print((value)+"\t");
                }
                else System.out.print("*\t");
                  
              }
          }
          System.out.println();           
        }
       
        
        ArrayList lista=ArffConverter.partitionFromWindow(tm,"myRelation",4);
        for(int i=0;i<lista.size();i++)
        {
            System.out.println("***Content***");
            System.out.println(lista.get(i));
            System.out.println();
        }
       //System.out.println(ArffConverter.fromWindow(tm,"myRelation",-1));
        
       //FileOutputStream fos=new FileOutputStream("/Users/mjdivan/archivo.arff",true);
       //ArffConverter.fromWindowToFile(tm,"myRelation",fos,"UTF-8");
        
    }
}
