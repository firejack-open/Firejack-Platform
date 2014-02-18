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

package net.firejack.platform.core.store.registry.resource;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.core.cache.IResourceVersionCacheService;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.resource.*;
import net.firejack.platform.core.store.registry.RegistryNodeStore;
import net.firejack.platform.core.utils.CollectionUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class ResourceStore<R extends AbstractResourceModel> extends RegistryNodeStore<R> implements IResourceStore<R> {

    @Autowired
    protected IResourceVersionCacheService<R, AbstractResourceVersionModel> resourceVersionCacheService;

    protected IResourceVersionStore<AbstractResourceVersionModel<R>> resourceVersionStore;

    /**
     * @param resourceVersionStore
     */
    public void setResourceVersionStore(IResourceVersionStore<AbstractResourceVersionModel<R>> resourceVersionStore) {
        this.resourceVersionStore = resourceVersionStore;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public R findByUID(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Empty UID parameter.");
        }
        Criteria criteria = getSession().createCriteria(getClazz());
        criteria.createAlias("uid", "uid");
        criteria.add(Restrictions.eq("uid.uid", uid));
        criteria.setFetchMode("resourceVersion", FetchMode.JOIN);
        return (R) criteria.uniqueResult();
    }

    @Override
    @Transactional
    public R deleteByUID(String uid) {
        R resource = findByUID(uid);
        if (resource != null) {
            /*if (resource.getResourceVersion() != null) {
                               getHibernateTemplate().delete(resource.getResourceVersion());
                           }
                           getHibernateTemplate().delete(resource);*/
            deleteRecursively(resource);
            //delete(resource);
        }
        return resource;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cultures> findAvailableCulturesByCollectionId(Long collectionId) {
        return findByQuery(null, null, "Resource.findAvailableCulturesByCollectionId",
                "collectionId", collectionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion nameCriterion = Restrictions.like("name", "%" + term + "%");
        criterions.add(nameCriterion);
        return findAllWithFilter(criterions, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findAllBySearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdCriterion = Restrictions.in("parent.id", registryNodeIds);
        Criterion nameCriterion = Restrictions.like("name", "%" + term + "%");
        LogicalExpression expressionAll = Restrictions.and(registryNodeIdCriterion, nameCriterion);
        criterions.add(expressionAll);
        return findAllWithFilter(criterions, filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<R> findAllByLikeLookupPrefix(final String lookupPrefix) {
        if (StringUtils.isBlank(lookupPrefix)) {
            return Collections.emptyList();
        }
        return getHibernateTemplate().execute(new HibernateCallback<List<R>>() {
            public List<R> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.like("lookup", lookupPrefix + "%"));
                criteria.setFetchMode("resourceVersions", FetchMode.JOIN);
                return (List<R>) criteria.list();
            }
        });
    }

    @Override
    @Transactional
    public void save(R resource, boolean isCreateAutoDescription) {
        boolean isNew = resource.getId() == null;
        if (isNew) {
            resource.setLastVersion(1);
            resource.setPublishedVersion(null);
        } else {
            R foundResource = findById(resource.getId());
            if (foundResource.getLastVersion() != null) {
                resource.setLastVersion(foundResource.getLastVersion());
            } else {
                resource.setLastVersion(1);
            }
            resource.setPublishedVersion(foundResource.getPublishedVersion());
            resource.setParent(foundResource.getParent());
        }
        resourceVersionCacheService.removeCacheByResource(resource);
        super.save(resource, isCreateAutoDescription);
        @SuppressWarnings("unchecked")
        AbstractResourceVersionModel<R> resourceVersion = resource.getResourceVersion();
        if (isNew) {
            resourceVersion.setVersion(1);
            if (resourceVersion.getStatus() == null) {
                resourceVersion.setStatus(ResourceStatus.WORKING);
            }
//            startCase(resource);
        } else {
            Integer selectedVersion = resource.getSelectedVersion();
            if (resourceVersion.getId() == null) {
                AbstractResourceVersionModel<R> foundResourceVersion = resourceVersionStore.findByResourceIdCultureAndVersion(
                        resource.getId(), resourceVersion.getCulture(), selectedVersion);
                if (foundResourceVersion != null) {
                    resourceVersion.setId(foundResourceVersion.getId());
                } else {
                    resourceVersion.setVersion(selectedVersion);
                }
            }
            if (resourceVersion.getStatus() == null) {
                resourceVersion.setStatus(ResourceStatus.WORKING);
            }
        }
        resourceVersion.setUpdated(new Date());
        resourceVersion.setResource(resource);
        resourceVersionCacheService.removeCacheByResourceVersion(resourceVersion);
        resourceVersionStore.saveOrUpdate(resourceVersion);
    }

    @Override
    @Transactional
    public List<AbstractResourceVersionModel<R>> save(R resource, List<AbstractResourceVersionModel<R>> resourceVersions) {
        R foundResource = findByLookup(resource.getLookup());
        if (foundResource == null) {
            if (resource.getParent() == null) {
                throw new BusinessFunctionException("Can't create RESOURCE [lookup: " + resource.getLookup() + "] without parent.");
            }
            resource.setLastVersion(1);
            resourceVersionCacheService.removeCacheByResource(resource);
            super.save(resource, false);

            for (AbstractResourceVersionModel resourceVersion : resourceVersions) {
                resourceVersionCacheService.removeCacheByResourceVersion(resourceVersion);
                resourceVersionStore.saveOrUpdate(resourceVersion);
            }
        } else {
            foundResource.setName(resource.getName());
            foundResource.setDescription(resource.getDescription());
            resourceVersions = resourceVersionStore.createNewResourceVersions(foundResource, resourceVersions);
        }
        return resourceVersions;
    }

    @Override
    @Transactional(rollbackFor = OpenFlameRuntimeException.class)
    public void mergeForGenerator(R resource, List<AbstractResourceVersionModel<R>> resourceVersionList) {
        if (resource == null) {
            throw new OpenFlameRuntimeException("Resource to merge should not be null.");
        }
        ResourceModel oldResource = (ResourceModel) findByUID(resource.getUid().getUid());
        if (oldResource == null) {
            throw new BusinessFunctionException(
                    "Failed to update resource with resource version, because initial resource was not found in database.");
        }
        if (resourceVersionList == null || resourceVersionList.isEmpty()) {
            throw new OpenFlameRuntimeException("Resource version list should not be empty.");
        }
        boolean lastVersionWasNotFound = true;
        for (AbstractResourceVersionModel resourceVersion : resourceVersionList) {
            if (resource.getLastVersion().equals(resourceVersion.getVersion())) {
                lastVersionWasNotFound = false;
                break;
            }
        }
        if (lastVersionWasNotFound) {
            throw new OpenFlameRuntimeException(
                    "The version of resource should have corresponding resource version.");
        }
        resource.setId(oldResource.getId());
        resource.setUid(oldResource.getUid());

        super.save(resource, false);

        List<AbstractResourceVersionModel<R>> oldResourceVersions =
                resourceVersionStore.findAllResourceVersions(resource.getId());
        if (CollectionUtils.isEmpty(oldResourceVersions)) {
            throw new OpenFlameRuntimeException(
                    "Resource should have at least one Resource Version.");
        }

        Map<Cultures, Map<Integer, AbstractResourceVersionModel<R>>> resourceVersionMap = getResourceVersionMap(resourceVersionList);
        Map<Cultures, Map<Integer, AbstractResourceVersionModel<R>>> oldResourceVersionMap = getResourceVersionMap(oldResourceVersions);

        List<AbstractResourceVersionModel<R>> deleteList = new ArrayList<AbstractResourceVersionModel<R>>();
        List<AbstractResourceVersionModel<R>> updateList = new ArrayList<AbstractResourceVersionModel<R>>();
        for (Map.Entry<Cultures, Map<Integer, AbstractResourceVersionModel<R>>> oldVersionsByCultureEntry :
                oldResourceVersionMap.entrySet()) {
            Map<Integer, AbstractResourceVersionModel<R>> newVersionsForCulture =
                    resourceVersionMap.get(oldVersionsByCultureEntry.getKey());
            if (newVersionsForCulture == null) {
                deleteList.addAll(oldVersionsByCultureEntry.getValue().values());
            } else {
                for (Map.Entry<Integer, AbstractResourceVersionModel<R>> oldResourceVersionEntry :
                        oldVersionsByCultureEntry.getValue().entrySet()) {
                    AbstractResourceVersionModel<R> newResourceVersion =
                            newVersionsForCulture.get(oldResourceVersionEntry.getKey());
                    if (newResourceVersion == null) {
                        deleteList.add(oldResourceVersionEntry.getValue());
                    } else {
                        AbstractResourceVersionModel<R> oldResourceVersion = oldResourceVersionEntry.getValue();
                        oldResourceVersion.setUpdated(new Date());
                        oldResourceVersion.setStatus(newResourceVersion.getStatus());
                        ResourceType type = resource.getResourceType();
                        switch (type) {
                            case TEXT:
                                TextResourceVersionModel oldText = (TextResourceVersionModel) oldResourceVersion;
                                TextResourceVersionModel newText = (TextResourceVersionModel) newResourceVersion;
                                oldText.setText(newText.getText());
                                break;
                            case HTML:
                                HtmlResourceVersionModel oldHtml = (HtmlResourceVersionModel) oldResourceVersion;
                                HtmlResourceVersionModel newHtml = (HtmlResourceVersionModel) newResourceVersion;
                                oldHtml.setHtml(newHtml.getHtml());
                                break;
                            case IMAGE:
                                ImageResourceVersionModel oldImage = (ImageResourceVersionModel) oldResourceVersion;
                                ImageResourceVersionModel newImage = (ImageResourceVersionModel) newResourceVersion;
                                oldImage.setTitle(newImage.getTitle());
                                oldImage.setHeight(newImage.getHeight());
                                oldImage.setMimeType(newImage.getMimeType());
                                oldImage.setOriginalFilename(newImage.getOriginalFilename());
                                oldImage.setTemporaryFilename(newImage.getTemporaryFilename());
                                break;
                            case AUDIO:
                                AudioResourceVersionModel oldAudio = (AudioResourceVersionModel) oldResourceVersion;
                                AudioResourceVersionModel newAudio = (AudioResourceVersionModel) newResourceVersion;
                                oldAudio.setOriginalFilename(newAudio.getOriginalFilename());
                                oldAudio.setTemporaryFilename(newAudio.getTemporaryFilename());
                                break;
                            case VIDEO:
                                VideoResourceVersionModel oldVideo = (VideoResourceVersionModel) oldResourceVersion;
                                VideoResourceVersionModel newVideo = (VideoResourceVersionModel) newResourceVersion;
                                oldVideo.setOriginalFilename(newVideo.getOriginalFilename());
                                oldVideo.setTemporaryFilename(newVideo.getTemporaryFilename());
                                break;
                            case DOCUMENT:
                                DocumentResourceVersionModel oldDocument = (DocumentResourceVersionModel) oldResourceVersion;
                                DocumentResourceVersionModel newDocument = (DocumentResourceVersionModel) newResourceVersion;
                                oldDocument.setOriginalFilename(newDocument.getOriginalFilename());
                                oldDocument.setTemporaryFilename(newDocument.getTemporaryFilename());
                                break;
                            case FILE:
                                FileResourceVersionModel oldFile = (FileResourceVersionModel) oldResourceVersion;
                                FileResourceVersionModel newFile = (FileResourceVersionModel) newResourceVersion;
                                oldFile.setOriginalFilename(newFile.getOriginalFilename());
                                oldFile.setTemporaryFilename(newFile.getTemporaryFilename());
                                break;
                            default:
                                throw new OpenFlameRuntimeException("Unsupported type of resource: [" + type.name() + "]");
                        }
                        updateList.add(oldResourceVersion);
                    }
                }
            }
        }

        List<AbstractResourceVersionModel<R>> createList = new ArrayList<AbstractResourceVersionModel<R>>();
        for (Map.Entry<Cultures, Map<Integer, AbstractResourceVersionModel<R>>> newVersionsByCultureEntry :
                resourceVersionMap.entrySet()) {
            Map<Integer, AbstractResourceVersionModel<R>> oldVersionsForCulture =
                    oldResourceVersionMap.get(newVersionsByCultureEntry.getKey());
            if (oldVersionsForCulture == null) {
                createList.addAll(newVersionsByCultureEntry.getValue().values());
            } else {
                Map<Integer, AbstractResourceVersionModel<R>> newVersionsByCulture = newVersionsByCultureEntry.getValue();
                for (Map.Entry<Integer, AbstractResourceVersionModel<R>> newResourceVersionEntry :
                        newVersionsByCulture.entrySet()) {
                    if (!oldVersionsForCulture.containsKey(newResourceVersionEntry.getKey())) {
                        createList.add(newResourceVersionEntry.getValue());
                    }
                }
            }
        }

        resourceVersionStore.deleteAll(deleteList);
        resourceVersionStore.saveOrUpdateAll(updateList);
        resourceVersionStore.saveOrUpdateAll(createList);
    }

    @Override
    @Transactional
    public Integer setMaxResourceVersion(final R resource) {
        Integer maxVersion = getHibernateTemplate().execute(new HibernateCallback<Integer>() {
            public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(AbstractResourceVersionModel.class);
                criteria.add(Restrictions.eq("resource.id", resource.getId()));
                criteria.add(Restrictions.le("version", resource.getLastVersion()));

                ProjectionList projectionList = Projections.projectionList();
                projectionList.add(Projections.max("version"));
                criteria.setProjection(projectionList);
                return (Integer) criteria.uniqueResult();
            }
        });

        resource.setLastVersion(maxVersion);
        if (resource.getPublishedVersion() != null && resource.getPublishedVersion() > maxVersion) {
            resource.setPublishedVersion(maxVersion);
        }
        update(resource);
        return maxVersion;
    }

    @Override
    @Transactional(rollbackFor = IOException.class)
    public void delete(R resource) {
        Long resourceId = resource.getId();

        String  resourcesFolder = null;
        if (resource instanceof ImageResourceModel) {
            resourcesFolder = helper.getImage();
        } else if (resource instanceof AudioResourceModel) {
            resourcesFolder = helper.getAudio();
        } else if (resource instanceof VideoResourceModel) {
            resourcesFolder = helper.getVideo();
        }

	    if (resourcesFolder != null) {
		    List<AbstractResourceVersionModel<R>> resourceVersions = resourceVersionStore.findAllResourceVersions(resourceId);
		    for (AbstractResourceVersionModel<R> resourceVersion : resourceVersions) {
			    String resourceVersionFilename = resourceVersion.getId() + "_" +
					    resourceVersion.getVersion() + "_" +
					    resourceVersion.getCulture().name();
			    OPFEngine.FileStoreService.deleteFile(OpenFlame.FILESTORE_CONTENT, resourceVersionFilename,resourcesFolder, String.valueOf(resourceId));
		    }
			    OPFEngine.FileStoreService.deleteDirectory(OpenFlame.FILESTORE_CONTENT,resourcesFolder, String.valueOf(resourceId));
	    }

	    resourceVersionStore.deleteAllByResourceId(resourceId);
        super.delete(resource);
    }

    private Map<Cultures, Map<Integer, AbstractResourceVersionModel<R>>> getResourceVersionMap(List<AbstractResourceVersionModel<R>> resourceVersions) {
        Map<Cultures, Map<Integer, AbstractResourceVersionModel<R>>> resourceVersionMap =
                new HashMap<Cultures, Map<Integer, AbstractResourceVersionModel<R>>>();
        for (AbstractResourceVersionModel<R> resourceVersion : resourceVersions) {
            Map<Integer, AbstractResourceVersionModel<R>> resourceVersionsByCulture = resourceVersionMap.get(resourceVersion.getCulture());
            if (resourceVersionsByCulture == null) {
                resourceVersionsByCulture = new HashMap<Integer, AbstractResourceVersionModel<R>>();
                resourceVersionMap.put(resourceVersion.getCulture(), resourceVersionsByCulture);
            }
            resourceVersionsByCulture.put(resourceVersion.getVersion(), resourceVersion);
        }
        return resourceVersionMap;
    }

}
