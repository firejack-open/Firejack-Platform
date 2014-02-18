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

package net.firejack.platform.model.event;

import net.firejack.platform.api.config.domain.Config;
import org.springframework.context.ApplicationEvent;

public class ChangeConfigEvent extends ApplicationEvent {
    private static final long serialVersionUID = 398043607878768371L;

    private Config config;

    /**
     * @param config
     */
    public ChangeConfigEvent(Config config) {
        super(config);
        this.config = config;
    }

    /**
     * @return
     */
    public Config getConfig() {
        return config;
    }

    /**
     * @param config
     */
    public void setConfig(Config config) {
        this.config = config;
    }

}
