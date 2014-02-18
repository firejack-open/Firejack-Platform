package net.firejack.platform.web.mina.aop;

import net.firejack.platform.api.registry.model.LogLevel;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ClassUtils;
import net.firejack.platform.provider.ProgressListener;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import net.firejack.platform.web.mina.bean.Status;
import net.firejack.platform.web.mina.bean.StatusType;
import net.firejack.platform.web.security.filter.OpenFlameFilter;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

@Aspect
@Order(2)
@Component("progressAspect")
public class ProgressAspect implements Callback, ProgressListener, ManuallyProgress, ProgressReadStatus {
    private static final Logger logger = Logger.getLogger(ProgressAspect.class);

    private final Lock lock = new ReentrantLock();
    private final ThreadLocal<Progress> current = new ThreadLocal<Progress>();
    private final WeakHashMap<String, List> container = new WeakHashMap<String, List>();
    private static int processCount;

    @Around(value = "target(broker) && @target(component)", argNames = "pjp, broker,component")
    public Object start(ProceedingJoinPoint pjp, ServiceBroker broker, ProgressComponent component) throws Throwable {
        if (!isPrecessed() || component.weight() == -1) {
            return pjp.proceed();
        }

        int weight = component.weight();
        if (component.upload()) {
            weight += UPLOAD_WEIGHT;
        }

        Progress progress = new Progress(pjp, this, weight, lock);
        container.remove(getPageUid());

        logger.info("Progress Start: " + broker.getClass().getSimpleName());
        progress.start();
        if (component.upload()) {
            int percent = progress.progress(UPLOAD_WEIGHT);
            return new ServiceResponse<Status>(new Status("Starting Process...", percent, StatusType.RUNNING), "", true);
        } else {
            return new ServiceResponse<Status>(new Status("Starting...", 0, StatusType.STARTED, component.showLogs()), "", true);
        }
    }

    @Around(value = "@annotation(progressStatus)", argNames = "pjp,progressStatus")
    public Object status(ProceedingJoinPoint pjp, ProgressStatus progressStatus) throws Throwable {
        Thread thread = Thread.currentThread();
        if (!isPrecessed() || !(thread instanceof Progress)) {
            return pjp.proceed();
        }

        Progress state = (Progress) thread;

        int progress = state.progress(0);
        int weight = state.percent(progressStatus.weight());
        Status status = new Status(progressStatus.description(), progress, weight, StatusType.RUNNING);
        ServiceResponse<Status> response = new ServiceResponse<Status>(status, "", true);
        send(response);

        Object result = pjp.proceed();

        state.progress(progressStatus.weight());
        return result;
    }

    @Around(value = "target(broker) && @target(status)", argNames = "pjp,broker,status")
    public Object status(ProceedingJoinPoint pjp, ServiceBroker broker, ProgressStatus status) throws Throwable {
        return status(pjp, status);
    }

    @Override
    public void error(Throwable throwable) {
        logger.error(throwable.getMessage(), throwable);
        ServiceResponse<Status> response = new ServiceResponse<Status>(new Status(throwable.getMessage(), 0, StatusType.ERROR), throwable.getMessage(), false);
        error(response);
    }


    public void error(ServiceResponse response) {
        send(response);
        current.remove();
    }

    @Override
    public void end(ServiceResponse response) {
        send(new ServiceResponse<Status>(new Status("Completed message", 100, StatusType.COMPLETED), "Status complete message", true));
        send(response);
        current.remove();
    }

    public void start(int totalWeight) {
        if (isPrecessed()) {
            Progress progress = new Progress(lock, totalWeight);
            current.set(progress);
            logger.info("Total elements: " + totalWeight);
            Status status = new Status("Starting...", 0, StatusType.STARTED);
            ServiceResponse<Status> response = new ServiceResponse<Status>(status, "", true);
            send(response);
        }
    }

    public void status(String message, int processWeight) {
        status(message, processWeight, LogLevel.NONE);
    }

