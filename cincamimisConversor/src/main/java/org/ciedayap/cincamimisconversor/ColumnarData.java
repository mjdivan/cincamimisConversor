/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.ciedayap.cincamimis.LikelihoodDistributionException;
import org.ciedayap.cincamimis.Quantitative;
import org.ciedayap.utils.NullableBigDecimal;
import org.ciedayap.utils.QuantitativeUtils;
import org.ciedayap.utils.StringUtils;

/**
 * This class is responsible for the columnar organization of the data in memory.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class ColumnarData implements Serializable{
    private final ArrayList<Quantitative> data;
    private final ArrayList<String> dataqualitative;
    private Attribute columnName;
    private final Quantitative nulo;

    /**
     * Default constructor. It is responsible for the ArrayList instance
     * related to the data. The column name must be defined for associating
     * the data with a given attribute.
     */
    public ColumnarData()
    {
        this.data=new ArrayList();  
        this.dataqualitative=new ArrayList();
        nulo=new Quantitative();
    }
    
    public synchronized static ColumnarData create(Attribute at)
    {
        ColumnarData cd=new ColumnarData();
        cd.setColumnName(at);
        return cd;
    }

    /**
     * @return the columnName to be represened 
     */
    public Attribute getColumnName() {
        return columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(Attribute columnName) {
        this.columnName = columnName;
    }
    
    /**
     * It Incorporates a quantitative measure in the arraylist in memory.
     * @param value The quantitative value to be incorporated
     * @return TRUE if the value was incorporated, false otherwise.
     * @throws AttributeException This exception is raised when the column name was not defined.
     */
    public synchronized boolean addValue(Quantitative value) throws AttributeException
    {
        if(columnName==null) throw new AttributeException("The column name is not defined.");
        if(data==null) return false;
        if(!columnName.isQuantitative())throw new AttributeException("The column is defined as qualitative.");
        
        Boolean rdo=QuantitativeUtils.isConsistent(value);
        if(value==null || rdo==null || !rdo) return false;
        
        return data.add(value);
    }
    
    /**
     * It incorporates a qualitative measure in the specific arraylist in memory
     * @param value The qualitative value as string
     * @return TRUE if the value was succesfully added, FALSE otherwise
     * @throws AttributeException  It is raised when the column is not defined, or when the quantitative kind
     * is defined for the associated attribute
     */
    public synchronized boolean addValue(String value) throws AttributeException
    {
        if(columnName==null) throw new AttributeException("The column name is not defined.");
        if(dataqualitative==null) return false;
        if(columnName.isQuantitative())throw new AttributeException("The column is defined as quantitative.");
        if(StringUtils.isEmpty(value)) return false;
                
        return dataqualitative.add(value);        
    }
    
    /**
     * Incorporates an empty quantitative or qualitative instance at the end of the list in the array in memory.
     * When the value is quantitative, It is possible detect this kind of instance by mean of the use of the QuantitativeUtils.isConsistent(),
     * because the result will be false in this case.
     * @return TRUE if the empty instance was incorporated, false otherwise.
     * @throws AttributeException This exception is raised when the column name was not defined
     * @see QuantitativeUtils
     */
    public synchronized boolean addNull() throws AttributeException
    {
        if(columnName==null) throw new AttributeException("The column name is not defined.");
        if(columnName.isQuantitative() && data==null) return false;
        if(!columnName.isQuantitative() && dataqualitative==null) return false;

        return (columnName.isQuantitative())?data.add(nulo):dataqualitative.add(StringUtils.NULL_Pattern);
    }
    
    /**
     * This method removes the value in the indicated position.
     * @param index the index to be deleted from the array.
     * @return TRUE if the value could be deleted from the array, flase otherwise.
     * @throws AttributeException This exception is raised when the column name was not defined
     */
    public synchronized boolean removeValue(int index) throws AttributeException
    {
        if(columnName==null) throw new AttributeException("The column name is not defined.");
        if(columnName.isQuantitative() && data==null) return false;
        if(!columnName.isQuantitative() && dataqualitative==null) return false;

        if(index<0 || (columnName.isQuantitative() && index>=data.size()) ||
                (!columnName.isQuantitative() && index>=dataqualitative.size())) return false;
       
        Object ptr;
        if(columnName.isQuantitative())
            ptr=data.remove(index);
        else
            ptr=dataqualitative.remove(index);
        
        return (ptr!=null);
    }
    
    /**
     * It removes all the values from the list
     * @return TRUE if the values ci¡ould be removed, FALSE otherwise.
     * @throws AttributeException This exception is raised when the column name was not defined
     */
    public synchronized boolean removeAllValues() throws AttributeException
    {
        if(columnName==null) throw new AttributeException("The column name is not defined.");
        if(columnName.isQuantitative() && data==null) return false;
        if(!columnName.isQuantitative() && dataqualitative==null) return false;
        
        if(columnName.isQuantitative()) data.clear();
        else dataqualitative.clear();
        
        return Boolean.TRUE;
    }
     
    /**
     * It gets the consistent value (estimated or deterministic) from the indicated index from the array. 
     * @param index The position of the value inside of the array
     * @return the Quantitative instance if the value is consistent, null otherwise.
     * @throws AttributeException This exception is raised when the column name was not defined
     */
    public synchronized Object getValueAt(Integer index) throws AttributeException
    {
        if(columnName==null) throw new AttributeException("The column name is not defined.");        
        if(columnName.isQuantitative() && data==null) return null;
        if(!columnName.isQuantitative() && dataqualitative==null) return null;
        
        if(index==null) return null;
        if(index<0 || (columnName.isQuantitative() && index>=data.size()) || 
               (!columnName.isQuantitative() && index>=dataqualitative.size())) return null;
        
        if(columnName.isQuantitative())
        {
            Quantitative q=data.get(index);
            if(!QuantitativeUtils.isConsistent(q)) return null;

            return q;
        }
        
        //Returning the qualitative value
        String val=dataqualitative.get(index);
        if(StringUtils.isNull(val)) return null;
        
        return val;
    }

    /**
     * This method verifies if the value in the given position is consistent or not.
     * When the value is qualitative, it verifies that the value be a determiated value (Not null).
     * @param index The index to be verified.
     * @return TRUE if the value is consistent, FALSE otherwise.
     * @throws AttributeException This exception is raised when the column name was not defined
     */
    public boolean existValue(int index) throws AttributeException
    {
        if(columnName==null) throw new AttributeException("The column name is not defined.");
        if(columnName.isQuantitative() && data==null) return false;
        if(!columnName.isQuantitative() && dataqualitative==null) return false;
        if(index<0 || (columnName.isQuantitative() && index>=data.size())
                || (!columnName.isQuantitative() && index>=dataqualitative.size())) return false;
        
        if(columnName.isQuantitative())
        {
            Quantitative q=data.get(index);

            return QuantitativeUtils.isConsistent(q);        
        }
        
        String val=dataqualitative.get(index);
        if(StringUtils.isNull(val)) return false;
        
        return !StringUtils.isEmpty(val);
    }
    
    /**
     * This method converts the Quantitative/Qualitative Instances in one array with
     * Optional Instances. The Optional is able to represent
     * a BigDecimal, a string or even a null value.
     * @return NULL if the original array is null or its size is 0 (cero); else 
     * this method will return a new array with a set of Optional instances
     * in the same order that the original array.
     */
    public List<Optional> getValues()
    {
        if(columnName==null) return null;
        if(columnName.isQuantitative() && (data==null || data.isEmpty())) return null;
        if(!columnName.isQuantitative() && (dataqualitative==null || dataqualitative.isEmpty())) return null;
        
        if(columnName.isQuantitative())
            return data.stream()
                    .map((q) -> QuantitativeUtils.toNullableBigDecimal(q))
                    .collect(Collectors.toList());               
        else
            return dataqualitative.stream()
                    .map((q) -> StringUtils.toNullableString(q))
                    .collect(Collectors.toList());               
    }

    /**
     * This method converts the Quantitative Instances in one array just with
     * BigDecimal Instances (all the instances have a value). The NullableBigDecimal is able to represent
     * a BigDecimal, or even, a null value.
     * @return NULL if the original array is null or its size is 0 (cero); else 
     * this method will return a new array with a set of NullableBigDecimal instances
     * in the same order that the original array.
     */
    public List<Object> getValuesWithoutNull()
    {
        if(columnName==null) return null;
        if(columnName.isQuantitative() && (data==null || data.isEmpty())) return null;
        if(!columnName.isQuantitative() && (dataqualitative==null || dataqualitative.isEmpty())) return null;
        
        if(columnName.isQuantitative())
        {
            List<Object> myList= data.stream()
                    .filter(p->QuantitativeUtils.isConsistent(p))
                    .map((Quantitative q) -> 
                    {
                        if(QuantitativeUtils.isDeterministic(q)) return q.getDeterministicValue();

                        try {
                            return QuantitativeUtils.mathematicalExpectation(q);
                        } catch (LikelihoodDistributionException ex) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList());             

            for(int i=0;myList!=null && i<myList.size();i++)
            {
                BigDecimal bd=(BigDecimal)myList.get(i);
                if(bd==null) 
                {
                    myList.remove(i);
                    i--;
                }
            }

            return myList;
        }
        else
        {
            List<Object> myList= dataqualitative.stream()
                    .filter(p->!StringUtils.isEmpty(p)) //Not Empty
                    .filter(p->!StringUtils.isNull(p)) //Not Null                    
                    .collect(Collectors.toList());             

            return myList;            
        }
    }
    
    /**
     * It returns the current quantity of records in the list.
     * @return the number of records in the list, considering the nulls.
     */
    public synchronized int size()
    {
        if(columnName==null) return 0;
        if(columnName.isQuantitative() && (data==null || data.isEmpty())) return 0;
        if(!columnName.isQuantitative() && (dataqualitative==null || dataqualitative.isEmpty())) return 0;

        return (columnName.isQuantitative())?data.size():dataqualitative.size();
    }
    
    /**
     * It indicates if the data list is empty or not.
     * @return TRUE if the data list is empty, FALSE otherwise.
     */
    public synchronized boolean isEmpty()
    {
        if(columnName==null) return true;
        if(columnName.isQuantitative() && data==null) return true;
        if(!columnName.isQuantitative() && dataqualitative==null) return true;

        return (columnName.isQuantitative())?data.isEmpty():dataqualitative.isEmpty();
    }    
}
