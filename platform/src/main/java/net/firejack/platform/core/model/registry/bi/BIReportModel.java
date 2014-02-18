package net.firejack.platform.core.model.registry.bi;
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


import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.IEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("BIRPT")
public class BIReportModel extends RegistryNodeModel implements IEntity {
    private static final long serialVersionUID = 5824151706909855523L;

    private String serverName;
    private Integer port;
    private String parentPath;
    private String urlPath;
    private EntityProtocol protocol;
    private RegistryNodeStatus status;
    private List<BIReportFieldModel> fields;

    @Column(length = 1024)
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Column(length = 255)
    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    @Column(length = 2048)
    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    @Enumerated
    public EntityProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(EntityProtocol protocol) {
        this.protocol = protocol;
    }

    @Enumerated
    public RegistryNodeStatus getStatus() {
        return status;
    }

    public void setStatus(RegistryNodeStatus status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
   	@OnDelete(action = OnDeleteAction.CASCADE)
    public List<BIReportFieldModel> getFields() {
        return fields;
    }

    public void setFields(List<BIReportFieldModel> fields) {
        this.fields = fields;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.BI_REPORT;
    }
}
