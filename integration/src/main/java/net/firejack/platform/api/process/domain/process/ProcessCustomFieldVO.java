/**
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
package net.firejack.platform.api.process.domain.process;

import net.firejack.platform.core.domain.AbstractDTO;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

public abstract class ProcessCustomFieldVO extends AbstractDTO {
	private static final long serialVersionUID = 3020203578579413102L;

	private String fieldLookup;
	protected Object value;

	/**
	 * Gets the field lookup
	 *
	 * @return lookup of the field
	 */
	public String getFieldLookup() {
		return fieldLookup;
	}

	/**
	 * Sets the field lookup
	 *
	 * @param fieldLookup lookup of the field
	 */
	public void setFieldLookup(String fieldLookup) {
		this.fieldLookup = fieldLookup;
	}

	/**
	 * Gets the value
	 *
	 * @return value of the field
	 */
	@XmlTransient
	public Object getValue() {
		return value;
	}

	/**
	 * Instantiates the correct concrete class depending on the value type
	 *
	 * @param fieldLookup lookup of the field
	 * @param value       value of the field
	 *
	 * @return instance of a correct concrete class
	 */
	public static ProcessCustomFieldVO instantiate(String fieldLookup, Object value) {
		if (value instanceof Double) {
			return new ProcessCustomDoubleFieldVO(fieldLookup, (Double) value);
		} else if (value instanceof Integer) {
			return new ProcessCustomIntegerFieldVO(fieldLookup, (Integer) value);
		} else if (value instanceof Long) {
			return new ProcessCustomLongFieldVO(fieldLookup, (Long) value);
		} else if (value instanceof Boolean) {
			return new ProcessCustomBooleanFieldVO(fieldLookup, (Boolean) value);
		} else if (value instanceof Date) {
			return new ProcessCustomDateFieldVO(fieldLookup, (Date) value);
		} else if (value instanceof String) {
			return new ProcessCustomStringFieldVO(fieldLookup, (String) value);
		}
		return null;
	}
}
