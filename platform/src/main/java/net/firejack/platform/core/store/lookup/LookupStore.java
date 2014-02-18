/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.store.lookup;

import com.sun.xml.txw2.IllegalAnnotationException;
import net.firejack.platform.core.annotation.Lookup;
import net.firejack.platform.core.annotation.PlaceHolder;
import net.firejack.platform.core.annotation.PlaceHolders;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.exception.LookupNotUniqueException;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.SearchModel;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.UID;
import net.firejack.platform.core.model.registry.ISortable;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import net.firejack.platform.core.store.BaseTreeStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IResourceAccessFieldsStore;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unchecked")
@Component("lookupStore")
public class LookupStore<E extends LookupModel, ID extends Serializable> extends BaseTreeStore<E, ID> implements ILookupStore<E, ID> {

    @Resource(name = "lookupTree")
    private Map<Class, LookupReference> map;
    @Resource(name = "placeHolders")
    private Map<String, Class> placeHolders;
    @Resource(name = "searchTables")
    private LinkedList<RegistryNodeType> searchTables;
    @Resource(name = "resourceAccessFieldsStories")
    private Map<RegistryNodeType, IResourceAccessFieldsStore<E>> resourceAccessFieldsStories;

    private IRegistryNodeStore<E> registryNodeStore;
    @Autowired
    private ICacheDataProcessor cacheProcessor;

    /**
     * @return
     */
    public IRegistryNodeStore<E> getRegistryNodeStore() {
        if (registryNodeStore == null) {
            registryNodeStore = OpenFlameSpringContext.getBean("registryNodeStore");
        }
        return registryNodeStore;
    }

    @PostConstruct
    private void init() throws ClassNotFoundException {
        if (map.isEmpty()) {
            map.clear();
            Map allClassMetadata = getHibernateTemplate().getSessionFactory().getAllClassMetadata();
            for (Object o : allClassMetadata.keySet()) {
                Class<?> entity = Class.forName((String) o);
                createTree(entity);
                findPlaceHolder(entity);
	            loadSearchTables(entity);
            }
        }
    }

	private void createTree(Class<?> entity) {
		Lookup lookup = entity.getAnnotation(Lookup.class);
		Table table = entity.getAnnotation(Table.class);
		DiscriminatorColumn column = entity.getAnnotation(DiscriminatorColumn.class);
		DiscriminatorValue discriminator = entity.getAnnotation(DiscriminatorValue.class);
		if (lookup != null && !map.containsKey(entity)) {
			Class<? extends LookupModel> clazz = lookup.parent();
			String suffix = lookup.suffix();
			String columnName = column != null ? column.name() : null;

			if (clazz.equals(LookupModel.class)) {
				map.put(entity, new LookupReference(clazz, table.name(), columnName, suffix));
				return;
			}
			if (!map.containsKey(clazz)) {
				if (!clazz.isAnnotationPresent(Lookup.class)) {
					throw new IllegalAnnotationException("Abstract refers to a non existent parent");
				}
				createTree(clazz);
			}

			LookupReference parent = map.get(clazz);
			if (parent.isDiscriminatorTable() && discriminator != null) {
				parent.addDiscriminator(discriminator.value(), suffix);
			} else {
				LookupReference child = new LookupReference(clazz, table.name(), columnName, suffix);
				parent.addChild(child);
				map.put(entity, child);
			}
		}
	}

    private void findPlaceHolder(Class<?> entity) {
        PlaceHolder placeHolder = entity.getAnnotation(PlaceHolder.class);
        PlaceHolders placeHolders = entity.getAnnotation(PlaceHolders.class);
        if (placeHolder != null || placeHolders != null) {
            String name = placeHolder != null ? placeHolder.key() : placeHolders.name();
            this.placeHolders.put(name, entity);
        }
    }

	private void loadSearchTables(Class<?> entity) {
		Table table = entity.getAnnotation(Table.class);
		if (table != null && isExtendedLookupModel(entity)) {
			RegistryNodeType type = RegistryNodeType.findByClass(entity);
			if (type != null && type != RegistryNodeType.ACTION_PARAMETER && type != RegistryNodeType.FIELD) {
				searchTables.add(type);
			}
		}
	}

