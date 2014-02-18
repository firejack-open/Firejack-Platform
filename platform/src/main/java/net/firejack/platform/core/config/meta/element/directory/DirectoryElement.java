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

package net.firejack.platform.core.config.meta.element.directory;

import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.IFieldElementContainer;
import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;

import java.util.List;


public class DirectoryElement extends PackageDescriptorElement<DirectoryModel> implements IFieldElementContainer {

    private DirectoryType directoryType;
    private String serverName;
    private String urlPath;
    private RegistryNodeStatus status;
    private List<IFieldElement> fields;

    /**
     * @return
     */
    public DirectoryType getDirectoryType() {
        return directoryType;
    }

    /**
     * @param directoryType
     */
    public void setDirectoryType(DirectoryType directoryType) {
        this.directoryType = directoryType;
    }

    /**
     * @return
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return
     */
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * @param urlPath
     */
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    /**
     * @return
     */
    public RegistryNodeStatus getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(RegistryNodeStatus status) {
        this.status = status;
    }

    @Override
    public IFieldElement[] getFields() {
        return fields == null ? null : fields.toArray(new IFieldElement[fields.size()]);
    }

    @Override
    public void setFields(List<IFieldElement> fields) {
        this.fields = fields;
    }

    @Override
    public Class<DirectoryModel> getEntityClass() {
        return DirectoryModel.class;
    }
}
