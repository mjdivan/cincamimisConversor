/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

import java.util.concurrent.ConcurrentHashMap;
import org.ciedayap.cincamimis.Quantitative;
import org.ciedayap.utils.StringUtils;

/**
 * This class is responsible for showing the set of measures obtained from
 * the CINCAMI/MIS stream like a Table. This is friendlier in the 
 * environments which require the Tuple idea on each set of measures.
 * @author Mario Diván
 * @version 1.0
 */
public class CincamimisWindow implements TableView{
    /**
     * The set of ordered attributes which integrate the Table
     */
    private final Attributes globalAttributes;
    /**
     * It is responsible for mapping instances of ColumnarData for managing
     * the data under the columnar background. The key is associated with the
     * hashcode method from Attribute class.
     */
    private final ConcurrentHashMap<Long,ColumnarData> columns;
    /**
     * It represents the quantity of records in the table
     */
    private Integer currentLength;
    /**
     * It represents the current state of the object
     */
    private short state;
    
    /**
     * It represents the start point of the object, it is initialized but it has not data. That
     * is to say, the object is empty, it does not contain data.
     */
    public static final short STARTED=0;
    /**
     * It represents the situation in which the object is being populated with
     * data, but the data feeding is currently active.
     */
    public static final short CREATING=1;
    /**
     * It represents that the data was loaded, and they are available to be consumed.
     */
    public static final short CREATED=2;
    
    /**
     * The current row position in the table. Initially is -1L, because it never has been called.
     * After the window was created and at least it has one record, the current row will be 0.
     */
    private Integer currentRow;
    
    /**
     * The constructor for the CINCAMI/MIS Window which is viewable like a Table.
     * Once the attributes was defined, they cannot be changed.
     * 
     * @param myAttributes The set of attributes which will integrate the table as columns
     * @throws org.ciedayap.cincamimisconversor.AttributeException It is raised when the columns are not defined
     * @throws org.ciedayap.cincamimisconversor.WindowException It is raised when the object state cannot be changed.
     */
    public CincamimisWindow(Attributes myAttributes) throws AttributeException, WindowException
    {
        if(myAttributes==null || myAttributes.columnCount()==0) throw new AttributeException("There are not columns in the definition");
        
        if(!changeStateTo(STARTED))
        {
            throw new WindowException("It is not possible change the state");
        }
        
        columns=new ConcurrentHashMap(myAttributes.columnCount());
        
        //The hash key is given by the column family name and column name
        //Initialize the HashMap
        for(int i=0;i<myAttributes.columnCount();i++)            
        {
            Attribute at=myAttributes.get(i);
            ColumnarData cd=ColumnarData.create(at);
            columns.put((long)at.hashCode(), cd);            
        }
        
        this.globalAttributes=myAttributes;
        currentLength=0;
        currentRow=-1;
    }
    
    /**
     * It changes the current state of the object if it is possible, depending of
     * the origin and target state.
     * The Initial State is STARTED. The valid transition is just to the
     * CREATING state. Once we get out from the STARTED state, it is not possible come back.
     * It is possible to arrive to the CREATING state from STARTED or 
     * CREATED, but the unique out transition from here, it is to the CREATED state.
     * It is possible to arrive to the  CREATED state just from the CREATING state.
     * From the CREATED state,It is just possible  the transition to the CREATING state.
     * @param toState
     * @return TRUE if the state was succesfully changed, FALSE otherwise.
     */
    protected synchronized final boolean changeStateTo(short toState)
    {
        switch(toState)
        {
            case STARTED:
                if(getState()==CREATING || getState()==CREATED) return false;//The current state is the same
                this.state=toState;
                
                return true;//This state is only valid at the beginning of the life cycle of the object.
            case CREATING:
                if(this.globalAttributes==null || this.globalAttributes.columnCount()==0) return false;
                if(columns==null) return false;
                
                this.state=toState;
                return true;
            case CREATED:                
                if(this.getState()==CREATING)
                {
                    if(this.currentLength==null || this.currentLength==0) return false;
                    if(this.columns.isEmpty()) return false;
                    
                    this.state=toState;
                    return true;
                }
            default:
                return false;
        }
    }
    
    @Override
    public synchronized Tuple getFirst() 
    {
        if(state!=CincamimisWindow.CREATED) return null;
        
        return getRowAt(0);
    }

    @Override
    public synchronized Tuple getRowAt(Integer index) {
        if(state!=CincamimisWindow.CREATED) return null;
        
        Tuple ret=Tuple.create(globalAttributes);
        if(ret==null) return null;
        
        for(int i=0;i<globalAttributes.columnCount();i++)
        {
            Attribute at=globalAttributes.get(i);
            ColumnarData cd=columns.get((long)at.hashCode()); 
            if(cd!=null && !cd.isEmpty())
            {
                if(at.isQuantitative())
                {                    
                    Quantitative qvalue;

                    try {
                        qvalue=(Quantitative)cd.getValueAt(index);
                    } catch (AttributeException ex) {                       
                        return null;
                    }

                    ret.update(at, qvalue);
                }
                else
                {
                    String qvalue;

                    try {
                        qvalue=(String)cd.getValueAt(index);
                    } catch (AttributeException ex) {                       
                        return null;
                    }

                    ret.update(at, qvalue);                    
                }
            }

        }
        
        return ret;        
    }

