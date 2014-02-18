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

package net.firejack.platform.core.config.meta.utils;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.INamedPackageDescriptorElement;
import net.firejack.platform.core.config.meta.IPackageDescriptorElement;
import net.firejack.platform.core.config.meta.diff.DifferenceType;
import net.firejack.platform.core.config.meta.diff.IPackageDescriptorElementDiff;
import net.firejack.platform.core.config.meta.diff.PackageDescriptorElementDiff;
import net.firejack.platform.core.config.meta.exception.DuplicateLookupException;
import net.firejack.platform.core.config.meta.exception.DuplicateUIDException;
import net.firejack.platform.core.config.meta.exception.DuplicatedPackageDescriptorElementException;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.utils.*;

import java.lang.reflect.Array;
import java.sql.Types;
import java.util.*;


public final class DiffUtils {

    public final static String PATH_SEPARATOR = ".";
    public final static String PATH_SEPARATOR_REGEXP = "\\.";

    public final static String REGISTRY_NODE_ENTITY_NAME = "Registry Node";

    /**
     * @param targetsList
     * @return
     * @throws net.firejack.platform.core.config.meta.exception.DuplicateUIDException
     *
     */
    public static <T extends INamedPackageDescriptorElement> Map<String, T> produceElementByUIDMap(T[] targetsList) throws DuplicateUIDException {
        Map<String, T> targetByUIDMap = new HashMap<String, T>();
        if (targetsList != null) {
            for (T target : targetsList) {
                if (targetByUIDMap.containsKey(target.getUid())) {
                    throw new DuplicateLookupException("Duplicate UID [" + target.getUid() + "] for update target with name = \"" + target.getName() + "\"");
                }
                targetByUIDMap.put(target.getUid(), target);
            }
        }
        return targetByUIDMap;
    }

    /**
     * @param targetElementsCollection collection of target elements
     * @return map of elements by their uid values
     * @throws net.firejack.platform.core.config.meta.exception.DuplicateUIDException may throw DuplicateUIDException
     *
     */
    public static <T extends INamedPackageDescriptorElement> Map<String, T> produceElementByUIDMap(
            Collection<T> targetElementsCollection) throws DuplicateUIDException {
        Map<String, T> targetByUIDMap = new HashMap<String, T>();
        if (targetElementsCollection != null) {
            for (T target : targetElementsCollection) {
                if (targetByUIDMap.containsKey(target.getUid())) {
                    throw new DuplicateLookupException("Duplicate UID [" + target.getUid() + "] for update target with name = \"" + target.getName() + "\"");
                }
                targetByUIDMap.put(target.getUid(), target);
            }
        }
        return targetByUIDMap;
    }

    /**
     * @param targetsList
     * @return
     * @throws net.firejack.platform.core.config.meta.exception.DuplicatedPackageDescriptorElementException
     *
     */
    public static <T extends INamedPackageDescriptorElement> Map<String, T> produceTargetByNameMap(T[] targetsList) throws DuplicatedPackageDescriptorElementException {
        Map<String, T> targetByNameMap = new HashMap<String, T>();
        List<String> lookupList = new ArrayList<String>();
        if (targetsList != null) {
            for (T target : targetsList) {
                String lookup = lookup(target);
                if (lookupList.contains(lookup)) {
                    throw new DuplicateLookupException("Duplicate lookup for update target with name = \"" + target.getName() + "\"");
                }
                lookupList.add(lookup);
                String refPath = getRefPath(target);
                targetByNameMap.put(refPath, target);
            }
        }
        return targetByNameMap;
    }

    /**
     * @param namedElements
     * @param name
     * @return
     */
    public static <T extends IHasName> T findNamedElement(T[] namedElements, String name) {
        if (namedElements != null) {
            for (T namedElement : namedElements) {
                if (namedElement.getName().equalsIgnoreCase(name)) {
                    return namedElement;
                }
            }
        }
        return null;
    }

    /**
     * @param uidElements
     * @param uid
     * @return
     */
    public static <T extends IUIDContainer> T findUIDElement(T[] uidElements, String uid) {
        if (uidElements != null) {
            for (T uidElement : uidElements) {
                if (uidElement.getUid().equalsIgnoreCase(uid)) {
                    return uidElement;
                }
            }
        }
        return null;
    }