	private boolean isExtendedLookupModel(Class<?> entity) {
		return !entity.equals(Object.class) && (entity.equals(LookupModel.class) || isExtendedLookupModel(entity.getSuperclass()));
	}

	@Override
    @Transactional(readOnly = true)
    public List<E> findAllByParentIdWithFilter(Long id, SpecifiedIdsFilter<Long> filter) {
        return findAllByParentIdWithFilter(id, filter, null, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAllByParentIdWithFilter(Long registryNodeId, SpecifiedIdsFilter<Long> filter, Paging paging) {
        Criteria criteria = createCriteriaForFilter(getSession(), filter);
        if (registryNodeId != null && registryNodeId != 0) {
            criteria.add(Restrictions.eq("parent.id", registryNodeId));
        }
        String sortColumn = paging.getSortColumn();
        boolean isSortable = getClazz().isAssignableFrom(ISortable.class);
        if (StringUtils.isBlank(sortColumn)) {
            if (isSortable) {
                criteria.addOrder(Order.asc("sortPosition"));
            }
            criteria.addOrder(Order.asc("name"));
        } else {
            boolean asc = paging.getSortDirection() == null || paging.getSortDirection() == SortOrder.ASC;
            criteria.addOrder(asc ? Order.asc(sortColumn) : Order.desc(sortColumn));
            if (isSortable) {
                criteria.addOrder(asc ? Order.asc("sortPosition") : Order.desc("sortPosition"));
            }
        }
        if (paging.getLimit() != null && paging.getLimit() > -1) {
            criteria.setMaxResults(paging.getLimit());
        }
        if (paging.getOffset() != null && paging.getOffset() > -1) {
            criteria.setFirstResult(paging.getOffset());
        }
        return (List<E>) criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAllByParentIdWithFilter(Long id, SpecifiedIdsFilter<Long> filter, Integer version) {
        return findAllByParentIdWithFilter(id, filter, version, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAllByParentIdWithFilter(Long id, SpecifiedIdsFilter<Long> filter, Boolean isSortable) {
        return findAllByParentIdWithFilter(id, filter, null, isSortable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAllByParentIdWithFilter(Long id, SpecifiedIdsFilter<Long> filter, Integer version, Boolean isSortable) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Map<String, String> aliases = new HashMap<String, String>();
        if (id != null && id != 0) {
            Criterion registryNodeIdCriterion = Restrictions.eq("parent.id", id);
            criterions.add(registryNodeIdCriterion);
        }
        Order[] orders;
        if (instantiate() instanceof ISortable || isSortable) {
            orders = new Order[2];
            orders[0] = Order.asc("sortPosition");
            orders[1] = Order.asc("name");
        } else {
            orders = new Order[1];
            orders[0] = Order.asc("name");
        }
        return findAllWithFilter(criterions, aliases, filter, orders);
    }

    @Override
    @Transactional(readOnly = true)
    public E findByLookup(String lookup) {
        return findByLookup(lookup, false);
    }

    @Override
    @Transactional(readOnly = true)
    public E findByLookup(final String lookup, final boolean withParent) {
        return getHibernateTemplate().execute(new HibernateCallback<E>() {
            public E doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.eq("lookup", lookup));
                if (withParent) {
                    criteria.setFetchMode("parent", FetchMode.JOIN);
                }
                return (E) criteria.uniqueResult();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAllByLookupList(final Collection<String> lookupList) {
        return getHibernateTemplate().execute(new HibernateCallback<List<E>>() {
            public List<E> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.in("lookup", lookupList));
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<E> findAllByLikeLookupPrefix(final String lookupPrefix) {
        if (StringUtils.isBlank(lookupPrefix)) {
            return Collections.emptyList();
        }
        return getHibernateTemplate().execute(new HibernateCallback<List<E>>() {
            public List<E> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.like("lookup", lookupPrefix + "%"));
                return (List<E>) criteria.list();
            }
        });
    }

    /**
     * @param entity
     */
    public void saveHash(E entity) {
        if (entity.getId() != null) {
            return;
        }

        String newLookup = findLookup(entity);
        net.firejack.platform.core.model.Lookup hash = entity.getHash();
        if (hash == null) {
            hash = new net.firejack.platform.core.model.Lookup();
            entity.setHash(hash);
        }
        entity.setLookup(newLookup);
        hash.setHash(generateHash(entity, newLookup));
    }

    /**
     * @param entities
     */
    public void saveHash(List<E> entities) {
        for (E entity : entities) {
            saveHash(entity);
        }
    }

    /**
     @Override
    @Transactional
    public void saveOrUpdate(O entity) {
        boolean isNew = entity != null && entity.getId() == null;
        super.saveOrUpdate(entity);
        if (isNew) {
            SecuredRecordModel sr = new SecuredRecordModel();
            sr.setExternalNumberId(entity.getId());
            sr.setRegistryNode(entity.);
            //securedRecordStore.saveOrUpdate();
        }
    }

    @Override
    @Transactional
    public void saveOrUpdateAll(List<O> entities) {
        super.saveOrUpdateAll(entities);
    }
     **/

    @Override
    @Transactional
    public void saveOrUpdate(E entity) {
        try {
            //boolean isNew = entity != null && entity.getId() == null;
            saveHash(entity);
            IResourceAccessFieldsStore<E> resourceAccessFieldsStore = resourceAccessFieldsStories.get(entity.getType());
            if (resourceAccessFieldsStore != null) {
                resourceAccessFieldsStore.updateResourceAccessFields(entity);
            }
            super.saveOrUpdate(entity);
            /*if (isNew) {
                RegistryNodeType registryNodeType = entity.getType();
                LookupModel parent = (LookupModel) entity.getParent();
                Long parentId = parent == null ? null : parent.getId();
                String parentPath = parent == null ? null : parent.getType().getEntityPath();
                OPFEngine.SecurityManagerService.createSecuredRecord(
                        entity.getId(), entity.getName(), registryNodeType.getEntityPath(), parentId, parentPath);
            }*/
        } catch (DataIntegrityViolationException e) {
            if (checkUniqueLookupException(e)) {
                String name = entity.getClass().getSimpleName();
                logger.error("Entity: " + name + " Lookup: " + entity.getLookup());
                throw new LookupNotUniqueException(name, entity.getLookup(), "validation.lookup.not.unique", entity.getLookup(), name);
            }
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void saveOrUpdateAll(List<E> entities) {
        try {
            saveHash(entities);
            super.saveOrUpdateAll(entities);
        } catch (DataIntegrityViolationException e) {
            if (checkUniqueLookupException(e)) {
                throw new LookupNotUniqueException("", "", "validation.lookup.not.unique", "", "");
            }
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void merge(E entity) {
        updateLookup(entity);
        IResourceAccessFieldsStore<E> resourceAccessFieldsStore = resourceAccessFieldsStories.get(entity.getType());
        if (resourceAccessFieldsStore != null) {
            resourceAccessFieldsStore.updateResourceAccessFields(lazyInitializeIfNeed(entity));
        }
        super.merge(entity);
    }

    private String generateHash(E entity, String lookup) {
        Class<? extends LookupModel> entityClass = entity.getClass();
        Lookup annotation = entityClass.getAnnotation(Lookup.class);
        if (annotation != null)
            lookup += annotation.suffix();

        return SecurityHelper.hash(lookup);
    }

    /**
     * @param baseEntity
     */
    public void updateLookup(E baseEntity) {
        if (baseEntity.getId() == null) {
            return;
        }

        final String newLookup = findLookup(baseEntity);

        Class<? extends LookupModel> clazz = baseEntity.getClass();

        final LookupReference tree = getLookupClass(clazz);
        if (tree == null) {
            throw new IllegalAccessError("Not found marker @Lookup base entity");
        }

        LookupModel entity;
        try {
            entity = getHibernateTemplate().get(clazz, baseEntity.getId());
        } catch (HibernateSystemException e) {
            entity = (LookupModel) getHibernateTemplate().get(clazz.getSuperclass(), baseEntity.getId());
        }

	    if (!entity.getLookup().equals(newLookup)) {
			baseEntity.setChildCount(entity.getChildCount());

            try {
                final LookupModel finalEntity = entity;
	            final boolean uuid = entity.getType() == RegistryNodeType.ROOT_DOMAIN || entity.getType() == RegistryNodeType.PACKAGE;
                getHibernateTemplate().execute(new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                    update(session, tree, finalEntity, uuid, newLookup);
	                    return 1;
                    }
                });
            } catch (DataIntegrityViolationException e) {
                if (checkUniqueLookupException(e)) {
                    throw new LookupNotUniqueException("", "", "validation.lookup.not.unique", "", "");
                }
                logger.error(e.getMessage(), e);
            }
	    }
        if (baseEntity.getChildCount() == null) {
            baseEntity.setChildCount(entity.getChildCount());
        }
	    baseEntity.setLookup(newLookup);
    }

	@Override
    @Transactional(readOnly = true)
    public List findPlaceHolderEntity(final String lookup, String filter) {
        final Class<?> clazz = placeHolders.get(filter.trim());
        if (clazz == null) return Collections.emptyList();

		final String prefix;
		if (filter.trim().equalsIgnoreCase("config")) {
			prefix = DiffUtils.path(lookup) + '.';
		} else {
			prefix = lookup;
		}

		try {
            return find(null, null, filter.trim() + ".load", "lookup", prefix);
        } catch (HibernateSystemException e) {
            return getHibernateTemplate().executeFind(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    Criteria criteria = session.createCriteria(clazz);
                    criteria.add(Restrictions.like("lookup", lookup , MatchMode.START));
                    return criteria.list();
                }
            });
        }
    }

	@Transactional(readOnly = true)
	public List<SearchModel> search(final String term, final String prefix, final RegistryNodeType type, final Paging paging) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public List<SearchModel> doInHibernate(Session session) throws HibernateException, SQLException {
				List<String> excludable = RegistryNodeType.excludable();

				boolean existPrefix = StringUtils.isNotEmpty(prefix);
				boolean existType = type != null;
				boolean existExcludable = !excludable.isEmpty();

				StringBuilder sql = buildSearchQuery(paging, existPrefix, existType, false, existExcludable);
				Query query = session.createSQLQuery(sql.toString());

				query.setParameter("term", "%" + term + "%");
				if (!excludable.isEmpty()) {
					query.setParameterList("excludable", excludable);
				}
				if (existPrefix) {
					query.setParameter("prefix", prefix + "%");
				}
				if (existType) {
					query.setParameter("type", type.getType());
				}
				if (paging != null && paging.getOffset() > 0) {
					query.setFirstResult(paging.getOffset());
				}
				if (paging != null && paging.getLimit() > 0) {
					query.setMaxResults(paging.getLimit());
				}

				List<SearchModel> list = new ArrayList<SearchModel>();
				List<Object[]> result = query.list();
				for (Object[] objects : result) {
					list.add(new SearchModel(objects));
				}
				return list;
			}
		});
	}

	@Transactional(readOnly = true)
	public Integer searchCount(final String term, final String prefix, final RegistryNodeType type) {
		return getHibernateTemplate().execute(new HibernateCallback<Integer>() {
			public Integer doInHibernate(Session session) throws HibernateException, SQLException {
				List<String> excludable = RegistryNodeType.excludable();

				boolean existPrefix = StringUtils.isNotEmpty(prefix);
				boolean existType = type != null;
				boolean existExcludable = !excludable.isEmpty();

				StringBuilder sql = buildSearchQuery(null, existPrefix, existType, true, existExcludable);
				Query query = session.createSQLQuery(sql.toString());

				query.setParameter("term", "%" + term + "%");
				if (existExcludable) {
					query.setParameterList("excludable", excludable);
				}
				if (existPrefix) {
					query.setParameter("prefix", prefix + "%");
				}
				if (existType) {
					query.setParameter("type", type.getType());
				}

				List<Number> result = query.list();
				if (result.isEmpty()) {
					return 0;
				} else {
					return result.get(0).intValue();
				}
			}
		});
	}

	private StringBuilder buildSearchQuery(Paging paging, boolean prefix, boolean type, boolean counting, boolean excludable) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		if (counting) {
			sql.append("count(search.id)");
		} else {
			sql.append("search.id, search.name, search.path, search.lookup, search.description, search.type");
		}
		sql.append(" FROM (\n");
		for (RegistryNodeType nodeType : searchTables) {
			sql.append("\tSELECT ").append("id, name, path, lookup, description");
			if (nodeType.equals(RegistryNodeType.REGISTRY_NODE)) {
				sql.append(", type");
			} else {
				sql.append(", \'").append(nodeType).append("\' AS type");
			}
			sql.append(" FROM ").append(nodeType.getTable());
			if (!searchTables.getLast().equals(nodeType)) {
				sql.append("\n\t\tUNION\n ");
			}
		}

		sql.append(" \n) AS search WHERE").append(" (search.name like :term OR search.lookup like :term OR search.description like :term)");
		if (excludable) {
			sql.append(" AND search.type not in (:excludable)");
		}
		if (type) {
			sql.append(" AND search.type = :type");
		}
		if (prefix) {
			sql.append(" AND search.lookup like :prefix");
		}
		if (!counting && paging != null && paging.getSortColumn() != null) {
			sql.append(" \nORDER BY ").append(paging.getSortColumn()).append(" ASC");
		}
		return sql;
	}

	private void update(Session session, LookupReference reference, LookupModel entity, boolean uuid, String newLookup) {
		StringBuilder sql = new StringBuilder("UPDATE ").append(reference.getTable()).append(" e, ");
		sql.append(net.firejack.platform.core.model.Lookup.class.getAnnotation(Table.class).name()).append(" h ");
		if (uuid) {
			sql.append(", ").append(UID.class.getAnnotation(Table.class).name()).append(" u ");
		}
		sql.append("SET   e.path = REPLACE(e.path, :lookup, :newLookup),");
		sql.append("   e.lookup = REPLACE(e.lookup, :lookup, :newLookup),");
		if (reference.isDiscriminatorTable() && reference.getDiscriminators() != null) {
			sql.append("   h.hash = CASE ");
			for (Map.Entry<String, String> entry : reference.getDiscriminators().entrySet()) {
				sql.append("      WHEN e.").append(reference.getColumn()).append(" = '").append(entry.getKey()).append("' THEN ");
				sql.append("MD5(CONCAT(REPLACE(e.lookup, :lookup, :newLookup),'").append(entry.getValue()).append("\'))");
			}
			sql.append("      ELSE");
			sql.append("          MD5(REPLACE(e.lookup, :lookup, :newLookup))");
			sql.append("      END ");
		} else {
			sql.append("   h.hash = MD5(CONCAT(REPLACE(e.lookup, :lookup, :newLookup),'").append(reference.getSuffix()).append("')) ");
		}
		if (uuid) {
			sql.append(",    u.uid =  uuid() ");
			sql.append("WHERE e.id_hash = h.id AND e.id_uid = u.id AND (e.lookup like CONCAT(:lookup, '.%') OR e.lookup = :lookup)");
		} else {
			sql.append("WHERE e.id_hash = h.id AND (e.lookup like CONCAT(:lookup, '.%') OR e.lookup = :lookup)");
		}

        Query query = session.createSQLQuery(sql.toString());
        query.setParameter("lookup", entity.getLookup());
        query.setParameter("newLookup", newLookup);
        query.executeUpdate();

        if (ConfigContainer.isAppInstalled() && entity instanceof EntityModel) {
            cacheProcessor.updateEntityLookup(entity.getLookup(), newLookup);
        }

        if (reference.getChildren() != null) {
            for (LookupReference stub : reference.getChildren()) {
                update(session, stub, entity, uuid, newLookup);
            }
        }
    }

	protected String findLookup(LookupModel entity) {
		String lookup;
		BaseEntityModel parent = entity.getParent();
		if (parent != null) {
			RegistryNodeModel node = (RegistryNodeModel) getRegistryNodeStore().findById(parent.getId());
			lookup = DiffUtils.lookup(node.getLookup(), entity.getName());
			entity.setPath(node.getLookup());
			entity.setParent(node);
		} else if (entity.getType().equals(RegistryNodeType.ROOT_DOMAIN)) {
			lookup = DiffUtils.rootDomainLookup(entity.getName());
			entity.setPath("");
		} else {
			throw new IllegalArgumentException("Parent was not found for: " + entity.getLookup());
		}

		return lookup;
	}

	private LookupReference getLookupClass(Class clazz) {
        LookupReference reference = map.get(clazz);
        if (reference == null && clazz != null) {
            return getLookupClass(clazz.getSuperclass());
        }
        return reference;
    }
}
