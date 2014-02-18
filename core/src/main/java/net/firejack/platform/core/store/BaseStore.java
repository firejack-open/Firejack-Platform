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

package net.firejack.platform.core.store;

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.SortOrder;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;


public class BaseStore<E extends BaseEntityModel, ID extends Serializable>  extends AbstractStore<E, ID> implements IStore<E, ID> {

    @Override
    @Transactional(readOnly = true)
    public E load(ID id) {
        return getHibernateTemplate().load(clazz, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAll() {
        return getHibernateTemplate().loadAll(clazz);
    }

	@Override
    @Transactional(readOnly = true)
    public int count(final List<Criterion> criterions, final SpecifiedIdsFilter filter) {
        return count(criterions, null, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public int count(final List<Criterion> criterions, final Map<String, String> aliases, final SpecifiedIdsFilter filter) {
        return getHibernateTemplate().execute(new HibernateCallback<Integer>() {
            @Override
            public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);

                if (aliases != null && !aliases.isEmpty()) {
                    for (Map.Entry<String, String> alias : aliases.entrySet()) {
                        criteria.createAlias(alias.getKey(), alias.getValue());
                    }
                }

                if (criterions != null && !criterions.isEmpty()) {
                    for (Criterion restrictions : criterions) {
                        criteria.add(restrictions);
                    }
                }

                ScrollableResults scroll = criteria.scroll();
                return scroll.last() ? scroll.getRowNumber() + 1 : 0;
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<E> findAllWithFilter(final SpecifiedIdsFilter filter) {
        return findAllWithFilter(null, filter);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<E> findAllWithFilter(Integer offset, Integer limit, List<Criterion> criterions, SpecifiedIdsFilter filter, Order... orders) {
        return findAllWithFilter(offset, limit, criterions, null, filter, null, null, null, orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAllWithFilter(List<Criterion> criterions, SpecifiedIdsFilter filter, Order... orders) {
        return findAllWithFilter(null, null, criterions, filter, orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAllWithFilter(List<Criterion> criterions, Map<String, String> aliases, SpecifiedIdsFilter filter, Order... orders) {
        return findAllWithFilter(null, null, criterions, aliases, filter, null, null, null, orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAllWithFilter(List<Criterion> criterions, SpecifiedIdsFilter filter, Paging paging) {
        Integer offset = null;
        Integer limit = null;
        if (paging != null) {
            offset = paging.getOffset();
            limit = paging.getLimit();
        }
        return findAllWithFilter(offset, limit, criterions, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAllWithFilter(Integer offset, Integer limit, List<Criterion> criterions, Map<String, String> aliases, SpecifiedIdsFilter filter, Order... orders) {
        return findAllWithFilter(offset, limit, criterions, aliases, filter, null, null, null, orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAllWithFilter(List<Criterion> criterions, Map<String, String> aliases, SpecifiedIdsFilter filter, Paging paging) {
        return findAllWithFilter(criterions, aliases, filter, null, paging);
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAllWithFilter(List<Criterion> criterions, Map<String, String> aliases, SpecifiedIdsFilter filter, List<String> fetchPaths, Paging paging) {
        Integer offset = null;
        Integer limit = null;
        Order order = null;
        if (paging != null) {
            if (paging.getLimit() != null && paging.getLimit() > -1) {
                limit = paging.getLimit();
            }
            if (paging.getOffset() != null && paging.getOffset() > -1) {
                offset = paging.getOffset();
            }
            if (StringUtils.isNotBlank(paging.getSortColumn())) {
                if (paging.getSortDirection().equals(SortOrder.ASC)) {
                    order = Order.asc(paging.getSortColumn());
                } else {
                    order = Order.desc(paging.getSortColumn());
                }
            }
        }
        return findAllWithFilter(offset, limit, criterions, aliases, filter, null, null, fetchPaths, order);
    }

    public <ES> List<ES> findAllWithFilter(final Integer offset, final Integer limit, final List<Criterion> criterions, final Map<String, String> aliases, final SpecifiedIdsFilter filter, final Projection projection, final Class<ES> projectionClazz, final List<String> fetchPaths, final Order... orders) {
        return getHibernateTemplate().execute(new HibernateCallback<List<ES>>() {
            public List<ES> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);
                if (limit != null && limit > -1) {
                    criteria.setMaxResults(limit);
                }
                if (offset != null && offset > -1) {
                    criteria.setFirstResult(offset);
                }

                if (aliases != null && !aliases.isEmpty()) {
                    for (Map.Entry<String, String> alias : aliases.entrySet()) {
                        criteria.createAlias(alias.getKey(), alias.getValue(), CriteriaSpecification.LEFT_JOIN);
                    }
                }

                if (criterions != null && !criterions.isEmpty()) {
                    for (Criterion restrictions : criterions) {
                        criteria.add(restrictions);
                    }
                }

                if (projection != null) {
                    criteria.setProjection(projection);
                }

                if (projectionClazz != null) {
                    ResultTransformer transformer = Transformers.aliasToBean(projectionClazz);
                    criteria.setResultTransformer(transformer);
                } else {
                    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                }

                if (fetchPaths != null) {
                    for (String fetchPath : fetchPaths) {
                        criteria.setFetchMode(fetchPath, FetchMode.JOIN);
                    }
                }

                if (orders != null) {
                    for (Order order : orders) {
                        if (order != null) {
                            criteria.addOrder(order);
                        }
                    }
                }

                return (List<ES>) criteria.list();
            }
        });
    }

    public Integer findCountWithFilter(final List<Criterion> criterions, final Map<String, String> aliases, final SpecifiedIdsFilter filter, final Projection projection) {
        return getHibernateTemplate().execute(new HibernateCallback<Integer>() {
            public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);

                if (aliases != null && !aliases.isEmpty()) {
                    for (Map.Entry<String, String> alias : aliases.entrySet()) {
                        criteria.createAlias(alias.getKey(), alias.getValue(), CriteriaSpecification.LEFT_JOIN);
                    }
                }

                if (criterions != null && !criterions.isEmpty()) {
                    for (Criterion restrictions : criterions) {
                        criteria.add(restrictions);
                    }
                }

                if (projection != null) {
                    criteria.setProjection(projection);
                }

                ScrollableResults scroll = criteria.scroll();
                return scroll.last() ? scroll.getRowNumber() + 1 : 0;
            }
        });
    }

    /**
     * Initializes entities from DB by IDs and the associated entities (specified by fetchPaths) for joins
     *
     * @param ids        - IDs of the entities to be initialized
     * @param fetchPaths - paths for the associated entities to be joined
     * @return list of initialized entities
     */
    public List<E> initializeByIds(List<ID> ids, String... fetchPaths) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        Criterion idsCriterion = Restrictions.in("id", ids);
        return findAllWithFilter(null, null, Arrays.asList(idsCriterion), null, null, null, null, Arrays.asList(fetchPaths));
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public int count(final String queryName, final boolean useScroll, final Object... params) throws DataAccessException {
        return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(queryName);
                setQueryParams(query, params);
                Integer count;
                if (useScroll) {
                    ScrollableResults scroll = query.scroll();
                    count = scroll.last() ? scroll.getRowNumber() + 1 : 0;
                } else {
                    count = ((Number) query.uniqueResult()).intValue();
                }
                return count;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<E> find(final Integer offset, final Integer limit, final String queryName, final Object... params)
            throws DataAccessException {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = getNamedQuery(session, queryName, offset, limit);
                setQueryParams(query, params);
                return (List<E>) query.list();
            }
        });
    }

    @Transactional(readOnly = true)
    public E findSingle(String queryName, Object... params) throws DataAccessException {
        List<E> result = find(null, 1, queryName, params);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    @Transactional(readOnly = true)
    public <T> int countByExample(final T example) {
        return countByExample(example, Collections.<String>emptyList());
    }

    /**
     * @param example
     * @param nullableAssociations
     * @return
     */
    @Transactional(readOnly = true)
    public <T> int countByExample(final T example, final List<String> nullableAssociations) {
        return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                List<String> nullableList = nullableAssociations == null ? Collections.<String>emptyList() : nullableAssociations;
                Criteria criteria = createCriteria(session, null, null, example, nullableList);
                criteria.setProjection(Projections.rowCount());
                @SuppressWarnings("unchecked")
                List<Number> result = criteria.list();
                if (result.isEmpty()) {
                    return 0;
                } else {
                    return result.get(0).intValue();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public <T> List<T> findByExampleObject(Integer offset, Integer limit, T example) {
        if (limit == null) {
            limit = -1;
        }
        if (offset == null) {
            offset = -1;
        }
        return getHibernateTemplate().findByExample(example, offset, limit);
    }

	/**
	 *
	 * @param offset
	 * @param limit
	 * @param queryName
	 * @param params
	 * @param <T>
	 * @return
	 */
    @Transactional(readOnly = true)
    public <T> List<T> findByQuery(final Integer offset, final Integer limit, final String queryName, final Object... params) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = getNamedQuery(session, queryName, offset, limit);
                setQueryParams(query, params);
                return (List<T>) query.list();
            }
        });
    }

    @Transactional(readOnly = true)
    protected List<E> findByQuery(final Integer offset, final Integer limit, final String queryStr, final Map queryParams, final ResultTransformer transformer) {
        return getHibernateTemplate().execute(new HibernateCallback<List<E>>() {
            public List<E> doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery(queryStr);
                if (offset != null && offset > -1) {
                    query.setFirstResult(offset);
                }
                if (limit != null && limit > -1) {
                    query.setMaxResults(limit);
                }
                query.setProperties(queryParams);
                query.setResultTransformer(transformer);
                return query.list();
            }
        });
    }

    @Transactional(readOnly = true)
    protected long countByQuery(final String queryStr, final Map queryParams) {
        return getHibernateTemplate().execute(new HibernateCallback<Long>() {
            public Long doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery(queryStr);
                query.setProperties(queryParams);
                return (Long)query.uniqueResult();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public E findByExample(final E example) {
        return getHibernateTemplate().execute(new HibernateCallback<E>() {
            public E doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteria(session, null, null, example);
                List results = criteria.list();
                return (E) (!results.isEmpty() ? results.get(0) : null);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public <T> List<T> findByExample(final Integer offset, final Integer limit, final T example) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteria(session, offset, limit, example);
                return new ArrayList<T>(criteria.list());
            }
        });
    }

	/**
	 *
	 * @param criterions
	 * @param aliases
	 * @param filter
	 * @return
	 */
    @Transactional(readOnly = true)
    public E findByCriteria(final List<Criterion> criterions, final Map<String, String> aliases, final SpecifiedIdsFilter filter) {
        return getHibernateTemplate().execute(new HibernateCallback<E>() {
            public E doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);

                if (aliases != null && !aliases.isEmpty()) {
                    for (Map.Entry<String, String> alias : aliases.entrySet()) {
                        criteria.createAlias(alias.getKey(), alias.getValue(), CriteriaSpecification.LEFT_JOIN);
                    }
                }

                if (criterions != null && !criterions.isEmpty()) {
                    for (Criterion restrictions : criterions) {
                        criteria.add(restrictions);
                    }
                }
                List results = criteria.list();
                return (E) (!results.isEmpty() ? results.get(0) : null);
            }
        });
    }

    @Override
    @Transactional
    public void deleteAll(List<E> entities) {
        getHibernateTemplate().deleteAll(entities);
    }

    @Override
    @Transactional
    public void lock(E entity) {
        getHibernateTemplate().lock(entity, LockMode.NONE);
    }

    @Override
    @Transactional
    public void refresh(E entity) {
        getHibernateTemplate().refresh(entity);
    }

    @Override
    @Transactional
    public void merge(E entity) {
        getHibernateTemplate().merge(entity);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected Criteria createCriteriaForFilter(Session session, SpecifiedIdsFilter filter) {
        Criteria criteria = session.createCriteria(getClazz());
        criteria.setFetchMode("parent", FetchMode.JOIN);
        if (filter != null) {
            if (filter.getAll() == Boolean.TRUE) {
                if (!filter.getUnnecessaryIds().isEmpty()) {
                    criteria.add(Restrictions.not(Restrictions.in("id", filter.getUnnecessaryIds())));
                }
            } else {
                if (!filter.getNecessaryIds().isEmpty()) {
                    criteria.add(Restrictions.in("id", filter.getNecessaryIds()));
                }
            }
        }
        return criteria;
    }

    protected Order createOrder(String sortColumn, SortOrder sortDirection) {
        Order order = null;
        if (sortColumn != null) {
            if (SortOrder.DESC.equals(sortDirection)) {
                order = Order.desc(sortColumn);
            } else {
                order = Order.asc(sortColumn);
            }
        }
        return order;
    }

    private Criteria createCriteria(Session session, Integer offset, Integer limit, Object example) {
        return createCriteria(session, offset, limit, example, Collections.<String>emptyList());
    }

    private Criteria createCriteria(Session session, Integer offset, Integer limit, Object example, List<String> nullableAssociations) {
        try {
            Class<?> exampleClass = example.getClass();

            Criteria criteria = session.createCriteria(exampleClass);
//			Map<String, Criteria> subcriterias = new HashMap<String, Criteria>();

            if (limit != null && limit > -1) {
                criteria.setMaxResults(limit);
            }
            if (offset != null && offset > -1) {
                criteria.setFirstResult(offset);
            }

            Example exampleQuery = createExample(example);
            criteria.add(exampleQuery);

            SessionFactory sessionFactory = getHibernateTemplate().getSessionFactory();
            ClassMetadata meta = sessionFactory.getClassMetadata(exampleClass);
            String[] names = meta.getPropertyNames();
            Type[] propertyTypes = meta.getPropertyTypes();
            for (int i = 0; i < propertyTypes.length; i++) {
                if (propertyTypes[i].isAssociationType() && !propertyTypes[i].isCollectionType()) {
                    String name = names[i];
                    Object value = PropertyUtils.getProperty(example, name);
                    if (value != null) {
                        Example subExample = createExample(value);
                        Criteria subcriteria = criteria.createCriteria(name);
//						subcriterias.put(name, subcriteria);
                        subcriteria.add(subExample);
                    } else if (nullableAssociations.contains(name)) {
                        criteria.add(Restrictions.isNull(name));
                    }
                } else if (propertyTypes[i].isCollectionType()) {
                    String name = names[i];
                    Collection values = (Collection) PropertyUtils.getProperty(example, name);
                    JoinTable joinTable = getMethodAnnotation(JoinTable.class, example, name);
                    if (values != null && values.size() > 0 && joinTable != null) {
                        Table table = getClassAnnotation(Table.class, example);
                        Enumerated enumerated = getMethodAnnotation(Enumerated.class, example, name);
                        Object obj = values.iterator().next();
                        if (obj.getClass().isEnum()) {
                            String sqlWhere = "{alias}.id IN (SELECT DISTINCT id_" + table.name() + " FROM " + joinTable.name() + " WHERE element IN (";
                            List<String> ordinals = new ArrayList<String>();
                            for (Object v : values) {
                                if (enumerated != null && EnumType.STRING.equals(enumerated.value())) {
                                    ordinals.add("'" + String.valueOf(((Enum) v).name()) + "'");
                                } else {
                                    ordinals.add(String.valueOf(((Enum) v).ordinal()));
                                }
                            }
                            String whereValues = StringUtils.join(ordinals.toArray(), ",");
                            sqlWhere = sqlWhere + whereValues + "))";
                            criteria.add(Restrictions.sqlRestriction(sqlWhere));
                        }
                    }
                }
            }
            return criteria;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Query getNamedQuery(Session session, String queryName, Integer offset, Integer limit) {
        Query query = session.getNamedQuery(queryName);
        if (offset != null && offset > -1) {
            query.setFirstResult(offset);
        }
        if (limit != null && limit > -1) {
            query.setMaxResults(limit);
        }
        return query;
    }

    protected void setQueryParams(Query namedQuery, Object... params) {
        for (int i = 0; i < params.length; i += 2) {
            String param = (String) params[i];
            Object value = params[i + 1];
            if (value instanceof Collection<?>) {
                Collection<?> values = (Collection<?>) value;
                namedQuery.setParameterList(param, values);

            } else {
                namedQuery.setParameter(param, value);
            }
        }
    }

    private Example createExample(Object value) {
        return Example.create(value).enableLike(MatchMode.ANYWHERE).ignoreCase().setPropertySelector(new Example.PropertySelector() {
            private static final long serialVersionUID = 1L;

            public boolean include(Object obj, String property, Type type) {
                boolean include = obj != null;
                if (include && (obj instanceof String)) {
                    include = !"".equals(obj);
                }
                if (include && (obj instanceof Number)) {
                    include = !new Long(0l).equals(((Number) obj).longValue());
                }
                // dates are searched by search pattern
                if (include && (obj instanceof Date)) {
                    include = false;
                }
                return include;
            }
        });
    }

    private <T extends Annotation> T getClassAnnotation(Class<T> clazz, Object example) {
        T annotation = null;
        try {
            annotation = example.getClass().getAnnotation(clazz);
        } catch (Exception e) {
            logger.warn("Can't find " + clazz.getName() + " annotation.", e);
        }
        return annotation;
    }

    private <T extends Annotation> T getMethodAnnotation(Class<T> clazz, Object example, String name) {
        T annotation = null;
        try {
            PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(example, name);
            Method method = PropertyUtils.getReadMethod(propertyDescriptor);
            annotation = method.getAnnotation(clazz);
        } catch (Exception e) {
            logger.warn("Can't find " + clazz.getName() + " annotation.", e);
        }
        return annotation;
    }

    public int update(final String queryName, final Object... params) {
        return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(queryName);
                setQueryParams(query, params);
                return query.executeUpdate();
            }
        });
    }

	public void evict (E model ) {
		getHibernateTemplate().evict(model);
	}

    protected boolean checkUniqueLookupException(DataIntegrityViolationException e) {
        return e.getCause() instanceof ConstraintViolationException &&
               ((ConstraintViolationException) e.getCause()).getSQL().contains("opf_lookup") &&
               ((ConstraintViolationException) e.getCause()).getSQLException().getMessage().startsWith("Duplicate entry");
    }

    protected void setPaging(Criteria criteria, final Paging paging, final Order... orders) {
        if (paging != null) {
            Integer limit = paging.getLimit();
            Integer offset = paging.getOffset();
            String sortColumn = paging.getSortColumn();
            SortOrder sortDirection = paging.getSortDirection();

            if (limit != null && limit > -1) {
                criteria.setMaxResults(limit);
            }
            if (offset != null && offset > -1) {
                criteria.setFirstResult(offset);
            }

            if(StringUtils.isNotEmpty(sortColumn)) {
                Order order;
                if( SortOrder.ASC.equals(sortDirection) ) {
                    order = Order.asc(sortColumn);
                } else {
                    order = Order.desc(sortColumn);
                }
                criteria.addOrder(order);
            }
        }

        if (orders != null) {
            for (Order o : orders) {
                if (o != null) {
                    criteria.addOrder(o);
                }
            }
        }
    }

    protected <T> T lazyInitializeIfNeed(T t) {
        if (t instanceof HibernateProxy) {
            LazyInitializer lazyInitializer = ((HibernateProxy) t).getHibernateLazyInitializer();
            if (lazyInitializer.isReadOnlySettingAvailable()) {
                t = (T) lazyInitializer.getImplementation();
            }
        }
        return t;
    }
}
