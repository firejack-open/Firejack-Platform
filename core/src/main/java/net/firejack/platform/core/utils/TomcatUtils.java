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
