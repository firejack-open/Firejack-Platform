/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.web.controller.wrapper;

import net.firejack.platform.core.model.registry.resource.Cultures;


public class CultureWrapper {

    private Cultures culture;
    private boolean available;

    /***/
    public CultureWrapper() {
    }

    /**
     * @param culture
     */
    public CultureWrapper(Cultures culture) {
        this.culture = culture;
        this.available = false;
    }

    /**
     * @return
     */
    public Cultures getCulture() {
        return culture;
    }

    /**
     * @param culture
     */
    public void setCulture(Cultures culture) {
        this.culture = culture;
    }

    /**
     * @return
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * @param available
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

}
