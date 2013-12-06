/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.catalina.util;


import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;


/**
 * Support class to assist in firing LifecycleEvent notifications to
 * registered LifecycleListeners.
 * 
 * 一个支持的工具类。主要负责通知生命周期事件到对应的listener上。
 *
 * @author Craig R. McClanahan
 * @version $Id: LifecycleSupport.java 1370569 2012-08-07 22:13:00Z markt $
 */

public final class LifecycleSupport {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new LifecycleSupport object associated with the specified
     * Lifecycle component.
     *
     * 根据传入的Lifecycle对象来构造一个LifecycleSupport对象。在tomcat中。大部分组件都是
     * 一个Lifecycle。
     * 
     * @param lifecycle The Lifecycle component that will be the source
     *  of events that we fire
     */
    public LifecycleSupport(Lifecycle lifecycle) {
    	// 这里的super()到底是想做什么？ 父类都已经是Object了啊。  TODO
        super();
        this.lifecycle = lifecycle;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The source component for lifecycle events that we will fire.
     */
    private final Lifecycle lifecycle;


    /**
     * The set of registered LifecycleListeners for event notifications.
     * 
     * 对应的当前组件的监听器(listener)列表。
     * 这里用数组而不是ArrayList对象是为什么呢。习惯？ TODO
     */
    private LifecycleListener listeners[] = new LifecycleListener[0];

    private final Object listenersLock = new Object(); // Lock object for changes to listeners


    // --------------------------------------------------------- Public Methods


    /**
     * Add a lifecycle event listener to this component.
     * 
     * 为当前组件新建一个listener。
     *
     * @param listener The listener to add
     */
    public void addLifecycleListener(LifecycleListener listener) {

      synchronized (listenersLock) {
          LifecycleListener results[] = new LifecycleListener[listeners.length + 1];
          for (int i = 0; i < listeners.length; i++){
              results[i] = listeners[i];
          }
          results[listeners.length] = listener;
          listeners = results;
      }

    }


    /**
     * Get the lifecycle listeners associated with this lifecycle. If this
     * Lifecycle has no listeners registered, a zero-length array is returned.
     * 
     * 返回当前组件的所有监听器(listener)。 如果当前组件没有任何监听器。则返回
     * 一个大小为0的数组。
     */
    public LifecycleListener[] findLifecycleListeners() {

        return listeners;

    }


    /**
     * Notify all lifecycle event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     * 
     * 通知对应组件所有的listener，你们监听的组件有事情发生了！你们看要
     * 不要处理一下！ 
     * 
     * 
     * @param type Event type
     * @param data Event data
     */
    public void fireLifecycleEvent(String type, Object data) {

        LifecycleEvent event = new LifecycleEvent(lifecycle, type, data);
        // 这里为什么要多这一步赋值？直接调用不行么? TODO
        LifecycleListener interested[] = listeners;	
        for (int i = 0; i < interested.length; i++)
            interested[i].lifecycleEvent(event);

    }


    /**
     * Remove a lifecycle event listener from this component.
     * 
     * 移除一个组件对应的监听器。
     *
     * @param listener The listener to remove
     */
    public void removeLifecycleListener(LifecycleListener listener) {

        synchronized (listenersLock) {
            int n = -1;
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] == listener) {
                    n = i;
                    break;
                }
            }
            if (n < 0)
                return;
            LifecycleListener results[] =
              new LifecycleListener[listeners.length - 1];
            int j = 0;
            for (int i = 0; i < listeners.length; i++) {
                if (i != n)
                    results[j++] = listeners[i];
            }
            listeners = results;
        }

    }


}
