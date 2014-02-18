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

import java.util.ArrayList;
import java.util.List;


public class Buffer {

    public static final String FIRST_BUFFER = "FIRST";
    public static final String SECOND_BUFFER = "SECOND";
    public static final String TEMP_BUFFER = "TEMP";

    private String name;
    private int volume;
    private int peak;
    private List<DetailedStatisticsInfo> buffer = new ArrayList<DetailedStatisticsInfo>();
    private BufferState state;


    /**
     * @param name
     * @param state
     */
    public Buffer(String name, BufferState state) {
        this.name = name;
        this.state = state;
    }

    /**
     * @param volume
     * @param peak
     */
    public Buffer(int volume, int peak) {
        this.volume = volume;
        this.peak = peak;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public int getVolume() {
        return volume;
    }

    /**
     * @param volume
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * @return
     */
    public int getPeak() {
        return peak;
    }

    /**
     * @param peak
     */
    public void setPeak(int peak) {
        this.peak = peak;
    }

    /**
     * @param detailedStatistic
     */
    public void add(DetailedStatisticsInfo detailedStatistic) {
        buffer.add(detailedStatistic);
        int size = buffer.size();
        if (size < volume) {
            this.state = BufferState.ACTIVE;
        } else if (size >= volume) {
            this.state = size >= peak ? BufferState.DISABLE : BufferState.FULL;
        }
    }

    /**
     * @return
     */
    public List<DetailedStatisticsInfo> getBuffer() {
        return buffer;
    }

    /**
     * @return
     */
    public BufferState getState() {
        return state;
    }

    /**
     * @param state
     */
    public void setState(BufferState state) {
        this.state = state;
    }

    public String toString() {
        return "Buffer{" +
                "name='" + name + '\'' +
                ", buffer=" + buffer.size() +
                ", state=" + state +
                '}';
    }
}
