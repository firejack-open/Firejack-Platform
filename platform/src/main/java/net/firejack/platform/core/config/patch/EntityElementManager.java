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

package net.firejack.platform.core.config.patch;

import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.construct.ConfigElementFactory;
import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.translate.sql.exception.TypeLookupException;
import net.firejack.platform.core.utils.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public final class EntityElementManager implements Cloneable {

    private String packageName;
    private String packagePath;
    private String packageReferencePath;
    private String packageDescription;
    private String prefix;
    private String contextUrl;
	private Integer newVersion;
    private String uid;
    private Map<String, IEntityElement> typesMap = new HashMap<String, IEntityElement>();
    private HashMap<String, Boolean> rootTypes = new HashMap<String, Boolean>();
    private Map<String, IEntityElement> aliasedEntities = new HashMap<String, IEntityElement>();
    private Map<String, IEntityElement> entitiesByUID = new HashMap<String, IEntityElement>();
    private ConfigElementFactory factory = ConfigElementFactory.getInstance();

    /**
     * @param path
     * @return
     */
    public boolean isPathRegistered(String path) {
        return typesMap.keySet().contains(path);
    }

    /**
     * @param packagePath
     * @param packageName
     * @param prefix
     * @param contextUrl
     * @param packageDescription
     * @param uid
     */
    public void registerPackageInfo(String packagePath, String packageName, String prefix, String contextUrl, String packageDescription, String uid) {
        this.packagePath = packagePath;
        this.packageName = packageName;
        this.packageDescription = packageDescription;
        this.uid = uid;
        String refPath = null;
        if (StringUtils.isNotBlank(packagePath)) {
            refPath = packagePath;
        }
        if (StringUtils.isNotBlank(packageName)) {
            refPath = (refPath == null) ? packageName : refPath + "." + packageName;
        }
        this.packageReferencePath = refPath;
        this.prefix = prefix;
        this.contextUrl = contextUrl;
    }

    /**
     * @return
     */
    public Integer getNewVersion() {
        return newVersion;
    }

    /**
     * @param newVersion
     */
    public void setNewVersion(Integer newVersion) {
        this.newVersion = newVersion;
    }

    /*public void registerEntityElement(String packageBase, IEntityElement packageEntity, String realPath) {
             if (packageEntity == null) {
                 throw new IllegalArgumentException("Failed to register nullable package entity");
             }
             if (typesMap.containsKey(packageBase)) {
                 throw new IllegalStateException("Path [" + packageBase + "] already used.");
             }
             if (DiffUtils.isEntityExtension(packageEntity)) {
                 rootTypes.put(packageEntity.getExtendedEntityPath(), Boolean.FALSE);
             }
             if (StringUtils.isNotBlank(packageEntity.getAlias())) {
                 aliasedEntities.put(packageEntity.getAlias(), packageEntity);
             }
             factory.attachPath(packageEntity, realPath);
             typesMap.put(packageBase, packageEntity);
         }*/

    /**
     * @param entityMetaLookup
     * @param packageEntity
     */
    public void registerEntityElement(String entityMetaLookup, IEntityElement packageEntity) {
        if (packageEntity == null) {
            throw new IllegalArgumentException("Failed to register nullable package entity");
        } else if (StringUtils.isBlank(entityMetaLookup) || !entityMetaLookup.endsWith("." + packageEntity.getName())) {
            throw new IllegalArgumentException("Wrong entity meta lookup [" + entityMetaLookup + "]");
        } else if (entitiesByUID.containsKey(packageEntity.getUid())) {
            throw new IllegalArgumentException("Trying to register entity that has already registered UID.");
        }
        if (typesMap.containsKey(entityMetaLookup)) {
            throw new IllegalStateException("Path [" + entityMetaLookup + "] already used.");
        }
        if (DiffUtils.isEntityExtension(packageEntity)) {
            if (!packageEntity.isTypeEntity()) {
                rootTypes.put(packageEntity.getExtendedEntityPath(), Boolean.FALSE);
            }
        }
        if (StringUtils.isNotBlank(packageEntity.getAlias())) {
            aliasedEntities.put(packageEntity.getAlias(), packageEntity);
        }

        String path = entityMetaLookup.substring(0,
                entityMetaLookup.length() - packageEntity.getName().length() - 1);
        factory.attachPath(packageEntity, path);
        typesMap.put(entityMetaLookup, packageEntity);
        entitiesByUID.put(packageEntity.getUid(), packageEntity);
    }

    /**
     * @throws net.firejack.platform.core.config.translate.sql.exception.TypeLookupException
     *
     */
    public void evaluateRootEntityElements() throws TypeLookupException {
        boolean notEvaluated;
        do {
            notEvaluated = false;
            for (String candidatePath : rootTypes.keySet()) {
                if (isInternalPath(candidatePath)) {
                    IEntityElement candidate = lookup(candidatePath);
                    if (DiffUtils.isEntityExtension(candidate)) {
                        rootTypes.remove(candidatePath);
                        notEvaluated = true;
                        break;
                    }
                }
            }
        } while (notEvaluated);
    }

    /**
     * @return
     */
    public Iterator<IEntityElement> registeredEntitiesIterator() {
        return typesMap.values().iterator();
    }

    /**
     * @param uid
     * @return
     * @throws net.firejack.platform.core.config.translate.sql.exception.TypeLookupException
     *
     */
    public IEntityElement lookupByUID(String uid) throws TypeLookupException {
        if (StringUtils.isBlank(uid)) {
            throw new IllegalArgumentException("Specified UID value should not be blank.");
        }
        IEntityElement entity = entitiesByUID.get(uid);
        if (entity == null) {
            throw new TypeLookupException("Failed to find type by specified UID.");
        }
        return entity;
    }

    /**
     * @param path
     * @return
     * @throws net.firejack.platform.core.config.translate.sql.exception.TypeLookupException
     *
     */
    public IEntityElement lookup(String path) throws TypeLookupException {
        if (StringUtils.isBlank(path)) {
            throw new TypeLookupException("Could not process empty path.");
        }
        IEntityElement packageEntity = typesMap.get(path);
        if (packageEntity == null) {
            throw new TypeLookupException("Failed to find type by specified path [" + path + "].");
        }
        return packageEntity;
    }

    /**
     * @param entity
     * @return
     * @throws net.firejack.platform.core.config.translate.sql.exception.TypeLookupException
     *
     */
    public IEntityElement lookupRootEntity(IEntityElement entity) throws TypeLookupException {
        if (entity == null) {
            throw new IllegalArgumentException("Could not process nullable entity.");
        }
        IEntityElement rootEntityCandidate = entity;
        while (StringUtils.isNotBlank(rootEntityCandidate.getExtendedEntityPath())) {
            rootEntityCandidate = lookup(rootEntityCandidate.getExtendedEntityPath());
        }
        return rootEntityCandidate;
    }

    /**
     * @param reference
     * @return
     * @throws net.firejack.platform.core.config.translate.sql.exception.TypeLookupException
     *
     */
    public IEntityElement lookup(Reference reference) throws TypeLookupException {
        if (reference == null) {
            throw new TypeLookupException("Could not process empty reference.");
        } else if (StringUtils.isBlank(reference.getRefPath())) {
            throw new TypeLookupException("Could not process empty reference path.");
        }

        return lookup(reference.getRefPath());
    }

    /**
     * @param entityName
     * @return
     * @throws net.firejack.platform.core.config.translate.sql.exception.TypeLookupException
     *
     */
    public IEntityElement lookupLeafEntity(String entityName) throws TypeLookupException {
        if (StringUtils.isBlank(entityName)) {
            throw new TypeLookupException("Could not process empty path.");
        }
        String shouldEndWith = "." + entityName;
        for (String path : typesMap.keySet()) {
            if (path.endsWith(shouldEndWith)) {
                return typesMap.get(path);
            }
        }
        throw new TypeLookupException("Failed to find type by specified entity name [" + entityName + "].");
    }

    /**
     * @param aliasedPath
     * @return
     * @throws net.firejack.platform.core.config.translate.sql.exception.TypeLookupException
     *
     */
    public IEntityElement lookupByType(String aliasedPath) throws TypeLookupException {
        if (StringUtils.isBlank(aliasedPath)) {
            throw new TypeLookupException("Could not process empty aliased type.");
        }
        IEntityElement packageEntity = typesMap.get(aliasedPath);
        if (packageEntity != null) {
            return packageEntity;
        }
        packageEntity = aliasedEntities.get(aliasedPath);
        if (packageEntity == null) {
            throw new TypeLookupException("Failed to find entity using specified aliased type.");
        }
        return packageEntity;
    }

    /**
     * @param entityName
     * @return
     * @throws net.firejack.platform.core.config.translate.sql.exception.TypeLookupException
     *
     */
    public String lookupKeyByEntityName(String entityName) throws TypeLookupException {
        if (StringUtils.isBlank(entityName)) {
            throw new TypeLookupException("Could not process empty aliased type.");
        }
        String shouldEndWith = "." + entityName;
        for (String path : typesMap.keySet()) {
            if (path.endsWith(shouldEndWith)) {
                return path;
            }
        }
        throw new TypeLookupException("Failed to find lookup key by specified entity name [" + entityName + "].");
    }

    /**
     * @param entity
     * @return
     */
    public boolean isRootEntity(IEntityElement entity) {
        if (entity == null || StringUtils.isBlank(entity.getName())) {
            throw new IllegalArgumentException("Could not process empty path.");
        }
        return getRootPath(entity) != null;
    }

    /**
     * @param rootType
     */
    public void useRootType(IEntityElement rootType) {
        String rootPath = getRootPath(rootType);
        if (rootPath == null) {
            throw new IllegalArgumentException("Specified root entity is not registered.");
        }
        rootTypes.put(rootPath, Boolean.TRUE);
    }

    /**
     * @param rootType
     * @return
     */
    public boolean isRootTypeUsed(IEntityElement rootType) {
        String rootPath = getRootPath(rootType);
        if (rootPath == null) {
            throw new IllegalArgumentException("Specified root entity is not registered.");
        }
        return rootTypes.get(rootPath) == Boolean.TRUE;
    }

    /***/
    public void clear() {
        typesMap.clear();
        rootTypes.clear();
        aliasedEntities.clear();
        registerPackageInfo(null, null, null, null, null, null);
    }

    /**
     * @return
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return
     */
    public String getPackageDescription() {
        return packageDescription;
    }

    /**
     * @return
     */
    public String getPackagePath() {
        return packagePath;
    }

    /**
     * @return
     */
    public String getPrefix() {
        return prefix;
    }

	public String getContextUrl() {
		return contextUrl;
	}

	/**
     * @return
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param path
     * @return
     */
    public boolean isInternalPath(String path) {
        return path.startsWith(packageReferencePath);
    }

    @Override
    protected EntityElementManager clone() {
        EntityElementManager registry = new EntityElementManager();
        registry.typesMap = new HashMap<String, IEntityElement>(this.typesMap);
        registry.registerPackageInfo(packagePath, packageName, prefix, contextUrl, packageDescription, uid);
        registry.newVersion = newVersion;
        registry.rootTypes = new HashMap<String, Boolean>(this.rootTypes);
        registry.aliasedEntities = new HashMap<String, IEntityElement>(this.aliasedEntities);
        return registry;
    }

    private String getRootPath(IEntityElement rootEntity) {
        String endsWith = "." + rootEntity.getName();
        for (String path : rootTypes.keySet()) {
            if (path.endsWith(endsWith)) {
                return path;
            }
        }
        return null;
    }
}
