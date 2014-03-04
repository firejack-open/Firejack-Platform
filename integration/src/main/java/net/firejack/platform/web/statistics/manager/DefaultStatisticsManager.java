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

package net.firejack.platform.web.statistics.manager;

import net.firejack.platform.web.statistics.engine.BasicBufferEngine;
import net.firejack.platform.web.statistics.engine.BufferCleaner;
import net.firejack.platform.web.statistics.engine.BufferEvent;
import net.firejack.platform.web.statistics.engine.event.AbstractEventManager;
import net.firejack.platform.web.statistics.engine.event.BufferEventManager;


public class DefaultStatisticsManager extends BaseStatisticsManager {

    public static final int DEFAULT_BUFFER_VOLUME = 20;
    public static final int DEFAULT_BUFFER_PEAK = 200;
    public static final int DEFAULT_BUFFER_INSERT_SIZE = 10;

    private int bufferVolume = DEFAULT_BUFFER_VOLUME;
    private int bufferPeak = DEFAULT_BUFFER_PEAK;
    private int bufferInsertSize = DEFAULT_BUFFER_INSERT_SIZE;

    private AbstractEventManager eventManager;

    /**
     * @param bufferVolume
     */
    public void setBufferVolume(int bufferVolume) {
        this.bufferVolume = bufferVolume;
    }

    /**
     * @param bufferPeak
     */
    public void setBufferPeak(int bufferPeak) {
        this.bufferPeak = bufferPeak;
    }

    /**
     * @param bufferInsertSize
     */
    public void setBufferInsertSize(int bufferInsertSize) {
        this.bufferInsertSize = bufferInsertSize;
    }

    @Override
    public BasicBufferEngine getBufferEngine() {
        if (bufferEngine == null) {
            bufferEngine = new BasicBufferEngine();
            bufferEngine.setPeak(bufferPeak);
            bufferEngine.setVolume(bufferVolume);
            bufferEngine.setEventPublisher(getEventManager());

            BufferCleaner bufferCleaner = new BufferCleaner();
            bufferCleaner.setInsertSize(bufferInsertSize);
            bufferCleaner.setStatisticsManager(this);

            getEventManager().registerListener(BufferEvent.class, bufferCleaner);
        }
        return bufferEngine;
    }

    /**
     * @return
     */
    public AbstractEventManager getEventManager() {
        if (eventManager == null) {
            eventManager = new BufferEventManager();
        }
        return eventManager;
    }

	public void destroy() {
		if (eventManager != null) {
			eventManager.shutdown();
		}
		super.destroy();
	}
}