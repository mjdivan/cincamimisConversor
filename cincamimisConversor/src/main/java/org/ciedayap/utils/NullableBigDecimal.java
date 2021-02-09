/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * This class encapsulate a BigDecimal allowing incorporates null when
 * there is not a given value.
 * 
 * @author Mario Diván
 * @version  1.0
 */
public class NullableBigDecimal implements Serializable{
    private BigDecimal value;
    
    /**
     * Default constructor. By default the value is established as null.
     */
    public NullableBigDecimal()
    {
        value=null; //By default the value is null
    }
    
    /**
     * Constructor which establishes a particular value
     * @param v The value to be incorporated
     */
    public NullableBigDecimal(BigDecimal v)
    {
        value=v;
    }

    /**
     * @return the value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(BigDecimal value) {
        this.value = (value!=null)?value:null;
    }
    
    /**
     * It indicates if the value was defined or not 
     * 
     * @return TRUE if it is nod defined, FALSE otherwise
     */
    public boolean isNull()
    {
        return (value==null);
    }
    
    @Override
    public int hashCode()
    {
      if(value==null) return BigDecimal.ZERO.hashCode();
      
      return value.hashCode();
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
        final NullableBigDecimal other = (NullableBigDecimal) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString()
    {
        return (value!=null)?value.toString():null;
    }
    
    /**
     * This method allows create a NullableBigDecimal from a BigDecimal
     * @param v The BigDeciaml to be incorporated in the instance
     * @return a NullabkleBigDeciaml
     */
    public static NullableBigDecimal create(BigDecimal v)
    {
        return new NullableBigDecimal(v);
    }
}
