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

package net.firejack.platform.core.config.meta.utils;

import net.firejack.platform.core.utils.IUIDContainer;


@SuppressWarnings("unused")
public final class UIDElementDiffWrapper<T extends IUIDContainer> {

    private String equalPropertyValue;
    private T oldElement;
    private T newElement;

    /**
     * @param equalPropertyValue
     * @param oldElement
     * @param newElement
     */
    public UIDElementDiffWrapper(
            String equalPropertyValue, T oldElement,
            T newElement) {
        this.equalPropertyValue = equalPropertyValue;
        this.oldElement = oldElement;
        this.newElement = newElement;
    }

    /**
     * @return
     */
    public String getEqualPropertyValue() {
        return equalPropertyValue;
    }

    /**
     * @return
     */
    public T getOldElement() {
        return oldElement;
    }

    /**
     * @return
     */
    public T getNewElement() {
        return newElement;
    }

}