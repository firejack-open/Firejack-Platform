package net.firejack.platform.core.utils;

import net.firejack.platform.core.annotation.Property;

import java.lang.reflect.Field;

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
