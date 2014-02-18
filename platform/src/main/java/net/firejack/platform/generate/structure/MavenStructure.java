package net.firejack.platform.generate.structure;

import net.firejack.platform.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;

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

public class MavenStructure implements Structure {
    private String name;
    private File project;
    private File ipad;
    private File src;
    private File resource;
    private File profile;
    private File webapp;
    private File js;
    private File webInf;
    private File lib;
    private File build;
    private File sql;

    /**
     * @param project
     * @param name
     */
    public MavenStructure(File project, String name) {
        this.name = name;
        this.project = project;
        this.profile = new File(project, "profile");
        this.ipad = FileUtils.create(project, "ipad", name);
        this.resource = FileUtils.create(project, "src", "main", "resources");
        this.src = FileUtils.create(project, "src", "main", "java");
        this.webapp = FileUtils.create(project, "src", "main", "webapp");
        this.js = new File(this.webapp, "js");
        this.webInf = FileUtils.create(webapp, "WEB-INF");
        this.lib = FileUtils.create(webInf, "lib");
        this.build = FileUtils.create(project, "build");
        this.sql = FileUtils.create(project, "db");
    }

    public String getName() {
        return name;
    }

    public File getProject() {
        return project;
    }

    public File getIpad() {
        return ipad;
    }

    public File getResource() {
        return resource;
    }

    public File getSrc() {
        return src;
    }

    public File getWebapp() {
        return webapp;
    }

    @Override
    public File getJS() {
        return js;
    }

    public File getWebInf() {
        return webInf;
    }

    public File getLib() {
        return lib;
    }

    public File getProfile() {
        return profile;
    }

    public File getBuild() {
        return build;
    }

    public File getDbSql() {
        return sql;
    }

    @Override
    public void clean() {
        try {
            if (project != null) {
                FileUtils.deleteDirectory(project);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MavenStructure");
        sb.append("{project=").append(project);
        sb.append('}');
        return sb.toString();
    }
}
