/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

/**
 * This class represents an exception related to the queue managing.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class QueueException extends Exception{
    /**
     * Default constructor
     */
    public QueueException()
    {
        
    }
    
    /**
     * Constructor with indicative message.
     * 
     * @param message The message to be associated to the exception.
     */
    public QueueException(String message)
    {
        super(message);
    }
}
