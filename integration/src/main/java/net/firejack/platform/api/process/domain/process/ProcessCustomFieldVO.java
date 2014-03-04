/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
