package net.firejack.platform.core.model.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
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

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Environments<E extends Entry> {

	@XmlElementRef
    private List<E> environments;

    /***/
    public Environments() {
    }

    /**
     * @param environments
     */
    public Environments(List<E> environments) {
        this.environments = environments;
    }

    /**
     * @return
     */
    public List<E> getEnvironments() {
        return environments;
    }

    /**
     * @param environments
     */
    public void setEnvironments(List<E> environments) {
        this.environments = environments;
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return this.environments == null || this.environments.isEmpty();
    }
}
