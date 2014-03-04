/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.web.statistics.engine.event;

import net.firejack.platform.model.event.IEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

public abstract class AbstractEventManager implements IEvent {

    protected ConcurrentMap<Class<? extends ApplicationEvent>, List> listenersMap;

	/**
	 *
	 * @param eventClass
	 * @param listener
	 * @param <E>
	 */
    public <E extends ApplicationEvent> void registerListener(Class<E> eventClass, ApplicationListener<E> listener) {
        if (listener != null) {
            getListeners(eventClass).add(listener);
        }
    }

    /**
     * @param eventClass
     * @return
     */
    public <E extends ApplicationEvent> List getListeners(Class<E> eventClass) {
        if (listenersMap == null) {
            listenersMap = new ConcurrentHashMap<Class<? extends ApplicationEvent>, List>();
        }
        @SuppressWarnings("unchecked")
        List<ApplicationListener<E>> listeners = (List<ApplicationListener<E>>) listenersMap.get(eventClass);
        if (listeners == null) {
            listeners = new ArrayList<ApplicationListener<E>>();
            listenersMap.putIfAbsent(eventClass, listeners);
        }
        return listeners;
    }

    @Override
    public <E extends ApplicationEvent> void event(final E event) {
        if (event != null) {
            @SuppressWarnings("unchecked")
            List<ApplicationListener<E>> listeners = (List<ApplicationListener<E>>) getListeners(event.getClass());
            for (final ApplicationListener<E> listener : listeners) {
                ExecutorService taskExecutor = getTaskExecutor();
                if (taskExecutor == null) {
                    listener.onApplicationEvent(event);
                } else {
                    taskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.onApplicationEvent(event);
                        }
                    });
                }
            }
        }
    }

    protected abstract ExecutorService getTaskExecutor();

    /***/
    public abstract void shutdown();

}