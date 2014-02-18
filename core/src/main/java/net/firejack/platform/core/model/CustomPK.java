package net.firejack.platform.core.model;
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


import net.firejack.platform.core.utils.ClassUtils;
import org.springframework.beans.BeanUtils;

import javax.persistence.Id;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;

public abstract class CustomPK implements Serializable {
    private static final long serialVersionUID = 3064728710953553424L;
    private static final String SPLITTER = "/";

    public CustomPK() {
	}

	public CustomPK(String key) {
		String[] params = key.split(SPLITTER);
		if (params.length % 2 == 0) {
			PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(getClass());

			for (int i = 0; i < params.length; i += 2) {
				for (PropertyDescriptor descriptor : descriptors) {
					if (descriptor.getName().equalsIgnoreCase(params[i])) {
						Class<?> type = descriptor.getPropertyType();
						Object value;
						if (AbstractModel.class.isAssignableFrom(type)) {
							value = createSimple(type, params[i + 1]);
						} else if (CustomPK.class.isAssignableFrom(type)) {
							throw new IllegalStateException("Embedded composite key:" + params[i]);
						} else {
							value = ClassUtils.convert(type, params[i + 1]);
						}
						ClassUtils.invoke(this, descriptor.getWriteMethod(), value);
						break;
					}
				}
			}
		}
	}

	@Override
	public final String toString() {
		StringBuilder builder = new StringBuilder();
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(getClass());

		for (PropertyDescriptor descriptor : descriptors) {
			Method readMethod = descriptor.getReadMethod();
			Method writeMethod = descriptor.getWriteMethod();
			if (readMethod != null && writeMethod != null) {
				Class<?> type = readMethod.getReturnType();
				Object result = ClassUtils.invoke(this, descriptor.getReadMethod());
				builder.append("/");
				builder.append(descriptor.getName());
				builder.append("/");
				if (AbstractModel.class.isAssignableFrom(type)) {
					builder.append(getPrimaryKey(result));
				} else {
					builder.append(result);
				}
			}
		}
		return builder.substring(1);
	}

	private Object createSimple(Class type, String value) {
		Object bean = BeanUtils.instantiate(type);

		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(type);
		for (PropertyDescriptor descriptor : descriptors) {
			Method readMethod = descriptor.getReadMethod();
			Id id = readMethod.getAnnotation(Id.class);
			if (id != null) {
				Object convert = ClassUtils.convert(descriptor.getPropertyType(), value);
				ClassUtils.invoke(bean, descriptor.getWriteMethod(), convert);
				break;
			}
		}
		return bean;
	}

	private Object getPrimaryKey(Object bean) {
		Object pk = null;
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(bean.getClass());
		for (PropertyDescriptor descriptor : descriptors) {
			Method readMethod = descriptor.getReadMethod();
			Id id = readMethod.getAnnotation(Id.class);
			if (id != null) {
				pk = ClassUtils.invoke(bean, descriptor.getReadMethod());
				break;
			}
		}
		return pk;
	}
}
