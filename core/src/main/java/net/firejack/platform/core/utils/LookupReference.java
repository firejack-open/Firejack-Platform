package net.firejack.platform.core.utils;

import java.util.HashMap;
import java.util.Map;

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

public class LookupReference extends Tree<LookupReference> {
	private Class clazz;
	private String table;
	private String column;
	private String suffix;
	private Map<String, String> discriminators;

	public LookupReference(Class clazz, String table, String column, String suffix) {
		this.clazz = clazz;
		this.table = table;
		this.column = column;
		this.suffix = suffix;
	}

	public Class getClazz() {
		return clazz;
	}

	public String getTable() {
		return table;
	}

	public String getColumn() {
		return column;
	}

	public String getSuffix() {
		return suffix;
	}

	public Map<String, String> getDiscriminators() {
		return discriminators;
	}

	public void addDiscriminator(String type, String suffix) {
		if (discriminators == null) {
			discriminators = new HashMap<String, String>();
		}
		discriminators.put(type, suffix);
	}

	public boolean isDiscriminatorTable() {
		return column != null;
	}
}