    /**
     * @param elements1
     * @param elements2
     * @return
     */
    public static <T extends IHasName> List<T> subtractByUID(T[] elements1, T[] elements2) {
        List<T> elementList = new ArrayList<T>();
        for (T e1 : elements1) {
            T equalEl = findNamedElement(elements2, e1.getName());
            if (equalEl == null) {
                elementList.add(e1);
            }
        }
        return elementList;
    }

    /**
     * @param elements1
     * @param elements2
     * @return
     */
    public static <T extends IHasName> List<T> subtractByName(T[] elements1, T[] elements2) {
        List<T> elementList = new ArrayList<T>();
        for (T e1 : elements1) {
            T equalEl = findNamedElement(elements2, e1.getName());
            if (equalEl == null) {
                elementList.add(e1);
            }
        }
        return elementList;
    }

    /**
     * @param elements1
     * @param elements2
     * @return
     */
    public static <T extends IUIDContainer> List<UIDElementDiffWrapper<T>> equalsByUIDMap(T[] elements1, T[] elements2) {
        List<UIDElementDiffWrapper<T>> equalElements = new ArrayList<UIDElementDiffWrapper<T>>();
        for (T e1 : elements1) {
            T elementEqualByUID = findUIDElement(elements2, e1.getUid());
            if (elementEqualByUID != null) {
                equalElements.add(new UIDElementDiffWrapper<T>(e1.getUid(), e1, elementEqualByUID));
            }
        }
        return equalElements;
    }

    /**
     * @param elements1
     * @param elements2
     * @return
     */
    public static <T extends IHasName> Map<String, Tuple<T, T>> equalsByNameMap(T[] elements1, T[] elements2) {
        Map<String, Tuple<T, T>> equalElements = new HashMap<String, Tuple<T, T>>();
        for (T e1 : elements1) {
            T equalByNameElement = findNamedElement(elements2, e1.getName());
            if (equalByNameElement != null) {
                equalElements.put(e1.getName(), new Tuple<T, T>(e1, equalByNameElement));
            }
        }
        return equalElements;
    }

    /**
     * @param item
     * @return
     */
    public static <T> Set<T> singleton(T item) {
        Set<T> set = new HashSet<T>();
        set.add(item);
        return set;
    }

    /**
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean objEquals(Object obj1, Object obj2) {
        return !(obj1 == null ^ obj2 == null) && ((obj1 != null && obj1.equals(obj2)) || obj1 == null);
    }

    /**
     * @param uidElementsWrapper
     * @return
     */
    public static <T extends IUIDContainer> boolean objEquals(UIDElementDiffWrapper<T> uidElementsWrapper) {
        return objEquals(uidElementsWrapper.getOldElement(), uidElementsWrapper.getNewElement());
    }

    /**
     * @param oldElements
     * @param newElements
     * @return
     */
    public static <T extends INamedPackageDescriptorElement> List<IPackageDescriptorElementDiff<?, T>> getTopLevelElementsDiffs(
            T[] oldElements, T[] newElements) {
        Map<String, T> oldElementsMap = produceTargetByNameMap(oldElements);
        Map<String, T> newElementsMap = produceTargetByNameMap(newElements);

        List<IPackageDescriptorElementDiff<?, T>> diffList = new ArrayList<IPackageDescriptorElementDiff<?, T>>();
        Set<String> oldKeys = oldElementsMap.keySet();
        Set<String> newKeys = newElementsMap.keySet();
        Set<String> commonRefPaths = new HashSet<String>();
        for (String refPath : oldKeys) {
            if (newKeys.contains(refPath)) {
                commonRefPaths.add(refPath);
            } else {
                diffList.add(new PackageDescriptorElementDiff<IPackageDescriptorElement, T>(
                        DifferenceType.REMOVED, oldElementsMap.get(refPath)));
            }
        }
        for (String refPath : newKeys) {
            if (!oldKeys.contains(refPath)) {
                diffList.add(new PackageDescriptorElementDiff<IPackageDescriptorElement, T>(
                        DifferenceType.ADDED, newElementsMap.get(refPath)));
            }
        }
        for (String refPath : commonRefPaths) {
            T oldElement = oldElementsMap.get(refPath);
            T newElement = newElementsMap.get(refPath);
            if (!oldElement.equals(newElement)) {
                diffList.add(new PackageDescriptorElementDiff<IPackageDescriptorElement, T>(
                        DifferenceType.UPDATED, newElement, new Object[]{oldElement}));
            }
        }
        return diffList;
    }

