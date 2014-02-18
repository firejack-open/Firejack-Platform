/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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