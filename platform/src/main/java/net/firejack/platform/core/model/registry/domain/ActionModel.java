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

package net.firejack.platform.core.model.registry.domain;

import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@javax.persistence.Entity
@DiscriminatorValue("ACT")
public class ActionModel extends RegistryNodeModel implements INavigableRegistryNode, IAllowCreateAutoDescription, IAllowDrag {

    private static final long serialVersionUID = 7321507156206392429L;
    private String serverName;
    private Integer port;
    private String parentPath;
    private String urlPath;
    private EntityProtocol protocol;
    private HTTPMethod method;
    private RegistryNodeStatus status;

    private EntityModel inputVOEntity;
    private RegistryNodeModel outputVOEntity;

    private String soapUrlPath;
    private String soapMethod;

    private List<ActionParameterModel> actionParameters;
    private List<PermissionModel> permissions;

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

    /**
     * @return
     */
    @Enumerated
    public EntityProtocol getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     */
    public void setProtocol(EntityProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @return
     */
    @Enumerated
    public HTTPMethod getMethod() {
        return method;
    }

    /**
     * @param method
     */
    public void setMethod(HTTPMethod method) {
        this.method = method;
    }

    /**
     * @return
     */
    @Column(name = "soap_url_path", length = 2048)
    public String getSoapUrlPath() {
        return soapUrlPath;
    }

    /**
     * @param soapUrlPath
     */
    public void setSoapUrlPath(String soapUrlPath) {
        this.soapUrlPath = soapUrlPath;
    }

    /**
     * @return
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_input_vo_entity")
    @ForeignKey(name = "fk_action_input_vo_entity")
    public EntityModel getInputVOEntity() {
        return inputVOEntity;
    }

    /**
     * @param inputVOEntity
     */
    public void setInputVOEntity(EntityModel inputVOEntity) {
        this.inputVOEntity = inputVOEntity;
    }

    /**
     * @return
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_output_vo_entity")
    @ForeignKey(name = "fk_action_output_vo_entity")
    public RegistryNodeModel getOutputVOEntity() {
        return outputVOEntity;
    }

    /**
     * @param outputVOEntity
     */
    public void setOutputVOEntity(RegistryNodeModel outputVOEntity) {
        this.outputVOEntity = outputVOEntity;
    }

    /**
     * @return
     */
    @Column(name = "soap_method", length = 255)
    public String getSoapMethod() {
        return soapMethod;
    }

    /**
     * @param soapMethod
     */
    public void setSoapMethod(String soapMethod) {
        this.soapMethod = soapMethod;
    }

    /**
     * @return
     */
    @Enumerated
    public RegistryNodeStatus getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(RegistryNodeStatus status) {
        this.status = status;
    }

    /**
     * @return
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_ACTION_PARAMETER")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderBy("orderPosition ASC")
    public List<ActionParameterModel> getActionParameters() {
        return actionParameters;
    }

    /**
     * @param actionParameters
     */
    public void setActionParameters(List<ActionParameterModel> actionParameters) {
        this.actionParameters = actionParameters;
    }

	public void addActionParameters(ActionParameterModel actionParameter) {
		if (actionParameters == null) {
			actionParameters = new ArrayList<ActionParameterModel>();
		}
		this.actionParameters.add(actionParameter);
	}


	@ManyToMany(targetEntity = PermissionModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_action_permission",
            joinColumns = @JoinColumn(name = "id_action", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "id_permission", referencedColumnName = "ID")
    )
    @ForeignKey(name = "FK_ACTION_PERMISSIONS")
    public List<PermissionModel> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionModel> permissions) {
        this.permissions = permissions;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.ACTION;
    }

}