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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public enum Env {

    M2_HOME,
    FIREJACK_URL,
    FIREJACK_CONFIG;

    private static final String fileName = "platform.properties";
	private List<EnvHandler> handlers;
    private String value;

    Env() {
        this.value = System.getenv(name());
        if (this.value == null) {
            this.value = System.getProperty(getPropertyName());
        } else {
            System.setProperty(getPropertyName(), this.value);
        }
    }


    /**
     * @return
     */
    public String getValue() {
        if (this.value == null) {
            this.value = System.getenv(name());
            if (this.value == null) {
                this.value = System.getProperty(getPropertyName());
            }
        }
        return value;
    }

    /**
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
	    if (value!=null) {
		    System.setProperty(getPropertyName(), value);
	    } else {
		    System.clearProperty(getPropertyName());
	    }
	    notify(value);
    }


    /**
     * @return
     */
    public String getPropertyName() {
        return name().toLowerCase().replaceAll("_", ".");
    }

    /**
     * @param name
     * @return
     */
    public static File getEnvFile(String name) {
	    return new File(Env.FIREJACK_CONFIG.getValue(), name);
    }

	public void addHandler(EnvHandler handler) {
		if (handlers == null) {
			handlers = new ArrayList<EnvHandler>();
		}
		handlers.add(handler);
	}

	public void notify(String value) {
		if (handlers != null) {
			for (EnvHandler handler : handlers) {
				handler.change(this, value);
			}
		}
	}

	/**
     * @return
     */
    public static File getDefaultEnvFile() {
        return getEnvFile(fileName);
    }

	public static void clean() {
		for (Env env : values()) {
			if (env.handlers != null) {
				env.handlers.clear();
			}
		}
	}
}
