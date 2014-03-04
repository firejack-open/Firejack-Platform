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

package net.firejack.platform.core.model;


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
