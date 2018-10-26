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

import org.jacorb.notification.interfaces.Disposable;
import org.jacorb.notification.interfaces.NotifyingDisposable;
import org.jacorb.notification.util.DisposableManager;

/**
 * Implementation of {@link PushTaskExecutorFactory} interface.
 * Threads are created one per client connection.
 * 
 * @author Orest Pankevych
 */
@SuppressWarnings("unused")
public class PerClientPushTaskExecutorFactory implements PushTaskExecutorFactory, NotifyingDisposable {
  
  /** Push task executor implementation. */
  private final PerClientPushTaskExecutor executor_;
  
  /** Default dispose manager. */
  private final DisposableManager disposableManager_;
  
  /**
   * Constructor.
   */
  public PerClientPushTaskExecutorFactory(PushTaskExecutorFactory delegate) {
    disposableManager_ = new DisposableManager();
    executor_ = new PerClientPushTaskExecutor();
  }
  
  /*
   * (non-Javadoc)
   * @see org.jacorb.notification.engine.PushTaskExecutorFactory#newExecutor(org.jacorb.notification.interfaces.NotifyingDisposable)
   */
  @Override
  public PushTaskExecutor newExecutor(NotifyingDisposable callbackingDisposable) {
    return executor_;
  }
  
  /*
   * (non-Javadoc)
   * @see org.picocontainer.Disposable#dispose()
   */
  @Override
  public void dispose() {
    disposableManager_.dispose();
  }
  
  /*
   * (non-Javadoc)
   * @see org.jacorb.notification.interfaces.NotifyingDisposable#registerDisposable(org.jacorb.notification.interfaces.Disposable)
   */
  @Override
  public void registerDisposable(Disposable d) {
    disposableManager_.addDisposable(d);
  }
  
}