	/**
	 *
	 * @param oldElements
	 * @param newElements
	 * @param itemClass
	 * @param resultClass
	 * @param <D>
	 * @param <T>
	 * @return
	 */
    public static <D extends IPackageDescriptorElementDiff<?, T>, T extends INamedPackageDescriptorElement>
    List<D> getTopLevelElementsDiffs(T[] oldElements, T[] newElements, Class<T> itemClass, Class<D> resultClass) {
        Map<String, T> oldElementsMap = produceTargetByNameMap(oldElements);
        Map<String, T> newElementsMap = produceTargetByNameMap(newElements);

        List<D> diffList = new ArrayList<D>();
        Set<String> oldKeys = oldElementsMap.keySet();
        Set<String> newKeys = newElementsMap.keySet();
        Set<String> commonRefPaths = new HashSet<String>();
        for (String refPath : oldKeys) {
            if (newKeys.contains(refPath)) {
                commonRefPaths.add(refPath);
            } else {
                D diff = ClassUtils.populate(resultClass,
                        new Tuple<Object, Class<?>>(Boolean.FALSE, Boolean.class),
                        new Tuple<Object, Class<?>>(oldElementsMap.get(refPath), itemClass));
                if (diff == null) {
                    throw new OpenFlameRuntimeException();
                } else {
                    diffList.add(diff);
                }
            }
        }
        for (String refPath : newKeys) {
            if (!oldKeys.contains(refPath)) {
                D diff = ClassUtils.populate(resultClass,
                        new Tuple<Object, Class<?>>(Boolean.TRUE, Boolean.class),
                        new Tuple<Object, Class<?>>(newElementsMap.get(refPath), itemClass));
                if (diff == null) {
                    throw new OpenFlameRuntimeException();
                } else {
                    diffList.add(diff);
                }
            }
        }
        for (String refPath : commonRefPaths) {
            T oldElement = oldElementsMap.get(refPath);
            T newElement = newElementsMap.get(refPath);
            if (!oldElement.equals(newElement)) {
                D diff = ClassUtils.populate(resultClass,
                        new Tuple<Object, Class<?>>(oldElement, itemClass),
                        new Tuple<Object, Class<?>>(newElement, itemClass));
                if (diff == null) {
                    throw new OpenFlameRuntimeException();
                } else {
                    diffList.add(diff);
                }
            }
        }
        return diffList;
    }

    /**
     * @param oldElements
     * @param newElements
     * @return
     */
    public static <T extends INamedPackageDescriptorElement> List
            <IPackageDescriptorElementDiff<?, T>> getTopLevelElementDiffsByUID(
            T[] oldElements, T[] newElements) {
        Map<String, T> oldElementsMap = produceElementByUIDMap(oldElements);
        Map<String, T> newElementsMap = produceElementByUIDMap(newElements);

        List<IPackageDescriptorElementDiff<?, T>> diffList = new ArrayList<IPackageDescriptorElementDiff<?, T>>();
        Set<String> oldUIDSet = oldElementsMap.keySet();
        Set<String> newUIDSet = newElementsMap.keySet();
        for (String uid : oldUIDSet) {
            if (newUIDSet.contains(uid)) {
                T oldElement = oldElementsMap.get(uid);
                T newElement = newElementsMap.get(uid);
                if (!oldElement.equals(newElement)) {
                    diffList.add(new PackageDescriptorElementDiff
                            <IPackageDescriptorElement, T>
                            (DifferenceType.UPDATED, newElement, new Object[]{oldElement}));
                }
            } else {
                diffList.add(new PackageDescriptorElementDiff
                        <IPackageDescriptorElement, T>
                        (DifferenceType.REMOVED, oldElementsMap.get(uid)));
            }
        }
        for (String uid : newUIDSet) {
            if (!oldUIDSet.contains(uid)) {
                diffList.add(new PackageDescriptorElementDiff
                        <IPackageDescriptorElement, T>
                        (DifferenceType.ADDED, newElementsMap.get(uid)));
            }
        }
        return diffList;
    }

