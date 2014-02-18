package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.annotation.Lookup;
import net.firejack.platform.core.model.registry.ISortable;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

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

/**
 * Class represents status entity
 */
@Entity
@Lookup(parent = RegistryNodeModel.class)
@Table(name = "opf_status")
public class StatusModel extends LookupModel<ProcessModel> implements ISortable {

    public static final String STATUS_STARTED = "Started";
    public static final String STATUS_FINISHED = "Finished";

	private static final long serialVersionUID = 1083887944959971449L;

	private Integer sortPosition;

	/**
	 * Gets the order position
	 *
	 * @return - position of the status within the process
	 */
	@Override
	@Column(name = "order_position", nullable = false, columnDefinition = "INTEGER UNSIGNED DEFAULT 0")
	public Integer getSortPosition() {
		return this.sortPosition;
	}

	/**
	 * Sets the order position
	 *
	 * @param sortPosition - position of the status within the process
	 */
	@Override
	public void setSortPosition(Integer sortPosition) {
		this.sortPosition = sortPosition;
	}

	/**
	 * Gets the type
	 *
	 * @return registry node type
	 */
	@Override
	@Transient
	public RegistryNodeType getType() {
		return RegistryNodeType.STATUS;
	}
}