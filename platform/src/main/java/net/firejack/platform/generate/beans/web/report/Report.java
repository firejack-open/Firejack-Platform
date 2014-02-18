package net.firejack.platform.generate.beans.web.report;
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

import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.key.Key;

@Properties(subpackage = "domain")
public class Report extends Model {
    private Model model;
    private boolean biReport;

    public Report() {
    }

    public Report(Model model) {
        super(model);
        this.model = model;
        setDomain(this);
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public boolean isBiReport() {
        return biReport;
    }

    public void setBiReport(boolean biReport) {
        this.biReport = biReport;
    }

    @Override
    public Key getKey() {
        return model.getKey();
    }
}
