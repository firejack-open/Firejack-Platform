package net.firejack.platform.core.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
