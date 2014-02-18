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

package net.firejack.platform.generate.beans;

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.tools.Utils;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;


public abstract class Base implements Import {
    protected String projectPath;
    protected String name;
    protected String originalName;
    protected String normalize;
    protected String lookup;
    protected String classPath;
    protected String entityPath;
    protected String serviceName;
    protected Properties properties;
    private String description;
    private Set<Import> imports;

    /***/
    public Base() {
        this.properties = this.getClass().getAnnotation(Properties.class);
    }

    public Base(String lookup) {
        this.lookup = lookup;
        this.properties = this.getClass().getAnnotation(Properties.class);
    }

    protected Base(String name, Properties properties) {
        this.name = name;
        this.properties = properties;
    }

    /**
     * @param base
     */
    public Base(Base base) {
        this.properties = this.getClass().getAnnotation(Properties.class);
        if (properties == null) {
            throw new IllegalArgumentException("Can't create Properties Annotation");
        }

        this.projectPath = base.getProjectPath();
        this.name = base.getSimpleName();
        this.originalName = base.getOriginalName();
        this.normalize = base.getNormalize();
        this.lookup = base.getLookup();
        this.classPath = base.getClassPath();
        this.entityPath = base.getEntityPath();
        this.serviceName = base.getServiceName();
        setDescription(base.getDescription());
    }

    public String getPackage() {
        if (classPath == null || (classPath.isEmpty() && properties.subpackage().isEmpty())) {
            return projectPath;
        } else if (classPath.isEmpty()) {
            return projectPath + DOT + properties.subpackage();
        } else if (properties.subpackage().isEmpty()) {
            return projectPath + DOT + classPath;
        } else {
            return projectPath + DOT + classPath + DOT + properties.subpackage();
        }
    }

    /**
     * @return
     */
    public String getProjectPath() {
        return projectPath;
    }

    /**
     * @param projectPath
     */
    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    /**
     * @return
     */
    public String getSimpleName() {
        return name;
    }

    /**
     * @return
     */
    public String getName() {
        return properties.prefix() + name + properties.suffix();
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getNormalize() {
        return normalize;
    }

    public void setNormalize(String normalize) {
        this.normalize = normalize;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public String getNavigationLookup() {
        if (projectPath != null) {
            StringBuilder builder = new StringBuilder(projectPath);
            if (StringUtils.isNotBlank(classPath))
                builder.append(DOT).append(classPath);
            return builder.append(DOT).append(normalize).toString();
        }
        return "";
    }

    public String getLookupPath() {
        return getNavigationLookup().replaceAll("\\.", "/");
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getEntityPath() {
        return entityPath;
    }

    public void setEntityPath(String entityPath) {
        this.entityPath = entityPath;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = Utils.documentFormatting(description, properties.descriptionLength());
    }

    /**
     * @return
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @param properties
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * @return
     */
    public Set<Import> getImports() {
        return imports;
    }

    protected void setImports(Set<Import> imports) {
        this.imports = imports;
    }

    /**
     * @param anImport
     */
    public void addImport(Import anImport) {
        if (anImport == null) return;
        if (this.imports == null) {
            this.imports = new TreeSet<Import>();
        }

        if (anImport.getPackage() != null) {
            this.imports.add(anImport);
        }
    }

    /**
     * @param anImport
     */
    public void removeImport(Import anImport) {
        if (this.imports != null) {
            this.imports.remove(anImport);
        }
    }

    @Override
    public int compareTo(Import bean) {
        return getFullName().compareTo(bean.getFullName());
    }

    @Override
    public String getFullName() {
        return getPackage() + DOT + getName();
    }

    /**
     * @return
     */
    public String getFileName() {
        return getName() + properties.extension();
    }

    /**
     * @return
     */
    public String getFilePosition() {
        String path = this.projectPath.replaceAll("\\.", "\\" + File.separator);
        path += File.separator + this.classPath.replaceAll("\\.", "\\" + File.separator);
        path += File.separator + properties.subpackage().replaceAll("\\.", "\\" + File.separator);
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Base)) return false;

        Base base = (Base) o;
        return !(name != null ? !name.equals(base.name) : base.name != null) && !(classPath != null ? !classPath.equals(base.classPath) : base.classPath != null);
    }

    @Override
    public int hashCode() {
        int result = classPath != null ? classPath.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("{name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
