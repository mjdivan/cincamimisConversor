/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

/**
 * This class represents an exception for the absence of the attribute metadata
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class AttributeException extends Exception{
    /**
     * Default public constructor
     */
    public AttributeException()
    {
        
    }
    
    /**
     * public constructor incorporating a descritive message
     * 
     * @param message The message to be included inside the exception
     */
    public AttributeException(String message)
    {
        super(message);
    }
    
}
