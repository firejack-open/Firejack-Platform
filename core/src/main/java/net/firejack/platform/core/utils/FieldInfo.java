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

package net.firejack.platform.core.utils;

import net.firejack.platform.core.annotation.Property;

import java.lang.reflect.Field;

public class FieldInfo {
	private Field field;
	private Class<?> type;
	private Class<?> genericType;
	private Property property;

	public FieldInfo(Field field) {
		this.field = field;
		this.type = field.getType();
		this.property = field.getAnnotation(Property.class);
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Class<?> getGenericType() {
		return genericType;
	}

	public void setGenericType(Class<?> genericType) {
		this.genericType = genericType;
	}

	public String name() {
		return property == null || property.name().isEmpty() ? field.getName() : property.name();
	}

	public boolean readonly() {
		return property.readonly();
	}
}
