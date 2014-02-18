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

package net.firejack.platform.core.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;

public class TomcatUtils {
    private static Log logger = LogFactory.getLog(TomcatUtils.class);

    /**
     * @param config
     * @return
     */
    public static Integer getCatalinaPort(ServletConfig config) {
        try {
            Object[] connectors = (Object[]) ClassUtils.getProperty(config, "config.parent.parent.parent.service.connectors");
            if (connectors != null) {
                for (Object connector : connectors) {
                    Object protocolHandler = ClassUtils.getProperty(connector, "protocolHandler");
                    if (protocolHandler != null && protocolHandler.getClass().getSimpleName().startsWith("Http11")) {
                        return (Integer) ClassUtils.getProperty(connector, "port");
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Can't find  server port");
        }
        return 80;
    }

    /**
     * @param event
     * @return
     */
    public static Integer getCatalinaPort(ServletContextEvent event) {
        try {
            Object[] connectors = (Object[]) ClassUtils.getProperty(event.getSource(), "context.context.parent.parent.service.connectors");
            if (connectors != null) {
                for (Object connector : connectors) {
                    Object protocolHandler = ClassUtils.getProperty(connector, "protocolHandler");
                    if (protocolHandler != null && protocolHandler.getClass().getSimpleName().startsWith("Http11")) {
                        return (Integer) ClassUtils.getProperty(connector, "port");
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Can't find  server port");
        }
        return 80;
    }

    /**
     * @param event
     * @return
     */
    public static String getCatalinaHost(ServletContextEvent event) {
        try {
            return (String) ClassUtils.getProperty(event.getSource(), "context.context.hostName");
        } catch (Exception e) {
            logger.warn("Can't find  server port");
        }
        return "localhost";
    }

    /**
     * @param event
     * @return
     */
    public static String getCatalinaContext(ServletContextEvent event) {
	    try {
		    return (String) ClassUtils.getProperty(event.getSource(), "context.context.encodedPath");
	    } catch (Exception e) {
		    logger.warn("Can't find  server port");
	    }
	    return "";
    }
}
