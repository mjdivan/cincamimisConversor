/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

/**
 * It groups the exceptions related to the CincamimisWindow concept
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class WindowException extends Exception{
    /**
     * Default constructor for the CincamismisWindow Exception
     */
    public WindowException()
    {
        
    }
    
    /**
     * Constructor incorporating a descriptive message
     * @param message The message to be included in the exception
     */
    public WindowException(String message)
    {
        super(message);
    }
}
