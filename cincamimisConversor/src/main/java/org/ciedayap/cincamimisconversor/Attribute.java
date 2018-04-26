/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class is responsible for representing a quantitative variable in terms 
 * of statistical analysis, which is associated with a measure coming from 
 * a metric in CINCAMI/MIS.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class Attribute implements Serializable{
    private String name;
    private Class kind;
    private Long order;
    private ColumnFamily columnFamily;
    private boolean quantitative;
    

    /**
     * Default constructor. By default, the attribute is quantitative.
     */
    public Attribute()
    {
        quantitative=true;
    }
    
    /**
     * Factory method.
     * 
     * @param name The name of the attribute
     * @param kind The kind of attribute expressed bu the class
     * @param order The associated order related to the attribute in a given table
     * @param cf The column family wich the attribute is belonging
     * @return The Attribute instance when is succesfully created.
     */
    public synchronized static Attribute create(String name,Class kind,Long order,ColumnFamily cf)
    {
        Attribute at=new Attribute();
        at.setName(name);
        at.setColumnFamily(cf);
        at.setKind(kind);
        at.setOrder(order);
        at.quantitative=true;
        
        return at;
    }

    /**
     * Factory method indicating the attribute interpretation. That is to say if the attribute is qualitative or quantitative.
     * 
     * @param name The name of the attribute
     * @param kind The kind of attribute expressed bu the class
     * @param order The associated order related to the attribute in a given table
     * @param cf The column family wich the attribute is belonging
     * @param pquantitative TRUE when the attribute should be interpreted as quantitative, FALSE when it should be interpreted
     * as qualitative.
     * @return The Attribute instance when is succesfully created.
     */
    public synchronized static Attribute create(String name,Class kind,Long order,ColumnFamily cf,boolean pquantitative)
    {
        Attribute at=new Attribute();
        at.setName(name);
        at.setColumnFamily(cf);
        at.setKind(kind);
        at.setOrder(order);
        at.quantitative=pquantitative;
        
        return at;
    }
    
    /**
     * @return the attribute name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the attribute name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the associated kind of column
     */
    public Class getKind() {
        return kind;
    }

    /**
     * @param kind the kind of column to set
     */
    public void setKind(Class kind) {
        this.kind = kind;
    }

    /**
     * @return the associated order in relation to a given column considering a
     * tabular view and counting from left to rigth.
     */
    public Long getOrder() {
        return order;
    }

    /**
     * @param order the given order to set to a column in tabular view and counting 
     * from left to right (e.g. column 1 is the first column from the left)
     */
    public void setOrder(Long order) {
        if(order==null || order<1) return;
        
        this.order = order;
    }

    /**
     * @return the columnFamily
     */
    public ColumnFamily getColumnFamily() {
        return columnFamily;
    }

    /**
     * @param columnFamily the columnFamily to set
     */
    public void setColumnFamily(ColumnFamily columnFamily) {
        this.columnFamily = columnFamily;
    }
    
    @Override
    public int hashCode()
    {
        return (name!=null && columnFamily!=null)?Objects.hash(columnFamily,name):null;
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
        final Attribute other = (Attribute) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.columnFamily, other.columnFamily)) {
            return false;
        }
        return true;
    }

    /**
     * @return the quantitative
     */
    public boolean isQuantitative() {
        return quantitative;
    }

    /**
     * @param quantitative the quantitative to set
     */
    public void setQuantitative(boolean quantitative) {
        this.quantitative = quantitative;
    }

}
