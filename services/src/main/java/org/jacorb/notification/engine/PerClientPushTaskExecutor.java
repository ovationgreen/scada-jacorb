/*
 *        JacORB - a free Java ORB
 *
 *   Copyright (C) 1999-2014 Gerald Brose / The JacORB Team.
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
 *
 */

package org.jacorb.notification.engine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jacorb.notification.servant.AbstractProxy;
import org.jacorb.notification.servant.AbstractProxyPushSupplier;
import org.jacorb.orb.ParsedIOR;
import org.jacorb.orb.iiop.IIOPProfile;

/**
 * Implementation of {@link PushTaskExecutor} interface.
 * Threads are created one per client connection.
 * 
 * @author Orest Pankevych
 */
public class PerClientPushTaskExecutor implements PushTaskExecutor {
  
  /** Cached executors. */
  final HashMap<IIOPProfile, ExecutorService> executors;
  
  /** Is active. */
  final AtomicBoolean isActive_;
  
  /**
   * Constructor.
   */
  public PerClientPushTaskExecutor() {
    executors = new HashMap<>();
    isActive_ = new AtomicBoolean(true);
  }
  
  /**
   * Threads are created one per client connection.
   */
  @Override
  public void executePush(PushTaskExecutor.PushTask pushTask) {
    if (!isActive_.get())
      return;
    
    AbstractProxyPushSupplier supplier = null;
    org.omg.CORBA.Object client = null;
    org.jacorb.orb.ORB orb = null;
    
    try {
      Field[] fields = pushTask.getClass().getDeclaredFields();
      for (Field field : fields) {
        if (field.getName().contains("this")) {
          field.setAccessible(true);
          supplier = (AbstractProxyPushSupplier)field.get(pushTask);
        }
      }
      if (supplier == null) {
        throw new ReflectiveOperationException("Supplier not found.");
      }
      
      Field field = AbstractProxy.class.getDeclaredField("client_");
      field.setAccessible(true);
      client = (org.omg.CORBA.Object)field.get(supplier);
      
      field = AbstractProxy.class.getDeclaredField("orb_");
      field.setAccessible(true);
      orb = (org.jacorb.orb.ORB)field.get(supplier);
    }
    catch (IllegalArgumentException | ClassCastException | ReflectiveOperationException | SecurityException e) {
      System.err.println(e.toString());
      return;
    }
    
    ParsedIOR   parsed  = new ParsedIOR(orb, client.toString());
    IIOPProfile profile = (IIOPProfile)parsed.getEffectiveProfile();
    ExecutorService executor;
    
    synchronized (this) {
      if (!isActive_.get())
        return;
      
      executor = executors.get(profile);
      if (executor == null) {
        
        ThreadFactory threadFactory = new WorkThreadFactory(profile);
        executors.put(profile, executor = Executors.newSingleThreadExecutor(threadFactory));
      }
    }
    
    try {
      if (isActive_.get()) {
        Work work = new Work(pushTask);
        executor.execute(work);
      }
    }
    catch (RejectedExecutionException e) {
      System.err.println(e.toString());
    }
  }
  
  /*
   * (non-Javadoc)
   * @see org.picocontainer.Disposable#dispose()
   */
  @Override
  public synchronized void dispose() {
    isActive_.set(false);
    
    for (ExecutorService executor : executors.values())
      executor.shutdownNow();
    
    executors.clear();
  }
  
  /**
   * Executor task.
   * 
   * @author Orest Pankevych
   */
  private class Work implements Runnable {
    
    /** Task to execute. */
    final PushTaskExecutor.PushTask pushTask;
   
    /**
     * Constructor.
     */
    Work(PushTaskExecutor.PushTask pushTask) {
      this.pushTask = pushTask;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
      if (isActive_.get()) {
        pushTask.doPush();
      }
    }
    
  }
  
  /**
   * The default thread factory.
   */
  static class WorkThreadFactory implements ThreadFactory {
    
    private final ThreadGroup group;
    private final String name;
    
    /**
     * Constructor.
     */
    WorkThreadFactory(IIOPProfile profile) {
      SecurityManager s = System.getSecurityManager();
      group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      name = "PushTaskExecutor#" + profile;
    }
    
    /*
     * (non-Javadoc)
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(group, r, name, 0);
      if (t.isDaemon())
        t.setDaemon(false);
      if (t.getPriority() != Thread.NORM_PRIORITY)
        t.setPriority(Thread.NORM_PRIORITY);
      return t;
    }
  }
  
}