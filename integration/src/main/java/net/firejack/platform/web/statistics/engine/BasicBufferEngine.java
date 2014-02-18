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

package net.firejack.platform.web.statistics.engine;

import net.firejack.platform.model.event.IEvent;
import org.apache.log4j.Logger;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BasicBufferEngine {

    public static int DEFAULT_TIME_PERIOD_IN_SECONDS = 60;

    protected final Logger logger = Logger.getLogger(getClass());

    private static Buffer BUFFER_FIRST = new Buffer(Buffer.FIRST_BUFFER, BufferState.READY);
    private static Buffer BUFFER_SECOND = new Buffer(Buffer.SECOND_BUFFER, BufferState.READY);
    private static Buffer BUFFER_TEMP = new Buffer(Buffer.TEMP_BUFFER, BufferState.READY);

    private int executionScheduledTime = DEFAULT_TIME_PERIOD_IN_SECONDS;
    private IEvent eventPublisher;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    /**
     * @param eventPublisher
     */
    public void setEventPublisher(IEvent eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

	/**
	 *
	 * @param executionScheduledTime
	 */
    public void setExecutionScheduledTime(int executionScheduledTime) {
        this.executionScheduledTime = executionScheduledTime;
    }

    /**
     * @return
     */
    public int getExecutionScheduledTime() {
        return executionScheduledTime;
    }

    /***/
    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
    }

    /**
     * @return
     */
    public IEvent getEventPublisher() {
        if (eventPublisher == null) {
            throw new IllegalStateException("Event publisher was not set.");
        }
        return eventPublisher;
    }

    /**
     * @param volume
     */
    public void setVolume(int volume) {
        BUFFER_FIRST.setVolume(volume);
        BUFFER_SECOND.setVolume(volume);
        BUFFER_TEMP.setVolume(volume);
    }

    /**
     * @param peak
     */
    public void setPeak(int peak) {
        BUFFER_FIRST.setPeak(peak);
        BUFFER_SECOND.setPeak(peak);
        BUFFER_TEMP.setPeak(peak);
    }

    public synchronized void put(DetailedStatisticsInfo detailedStatisticsInfo) {
        Buffer buffer = getCurrentBuffer();
        if (buffer != null) {
            logger.debug("SAVE: " + BUFFER_FIRST.toString() + "; " + BUFFER_SECOND.toString() + "; " + BUFFER_TEMP.toString());
            buffer.add(detailedStatisticsInfo);
        } else {
            logger.warn("SKIP: " + BUFFER_FIRST.toString() + "; " + BUFFER_SECOND.toString() + "; " + BUFFER_TEMP.toString());
        }
    }

    public void put(List<DetailedStatisticsInfo> detailedStatisticsInfoList) {//synchronized
        if (detailedStatisticsInfoList != null) {
            for (DetailedStatisticsInfo statisticsInfo : detailedStatisticsInfoList) {
                put(statisticsInfo);
            }
        }
    }

    /***/
    public void switchBuffers() {
        cleanBuffer(BUFFER_FIRST, true);
        cleanBuffer(BUFFER_SECOND, true);

        if (!BufferState.DISABLE.equals(BUFFER_TEMP.getState()) && !BufferState.CLEARING.equals(BUFFER_TEMP.getState())) {
            BUFFER_TEMP.setState(BufferState.ACTIVE);
        }

        logger.debug(BUFFER_FIRST.toString());
        logger.debug(BUFFER_SECOND.toString());
        logger.debug(BUFFER_TEMP.toString());
    }

    protected void runTracker() {
        switchBuffers();
    }

    /***/
    public void scheduleStatisticsTracker() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runTracker();
            }
        }, getExecutionScheduledTime(), getExecutionScheduledTime(), TimeUnit.SECONDS);
    }

    private Buffer getCurrentBuffer() {
        Buffer buffer = null;

        if (BufferState.READY.equals(BUFFER_FIRST.getState()) && !BufferState.ACTIVE.equals(BUFFER_SECOND.getState())) {
            BUFFER_FIRST.setState(BufferState.ACTIVE);
        } else if (BufferState.READY.equals(BUFFER_SECOND.getState()) && !BufferState.ACTIVE.equals(BUFFER_FIRST.getState())) {
            BUFFER_SECOND.setState(BufferState.ACTIVE);
        }

        if (BufferState.ACTIVE.equals(BUFFER_FIRST.getState())) {
            buffer = BUFFER_FIRST;
            if (BufferState.FULL.equals(BUFFER_SECOND.getState())) cleanBuffer(BUFFER_SECOND, false);
        } else if (BufferState.ACTIVE.equals(BUFFER_SECOND.getState())) {
            buffer = BUFFER_SECOND;
            if (BufferState.FULL.equals(BUFFER_FIRST.getState())) cleanBuffer(BUFFER_FIRST, false);
        }

        if ((BufferState.ACTIVE.equals(BUFFER_FIRST.getState()) || BufferState.ACTIVE.equals(BUFFER_SECOND.getState())) &&
                !BufferState.READY.equals(BUFFER_TEMP.getState()) && !BufferState.CLEARING.equals(BUFFER_TEMP.getState())) {
            cleanBuffer(BUFFER_TEMP, true);
        }

        if (BufferState.FULL.equals(BUFFER_FIRST.getState()) && BufferState.CLEARING.equals(BUFFER_SECOND.getState())) {
            buffer = BUFFER_FIRST;
        } else if (BufferState.FULL.equals(BUFFER_SECOND.getState()) && BufferState.CLEARING.equals(BUFFER_FIRST.getState())) {
            buffer = BUFFER_SECOND;
        }

        if (BufferState.CLEARING.equals(BUFFER_FIRST.getState()) && BufferState.CLEARING.equals(BUFFER_SECOND.getState()) &&
                (BufferState.ACTIVE.equals(BUFFER_TEMP.getState()) || BufferState.FULL.equals(BUFFER_TEMP.getState()))) {
            buffer = BUFFER_TEMP;
        }

        if (BufferState.DISABLE.equals(BUFFER_FIRST.getState()) || BufferState.DISABLE.equals(BUFFER_SECOND.getState())) {
            cleanBuffer(BUFFER_FIRST, false);
            cleanBuffer(BUFFER_SECOND, false);
        }
        return buffer;
    }

    private void cleanBuffer(Buffer buffer, boolean isForce) {
        if ((!BufferState.ACTIVE.equals(buffer.getState()) || isForce) && !BufferState.CLEARING.equals(buffer.getState())) {
            buffer.setState(BufferState.CLEARING);
            getEventPublisher().event(new BufferEvent(buffer));
        }
    }
}