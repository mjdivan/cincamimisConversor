/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

import org.ciedayap.cincamimis.Cincamimis;

/**
 * It is responsable for the translating from the CINCAMI/MIS message as String to
 * the CINCAMI/MIS Object Model.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public abstract class CincamimisTranslator implements Runnable{
    protected CincamimisQueue queue;
    
    /**
     * It is responsible for associating the CincamimisQueue object with the
     * Active Translators.
     * @param queue The queue to be consumed
     * @throws QueueException It is raised when the informed queue is null.
     */
    public CincamimisTranslator(CincamimisQueue queue) throws QueueException
    {
        if(queue==null) throw new QueueException("The informed queue is null");
        
        this.queue=queue;
    }
            

}
