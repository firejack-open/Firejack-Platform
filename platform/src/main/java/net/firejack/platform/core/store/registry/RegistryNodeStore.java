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

import net.firejack.platform.core.cache.RegistryNodeCacheService;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.TreeEntityModel;
import net.firejack.platform.core.model.registry.ISortable;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.model.user.UserProfileFieldModel;
import net.firejack.platform.core.store.lookup.LookupStore;
import net.firejack.platform.core.store.registry.resource.IHtmlResourceStore;
import net.firejack.platform.core.store.registry.resource.ITextResourceStore;
import net.firejack.platform.core.utils.ArrayUtils;
import net.firejack.platform.core.utils.OpenFlameSpringContext;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class RegistryNodeStore<R extends LookupModel> extends LookupStore<R, Long> implements IRegistryNodeStore<R> {

    @Autowired
    private RegistryNodeCacheService<R> cacheService;

    @Resource(name = "registryNodeStories")
    private Map<RegistryNodeType, IRegistryNodeStore<R>> registryNodeStories;

    private ITextResourceStore textResourceStore;
    private IHtmlResourceStore htmlResourceStore;

    @Autowired
    @Qualifier("progressAspect")
    private ManuallyProgress progress;

    /**
     * @return
     */
    public ITextResourceStore getTextResourceStore() {
        if (textResourceStore == null) {
            textResourceStore = (ITextResourceStore) OpenFlameSpringContext.getBean("textResourceStore");
        }
        return textResourceStore;
    }

    /**
     * @return
     */
    public IHtmlResourceStore getHtmlResourceStore() {
        if (htmlResourceStore == null) {
            htmlResourceStore = (IHtmlResourceStore) OpenFlameSpringContext.getBean("htmlResourceStore");
        }
        return htmlResourceStore;
    }



    /**
     * @param type
     * @return
     */
    public IRegistryNodeStore<R> getStoreByType(RegistryNodeType type) {
        IRegistryNodeStore<R> store = registryNodeStories.get(type);
        if (store == null) {
            store = getRegistryNodeStore();
        }
        return store;
    }

    /**
     * @return
     */
    public IPermissionStore getPermissionStore() {
        return (IPermissionStore) registryNodeStories.get(RegistryNodeType.PERMISSION);
    }

    /**
     * @return
     */
    public IRoleStore getRoleStore() {
        return (IRoleStore) registryNodeStories.get(RegistryNodeType.ROLE);
    }

    /**
     * @return
     */
    public IConfigStore getConfigStore() {
        return (IConfigStore) registryNodeStories.get(RegistryNodeType.CONFIG);
    }

	/**
     * @return
     */
    public RegistryNodeCacheService<R> getCacheService() {
        return cacheService;
    }

    @Override
    @Transactional(readOnly = true)
    public R findById(Long id) {
        R model = super.findById(id);
        if (model instanceof RegistryNodeModel) {
            Hibernate.initialize(((RegistryNodeModel) model).getMain());
        }
        return model;
    }

    @Override
    @Transactional(readOnly = true)
    public R findByIdWithParent(final Long id) {
        return getHibernateTemplate().execute(new HibernateCallback<R>() {
            @Override
            public R doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.eq("id", id));
                criteria.setFetchMode("parent", FetchMode.JOIN);
                return (R) criteria.uniqueResult();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findByIdsWithFilter(List<Long> ids, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdsCriterion = Restrictions.in("id", ids);
        criterions.add(registryNodeIdsCriterion);
        return findAllWithFilter(criterions, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findCountByParentIdsWithFilter(List<Long> ids, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdsCriterion = Restrictions.in("parent.id", ids);
        criterions.add(registryNodeIdsCriterion);
        return count(criterions, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findAllByParentIdsWithFilter(List<Long> ids, SpecifiedIdsFilter<Long> filter, Paging paging) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdsCriterion = Restrictions.in("parent.id", ids);
        criterions.add(registryNodeIdsCriterion);
        return findAllWithFilter(criterions, filter, paging);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findCountByLikeLookupWithFilter(String lookup, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion likeLookupCriterion = Restrictions.like("lookup", lookup + ".%");
        criterions.add(likeLookupCriterion);
        return count(criterions, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findAllByLikeLookupWithFilter(String lookup, SpecifiedIdsFilter<Long> filter, Paging paging) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion likeLookupCriterion = Restrictions.like("lookup", lookup + ".%");
        criterions.add(likeLookupCriterion);
        return findAllWithFilter(criterions, filter, paging);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<R> findAllParentsForEntityLookup(String lookup) {
        List<R> parentRegistryNodes = new ArrayList<R>();
        String[] lookupParts = lookup.split("\\.");
        for (int i = 2; i < lookupParts.length; i++) {
            String[] parentLookupParts = (String[]) ArrayUtils.subarray(lookupParts, 0, i);
            String parentLookup = StringUtils.join(parentLookupParts, ".");
            R parentRegistryNode = findByLookup(parentLookup);
            parentRegistryNodes.add(parentRegistryNode);
        }
//        Collections.reverse(parentRegistryNodes);
        return parentRegistryNodes;
    }

	@Override
	@Transactional(readOnly = true)
	public <T extends RegistryNodeModel> List<T> findAllParentsForLookup(String lookup, Class<?>... classes) {
		final LinkedList<String> lookups = new LinkedList<String>();
		String[] splits = lookup.split("(?=\\.)");
		String parent = "";
		for (String split : splits) {
			parent += split;
			lookups.add(parent);
		}
		lookups.removeFirst();
		lookups.removeLast();

		final String[] discriminators = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			DiscriminatorValue discriminator = classes[i].getAnnotation(DiscriminatorValue.class);
			discriminators[i] = discriminator.value();
		}

		return getHibernateTemplate().execute(new HibernateCallback<List<T>>() {
			public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(RegistryNodeModel.class, "rn");
				if (discriminators.length != 0) {
					criteria.add(Restrictions.in("rn.class", discriminators));
				}
				criteria.add(Restrictions.in("rn.lookup", lookups));
				return criteria.list();
			}
		});
	}

	@Override
	@Transactional(readOnly = true)
	public <T extends RegistryNodeModel> T findAllParentsById(Long id) {
		T child = (T) getHibernateTemplate().load(RegistryNodeModel.class, id);

		findAllParents(child);
		return child;
	}

	@Override
	@Transactional(readOnly = true)
	public <T extends LookupModel> void findAllParents(T entity) {
		final LinkedList<String> lookups = new LinkedList<String>();
		String[] splits = entity.getLookup().split("(?=\\.)");
		String parent = "";
		for (String split : splits) {
			parent += split;
			lookups.add(parent);
		}
		lookups.removeFirst();
		lookups.removeLast();

		List<T> parents = getHibernateTemplate().execute(new HibernateCallback<List<T>>() {
			public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(RegistryNodeModel.class);
				criteria.add(Restrictions.in("lookup", lookups));
				return criteria.list();
			}
		});

		if (parents.size() != 0) {
			entity.setParent(parents.get(parents.size() - 1));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> findAllDuplicateNamesByType(String _package, Class<?>... _class) {
		List<String> discriminators = new ArrayList<String>(_class.length);
		for (Class<?> _clas : _class) {
			DiscriminatorValue discriminator = _clas.getAnnotation(DiscriminatorValue.class);
			discriminators.add(discriminator.value());
		}

		Criteria criteria = getSession().createCriteria(getClazz());
		criteria.setProjection(Projections.projectionList().add(Projections.sqlGroupProjection("name as x", "x having count(x) > 1", new String[]{"x"}, new Type[]{Hibernate.STRING})));
		criteria.add(Restrictions.like("lookup", _package + ".%"));
		if (getClazz().isAnnotationPresent(DiscriminatorColumn.class) && !discriminators.isEmpty()) {
			criteria.add(Restrictions.in("class", discriminators));
		}
		return criteria.list();
	}

	@Override
	@Transactional(readOnly = true)
	public List<R> findAllDuplicateEntityByType(String _package, String name,Class<?>... _class) {
		List<String> discriminators = new ArrayList<String>(_class.length);
		for (Class<?> _clas : _class) {
			DiscriminatorValue discriminator = _clas.getAnnotation(DiscriminatorValue.class);
			discriminators.add(discriminator.value());
		}

		Criteria criteria = getSession().createCriteria(getClazz());
		criteria.add(Restrictions.like("lookup", _package + ".%"));
		criteria.add(Restrictions.eq("name", name));
		if (getClazz().isAnnotationPresent(DiscriminatorColumn.class) && !discriminators.isEmpty()) {
			criteria.add(Restrictions.in("class", discriminators));
		}

		List list = criteria.list();
		for (Object model : list) {
			findAllParents((LookupModel) model);
		}

		return list;
	}

	@Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<R> findChildrenByParentId(final Long registryNodeId, final SpecifiedIdsFilter<Long> filter) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);
                if (registryNodeId == null) {
                    criteria.add(Restrictions.isNull("parent"));
                } else {
                    criteria.add(Restrictions.eq("parent.id", registryNodeId));
                }
                return (List<RegistryNodeModel>) criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<R> findChildrenByParentIdAndTypes(final Long registryNodeId, final SpecifiedIdsFilter<Long> filter, final Class<?>... registryNodeClasses) {
	    List<String> discriminatorValues = new ArrayList<String>();
	    for (Class<?> registryNodeClass : registryNodeClasses) {
	        DiscriminatorValue value = registryNodeClass.getAnnotation(DiscriminatorValue.class);
	        if (value != null) {
	            discriminatorValues.add(value.value());
	        }
	    }
        return findChildrenByParentIdAndTypes(registryNodeId, discriminatorValues, filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<R> findChildrenByParentIdAndTypes(final Long registryNodeId, final List<String> discriminatorValues, final SpecifiedIdsFilter<Long> filter) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);
                if (registryNodeId == null) {
                    criteria.add(Restrictions.isNull("parent"));
                } else {
                    criteria.add(Restrictions.eq("parent.id", registryNodeId));
                }
                criteria.add(Restrictions.in("class", discriminatorValues));
                criteria.createAlias("main", "main", CriteriaSpecification.LEFT_JOIN);
                criteria.addOrder(Order.asc("sortPosition"));
                criteria.addOrder(Order.asc("name"));
                return (List<R>) criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<R> findAllByPrefixLookupAndTypes(final String lookup, final SpecifiedIdsFilter<Long> filter, final Class<?>... registryNodeClasses) {
	    final List<String> discriminatorValues = new ArrayList<String>();
	    for (Class<?> registryNodeClass : registryNodeClasses) {
	        DiscriminatorValue value = registryNodeClass.getAnnotation(DiscriminatorValue.class);
	        if (value != null) {
	            discriminatorValues.add(value.value());
	        }
	    }
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);
                if (StringUtils.isNotBlank(lookup)) {
                    criteria.add(Restrictions.like("lookup", lookup + ".%"));
                }
                criteria.add(Restrictions.in("class", discriminatorValues));
                criteria.addOrder(Order.asc("sortPosition"));
                criteria.addOrder(Order.asc("name"));
                return (List<R>) criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<R> findAllByLikeLookupWithFilter(final String lookup, final SpecifiedIdsFilter<Long> filter) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);
                criteria.add(Restrictions.like("lookup", lookup + ".%"));
                criteria.addOrder(Order.asc("sortPosition"));
                criteria.addOrder(Order.asc("name"));
                return (List<RegistryNodeModel>) criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<R> findAliasesById(final Long mainId, final SpecifiedIdsFilter<Long> filter) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);
                criteria.add(Restrictions.eq("main.id", mainId));
                return (List<RegistryNodeModel>) criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public R findByParentIdAndMainId(final Long parentId, final Long mainId) {
        return getHibernateTemplate().execute(new HibernateCallback<R>() {
            public R doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.eq("main.id", mainId));
                criteria.add(Restrictions.eq("parent.id", parentId));
                return (R) criteria.uniqueResult();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<R> findAllByMainId(final Long mainId) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public List<RegistryNodeModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.eq("main.id", mainId));
                return (List<RegistryNodeModel>) criteria.list();
            }
        });
    }

    @Override
    @Transactional
    public void save(R registryNode) {
        save(registryNode, true);
    }

    @Override
    @Transactional
    public void saveForGenerator(R registryNode) {
        save(registryNode, false);
    }

    @Override
    @Transactional
    public void save(R registryNode, boolean isCreateAutoDescription) {
        if (registryNode instanceof ISortable) {
            TreeEntityModel parentRegistryNode = registryNode.getParent();
            Integer position = ((ISortable) registryNode).getSortPosition();
            if (position == null || position == 0) {
                Integer maxSortPosition = findMaxOrderPosition(parentRegistryNode.getId());
                Integer sortPosition = maxSortPosition == null ? 1 : maxSortPosition + 1;
                ((ISortable) registryNode).setSortPosition(sortPosition);
            }
        }
        getCacheService().removeCacheByRegistryNode(registryNode);
        super.save(registryNode);

        if (isCreateAutoDescription) {
            getHtmlResourceStore().createAutoDescription((RegistryNodeModel) registryNode);
        }

        getPermissionStore().updateParent(registryNode.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findMaxOrderPosition(final Long parentId) {
        if (instantiate() instanceof RegistryNodeModel) {
            return getHibernateTemplate().execute(new HibernateCallback<Integer>() {
                public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                    Criteria criteria = session.createCriteria(getClazz());
                    criteria.add(Restrictions.eq("parent.id", parentId));
                    criteria.setProjection(Projections.max("sortPosition"));
                    return (Integer) criteria.uniqueResult();
                }
            });
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void saveWithoutUpdateChildren(R registryNode) {
        getCacheService().removeCacheByRegistryNode(registryNode);
        super.save(registryNode);
    }

    @Override
    @Transactional
    public void deleteRecursiveById(Long registryNodeId) {
        R registryNode = getRegistryNodeStore().findById(registryNodeId);
        if (registryNode != null) {
            deleteRecursively(registryNode);
        }
    }

    @Override
    @Transactional
    public void deleteRecursively(R registryNode) {
        List<R> childrenRegistryNodes = getRegistryNodeStore().findChildrenByParentId(registryNode.getId(), null);
        for (R childRegistryNode : childrenRegistryNodes) {
	        if (childRegistryNode.getType() != RegistryNodeType.RELATIONSHIP) {
		        deleteRecursively(childRegistryNode);
	        }
        }
        IRegistryNodeStore<R> store = getStoreByType(registryNode.getType());
	    registryNode = lazyInitializeIfNeed(registryNode);
        progress.status("Deleting object: " + registryNode.getLookup(), 1);
        store.delete(registryNode);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional
    public void delete(R registryNode) {
	    getCacheService().removeCacheByRegistryNode(registryNode);
	    if (registryNode instanceof RegistryNodeModel) {
		    getPermissionStore().deleteAllByRegistryNodeId(registryNode.getId());
		    getRoleStore().deleteAllByRegistryNodeId(registryNode.getId());
		    getConfigStore().deleteAllByRegistryNodeId(registryNode.getId());
            //processing user profile field deletion
            SimpleExpression criterion = Restrictions.eq("parent.id", registryNode.getId());
            Criteria criteria = getSession().createCriteria(UserProfileFieldModel.class);
            criteria.add(criterion);
            List<UserProfileFieldModel> profileFields = (List<UserProfileFieldModel>) criteria.list();
            if (profileFields != null && !profileFields.isEmpty()) {
                getHibernateTemplate().deleteAll(profileFields);
            }

            criteria = getSession().createCriteria(UserProfileFieldGroupModel.class);
            criteria.add(criterion);
            List<UserProfileFieldGroupModel> profileFieldGroupList = (List<UserProfileFieldGroupModel>) criteria.list();
            if (profileFieldGroupList != null && !profileFieldGroupList.isEmpty()) {
                getHibernateTemplate().deleteAll(profileFieldGroupList);
            }
	    }
	    super.delete(registryNode);
    }

    @Override
    @Transactional(readOnly = true)
    public String findRegistryNodeRefPath(Long registryNodeId) {
        StringBuilder sb = new StringBuilder();
        RegistryNodeModel registryNode = getHibernateTemplate().get(RegistryNodeModel.class, registryNodeId);
        if (registryNode != null && registryNode.getParent() != null) {
            RegistryNodeModel rn = getHibernateTemplate().get(
                    RegistryNodeModel.class, registryNode.getParent().getId());
            if (rn != null) {
                String rnName = checkRootDomainName(rn);
                sb.append(rnName);
                while (rn.getParent() != null) {
                    rn = getHibernateTemplate().get(RegistryNodeModel.class, rn.getParent().getId());
                    if (rn != null) {
                        rnName = checkRootDomainName(rn);
                        sb.insert(0, rnName).insert(rnName.length(), ".");
                    } else {
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public String findRegistryNodeRef(Long registryNodeId) {
        StringBuilder sb = new StringBuilder();
        RegistryNodeModel registryNode = getHibernateTemplate().get(RegistryNodeModel.class, registryNodeId);
        if (registryNode != null) {
            String rnName = checkRootDomainName(registryNode);
            sb.append(rnName);
            while (registryNode.getParent() != null) {
                registryNode = getHibernateTemplate().get(RegistryNodeModel.class, registryNode.getParent().getId());
                if (registryNode != null) {
                    rnName = checkRootDomainName(registryNode);
                    sb.insert(0, rnName).insert(rnName.length(), ".");
                } else {
                    break;
                }
            }
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public void movePosition(Long registryNodeId, Long newRegistryNodeParentId, Long oldRegistryNodeParentId, Integer position) {
        R movedRegistryNode = findById(registryNodeId);
        if (movedRegistryNode instanceof ISortable) {
            if (newRegistryNodeParentId.equals(oldRegistryNodeParentId)) {
                R newRegistryNodeParent = findById(newRegistryNodeParentId);
                List<R> registryNodesForNewParent = findAllByParentIdWithFilter(newRegistryNodeParentId, null, true);
                List<R> registryNodes = new ArrayList<R>();
                for (R registryNode : registryNodesForNewParent) {
                    if (registryNode.getId().equals(movedRegistryNode.getId())) {
                        continue;
                    }
                    registryNode = lazyInitializeIfNeed(registryNode);
                    if (registryNode instanceof ISortable) {
                        registryNodes.add(registryNode);
                    }
                }
                int newSortPosition = 1;
                boolean isLast = true;
                for (int i = 0; i < registryNodes.size(); i++) {
                    if (i == position) {
                        ((ISortable) movedRegistryNode).setSortPosition(newSortPosition);
                        newSortPosition++;
                        isLast = false;
                    }
                    R registryNode = registryNodes.get(i);
                    ((ISortable) registryNode).setSortPosition(newSortPosition);
                    newSortPosition++;
                }
                if (isLast) {
                    ((ISortable) movedRegistryNode).setSortPosition(newSortPosition);
                }
                movedRegistryNode.setParent(newRegistryNodeParent);
                registryNodes.add(movedRegistryNode);
                updateAll(registryNodes);
            } else {
                R newRegistryNodeParent = findById(newRegistryNodeParentId);
                List<R> registryNodesForNewParent = findAllByParentIdWithFilter(newRegistryNodeParentId, null, true);
                List<R> registryNodes = new ArrayList<R>();
                int newSortPosition = 1;
                boolean isLast = true;
                for (R registryNode : registryNodesForNewParent) {
                    registryNode = lazyInitializeIfNeed(registryNode);
                    if (registryNode instanceof ISortable) {
                        if (newSortPosition == position + 1) {
                            ((ISortable) movedRegistryNode).setSortPosition(newSortPosition);
                            newSortPosition++;
                            isLast = false;
                        }
                        ((ISortable) registryNode).setSortPosition(newSortPosition);
                        registryNodes.add(registryNode);
                        newSortPosition++;
                    }
                }
                if (isLast) {
                    ((ISortable) movedRegistryNode).setSortPosition(newSortPosition);
                }
                movedRegistryNode.setParent(newRegistryNodeParent);

                IRegistryNodeStore<R> store = getStoreByType(movedRegistryNode.getType());
                store.save(movedRegistryNode);

                updateAll(registryNodes);

                List<R> registryNodesForOldParent = findAllByParentIdWithFilter(oldRegistryNodeParentId, null, true);
                registryNodes = new ArrayList<R>();
                newSortPosition = 1;
                for (R registryNode : registryNodesForOldParent) {
                    registryNode = lazyInitializeIfNeed(registryNode);
                    if (registryNode instanceof ISortable) {
                        if (registryNode.getId().equals(movedRegistryNode.getId())) {
                            continue;
                        }
                        ((ISortable) registryNode).setSortPosition(newSortPosition);
                        registryNodes.add(registryNode);
                        newSortPosition++;
                    }
                }
                updateAll(registryNodes);
            }
        } else {
            R newRegistryNodeParent = findById(newRegistryNodeParentId);
            movedRegistryNode.setParent(newRegistryNodeParent);
//            IRegistryNodeStore<R> store = getStoreByType(movedRegistryNode.getType());
//            store.save(movedRegistryNode);
            save(movedRegistryNode);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findExternalCountByLookup(String lookup) {
        return count("RegistryNode.findExternalCountByLookup", false, "lookup", lookup);
    }

	@Override
	@Transactional(readOnly = true)
	public List<R> findAllByName(String _package, String name) {
		Criteria criteria = getSession().createCriteria(getClazz());
		criteria.add(Restrictions.like("lookup", _package, MatchMode.START));
		criteria.add(Restrictions.ilike("name", name));
		return (List<R>) criteria.list();
	}

	private void updateAll(List<R> registryNodes) {
        for (R registryNode : registryNodes) {
            IRegistryNodeStore<R> store = getStoreByType(registryNode.getType());
            store.update(registryNode);
        }
    }

    private String checkRootDomainName(RegistryNodeModel rootDomainCandidate) {
        String rnName;
        if (rootDomainCandidate.getType() == RegistryNodeType.ROOT_DOMAIN) {
            rnName = rootDomainCandidate.getName();
            String[] firstLevelDomains = rnName.split("\\.");
            if (firstLevelDomains.length != 2) {
                throw new OpenFlameRuntimeException("Wrong format of root domain's name.");
            }
            rnName = firstLevelDomains[1] + "." + firstLevelDomains[0];
        } else {
            rnName = rootDomainCandidate.getName();
        }
        return rnName;
    }
}
