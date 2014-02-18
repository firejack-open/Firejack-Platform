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

package net.firejack.platform.core.utils;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.model.AbstractModel;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Factory extends PropertyUtilsBean {
    protected static final Logger logger = Logger.getLogger(Factory.class);

    private final Pattern pattern = Pattern.compile("\\b[a-z][A-Z]");
    private final ThreadLocal<Map> cache = new InheritableThreadLocal<Map>();
    private final Map<Class, List<FieldInfo>> fields = new HashMap<Class, List<FieldInfo>>();
    private final Map<String, XmlAdapter> adapters = new HashMap<String, XmlAdapter>();

    public void setAdapters(List<XmlAdapter> adapters) {
        for (XmlAdapter adapter : adapters) {
            addAdapter(adapter);
        }
    }

    public void addAdapter(XmlAdapter adapter) {
        Class<? extends XmlAdapter> adapterClass = adapter.getClass();
        Type[] typeArguments = ((ParameterizedTypeImpl) adapterClass.getGenericSuperclass()).getActualTypeArguments();
        String marshal = ((Class) typeArguments[0]).getName() + ((Class) typeArguments[1]).getName();
        String unmarshal = ((Class) typeArguments[1]).getName() + ((Class) typeArguments[0]).getName();
        adapters.put(marshal, adapter);
        adapters.put(unmarshal, adapter);
    }

    public <T extends AbstractDTO> List<T> convertTo(Class<T> clazz, List entities) {
        List<T> list = new ArrayList<T>();
        if (entities != null) {
            for (Object entity : entities) {
                T dto = convertTo(clazz, entity);
                list.add(dto);
            }
        }
        return list;
    }

    public <T> List<T> convertFrom(Class<T> clazz, List dtos) {
        List<T> list = new ArrayList<T>();
        if (dtos != null) {
            for (Object dto : dtos) {
                T entity = convertFrom(clazz, dto);
                list.add(entity);
            }
        }
        return list;
    }

    public <T extends AbstractDTO> T convertTo(Class<T> clazz, Object entity) {
        try {
            return convertTo0(clazz, entity);
        } finally {
            cache.remove();
        }
    }

    public <T> T convertFrom(Class<T> clazz, Object dto) {
        try {
            return convertFrom0(clazz, dto);
        } finally {
            cache.remove();
        }
    }

    private <T extends AbstractDTO> T convertTo0(Class<?> clazz, Object entity) {
        if (entity == null) return null;

        if (entity instanceof HibernateProxy)
            entity = ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();

        Object dto = null;
        try {
            dto = clazz.newInstance();
            List<FieldInfo> infos = fields.get(clazz);
            if (infos == null) {
                infos = getAllField(clazz, clazz, new ArrayList<FieldInfo>());
                fields.put(clazz, infos);
            }
            for (FieldInfo info : infos) {
                Class<?> type = info.getType();
                String fieldName = info.getField().getName();

                Object value = get(entity, info.name(), type);
                if (value != null) {
                    if (value instanceof AbstractModel || value.getClass().isAnnotationPresent(XmlAccessorType.class)) {
                        if (contains(value)) {
                            Object convert = get(value);
                            set(dto, fieldName, convert);
                        } else {
                            add(value, null);
                            Object convert = convertTo0(type, value);
                            add(value, convert);
                            set(dto, fieldName, convert);
                        }
                    } else if (value instanceof Collection) {
                        Collection result = (Collection) value;
                        Class<?> arrayType = info.getGenericType();
                        if (arrayType == null && result.size() != 0) {
                            arrayType = result.iterator().next().getClass();
                            info.setGenericType(arrayType);
                        }

                        if (AbstractDTO.class.isAssignableFrom(arrayType)) {
                            try {
                                if (value instanceof PersistentCollection) {
                                    result = new ArrayList();
                                } else {
                                    result = (Collection) value.getClass().newInstance();
                                }
                            } catch (InstantiationException e) {
                                result = new ArrayList();
                            }

                            for (Object o : (Collection) value) {
                                Object convert = convertTo0(arrayType, o);
                                result.add(convert);
                            }
                        }
                        set(dto, fieldName, result);
                    } else {
                        set(dto, fieldName, value);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return (T) dto;
    }

    private <T> T convertFrom0(Class<?> clazz, Object dto) {
        if (dto == null) return null;
        Object bean = null;
        try {
            bean = clazz.newInstance();
            List<FieldInfo> infos = fields.get(dto.getClass());
            if (infos == null) {
                infos = getAllField(dto.getClass(), dto.getClass(), new ArrayList<FieldInfo>());
                fields.put(dto.getClass(), infos);
            }
            for (FieldInfo info : infos) {
                if (info.readonly()) {
                    continue;
                }

                String name = info.name();
                Object entity = bean;
                clazz = entity.getClass();
                String[] lookup = name.split("\\.");

                if (lookup.length > 1) {
                    if (isNullValue(dto, info.getField().getName()))
                        continue;

                    for (int i = 0; i < lookup.length - 1; i++) {
                        Object instance = get(entity, lookup[i], null);
                        if (instance == null) {
                            FieldInfo fieldInfo = getField(clazz, lookup[i]);
                            Class<?> type = fieldInfo.getType();
                            if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
                                instance = type.newInstance();
                                set(entity, lookup[i], instance);

                                entity = instance;
                                clazz = type;
                            }
                        } else {
                            entity = instance;
                            clazz = instance.getClass();
                        }
                    }
                    name = lookup[lookup.length - 1];
                }

                FieldInfo distField = getField(clazz, name);
                if (distField != null) {
                    Class<?> type = distField.getType();
                    Object value = get(dto, info.getField().getName(), type);
                    if (value != null) {
                        if (value instanceof AbstractDTO) {
                            if (contains(value)) {
                                Object convert = get(value);
                                set(entity, name, convert);
                            } else {
                                add(value, null);
                                Object convert = convertFrom0(type, value);
                                add(value, convert);
                                set(entity, name, convert);
                            }
                        } else if (value instanceof Collection) {
                            Collection result = (Collection) value;
                            Class<?> arrayType = distField.getGenericType();
                            if (AbstractModel.class.isAssignableFrom(arrayType) || arrayType.isAnnotationPresent(XmlAccessorType.class)) {
                                try {
                                    result = (Collection) value.getClass().newInstance();
                                } catch (InstantiationException e) {
                                    result = new ArrayList();
                                }

                                for (Object o : (Collection) value) {
                                    Object convert = convertFrom0(arrayType, o);
                                    result.add(convert);
                                }
                            }
                            set(entity, name, result);
                        } else {
                            set(entity, name, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return (T) bean;
    }

    private FieldInfo getField(Class<?> clazz, String name) {
        List<FieldInfo> fieldInfos = fields.get(clazz);
        if (fieldInfos != null)
            for (FieldInfo info : fieldInfos) {
                if (info.getField().getName().equals(name))
                    return info;
            }

        Field field = ClassUtils.getField(clazz, name);
        FieldInfo info = null;
        if (field != null) {
            info = getField(clazz, field);
            if (fieldInfos == null) {
                fieldInfos = new ArrayList<FieldInfo>();
                fields.put(clazz, fieldInfos);
            }
            fieldInfos.add(info);
        }
        return info;
    }

    private FieldInfo getField(Class<?> clazz, Field field) {
        FieldInfo info = null;
        if (field != null) {
            info = new FieldInfo(field);
            if (field.getGenericType() instanceof TypeVariable) {
                TypeVariable genericType = (TypeVariable) field.getGenericType();
                TypeVariable<?>[] parameters = genericType.getGenericDeclaration().getTypeParameters();
                for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i].getName().equals(genericType.getName())) {
                        Class parameterClass = getGenericParameterClass(clazz, (Class) genericType.getGenericDeclaration(), i);
                        info.setType(parameterClass);
                        break;
                    }
                }
            }

            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();

                Class<?> arrayType = null;
                if (genericType.getActualTypeArguments()[0] instanceof TypeVariable) {
                    TypeVariable genericType1 = (TypeVariable) genericType.getActualTypeArguments()[0];
                    TypeVariable<?>[] parameters = genericType1.getGenericDeclaration().getTypeParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        if (parameters[i].getName().equals(genericType1.getName())) {
                            arrayType = getGenericParameterClass(clazz, (Class) genericType1.getGenericDeclaration(), i);
                            break;
                        }
                    }
                } else {
                    arrayType = (Class<?>) genericType.getActualTypeArguments()[0];
                }
                info.setGenericType(arrayType);
            }
        }
        return info;
    }

    private List<FieldInfo> getAllField(Class<?> src, Class<?> clazz, List<FieldInfo> fields) {
        if (clazz != Object.class) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                Property property = field.getAnnotation(Property.class);
                if (property != null) {
                    fields.add(getField(src, field));
                }
            }
            getAllField(src, clazz.getSuperclass(), fields);
        }
        return fields;
    }

    private String fixPath(String name) {
        Matcher matcher = pattern.matcher(name);
        StringBuffer buffer = new StringBuffer(name.length());
        while (matcher.find())
            matcher.appendReplacement(buffer, StringUtils.capitalize(matcher.group()));
        matcher.appendTail(buffer);

        name = buffer.toString();
        if (name.startsWith("_"))
            name = name.substring(1);
        return name;
    }

    private Object get(Object bean, String name, Class valueType) {
        try {
            Object value;
            try {
                value = getInstance().getProperty(bean, name);
            } catch (NoSuchMethodException e) {
                value = getInstance().getProperty(bean, fixPath(name));
            } catch (Exception e) {
                value = null;
            }

            if (!Hibernate.isInitialized(value))
                value = null;

            if (value != null && valueType != null && value.getClass() != valueType) {
                XmlAdapter adapter = adapters.get(valueType.getName() + value.getClass().getName());
                if (adapter != null)
                    try {
                        return adapter.marshal(value);
                    } catch (ClassCastException e) {
                        return adapter.unmarshal(value);
                    }
            }

            return value;
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    private void set(Object bean, String name, Object value) {
        if (value != null) {
            try {
                getInstance().setProperty(bean, name, value);
            } catch (NoSuchMethodException e) {
                try {
                    getInstance().setProperty(bean, fixPath(name), value);
                } catch (Exception ex) {
                    logger.warn(e.getMessage());
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
    }

    private boolean isNullValue(Object bean, String name) {
        return get(bean, name, null) == null;
    }

    public Class getGenericParameterClass(Class actualClass, Class genericClass, int parameterIndex) {
        if (!genericClass.isAssignableFrom(actualClass) || genericClass.equals(actualClass)) {
            throw new IllegalArgumentException("Class " + genericClass.getName() + " is not a superclass of " + actualClass.getName() + ".");
        }

        Stack<ParameterizedType> types = new Stack<ParameterizedType>();

        Class clazz = actualClass;

        while (true) {
            Type currentType = genericClass.isInterface() ? getGenericInterface(clazz, genericClass) : clazz.getGenericSuperclass();

            Type rawType;
            if (currentType instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) currentType;
                types.push(type);
                rawType = type.getRawType();
            } else {
                types.clear();
                rawType = currentType;
            }

            if (!rawType.equals(genericClass)) {
                clazz = (Class) rawType;
            } else {
                break;
            }
        }

        if (types.isEmpty()) {
            return (Class) genericClass.getTypeParameters()[parameterIndex].getGenericDeclaration();
        }

        Type result = types.pop().getActualTypeArguments()[parameterIndex];

        while (result instanceof TypeVariable && !types.empty()) {
            int actualArgumentIndex = getParameterTypeDeclarationIndex((TypeVariable) result);
            ParameterizedType type = types.pop();
            result = type.getActualTypeArguments()[actualArgumentIndex];
        }

        if (result instanceof TypeVariable) {
            throw new IllegalStateException("Unable to resolve type variable " + result + "." + " Try to replace instances of parametrized class with its non-parameterized subtype.");
        }

        if (result instanceof ParameterizedType) {
            result = ((ParameterizedType) result).getRawType();
        }

        if (result == null) {
            throw new IllegalStateException("Unable to determine actual parameter type for " + actualClass.getName() + ".");
        }

        if (!(result instanceof Class)) {
            throw new IllegalStateException("Actual parameter type for " + actualClass.getName() + " is not a Class.");
        }

        return (Class) result;
    }

    public int getParameterTypeDeclarationIndex(TypeVariable typeVariable) {
        GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
        TypeVariable[] typeVariables = genericDeclaration.getTypeParameters();
        for (int i = 0; i < typeVariables.length; i++) {
            if (typeVariables[i].equals(typeVariable)) {
                return i;
            }
        }
        throw new IllegalStateException("Argument " + typeVariable.toString() + " is not found in " + genericDeclaration.toString() + ".");
    }

    public Type getGenericInterface(Class sourceClass, Class genericInterface) {
        Type[] types = sourceClass.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof Class) {
                if (genericInterface.isAssignableFrom((Class) type)) {
                    return type;
                }
            } else if (type instanceof ParameterizedType) {
                if (genericInterface.isAssignableFrom((Class) ((ParameterizedType) type).getRawType())) {
                    return type;
                }
            }
        }
        return null;
    }

    private void add(Object key, Object value) {
        cache.get().put(key, value);
    }

    private Object get(Object key) {
        return cache.get().get(key);
    }

    private boolean contains(Object key) {
        Map map = cache.get();
        if (map == null) {
            map = new HashMap();
            cache.set(map);
        }
        return map.containsKey(key);
    }
}