    @Override
    public synchronized Integer getRowCount() {        
        Attribute col0=this.globalAttributes.get(0);
        if(col0==null) return null;
        ColumnarData cd=this.columns.get((long)col0.hashCode());
        if(cd==null) return null;
        
        this.currentLength=cd.size();
        
        return this.currentLength;
    }

    public int columnCount()            
    {
        switch(state)
        {
            case STARTED:
            case CREATING:
            case CREATED:                        
                return this.globalAttributes.columnCount();
            default:
                return 0;
        }        
    }
    
    @Override
    public ColumnarData getColumnData(int index) {
        switch(state)
        {
            case STARTED:
            case CREATING:
            case CREATED:        
                Attribute myAttribute=getColumnMetadata(index);
                if(myAttribute==null) return null;

                return this.columns.get((long)myAttribute.hashCode());
            default:
                return null;
        }
    }

    @Override
    public Attribute getColumnMetadata(int index) {
        switch(state)
        {
            case STARTED:
            case CREATING:
            case CREATED:
                if(index<0 || index>globalAttributes.columnCount()) return null;
                return globalAttributes.get(index);
            default:
                return null;
        }
    }

    @Override
    public Integer[] getColumnIndex(String name) {
        if(StringUtils.isEmpty(name)) return null;
        
        switch(state)
        {
            case STARTED:
            case CREATING:
            case CREATED:
                return globalAttributes.getAttributeIndexByName(name);
            default:
                return null;
        }

    }

    @Override
    public synchronized Tuple next() {
        if(state!=CREATED) return null;
        Integer rc=this.getRowCount();
        if(rc==null || rc==0) return null;//The dataset is empty
        
        if(this.currentRow<0)
        {
            Tuple p=this.getRowAt(0);
            if(p==null) return null;
            
            //The cursos is located at the first position
            currentRow=0;
            return p;
        }
        
        if((currentRow+1)>=rc)
        {//The end has been reached
            return null;
        }
        
        Tuple p=this.getRowAt((currentRow+1));
        if(p!=null)
        {
            currentRow++;
            return p;
        }
        
        return null;
    }

    @Override
    public Integer currentRow() {
        if(state!=CREATED) return null;
        
        return this.currentRow;
    }

    @Override
    public synchronized boolean addTuple(Tuple t) {
        if(state!=CREATING) return false;
        
        if(t==null) return false;
        if(!t.isConsistent()) return false;
        ColumnarData cd;
        
        for(int i=0;i<this.globalAttributes.columnCount();i++)
        {
            Attribute at=globalAttributes.get(i);
            
            if(at.isQuantitative())
            {
                Quantitative value=(Quantitative)t.getValue(at);
                cd=columns.get((long)at.hashCode());

                try{
                    if(value==null) cd.addNull();
                    else cd.addValue(value);
                }catch(AttributeException e)
                {

                }
            }
            else
            {
                String value=(String)t.getValue(at);
                cd=columns.get((long)at.hashCode());

                try{
                    if(value==null || StringUtils.isEmpty(value) || StringUtils.isNull(value)) cd.addNull();
                    else cd.addValue(value);
                }catch(AttributeException e)
                {

                }                
            }
            
            this.currentLength=cd.size();
            this.currentRow=-1;
        }            
        
        return true;
    }

    @Override
    public boolean removeAt(int index) {
        if(state!=CREATING) return false;
        if(this.getCurrentLength()==null) return false;
        if(index<0 || index>=this.getCurrentLength()) return false;
        
        ColumnarData cd;
        
        for(int i=0;i<this.globalAttributes.columnCount();i++)
        {
            Attribute at=globalAttributes.get(i);            
            cd=columns.get((long)at.hashCode());
            
            try{
                cd.removeValue(index);                
            }catch(AttributeException e)
            {
                
            }
            
            this.currentLength=cd.size();            
        }            
        
        if(currentRow>=index) currentRow--;
        
        return true;
    }

    @Override
    public boolean removeAll() {
        if(state!=CREATING) return false;
        if(this.getCurrentLength()==null) return false;
        
        columns.values().forEach((cd) -> {
            try {
                cd.removeAllValues();
            } catch (AttributeException ex) {
                
            }
        });

        this.currentLength=0;
        this.currentRow=-1;
        
        return true;
    }

    @Override
    public boolean moveCursor(int index) {
        if(state!=CREATED) return false;
        if(this.getCurrentLength()==null) return false;
        if(index<0 || index>=this.getCurrentLength()) return false;

        this.currentRow=index;
        return true;
    }

    /**
     * @return the current state related to the instance
     */
    public short getState() {
        return state;
    }

    /**
     * @return the current row related to the cursor just when the state is CREATED; null otherwise.
     */
    public synchronized Integer getCurrentRow() {
        if(state==CREATED) return currentRow;
        
        return null;
    }

    /**
     * @return the currentLength
     */
    public synchronized Integer getCurrentLength() {
        return currentLength;
    }
    
}
