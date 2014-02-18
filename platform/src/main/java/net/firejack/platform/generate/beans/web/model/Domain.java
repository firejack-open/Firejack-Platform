package net.firejack.platform.generate.beans.web.model;

import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.key.Key;

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

@Properties(subpackage = "domain")
public class Domain extends Model<Domain> {
    private Model model;

    /***/
    public Domain() {
    }

    /**
     * @param model
     */
    public Domain(Model model) {
        super(model);
        this.model = model;
    }

    /**
     * @return
     */
    public Model getModel() {
        return model;
    }

    /**
     * @param model
     */
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public Key getKey() {
        return model.getKey();
    }

    @Override
    public boolean isSingle() {
        return model.isSingle();
    }
}
