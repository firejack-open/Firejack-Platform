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
