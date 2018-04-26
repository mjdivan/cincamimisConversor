/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
import org.ciedayap.utils.StringUtils;

/**
 * It is responsible for the definition of the columns in a tabular view, 
 * keeping the appropiated order.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class Attributes implements Serializable{
    private final ArrayList<Attribute> metadata;
    
    public Attributes()
    {
        metadata=new ArrayList();
    }
    
    
    /**
     * It is responsible for incorporating a new attribute in the column list.
     * if the column is present, then the column is updated, else it will be added.
     * 
     * @param at Attribute to be added
     * @return true if the column was added, false otherwise.
     */
    public synchronized boolean add(Attribute at)
    {
        if(at==null || metadata==null) return false;
        int idx=-1;
        if(metadata.contains(at)) {
            idx=metadata.indexOf(at); 
            metadata.remove(at);
        }
        
        if(idx<0) return metadata.add(at);
        
        metadata.add(idx, at);     
        
        return metadata.contains(at);
    }
    
    /**
     * It is responsible for removing the indicated attribute from the 
     * column list.
     * @param at The attribute to be removed
     * @return TRUE if the column was succesfully removed, false otherwise.
     */
    public synchronized boolean remove(Attribute at)
    {
        if(at==null || metadata==null) return false;
        
        return metadata.remove(at);
    }
    
    /**
     * It is responsible for checking whether an attribute is present or not
     * along the column list
     * @param at The attribute to be checked
     * @return TRUE if the attribute is present, false otherwise.
     */
    public boolean isPresent(Attribute at)
    {
        if(at==null || metadata==null) return false;
        
        return metadata.contains(at);
    }
    
    /**
     * It returns the order associated with the first ocurrence of the attribute
     * along the attribute list.
     * @param at Attribute to be searched.
     * @return The index related to the attribute position, null when the attribute
     * has not been located.
     */
    public Long getOrder(Attribute at)
    {
        if(at==null || metadata==null) return null;
        
        int idx=metadata.indexOf(at);
        
        return (idx<0)?null:(long)idx;
    }
    
    /**
     * It returns the attribute object in the position given by index
     * @param index The index related to a given attribute
     * @return The attribute related to the given index, null otherwise.
     */
    public Attribute get(int index)
    {
        if(metadata==null || metadata.isEmpty()) return null;
        if(index<0 || index>=metadata.size()) return null;
       
        return metadata.get(index);        
    }
    
    /**
     * It is responsible for looking for attributes which match with the name 
     * as parameter.
     * @param name The attribute name to be searched
     * @return An array of Attribute instances with the attributes which match with
     * the given name as parameter
     */
    public Optional<Attribute> getAttributeByName(String name)
    {
        if(metadata==null) return null;
        if(StringUtils.isEmpty(name)) return null;
        
        return metadata.stream()             
                .filter(at->at.getName().equalsIgnoreCase(name))
                .distinct()
                .findFirst();
        
    }

    /**
     * It returns the column index related to each attribute which math the name with
     * the given parameter
     * @param name The column name to be verified
     * @return An array of integer with the positions of each column which match
     * with the column name given as parameter, null otherwise.
     */
    public Integer[] getAttributeIndexByName(String name)
    {
        if(metadata==null) return null;
        if(StringUtils.isEmpty(name)) return null;
        ArrayList<Integer> lista=new ArrayList();
        if(metadata.isEmpty()) return (Integer[])lista.toArray();
        
        for(int i=0;i<metadata.size();i++)
        {
            String objectname=metadata.get(i).getName();
            if(!StringUtils.isEmpty(objectname) && objectname.equalsIgnoreCase(name))
                lista.add(i);
        }
        
        return (Integer[])lista.toArray();                
    }
    
    /**
     * It returns the quantity of columns in the tabular view
     * @return The quantity of columns
     */
    public int columnCount()
    {
        if(metadata==null) return 0;
        return metadata.size();
    }
}
