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

package net.firejack.platform.core.store.registry.resource;

import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.core.cache.IResourceVersionCacheService;
import net.firejack.platform.core.model.registry.IAllowCreateAutoDescription;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.model.registry.resource.HtmlResourceVersionModel;
import net.firejack.platform.core.store.BaseStore;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ResourceVersionStore<RV extends AbstractResourceVersionModel> extends BaseStore<RV, Long> implements IResourceVersionStore<RV> {

    @Autowired
    private IResourceVersionCacheService<AbstractResourceModel, RV> resourceVersionCacheService;

    private IResourceStore<AbstractResourceModel> resourceStore;

    /**
     * @param resourceStore
     */
    public void setResourceStore(IResourceStore<AbstractResourceModel> resourceStore) {
        this.resourceStore = resourceStore;
    }

    @Override
    @Transactional(readOnly = true)
    public RV findByResourceIdCultureAndVersion(final Long resourceId, final Cultures culture, final Integer version) {
        return findSingle("ResourceVersion.findByResourceIdCultureAndVersion",
                "resourceId", resourceId, "culture", culture, "version", version);
    }

    @Override
    @Transactional(readOnly = true)
    public RV findLastVersionByResourceIdCulture(final Long resourceId, final Cultures culture) {
        return findSingle("ResourceVersion.findLastVersionByResourceIdCulture",
                "resourceId", resourceId, "culture", culture);
    }

    @Override
    @Transactional(readOnly = true)
    public RV findLastVersionByResourceId(Long resourceId) {
        return findSingle("ResourceVersion.findLastVersionByResourceId",
                "resourceId", resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public RV findLastVersionByLookup(String resourceLookup) {
        return findSingle("ResourceVersion.findLastVersionByResourceLookup",
                "lookup", resourceLookup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RV> findLastVersionsByResourceId(Long resourceId) {
        return find(null, null, "ResourceVersion.findLastVersionByResourceId",
                "resourceId", resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RV> findByResourceIdAndVersion(Long resourceId, Integer version) {
        return find(null, null, "ResourceVersion.findByResourceIdAndVersion",
                "resourceId", resourceId, "version", version);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cultures> findCulturesForLastVersionByResourceIds(final List<Long> resourceIds) {
        return getHibernateTemplate().executeFind(new HibernateCallback<List<Cultures>>() {
            @Override
            public List<Cultures> doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery("ResourceVersion.findCulturesForLastVersionByResourceIds");
                setQueryParams(query, "resourceIds", resourceIds);
                return query.list();
            }
        });
    }

    @Override
    @Transactional
    public RV createNewResourceVersion(long resourceId, int version, Cultures culture) {
        RV newResourceVersion = instantiate();
        AbstractResourceModel resource = resourceStore.findById(resourceId);
        newResourceVersion.setResource(resource);
        newResourceVersion.setVersion(version);
        newResourceVersion.setCulture(culture);
        newResourceVersion.setStatus(ResourceStatus.WORKING);
        saveOrUpdate(newResourceVersion);
        return newResourceVersion;
    }

    @Override
    @Transactional
    public List<RV> createNewResourceVersions(AbstractResourceModel resource, List<RV> resourceVersions) {
        Integer newVersion = resource.getLastVersion() + 1;
        resource.setLastVersion(newVersion);
        resourceVersionCacheService.removeCacheByResource(resource);
        resourceStore.merge(resource);
        List<RV> newResourceVersions = new ArrayList<RV>();
        if (!resourceVersions.isEmpty()) {
            for (RV resourceVersion : resourceVersions) {
                RV newResourceVersion = instantiate();
                copyResourceVersionProperties(newResourceVersion, resourceVersion);
                newResourceVersion.setId(null);
                newResourceVersion.setResource(resource);
                newResourceVersion.setVersion(newVersion);
                newResourceVersion.setStatus(ResourceStatus.WORKING);
                newResourceVersion.setUpdated(new Date());
                newResourceVersion.setCreated(null);
                resourceVersionCacheService.removeCacheByResourceVersion(resourceVersion);
                saveOrUpdate(newResourceVersion);
                newResourceVersions.add(newResourceVersion);
            }
        }
        return newResourceVersions;
    }

    @Override
    @Transactional
    public void saveOrUpdate(RV resourceVersion) {
        resourceVersion.setUpdated(new Date());
        super.saveOrUpdate(resourceVersion);

        AbstractResourceModel resource = resourceVersion.getResource();
        RegistryNodeModel folder = resource.getParent();
        if (resource.getName().equals(HtmlResourceStore.DESCRIPTION) && folder != null && folder.getType() == RegistryNodeType.FOLDER) {
            RegistryNodeModel parent = lazyInitializeIfNeed(folder.getParent());
            if (parent != null && parent instanceof IAllowCreateAutoDescription) {
                parent.setDescription(((HtmlResourceVersionModel) resourceVersion).getHtml());
            }
        }
        resourceVersionCacheService.removeCacheByResourceVersion(resourceVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RV> findAllResourceVersions(final Long id) {
        return getHibernateTemplate().execute(new HibernateCallback<List<RV>>() {
            public List<RV> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.eq("resource.id", id));
                return (List<RV>) criteria.list();
            }
        });
    }

    @Override
    @Transactional
    public void deleteAllByResourceId(Long resourceId) {
        List<RV> resourceVersions = findAllResourceVersions(resourceId);
        for (RV resourceVersion : resourceVersions) {
            super.delete(resourceVersion);
        }
    }

    @Override
    @Transactional
    public Integer deleteResourceVersion(RV resourceVersion) {
        super.delete(resourceVersion);
        getHibernateTemplate().flush();
        return resourceStore.setMaxResourceVersion(resourceVersion.getResource());
    }

    private void copyResourceVersionProperties(RV dest, RV orig) {
        PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();
        PropertyDescriptor[] propertyDescriptors = propertyUtils.getPropertyDescriptors(orig);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            if (ArrayUtils.contains(new String[]{"class", "id", "version", "status", "updated", "created"}, name)) {
                continue;
            }
            if (propertyUtils.isReadable(orig, name) && propertyUtils.isWriteable(dest, name)) {
                try {
                    Object value = propertyUtils.getSimpleProperty(orig, name);
                    if (value instanceof Timestamp) {
                        value = ConvertUtils.convert(value, Date.class);
                    }
                    BeanUtils.copyProperty(dest, name, value);
                } catch (Exception e) {
                    // Should not happen
                }
            }
        }
    }

	@Override
	@Transactional(readOnly = true)
	public List<RV> readResourcesByLookupList(List<String> lookup) {
		return find(null, null, "ResourceVersion.readResourcesByLookupList", "lookup", lookup);
	}

}
