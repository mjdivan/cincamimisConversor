/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

import java.io.Serializable;
import java.util.Objects;
import org.ciedayap.utils.StringUtils;

/**
 * This class is responsible for grouping the set of columns which shows similar
 * pattern accesing and/or size. This definition is important when the user
 * want to convert the cincami/mis stream to some columnar database, such as 
 * Apache HBase
 * 
 * @author Mario Diván
 * @version 1.o
 */
public class ColumnFamily implements Serializable {
    private String name;
    
    /**
     * Factory method for the column family
     * @param name the name to be given to the column family. The name must be printable.
     * @return An instance of column family if the name 
     */
    public synchronized static ColumnFamily create(String name)
    {
        if(name==null || name.trim().length()==0) return null;
        if(!StringUtils.isPrintable(name)) return null;
        
        ColumnFamily cf=new ColumnFamily();
        cf.setName(name);
        
        return cf;
    }
    
    /**
     * @return the column family name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the column family name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public int hashCode()
    {
        return (name!=null)?name.hashCode():null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ColumnFamily other = (ColumnFamily) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
}
