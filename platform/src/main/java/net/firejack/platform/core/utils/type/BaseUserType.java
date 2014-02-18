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

package net.firejack.platform.core.utils.type;

import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public abstract class BaseUserType<T extends Serializable> implements UserType {
	public static final String ENTRY_DELIMITER = "|";
	public static final String ENTRY_DELIMITER_REGEXP = "\\"+ENTRY_DELIMITER;

	protected List<T> convert(String source) {
		List<T> indexes = new ArrayList<T>();
		for (String token : source.split(ENTRY_DELIMITER_REGEXP)) {
			if (!"".equals(token)) {
				T id = valueOf(token);
				indexes.add(id);
			}
		}
		return indexes;
	}

	protected String convert(List<T> entries) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < entries.size(); i++) {
			builder.append(valueOf0(entries.get(i)));
			if (i != entries.size() - 1) {
				builder.append(ENTRY_DELIMITER);
			}
		}

		return builder.toString();
	}

	protected abstract T valueOf(String element);
	protected abstract String valueOf0(T element);
}