	/**
	 *
	 * @param oldElements
	 * @param newElements
	 * @param itemClass
	 * @param resultClass
	 * @param <D>
	 * @param <T>
	 * @return
	 */
    public static <D extends IPackageDescriptorElementDiff<?, T>, T extends INamedPackageDescriptorElement>
    List<D> getTopLevelElementDiffsByUID(T[] oldElements, T[] newElements, Class<T> itemClass, Class<D> resultClass) {
        Map<String, T> oldElementsMap = produceElementByUIDMap(oldElements);
        Map<String, T> newElementsMap = produceElementByUIDMap(newElements);

        List<D> diffList = new ArrayList<D>();
        Set<String> oldUIDSet = oldElementsMap.keySet();
        Set<String> newUIDSet = newElementsMap.keySet();
        for (String uid : oldUIDSet) {
            if (newUIDSet.contains(uid)) {
                T oldElement = oldElementsMap.get(uid);
                T newElement = newElementsMap.get(uid);
                if (!oldElement.equals(newElement)) {
                    D diff = ClassUtils.populate(resultClass,
                            new Tuple<Object, Class<?>>(oldElement, itemClass),
                            new Tuple<Object, Class<?>>(newElement, itemClass));
                    if (diff == null) {
                        throw new OpenFlameRuntimeException();
                    } else {
                        diffList.add(diff);
                    }
                }
            } else {
                D diff = ClassUtils.populate(resultClass,
                        new Tuple<Object, Class<?>>(Boolean.FALSE, Boolean.class),
                        new Tuple<Object, Class<?>>(oldElementsMap.get(uid), itemClass));
                if (diff == null) {
                    throw new OpenFlameRuntimeException();
                } else {
                    diffList.add(diff);
                }
            }
        }
        for (String uid : newUIDSet) {
            if (!oldUIDSet.contains(uid)) {
                D diff = ClassUtils.populate(resultClass,
                        new Tuple<Object, Class<?>>(Boolean.TRUE, Boolean.class),
                        new Tuple<Object, Class<?>>(newElementsMap.get(uid), itemClass));
                if (diff == null) {
                    throw new OpenFlameRuntimeException();
                } else {
                    diffList.add(diff);
                }
            }
        }
        return diffList;
    }

	/**
	 *
	 * @param itemList
	 * @param clazz
	 * @param <T>
	 * @return
	 */
    public static <T> T[] getArray(Collection<T> itemList, Class<T> clazz) {
        return itemList == null || itemList.isEmpty() ?
                null : itemList.toArray((T[]) Array.newInstance(clazz, itemList.size()));
    }

    public static String uid(String lookup) {
        StringBuilder sb = new StringBuilder(SecurityHelper.hash(lookup));
        sb.insert(8, '-').insert(13, '-').insert(18, '-').insert(23, '-');
        return sb.toString();
    }

    /**
     * @param path
     * @param name
     * @return
     */
    public static String lookup(String path, String name) {
        return StringUtils.isBlank(path) ? StringUtils.normalize(name) : path + PATH_SEPARATOR + StringUtils.normalize(name);
    }

    /**
     * @param lookup
     * @return
     */
    public static String path(String lookup) {
	    int end = lookup.lastIndexOf('.');
	    return lookup.substring(0, end);
    }

