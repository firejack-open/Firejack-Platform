package net.firejack.platform.model.config.hibernate;
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


import net.firejack.platform.core.model.registry.DatabaseName;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.dialect.Dialect;

public class OpenFlameNamingStrategy extends ImprovedNamingStrategy {
	private static final long serialVersionUID = 3786236774332079335L;

	private Dialect dialect;
	private boolean upper;

	public OpenFlameNamingStrategy(DatabaseName name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		upper = name == DatabaseName.Oracle;

		Class<?> dl = Class.forName(name.getDialect());
		this.dialect = (Dialect) dl.newInstance();
	}

	public String tableName(String tableName) {
		String name = super.tableName(tableName);

		if (upper) name = name.toUpperCase();
		return quote(name);
	}

	/**
	 * Convert mixed case to underscores
	 */
	public String columnName(String columnName) {
		String name = super.columnName(columnName);

		if (upper) name = name.toUpperCase();
		return quote(name);
	}

	public final String quote(String name) {
		if (name == null) {
			return null;
		}

		if (name.charAt(0) == '`') {
			return dialect.openQuote() + name.substring(1, name.length() - 1) + dialect.closeQuote();
		} else {
			return dialect.openQuote() + name + dialect.closeQuote();
		}
	}
}
