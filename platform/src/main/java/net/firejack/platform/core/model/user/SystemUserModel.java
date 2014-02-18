/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.core.model.user;

import net.firejack.platform.core.model.registry.domain.SystemModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

@Entity
@DiscriminatorValue("SYS")
public class SystemUserModel extends BaseUserModel {
    private static final long serialVersionUID = 7159344576475499176L;

    private SystemModel system;

    private String consumerKey;
    private String consumerSecret;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_system")
    @ForeignKey(name = "FK_SYSTEM_USER_SYSTEM")
    public SystemModel getSystem() {
        return system;
    }

    public void setSystem(SystemModel system) {
        this.system = system;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

}
