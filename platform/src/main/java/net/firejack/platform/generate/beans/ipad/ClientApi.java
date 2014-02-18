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


import net.firejack.platform.core.utils.FileUtils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ClientApi implements iPad {
    private String name;
    private List<ClientMethod> methods;
    private Set<String> imports = new TreeSet<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClientMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<ClientMethod> methods) {
        this.methods = methods;
    }

    public Set<String> getImports() {
        return imports;
    }

    public void addImport(Entity header) {
        this.imports.add(header.getName());
    }

    public void addImport(String entity) {
        this.imports.add(entity);
    }

    @Override
    public String getFilePosition() {
        return FileUtils.construct("${name}", "api");
    }
}
