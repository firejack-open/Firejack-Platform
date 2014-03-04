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

package net.firejack.platform.installation.scheduler;

import net.firejack.platform.core.utils.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.EnumMap;

@Component
public class CommandScheduler {
    private static final Logger logger = Logger.getLogger(CommandScheduler.class);
    private EnumMap<Command, CommandListener> listeners = new EnumMap<Command, CommandListener>(Command.class);

    /**
     * @param command
     * @param listener
     */
    public void addListener(Command command, CommandListener listener) {
        listeners.put(command, listener);
    }

    @Scheduled(cron = "*/5 * * * * ?")
    private void schedule() {
        for (Command command : Command.values()) {
            File path = command.getPath();
            if (path != null && path.exists()) {
                try {
                    CommandListener listener = listeners.get(command);
                    if (listener != null) {
                        listener.execute();
                    }
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    FileUtils.deleteQuietly(path);
                }
            }
        }
    }
}
