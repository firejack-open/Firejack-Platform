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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.NavigationElementType;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.Factory;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import org.hibernate.*;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@SuppressWarnings("unused")
@Component("navigationElementStore")
public class NavigationElementStore extends RegistryNodeStore<NavigationElementModel>
        implements INavigationElementStore, IResourceAccessFieldsStore<NavigationElementModel> {

    @Autowired
    @Qualifier("cacheProcessor")
    private ICacheDataProcessor cacheProcessor;
    @Autowired
    private Factory modelFactory;

    @Autowired
    private IPermissionStore permissionStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(NavigationElementModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public NavigationElementModel findById(Long id) {
        return findSingle("NavigationElement.findByIdWithPermissions", "id", id);
    }

    @Override
    @Transactional(readOnly = true)
    public NavigationElementModel findByLookup(String lookup, boolean loadPermissions) {
        NavigationElementModel navigationElement = super.findByLookup(lookup, false);
        if (loadPermissions && navigationElement != null) {
            initializePermissions(navigationElement);
        }
        return navigationElement;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NavigationElementModel> findAllByParentIdWithFilter(Long id, SpecifiedIdsFilter<Long> filter) {
        List<NavigationElementModel> elementList = findAllByParentIdWithFilter(id, filter, false);
        for (NavigationElementModel navigationElement : elementList) {
            initializePermissions(navigationElement);
        }
        return elementList;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<NavigationElementModel> findAllByLikeLookupPrefix(final String lookupPrefix) {
        if (StringUtils.isBlank(lookupPrefix)) {
            return Collections.emptyList();
        }
        return getHibernateTemplate().execute(new HibernateCallback<List<NavigationElementModel>>() {
            public List<NavigationElementModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.like("lookup", lookupPrefix + "%"));
                criteria.createCriteria("main", "main", CriteriaSpecification.LEFT_JOIN);
                return (List<NavigationElementModel>) criteria.list();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<NavigationElementModel> findAllWithPermissions() {
        return find(null, null, "NavigationElement.findAllWithPermissions");
    }

    @Override
    @Transactional(readOnly = true)
    public List<NavigationElementModel> findAllWithPermissions(String packageLookup) {
        return find(null, null, "NavigationElement.findAllWithPermissionsByBaseLookup",
                "lookupPattern", packageLookup + ".%"
        );
    }

    @Override
    @Transactional
    public void save(NavigationElementModel navigationElement) {
        boolean isNew = navigationElement.getId() == null;
        super.save(navigationElement);
        if (ConfigContainer.isAppInstalled()) {
            if (isNew) {
                NavigationElement nav = modelFactory.convertTo(NavigationElement.class, navigationElement);
                cacheProcessor.addNavigation(nav);
                permissionStore.createPermissionByNavigationElement(navigationElement);
            } else {
                navigationElement = load(navigationElement.getId());
                initializePermissions(navigationElement);
//                List<PermissionModel> permissionModels = navigationElement.getPermissions();
//                for (PermissionModel permissionModel : permissionModels) {
//                    if (!permissionModel.getParent().getId().equals(navigationElement.getParent().getId())) {
//                        permissionModel.setParent(navigationElement.getParent());
//                        permissionStore.save(permissionModel);
//                    }
//                }
                NavigationElement nav = modelFactory.convertTo(NavigationElement.class, navigationElement);
                cacheProcessor.updateNavigation(nav);
            }
        }
    }

    @Override
    public void update(NavigationElementModel navigationElement) {
        if (navigationElement.getElementType() ==  NavigationElementType.PAGE) {
            updateResourceAccessFields(navigationElement);
        }
        super.update(navigationElement);
        if (ConfigContainer.isAppInstalled()) {
            initializePermissions(navigationElement);
            NavigationElement nav = modelFactory.convertTo(NavigationElement.class, navigationElement);
            cacheProcessor.updateNavigation(nav);
        }
    }

    @Override
    @Transactional
    public void saveForGenerator(NavigationElementModel navigationElement) {//todo: implement batch save process to improve productivity of cache synchronization
        super.save(navigationElement, false);
    }

    @Override
    @Transactional
    public void delete(NavigationElementModel navigationElement) {
        List<PermissionModel> permissions = navigationElement.getPermissions();
        for (PermissionModel permission : permissions) {
            getPermissionStore().delete(permission);
        }
        super.delete(navigationElement);
        NavigationElement nav = modelFactory.convertTo(NavigationElement.class, navigationElement);
        cacheProcessor.removeNavigation(nav);
    }

    @Override
    public void updateResourceAccessFields(NavigationElementModel navigationElement) {//todo: check if cache update required here
        List<String> parentUrlPaths = new ArrayList<String>();
        NavigationElementModel navigation = null;

        List<RegistryNodeModel> parentRegistryNodes =
                ((RegistryNodeStore) getRegistryNodeStore()).findAllParentsForEntityLookup(navigationElement.getLookup());
        Collections.reverse(parentRegistryNodes);

        for (RegistryNodeModel parentRegistryNode : parentRegistryNodes) {
            parentRegistryNode = lazyInitializeIfNeed(parentRegistryNode);
            if (parentRegistryNode instanceof NavigationElementModel) {
                navigation = (NavigationElementModel) parentRegistryNode;
                break;
            }
        }
        if (navigation != null) {
            navigationElement.setServerName(navigation.getServerName());
            navigationElement.setParentPath(navigation.getParentPath());
            navigationElement.setProtocol(navigation.getProtocol());
            navigationElement.setPort(navigation.getPort());

            String urlPath = navigation.getUrlPath();
            urlPath = urlPath.startsWith("/") ? urlPath.substring(1) : urlPath;
            String[] urlPaths = urlPath.split("/");
            parentUrlPaths.addAll(Arrays.asList(urlPaths));
        }
        parentUrlPaths.add(StringUtils.normalize(navigationElement.getName()));
        String urlPathGenerated = "/" + StringUtils.join(parentUrlPaths.iterator(), "/");
        urlPathGenerated = urlPathGenerated.replaceAll("/+", "/");

        String pageUrl = urlPathGenerated;
        if (NavigationElementType.WORKFLOW.equals(navigationElement.getElementType())) {
            final Long entityId = navigationElement.getMain().getId();
            List<NavigationElementModel> entityNavigationElements = getHibernateTemplate().executeFind(new HibernateCallback() {
                public List<NavigationElementModel> doInHibernate(Session session) throws HibernateException, SQLException {
                    Criteria criteria = session.createCriteria(getClazz());
                    criteria.add(Restrictions.eq("main.id", entityId));
                    criteria.add(Restrictions.eq("elementType", NavigationElementType.PAGE));
                    return (List<NavigationElementModel>) criteria.list();
                }
            });
            if (entityNavigationElements.size() > 0) {
                NavigationElementModel entityNavigationElement = entityNavigationElements.get(0);
                pageUrl = entityNavigationElement.getPageUrl();
                navigationElement.setPageUrl(pageUrl);
            }
            if (StringUtils.isBlank(navigationElement.getUrlPath())) {
                navigationElement.setUrlPath(urlPathGenerated);
            }
        } else if (StringUtils.isBlank(navigationElement.getUrlPath()) && StringUtils.isBlank(navigationElement.getPageUrl())) {
            navigationElement.setUrlPath(urlPathGenerated);
            navigationElement.setPageUrl(pageUrl);
        }

        if (navigationElement.getId() != null) {
            List<NavigationElementModel> navigationElementModels = findAllByParentIdWithFilter(navigationElement.getId(), null);
            for (NavigationElementModel navigationElementModel : navigationElementModels) {
                updateResourceAccessFields(navigationElementModel);
                if (ConfigContainer.isAppInstalled()) {
                    initializePermissions(navigationElementModel);
                    NavigationElement nav = modelFactory.convertTo(NavigationElement.class, navigationElementModel);
                    cacheProcessor.updateNavigation(nav);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NavigationElementModel findByUID(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Empty UID parameter.");
        }
        Criteria criteria = getSession().createCriteria(getClazz());
        criteria.createAlias("uid", "uid");
        criteria.add(Restrictions.eq("uid.uid", uid));
        criteria.setFetchMode("permissions", FetchMode.JOIN);
        return (NavigationElementModel) criteria.uniqueResult();
    }

    private void initializePermissions(NavigationElementModel navElement) {
        List<PermissionModel> permissions = navElement.getPermissions();
        if (navElement.getPermissions() != null) {
            Hibernate.initialize(permissions);
            for (PermissionModel permission : navElement.getPermissions()) {
                Hibernate.initialize(permission);
            }
        }
    }

}
