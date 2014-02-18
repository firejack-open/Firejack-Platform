package net.firejack.platform.generate.beans.web.controller;

import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.Model;

import java.io.File;
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

@Properties(subpackage = "views", extension = ".jsp")
public class View extends Base {
    private Model model;
    private List<String> paths;

    /***/
    public View() {
    }

    /**
     * @param model
     */
    public View(Model model) {
        super(model);
        this.name = getName().toLowerCase();
        this.model = model;
        setProjectPath("");
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * @return
     */
    public List<String> getPaths() {
        return paths;
    }

    /**
     * @param paths
     */
    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public String getFilePosition() {
        String path = this.projectPath.replaceAll("\\.", "\\" + File.separator);
        path += File.separator + properties.subpackage().replaceAll("\\.", "\\" + File.separator);
        path += File.separator + this.classPath.replaceAll("\\.", "\\" + File.separator);
        return path;
    }
}