    /**
     * @param target
     * @return
     */
    public static String lookup(INamedPackageDescriptorElement target) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(target.getPath())) {
            String[] pathEntries = target.getPath().split(PATH_SEPARATOR_REGEXP);
            for (String pathEntry : pathEntries) {
                sb.append(StringUtils.normalize(pathEntry)).append(PATH_SEPARATOR);
            }
        }
        sb.append(StringUtils.normalize(target.getName()));
        return sb.toString();
    }

    /**
     * @param name
     * @return
     */
    public static String rootDomainLookup(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Root domain name should not be blank.");
        }
        String[] pathEntries = name.split(PATH_SEPARATOR_REGEXP);
        if (pathEntries.length != 2) {
            throw new IllegalArgumentException("Root domain name should consist of two domains.");
        }
        return StringUtils.normalize(pathEntries[1]) + PATH_SEPARATOR + StringUtils.normalize(pathEntries[0]);
    }

    /**
     * @param target
     * @return
     */
    public static String getRefPath(INamedPackageDescriptorElement target) {
        String refPath = target.getName();
        if (StringUtils.isNotBlank(target.getPath())) {
            refPath = target.getPath() + PATH_SEPARATOR + refPath;
        }
        return refPath;
    }

    /**
     * @param refPath
     * @return
     */
    public static String lookupByRefPath(String refPath) {
        if (StringUtils.isBlank(refPath)) {
            return refPath;
        }
        String[] pathEntries = refPath.split(PATH_SEPARATOR_REGEXP);
        pathEntries = StringUtils.getNotBlankStrings(pathEntries);

        if (pathEntries.length == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pathEntries.length - 1; i++) {
                sb.append(StringUtils.normalize(pathEntries[i])).append(PATH_SEPARATOR);
            }
            sb.append(StringUtils.normalize(pathEntries[pathEntries.length - 1]));
            return sb.toString();
        }
    }

    /**
     * @param type
     * @param columnSize
     * @param decimalDigits
     * @return
     */
    public static FieldType getFieldType(int type, Integer columnSize, Integer decimalDigits) {
        switch (type) {
            case Types.DATE:
                return FieldType.DATE;
            case Types.TIMESTAMP:
                return FieldType.EVENT_TIME;
            case Types.TIME:
                return FieldType.UPDATE_TIME;
            case Types.BIT:
            case Types.BOOLEAN:
                return FieldType.FLAG;
            case Types.CHAR:
            case Types.VARCHAR:
                if (columnSize != null) {
                    if (columnSize <= 64) {
                        return FieldType.TINY_TEXT;
                    } else if (columnSize <= 255) {
                        return FieldType.SHORT_TEXT;
                    } else if (columnSize <= 1024) {
                        return FieldType.MEDIUM_TEXT;
                    } else if (columnSize <= 2048) {
                        return FieldType.URL;
                    } else if (columnSize <= 4096) {
                        return FieldType.DESCRIPTION;
                    } else {
                        return FieldType.LONG_TEXT;
                    }
                } else {
                    return FieldType.SHORT_TEXT;
                }
            case Types.CLOB:
                return FieldType.LONG_TEXT;
            case Types.LONGVARCHAR:
                return FieldType.UNLIMITED_TEXT;
            case Types.BIGINT:
                return FieldType.LARGE_NUMBER;
            case Types.INTEGER:
                return FieldType.INTEGER_NUMBER;
            case Types.DECIMAL:
                return FieldType.DECIMAL_NUMBER;
            case Types.DOUBLE:
                return FieldType.DECIMAL_NUMBER;
            case Types.FLOAT:
                return FieldType.DECIMAL_NUMBER;
            case Types.REAL:
                return FieldType.CURRENCY;
        }
        return null;
    }

    /**
     * @param entity
     * @return
     */
    public static boolean isEntityExtension(IEntityElement entity) {
        return entity != null && StringUtils.isNotBlank(entity.getExtendedEntityPath());
    }

    /**
     * @param name
     * @return
     */
    public static String classFormatting(String name) {
        String[] strings = name.split("[\\s\\p{Punct}]+");
        StringBuilder builder = new StringBuilder();

        for (String string : strings) {
            builder.append(StringUtils.capitalize(string));
        }

        return builder.toString();
    }

    /**
     * @param rootDomainChildPath
     * @return
     */
    public static String findRootDomainName(String rootDomainChildPath) {
        String[] pathEntries = rootDomainChildPath.split(PATH_SEPARATOR_REGEXP);
        if (pathEntries.length < 2) {
            return null;
        }
        return pathEntries[1] + PATH_SEPARATOR + pathEntries[0];
    }

    public static String extractNameFromLookup(String lookup) {
        String[] names = lookup.split(PATH_SEPARATOR_REGEXP);
        return names[names.length - 1];
    }

    public static String extractPathFromLookup(String lookup) {
        String[] names = lookup.split(PATH_SEPARATOR_REGEXP);
        names = (String[]) ArrayUtils.remove(names, names.length - 1);
        return StringUtils.join(names, PATH_SEPARATOR);
    }

    public static String humanNameFromLookup(String lookup) {
        String lookupName = extractNameFromLookup(lookup);
        String[] lookupNames = lookupName.split("-");
        String[] names = new String[lookupNames.length];
        for (int i = 0, lookupNamesLength = lookupNames.length; i < lookupNamesLength; i++) {
            names[i] = StringUtils.capitalize(lookupNames[i]);
        }
        return StringUtils.join(names, " ");
    }
}