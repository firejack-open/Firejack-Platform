package net.firejack.platform.core.model;

import net.firejack.platform.core.model.registry.RegistryNodeType;

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

public class SearchModel extends BaseEntityModel {
	private static final long serialVersionUID = -4054727351470594525L;

	private String name;
	private String path;
	private String lookup;
	private String description;
	private RegistryNodeType type;

	public SearchModel() {
	}

	public SearchModel(Object... args) {
		super(((Number) args[0]).longValue());
		this.name = (String) args[1];
		this.path = (String) args[2];
		this.lookup = (String) args[3];
		this.description = (String) args[4];
		this.type = RegistryNodeType.find((String) args[5]);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RegistryNodeType getType() {
		return type;
	}

	public void setType(RegistryNodeType type) {
		this.type = type;
	}
}