    public void status(String message, int processWeight, LogLevel logLevel) {
        if (isPrecessed()) {
            Progress state = current.get();

            if (state == null && Thread.currentThread() instanceof Progress)
                state = (Progress) Thread.currentThread();

            if (state != null) {
                int progress = state.progress(0);
                int weight = state.percent(processWeight);
                Status status = new Status(message, progress, weight, StatusType.RUNNING, logLevel);
                ServiceResponse<Status> response = new ServiceResponse<Status>(status, "", true);
                send(response);
                state.progress(processWeight);
            }
        }
    }

    @Override
    public void progress(int percent) {
        if (isPrecessed()) {
            int p = percent * UPLOAD_WEIGHT / 100;
            ServiceResponse<Status> response = new ServiceResponse<Status>(new Status("Upload...", p, StatusType.RUNNING), "", true);
            send(response);
        }
    }

    private void send(ServiceResponse message) {
        String uid = getPageUid();
        List messages = container.get(uid);
        if (messages == null) {
            messages = new ArrayList();
            container.put(uid, messages);
        }
        messages.add(message);
        logger.info("Progress " + message.getItem());
    }

    @Override
    public List getProgress(String uid) {
        return container.remove(uid);
    }

    private boolean isPrecessed() {
        return OpenFlameFilter.getPageUID() != null;
    }

    private String getPageUid() {
        return OpenFlameFilter.getPageUID();
    }

    private static synchronized int next() {
        return processCount++;
    }

    protected class Progress extends Thread {
        private Lock lock;
        private Condition condition;
        private Progress parent;
        private Callback delegate;
        private MethodInvocation invocation;
        private ProceedingJoinPoint pjp;
        private List<Closeable> streams;
        private int totalWeight;
        private int process;

        protected Progress(ProceedingJoinPoint pjp, Callback delegate, int totalWeight, Lock lock) {
            this(lock, totalWeight);
            this.pjp = pjp;
            this.delegate = delegate;
            this.invocation = ExposeInvocationInterceptor.currentInvocation();
            this.streams = findCloseableParams(pjp.getArgs());
        }

        protected Progress(Lock lock, int totalWeight) {
            super("Progress Thread-" + next());
            this.lock = lock;
            this.condition = lock.newCondition();
            this.totalWeight = totalWeight;

            Thread thread = Thread.currentThread();
            if (thread instanceof Progress) {
                this.parent = (Progress) thread;
                this.process = parent.process;
                this.totalWeight = parent.totalWeight;
            }
        }

        private List<Closeable> findCloseableParams(Object... args) {
            List<Closeable> list = new ArrayList<Closeable>();
            for (Object arg : args) {
                if (arg instanceof NamedValues) {
                    Map all = ((NamedValues) arg).getAll();
                    for (Object o : all.values()) {
                        if (o instanceof Closeable) {
                            list.add((Closeable) o);
                        }
                    }
                } else if (arg instanceof Closeable) {
                    list.add((Closeable) arg);
                }
            }
            return list;
        }

        public int progress(int weight) {
            process += weight;
            return (process * 100) / totalWeight;
        }

        public int percent(int weight) {
            return (weight * 100) / totalWeight;
        }

        private void lock() throws InterruptedException {
            lock.lock();
            condition.await();
            lock.unlock();
        }

        private void unlock() {
            lock.lock();
            condition.signal();
            lock.unlock();
        }

        @Override
        public void start() {
            super.start();
            if (parent != null) {
                try {
                    lock();
                } catch (InterruptedException e) {
                    parent.interrupt();
                } finally {
                    unlock();
                }
            }
        }

        @Override
        public void run() {
            try {
                ExposeInvocationInterceptor instance = ExposeInvocationInterceptor.INSTANCE;
                ThreadLocal<MethodInvocation> threadLocal = ClassUtils.getProperty(instance, "invocation");
                threadLocal.set(invocation);

                ServiceResponse proceed = (ServiceResponse) pjp.proceed();
                if (parent == null) {
                    if (proceed.isSuccess()) {
                        delegate.end(proceed);
                    } else {
                        delegate.error(proceed);
                    }
                } else {
                    parent.process = process;
                }
            } catch (Throwable throwable) {
                delegate.error(throwable);
            } finally {
                unlock();
                for (Closeable stream : streams) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
            }
        }
    }
}
