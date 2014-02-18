package net.firejack.platform.generate.beans.ipad;
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


import java.util.List;

public class Project implements iPad {
    private String name;
    private String project;
    private List<FileReference> entities;
    private List<FileReference> controllers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public List<FileReference> getEntities() {
        return entities;
    }

    public void setEntities(List<FileReference> entities) {
        this.entities = entities;
    }

    public List<FileReference> getControllers() {
        return controllers;
    }

    public void setControllers(List<FileReference> controllers) {
        this.controllers = controllers;
    }

    @Override
    public String getFilePosition() {
        return "${name}.xcodeproj";
    }
}
