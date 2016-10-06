/*
 *        JacORB - a free Java ORB
 *
 *   Copyright (C) 1997-2014 Gerald Brose / The JacORB Team.
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Library General Public
 *   License as published by the Free Software Foundation; either
 *   version 2 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this library; if not, write to the Free
 *   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.jacorb.orb.giop;

import org.jacorb.config.Configuration;
import org.jacorb.config.ConfigurationException;
import org.omg.CORBA.portable.RemarshalException;

/**
 * Connections deliver replies to instances of this class.
 * The mechanism by which the ORB can retrieve the replies is
 * implemented in subclasses.
 *
 * @author Nicolas Noffke
 */
public abstract class ReplyPlaceholder
{
    protected final Object lock = new Object();
    protected boolean ready = false;
    protected boolean communicationException = false;
    protected boolean remarshalException = false;
    protected boolean timeoutException = false;

    protected MessageInputStream in = null;

    protected int timeout ;

    public void configure(Configuration configuration) throws ConfigurationException
    {
       timeout = configuration.getAttributeAsInteger("jacorb.connection.client.pending_reply_timeout", 0);
    }

    public void replyReceived( MessageInputStream in )
    {
        synchronized(lock)
        {
            if( ! timeoutException )
            {
                this.in = in;
                ready = true;
                lock.notifyAll();
            }
        }
    }

    public void cancel()
    {
        synchronized(lock)
        {
            if( in == null )
            {
                communicationException = true;
                ready = true;
                lock.notify();
            }
        }
    }

    public void retry()
    {
        synchronized(lock)
        {
            remarshalException = true;
            ready = true;
            lock.notify();
        }
    }

    /**
     * Non-public implementation of the blocking method that
     * returns a reply when it becomes available.  Subclasses
     * should specify a different method, under a different
     * name, that does any specific processing of the reply before
     * returning it to the caller.
     */
  protected MessageInputStream getInputStream(boolean hasTimeoutPolicy) throws RemarshalException {
    final boolean _shouldUseTimeout = !hasTimeoutPolicy && timeout > 0;
    
    long    _timeout = _shouldUseTimeout ? timeout : Long.MAX_VALUE;
    boolean _interrupted = Thread.currentThread().isInterrupted();
    long    _interruptedTimeout = -1;                                                                                             
    long    _startTime;
    long    _endTime;
    
    synchronized (lock) {                                                                                                         
      while (!ready && _timeout > 0) {
        _startTime = System.currentTimeMillis();
        try {
          lock.wait(_timeout);                                                                                                    
        }                                                                                                                         
        catch (InterruptedException e) {                                                                                          
          if (_interruptedTimeout < 0) {                                                                                          
            _interruptedTimeout = Long.getLong("jacorb.connection.client.interrupted_timeout", _timeout);                       
          }                                                                                                                       
          _timeout = Math.min(_interruptedTimeout, _timeout);                                                                     
          _interrupted = true;                                                                                                    
        }                                                                                                                         
        _endTime  = System.currentTimeMillis();                                                                                
        _timeout -= Math.abs(_endTime - _startTime);                                                                                
      }                                                                                                                           
                                                                                                                                  
      if (_interrupted) {                                                                                                         
        Thread.currentThread().interrupt();                                                                                       
      }                                                                                                                           
      if (!ready && _shouldUseTimeout) {                                                                                          
        timeoutException = true;                                                                                                  
      }                                                                                                                           
      if (remarshalException) {                                                                                                   
        throw new org.omg.CORBA.portable.RemarshalException();                                                                    
      }                                                                                                                           
      if (communicationException) {                                                                                               
        throw new org.omg.CORBA.COMM_FAILURE(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);                                  
      }                                                                                                                           
      if (timeoutException) {                                                                                                     
        throw new org.omg.CORBA.TIMEOUT("client timeout reached");                                                                
      }                                                                                                                           
                                                                                                                                  
      return in;
    }
  }
  
}