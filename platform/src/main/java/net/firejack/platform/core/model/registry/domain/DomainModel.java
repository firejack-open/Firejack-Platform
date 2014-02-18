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

package net.firejack.platform.core.model.registry.domain;

import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;


@Entity
@DiscriminatorValue("DOM")
public class DomainModel extends RegistryNodeModel implements IPrefixContainer, IDatabaseAssociated, IAllowDrag, IAllowDrop, IAllowCreateAutoDescription {

	private static final long serialVersionUID = -1983156757590680739L;
	private String prefix;
    private Boolean dataSource;
    private DatabaseModel database;
    private String wsdlLocation;
    private boolean xaSupport;

    @Override
    @Column(name = "prefix", length = 64)
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Boolean getDataSource() {
        return dataSource;
    }

    public void setDataSource(Boolean dataSource) {
        this.dataSource = dataSource;
    }

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_database")
	@ForeignKey(name = "FK_DATABASE_ASSOCIATION_DOMAIN")
	public DatabaseModel getDatabase() {
		return database;
	}

	public void setDatabase(DatabaseModel database) {
		this.database = database;
	}

    @Column(name = "urlPath")
    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    public boolean isXaSupport() {
        return xaSupport;
    }

    public void setXaSupport(boolean xaSupport) {
        this.xaSupport = xaSupport;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.DOMAIN;
    }

}
