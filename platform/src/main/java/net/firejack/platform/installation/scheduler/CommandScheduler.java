package net.firejack.platform.installation.scheduler;

import net.firejack.platform.core.utils.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.EnumMap;

/**
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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
