/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor;

import java.util.Observable;
import java.util.concurrent.ArrayBlockingQueue;
import org.ciedayap.cincamimis.Cincamimis;

/**
 * It is responsible for managing the queue associated with CINCAMIMIS messages.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class CincamimisQueue extends Observable{
    private final ArrayBlockingQueue<Cincamimis> measurementQueue;
    
    /**
     * Default constructor. It is responsible for defining the queue capacity to 10 
     * and the operation mode will be FIFO (Fisrt Input, First Output).
     */
    public CincamimisQueue()
    {
        measurementQueue=new ArrayBlockingQueue(10,true);
    }
  
    /**
     * It is responsible for defining the queue capacity and the operation mode.
     * @param capacity The queue capacity
     * @param fifo if {@code true} then queue accesses for threads blocked
     *        on insertion or removal, are processed in FIFO order;
     *        if {@code false} the access order is unspecified.
     */
    public CincamimisQueue(int capacity, boolean fifo)
    {
        measurementQueue=new ArrayBlockingQueue(((capacity<10)?10:capacity),fifo);
    }
    
    /**
     * It is responsible for incorporating a new CINCAMI/MIS message at the end 
     * of the queue
     * @param message The message to be incorporated
     * @return TRUE if the message was incorporated and the observers were notified.
     * @throws QueueException This exception is raised when there is not an 
     * initialized queue.
     */
    public synchronized boolean add(Cincamimis message) throws QueueException
    {
        if(message==null) return false;
        if(measurementQueue==null) throw new QueueException("CINCAMIMIS Queue not found");
        
        boolean rdo=measurementQueue.offer(message);
        
        if(rdo)
        {
            this.notifyObservers();
            return true;
        }
        
        return false;
    }

    /**
     * It is responsible for incorporating a new CINCAMI/MIS message at the end 
     * of the queue. if the queue is full, the first element is remnoved and the 
     * new element is incorporated at the end of the queue.
     * @param message The message to be incorporated
     * @return TRUE if the message was incorporated and the observers were notified.
     * @throws QueueException This exception is raised when there is not an 
     * initialized queue.
     * @throws InterruptedException if interrupted while waiting
     */
    public synchronized boolean addKeepingLast(Cincamimis message) throws QueueException, InterruptedException
    {
        if(message==null) return false;
        if(measurementQueue==null) throw new QueueException("CINCAMIMIS Queue not found");
        
        boolean rdo=measurementQueue.offer(message);
        
        while(!rdo)
        {
           measurementQueue.take();
           rdo=measurementQueue.offer(message);
        }
        
        if(rdo)
        {
            this.notifyObservers();
            return true;
        }
        
        return false;
    }    
    /**
     * It gives the first element from the queue without remove the element from the list
     * @return The first element in the queue without remove it. Additionally,
     * it returns null if the queue is empty.
     * @throws QueueException This exception is raised when there is not an initialized queue.
     */
    public synchronized Cincamimis firstAvailable() throws QueueException
    {
        if(measurementQueue==null) throw new QueueException("CINCAMIMIS Queue not found");
        
        return measurementQueue.peek();        
    }
    
    /**
     * It removes all the elements from the queue.
     * 
     * @throws QueueException This exception is raised when there is not an initialized queue.
     */
    public synchronized void clear() throws QueueException
    {
         if(measurementQueue==null) throw new QueueException("CINCAMIMIS Queue not found");

         measurementQueue.clear();
         this.notifyObservers();
    }
    
    /**
     * It gives the first element from the queue, removing it.
     * @return The first element from the queue, removing it. Additionally, it returns
     * null if the queue is empty.
     * @throws QueueException This exception is raised when there is not an initialized queue.
     */
    public synchronized Cincamimis firstAvailableandRemove() throws QueueException
    {
        if(measurementQueue==null) throw new QueueException("CINCAMIMIS Queue not found");
        if(measurementQueue.isEmpty()) return null;
        
        Cincamimis element=measurementQueue.poll();        
        if(element!=null) this.notifyObservers();
        
        return element;    
    }
    
    /**
     * It gives the first element from the queue, removing it. If the element is not 
     * available, then it waits until the element becomes available.
     * @return The first element from the queue, removing it. Additionally, it 
     * returns null if the queue is empty.
     * @throws QueueException This exception is raised when there is not an initialized queue.
     * @throws InterruptedException if interrupted while waiting
     */
    public synchronized Cincamimis firstAvailableandRemoveW() throws QueueException, InterruptedException
    {
        if(measurementQueue==null) throw new QueueException("CINCAMIMIS Queue not found");
        if(measurementQueue.isEmpty()) return null;
        
        Cincamimis element=measurementQueue.take();        
        if(element!=null) this.notifyObservers();
        
        return element;            
    }
    
    /**
     * It returns the remaining capacity related to the queue
     * @return The remaining capacity of the queue
     * @throws QueueException This exception is raised when there is not an initialized queue
     */
    public  int remainingCapacity() throws QueueException
    {
        if(measurementQueue==null) throw new QueueException("CINCAMIMIS Queue not found");
        
        return measurementQueue.remainingCapacity();
    }

    /**
     * It returns the quantity of elements in the queue
     * @return The quantity of elements in the queue
     */
    public  int size() 
    {
        if(measurementQueue==null) return 0;
        
        return measurementQueue.size();
    }    
    
    /**
     * It indicates whether the queue is null or not.
     * @return TRUE the queue is null, FALSE otherwise.
     */
    public boolean isEmpty()
    {
        if(measurementQueue==null) return true;
        
        return measurementQueue.isEmpty();
    }
}
