/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.ciedayap.cincamimis.Quantitative;
import org.ciedayap.utils.StringUtils;

/**
 * It is responsible for building the concept of tuple in the tabular view.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class Tuple implements Serializable{
    protected final Attributes attributes;
    private final ConcurrentHashMap<Long,Object> valuesByAttribute;
    
    public Tuple(Attributes at)
    {
        this.attributes=at;
        valuesByAttribute=new ConcurrentHashMap(at.columnCount());
    }
    
    /**
     * It returns the attributes related to the Tuple instance
     * @return  The associated attributes to the Tuple instance
     */
    public Attributes getAttributes()
    {
        return attributes;
    }
    
    /**
     * This method creates an empty tuple with the given attributes
     * 
     * @param at The attributes for composing the tuple.
     * @return a tuple instance
     */
    public synchronized static Tuple create(Attributes at)
    {
        if(at==null) return null;
        Tuple t= new Tuple(at);
        return t;
    }
    
    /**
     * It is responsible for updating/incorporating the attribute value.
     * @param at Attribute to be updated
     * @param value New value to be incorporated
     * @return TRUE if the updating could be made, false otherwise.
     */
    public final synchronized boolean update(Attribute at, Object value)
    {
        if(at==null || value==null) return false;
        if(valuesByAttribute==null) return false;
        
        if(valuesByAttribute.containsKey((long)at.hashCode()))
        {
            valuesByAttribute.replace((long)at.hashCode(), value);
        }
        else
        {
            valuesByAttribute.put((long)at.hashCode(), value);            
        }
        
        return true;
    }
   
    /**
     * It is responsible for removing the value from a given attribute.
     * In this way, the attribute will not have a value, being "null" if the tuple
     * is required.
     * @param at the attribute for removing the value from the tuple
     * @return TRUE if the attribute value was succesfully removed, false otherwise.
     */
   public synchronized boolean remove(Attribute at)
   {
       if(at==null) return false;
       if(valuesByAttribute==null) return false;
       
       Object previous=valuesByAttribute.remove((long)at.hashCode());
       
       return (previous != null);
   }
   
   /**
    * It is responsible for getting the value related to the attribute
    * given as parameter
    * @param at The attribute for which it is neccesary to know its value
    * @return It will return an Object (Quantitative or String) instance with the measure when 
    * the attribute and value are present, null otherwise.
    */
   public synchronized Object getValue(Attribute at)
   {
       if(at==null) return null;
       if(valuesByAttribute==null) return null;
       
       return valuesByAttribute.get((long)at.hashCode());
   }
   
   /**
    * It is responsible for verifying the existence of value in relation to the 
    * attribute given as parameter.-
    * 
    * @param at The attribute to be verified
    * @return TRUE if there exists value for the attribute, false otherwise
    */
   public synchronized boolean existValue(Attribute at)
   {
       if(at==null) return false;
       
       return getValue(at)!=null;
   }
   
   /**
    * It gives the index of the attributes which have the name given as parameter
    * @param name The column name to be seached
    * @return An array of Long with the index of columns with the name given as parameter,
    * null otherwise.
    */
   public synchronized Long[] getIndex(String name)
   {
       if(StringUtils.isEmpty(name)) return null;
       if(attributes==null) return null;
       
       ArrayList<Long> ret=new ArrayList();
       for(int i=0;i<attributes.columnCount();i++)
       {
           Attribute at=attributes.get(i);
           if(at!=null && !StringUtils.isEmpty(at.getName()) && at.getName().equalsIgnoreCase(name))
           {
               ret.add(new Long(i));
           }
       }
       
       return (Long[])ret.toArray();
   }

   /**
    * It gives the attribute which have the name given as parameter
    * @param name The column name to be searched
    * @return An Optional instance with the Attribute as value when it match with the given name, null otherwise.
    */
   public synchronized Optional<Attribute> getAttributesByName(String name)
   {
       if(StringUtils.isEmpty(name)) return null;
       if(attributes==null) return null;
       
       return attributes.getAttributeByName(name);
   }
   
   /**
    * It is responsible for checking the metadata availability and to verify than 
    * at least one value along the tuple should be defined.
    * 
    * @return TRUE if the attributes are defined and at least one value is defined
    * for one attribute, FALSE otherwise.
    */
   public synchronized boolean isConsistent()
   {
       if(attributes==null){
           System.out.println("[Tuple] No Attributes");
           return false;
       }
       if(attributes.columnCount()==0) 
       {
           System.out.println("[Tuple] Attributes. Column Count: 0");
           return false;
       }
       
       for(int i=0;i<attributes.columnCount();i++)
       {
           if(this.existValue(attributes.get(i))) return true;
       }
                     
       return false;
   }
}
