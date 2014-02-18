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

package net.firejack.platform.core.utils;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.sun.jersey.spi.scanning.AnnotationScannerListener;
import net.firejack.platform.core.annotation.PlaceHolder;
import net.firejack.platform.core.annotation.PlaceHolders;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.validation.exception.PropertyNotFoundException;
import org.apache.log4j.Logger;
import org.hibernate.type.DoubleType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import javax.persistence.Column;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


public class ClassUtils extends BeanUtils {

	private static final Logger logger = Logger.getLogger(ClassUtils.class);
    private static final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(false,true ));
   	private static final StandardEvaluationContext context = new StandardEvaluationContext();

	/**
	 * @param clazz
	 * @return
	 */
	public static <T> T populate(Class<T> clazz) {
		if (clazz != null) {
			try {
				return clazz.newInstance();
			} catch (InstantiationException e) {
				logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * @param clazz
	 * @param parameters
	 * @return
	 */
	public static <T> T populate(Class<T> clazz, Tuple<Object, Class<?>>... parameters) {
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		Object[] parameterValues = new Object[parameters.length];
		int i = 0;
		for (Tuple<Object, Class<?>> parameter : parameters) {
			parameterValues[i] = parameter.getKey();
			parameterTypes[i++] = parameter.getValue();
		}
		try {
			Constructor<T> constructor = clazz.getConstructor(parameterTypes);
			return constructor.newInstance(parameterValues);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * @param obj
	 * @param property
	 * @param clazz
	 * @param <T>
	 * @return
	 * @throws PropertyNotFoundException
	 * @throws OpenFlameRuntimeException
	 */
	public static <T> T getPropertyValue(Object obj, String property, Class<T> clazz)
			throws PropertyNotFoundException, OpenFlameRuntimeException {
		if (obj == null) {
			return null;
		}
		if (clazz == null || StringUtils.isBlank(property)) {
			throw new OpenFlameRuntimeException("Wrong config parameters for getPropertyValue() method");
		}
		PropertyDescriptor descriptor =
				BeanUtils.getPropertyDescriptor(obj.getClass(), property);
		if (descriptor != null) {
			Method readMethod = descriptor.getReadMethod();
			if (readMethod != null) {
				try {
					Object resultProperty = readMethod.invoke(obj);
					if (resultProperty == null) {
						return null;
					} else if (clazz.isInstance(resultProperty) ||
							(clazz == float.class && Float.class.isInstance(resultProperty)) ||
							(clazz == double.class && DoubleType.class.isInstance(resultProperty)) ||
							(clazz == long.class && Long.class.isInstance(resultProperty)) ||
							(clazz == int.class && Integer.class.isInstance(resultProperty)) ||
							(clazz == short.class && Short.class.isInstance(resultProperty)) ||
							(clazz == byte.class && Byte.class.isInstance(resultProperty)) ||
							(clazz == boolean.class && Boolean.class.isInstance(resultProperty))) {
						return (T) resultProperty;
					} else {
						throw new OpenFlameRuntimeException("Wrong class parameter [ " + clazz +
								"] for property type [" + resultProperty.getClass() + "]");
					}
				} catch (IllegalAccessException e) {
					logger.warn(e.getMessage(), e.getCause());
					throw new OpenFlameRuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new OpenFlameRuntimeException(e);
				}
			}
		}
		throw new PropertyNotFoundException();
	}

    public static <T> T getPropertyValueWithSilence(AbstractDTO dto, String propertyName, Class<T> clazz) {
        T result;
        try {
            result = ClassUtils.getPropertyValue(dto, propertyName, clazz);
        } catch (PropertyNotFoundException e) {
            result = null;
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
        return result;
    }

	/**
	 * @param sourceObj
	 * @param propertyName
	 * @param propertyValue
	 * @throws net.firejack.platform.core.validation.exception.PropertyNotFoundException
	 *
	 */
	public static void setPropertyValue(Object sourceObj, String propertyName, Object propertyValue)
			throws PropertyNotFoundException {
		PropertyDescriptor descriptor =
				BeanUtils.getPropertyDescriptor(sourceObj.getClass(), propertyName);
		if (descriptor != null) {
			Method writeMethod = descriptor.getWriteMethod();
			if (writeMethod != null) {
				try {
					writeMethod.invoke(sourceObj, propertyValue);
					return;
				} catch (IllegalAccessException e) {
					logger.warn(e.getMessage(), e.getCause());
					throw new OpenFlameRuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new OpenFlameRuntimeException(e);
				}
			}
		}
		throw new PropertyNotFoundException();
	}

	/**
	 * @param clazz
	 * @return
	 */
	public static Map<String, List<Method>> getClassMethods(Class<?> clazz) {
		Map<String, List<Method>> methodsMap = new HashMap<String, List<Method>>();
		do {
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				boolean methodOverridden = false;
				List<Method> methodsList = methodsMap.get(method.getName());
				if (methodsList == null) {
					methodsList = new ArrayList<Method>();
					methodsMap.put(method.getName(), methodsList);
				} else {
					for (Method m : methodsList) {
						if (Arrays.equals(method.getParameterTypes(), m.getParameterTypes())) {
							methodOverridden = true;
							break;
						}
					}
				}
				if (!methodOverridden) {
					methodsList.add(method);
				}
			}
		} while ((clazz = clazz.getSuperclass()) != null);
		return methodsMap;
	}

	/**
	 * Get object properties mapped to their read methods
	 *
	 * @param example object processed for properties read methods
	 * @return properties mapped to their read methods
	 */
	public static Map<String, Method> getReadMethods(Object example) {
		if (example == null) {
			throw new IllegalArgumentException("Argument should not be null.");
		}
		return getReadMethods(example.getClass());
	}

	/**
	 * Get object properties mapped to their read methods
	 *
	 * @param clazz object processed for properties read methods
	 * @return properties mapped to their read methods
	 */
	public static Map<String, Method> getReadMethods(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Argument should not be null.");
		}
		PropertyDescriptor[] propertyDescriptors;
		try {
			propertyDescriptors = BeanUtils.getPropertyDescriptors(clazz);
		} catch (BeansException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalStateException(e);
		}
		Map<String, Method> readMethods = new HashMap<String, Method>();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			Method readMethod = propertyDescriptor.getReadMethod();
			if (readMethod != null) {
				readMethods.put(propertyDescriptor.getName(), readMethod);
			}
		}
		return readMethods;
	}

	/**
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws java.io.IOException
	 */
	public static List<Class<?>> getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		return getAnnotatedClasses(packageName, null);
	}

	/**
	 * @param packageName
	 * @param annotationClass
	 * @return
	 * @throws ClassNotFoundException
	 * @throws java.io.IOException
	 */
	public static List<Class<?>> getAnnotatedClasses(String packageName, Class<? extends Annotation> annotationClass)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> files = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			String fileName = resource.getFile();
			String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
			files.add(new File(fileNameDecoded));
		}
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (File file : files) {
			List<Class<?>> packageClasses = findClasses(file, packageName);
			if (annotationClass == null) {
				classes.addAll(packageClasses);
			} else {
				for (Class<?> packageClass : packageClasses) {
					if (packageClass.isAnnotationPresent(annotationClass)) {
						classes.add(packageClass);
					}
				}
			}
		}
		return classes;
	}

	/**
	 * @param jarName
	 * @param packageName
	 * @return
	 */
	public static Map<String, String> getClassNamesInPackage(String jarName, String packageName) {
		Map<String, String> classes = new TreeMap<String, String>();

		packageName = packageName.replaceAll("\\.", "/");

		try {
			JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
			JarEntry jarEntry;
			String classSuffix = ".class";
			while ((jarEntry = jarFile.getNextJarEntry()) != null) {
				if ((jarEntry.getName().startsWith(packageName)) &&
						(jarEntry.getName().endsWith(".class"))) {
					String className = jarEntry.getName().replaceAll("/", "\\.");
					String fullClassName = className.substring(0, className.length() - classSuffix.length());
					int lastDotPos = fullClassName.lastIndexOf(".");
					className = lastDotPos < 0 ? fullClassName : fullClassName.substring(lastDotPos + 1);
					classes.put(fullClassName, className);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classes;
	}

	/**
	 * @param packageName
	 * @param annotationClass
	 * @return
	 */
	public static Set<Class<?>> getAllAnnotatedClasses(String packageName, Class<? extends Annotation> annotationClass) {
		SourceScanningResourceConfig scanningResourceConfig = new SourceScanningResourceConfig(annotationClass);
		PackageNamesScanner scanner = new PackageNamesScanner(new String[]{packageName});
		scanningResourceConfig.init(scanner);
		return scanningResourceConfig.getClasses();
	}

	/**
	 * @param entityClass
	 * @return
	 */
	public static Map<String, Method> getHibernateColumnMapping(Class<?> entityClass) {
		Map<String, Method> columnMapping = new HashMap<String, Method>();
		Map<String, Method> readMethods = getReadMethods(entityClass);
		for (String property : readMethods.keySet()) {
			Method readMethod = readMethods.get(property);
			Column columnAnnotation = readMethod.getAnnotation(Column.class);
			if (columnAnnotation != null) {
				String fieldName = StringUtils.isBlank(columnAnnotation.name()) ? property : columnAnnotation.name();
				columnMapping.put(fieldName, readMethod);
			}
		}
		return columnMapping;
	}

	private static List<Class<?>> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (file.isDirectory()) {
				classes.addAll(findClasses(file, packageName + "." + fileName));
			} else if (fileName.endsWith(".class")) {
				String className = packageName + '.' +
						fileName.substring(0, fileName.length() - 6);
				Class clazz = loadClass(className);
				classes.add(clazz);
			}
		}
		return classes;
	}

	/**
	 * @param fullClassName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClass(String fullClassName) throws ClassNotFoundException {
		Class clazz;
		try {
			clazz = Class.forName(fullClassName);
		} catch (ExceptionInInitializerError e) {
			// happen, for example, in classes, which depend on Spring to inject some beans, and which fail, if dependency is not fulfilled
			clazz = Class.forName(fullClassName, false,
					Thread.currentThread().getContextClassLoader());
		}
		return clazz;
	}

	/**
	 * @param clazz
	 * @param example
	 * @return
	 */
	public static <T extends Annotation> T getClassAnnotation(Class<T> clazz, Object example) {
		T annotation = null;
		try {
			annotation = example.getClass().getAnnotation(clazz);
		} catch (Exception e) {
			logger.warn("Can't find " + clazz.getName() + " annotation.", e);
		}
		return annotation;
	}

	/**
	 * @param annotationClass
	 * @param example
	 * @param name
	 * @return
	 */
	public static <T extends Annotation> T getReadMethodAnnotation(Class<T> annotationClass, Object example, String name) {
		T annotation = null;
		try {
			PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(example.getClass(), name);
			Method method = propertyDescriptor.getReadMethod();
			annotation = method.getAnnotation(annotationClass);
		} catch (Exception e) {
			logger.warn("Can't find " + annotationClass.getName() + " annotation.", e);
		}
		return annotation;
	}

	/**
	 * @param classToTest
	 * @param superClassCandidate
	 * @return
	 */
	public static boolean hasSuperClass(Class<?> classToTest, Class<?> superClassCandidate) {
		Class<?>[] classes = classToTest.getClasses();
		for (Class<?> clazz : classes) {
			if (clazz == superClassCandidate) {
				return true;
			}
		}
		return false;
	}

	public static <T> List<T> getPropertyList(List targets, String property) {
		if (targets == null || targets.isEmpty()) return Collections.emptyList();
		List<T> result = new ArrayList<T>();
		Field field = getField(targets.get(0).getClass(), property);
		for (Object target : targets) {
			T item = (T) getProperty(target, field);
			if(item != null) {
				result.add(item);
			}
		}
		return result;
	}

	/**
	 * @param target
	 * @param property
	 * @return
	 */
	public static <T> T getProperty(Object target, String property) {
		if (target == null) return null;
		try {
			String[] split = property.split("\\.");
			Object value = target;
			for (String prop : split) {
				Field field = getField(value.getClass(), prop);
				if (field != null) {
					value = field.get(value);
				} else {
					value = null;
					break;
				}
			}
			if (value != target) {
				return (T) value;
			}
		} catch (IllegalAccessException e) {
			logger.warn("Field not access");
		}
		return null;
	}

	public static <T> T getProperty(Object target, Field field) {
		if (target == null || field == null) return null;
		try {
			return (T) field.get(target);
		} catch (IllegalAccessException e) {
			logger.warn("Field not access");
		}
		return null;
	}

	/**
	 * @param target
	 * @param property
	 * @return
	 */
	public static <T> T getPropertyStatic(Class target, String property) {
		if (target == null) return null;
		try {
			Field field = getField(target, property);
			if (field != null) {
				return (T) field.get(null);
			}
		} catch (IllegalAccessException e) {
			logger.warn("Field not access");
		}
		return null;
	}

	/**
	 * @param target
	 * @param property
	 * @param value
	 */
	public static void setProperty(Object target, String property, Object value) {
		Field field = getField(target.getClass(), property);
		setProperty(target, field, value);
	}

	public static void setProperty(Object target, Field field, Object value) {
		try {
			field.set(target, value);
		} catch (IllegalAccessException e) {
			logger.warn("Field not access");
		}
	}

	/**
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static Field getField(Class clazz, String name) {
		if (!clazz.equals(Object.class)) {
			try {
				Field field = clazz.getDeclaredField(name);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException e) {
				return getField(clazz.getSuperclass(), name);
			}
		}
		return null;
	}

	public static List<Field> getFields(Class clazz, List<Field> list) {
		if (!clazz.equals(Object.class)) {
			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				list.add(field);
			}
			getFields(clazz.getSuperclass(), list);
		}
		return list;
	}

	public static List<Method> getMethods(Class clazz, List<Method> list) {
		if (!clazz.equals(Object.class)) {
			for (Method method : clazz.getDeclaredMethods()) {
				list.add(method);
			}
			getMethods(clazz.getSuperclass(), list);
		}
		return list;
	}

	public static Object convert(Field field, String value) {
		Class<?> type = field.getType();
		return convert(type, value);
	}

	public static Object convert(Class<?> type, String value) {
		if (type == String.class) {
			return value;
		} else if (type == Long.class || type == long.class) {
			return Long.decode(value);
		} else if (type == Integer.class || type == int.class) {
			return Integer.decode(value);
		} else if (type == Boolean.class || type == boolean.class) {
			return Boolean.valueOf(value);
		} else if (type == Byte.class || type == byte.class) {
			return Byte.decode(value);
		} else {
			throw new IllegalArgumentException("Can't convert type: "+ type);
		}
	}

	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 *
	 * @param source      the source bean
	 * @param target      the target bean
	 * @param includeNull need to set null values
	 * @throws BeansException if the copying failed
	 * @see org.springframework.beans.BeanWrapper
	 */
	public static void copyProperties(Object source, Object target, boolean includeNull) throws BeansException {
		copyProperties(source, target, null, null, includeNull);
	}

	/**
	 * Copy the property values of the given source bean into the given target bean.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 *
	 * @param source           the source bean
	 * @param target           the target bean
	 * @param editable         the class (or interface) to restrict property setting to
	 * @param ignoreProperties array of property names to ignore
	 * @param includeNull      need to set null values
	 * @throws org.springframework.beans.BeansException
	 *          if the copying failed
	 * @see org.springframework.beans.BeanWrapper
	 */
	public static void copyProperties(Object source, Object target, Class<?> editable, String[] ignoreProperties, boolean includeNull)
			throws BeansException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class<?> actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
						"] not assignable to Editable class [" + editable.getName() + "]");
			}
			actualEditable = editable;
		}
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null &&
					(ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						if (value != null || includeNull) {
							Method writeMethod = targetPd.getWriteMethod();
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							writeMethod.invoke(target, value);
						}
					} catch (Throwable ex) {
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}

	/**
	 * @param list
	 * @return
	 */
	public static Map<String, String> transformPlaceHolderEntity(List list) {
		return transformPlaceHolderEntity(list, true);
	}

	/**
	 * @param o
	 * @return
	 */
	public static Map<String, String> transformPlaceHolderEntity(Object o) {
		return transformPlaceHolderEntity(o, true);
	}

	/**
	 * @param list
	 * @param allowSimply
	 * @return
	 */
	public static Map<String, String> transformPlaceHolderEntity(List list, boolean allowSimply) {
		Map<String, String> map = new HashMap<String, String>();
		for (Object o : list) {
			map.putAll(transformPlaceHolderEntity(o, allowSimply));
		}
		return map;
	}

	public static Map<String, String> transformPlaceHolderEntity(Object o, boolean allowSimply) {
		Map<String, String> map = new HashMap<String, String>();

		Class<?> clazz = o.getClass();
		PlaceHolder holder = clazz.getAnnotation(PlaceHolder.class);
		PlaceHolders holders = clazz.getAnnotation(PlaceHolders.class);
		Map<String, String> property = getProperty(o, clazz, new HashMap<String, String>());

		if (holders != null) {
			for (PlaceHolder ph : holders.holders()) {
				String key = StringUtils.replace(ph.key(), property);
				String value = StringUtils.replace(ph.value(), property);
				map.put(key, value);
			}
		}

		if (allowSimply) {
			String prefix = holder != null ? holder.key() : holders.name();
			for (Map.Entry<String, String> entry : property.entrySet()) {
				map.put(prefix + "." + entry.getKey(), entry.getValue());
			}
		}
		return map;
	}

	private static Map<String, String> getProperty(Object o, Class<?> clazz, Map<String, String> store) {
		if (!clazz.equals(Object.class)) {
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				PlaceHolder annotation = method.getAnnotation(PlaceHolder.class);
				if (annotation != null) {
					try {
						String name = annotation.key();
						String value = String.valueOf(method.invoke(o));
						boolean normalize = annotation.normalize();
						if (normalize){
							value = StringUtils.normalize(value);
						}
						store.put(name, value);
					} catch (Exception e) {
						//ignore
					}
				}
			}
			getProperty(o, clazz.getSuperclass(), store);
		}
		return store;
	}

	public static Object invoke(Object obj , Method method, Object... args) {
		try {
			return method.invoke(obj, args);
		} catch (IllegalAccessException e) {
			logger.warn(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.warn(e.getMessage(), e);
		}

		return null;
	}

    public static String evaluate(String message, Object object) {
        context.setRootObject(object);
        Expression exp = parser.parseExpression(message);
        return exp.getValue(context, String.class);
    }

    private static class SourceScanningResourceConfig extends DefaultResourceConfig {

		private Class<? extends Annotation> annotationClass;

		public SourceScanningResourceConfig(Class<? extends Annotation> annotationClass) {
			this.annotationClass = annotationClass;
		}

		/**
		 * Initialize and scan for root resource and RuleSource classes
		 * using a scanner.
		 *
		 * @param scanner the scanner.
		 */
		public void init(final com.sun.jersey.core.spi.scanning.Scanner scanner) {
			final AnnotationScannerListener asl = new AnnotationScannerListener(annotationClass);
			scanner.scan(asl);

			getClasses().addAll(asl.getAnnotatedClasses());
		}

	}

}
