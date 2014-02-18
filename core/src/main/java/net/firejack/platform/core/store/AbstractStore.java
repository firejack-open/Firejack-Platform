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

package net.firejack.platform.core.store;

import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.AbstractModel;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.InstantiationException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AbstractStore<E extends AbstractModel, ID extends Serializable> implements IAbstractStore<E, ID> {
	protected Log logger = LogFactory.getLog(getClass());

    private static final String[] SUPPORTED_DATETIME_FORMATS = {"dd/MM/yyyy", "yyyy-MM-dd'T'HH:mm:ss"};

	protected Class<E> clazz;

	@Autowired(required = false)
	@Qualifier("template")
	protected HibernateTemplate template;

	/**
	 * @return store's model class
	 */
	public Class<E> getClazz() {
		return clazz;
	}

	/**
	 * @param clazz store's model class
	 */
	public void setClazz(Class<E> clazz) {
		this.clazz = clazz;
	}

	public E instantiate() {
		try {
			return getClazz().newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	protected HibernateTemplate getHibernateTemplate() {
		return this.template;
	}

    public void setTemplate(HibernateTemplate template) {
        this.template = template;
    }

    protected Session getSession() {
		return template.getSessionFactory().getCurrentSession();
	}

	@Override
	@Transactional
	public void saveOrUpdate(E entity) {
		getHibernateTemplate().saveOrUpdate(entity);
		getHibernateTemplate().flush();
	}

	@Override
	@Transactional
	public void saveOrUpdateAll(List<E> entities) {
		getHibernateTemplate().saveOrUpdateAll(entities);
		getHibernateTemplate().flush();
	}

	@Override
	@Transactional
	public void update(E entity) {
		getHibernateTemplate().update(entity);
		getHibernateTemplate().flush();
	}

	@Override
	@Transactional(readOnly = true)
	public List<E> search(LinkedList<Criterion> criterions, Paging paging) {
		return search(criterions, null, paging);
	}

    @Override
    @Transactional(readOnly = true)
    public List<E> search(LinkedList<Criterion> criterions, Map<String, String> aliases, Paging paging) {
        return search(criterions, aliases, paging, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> search(LinkedList<Criterion> criterions, Map<String, String> aliases, Paging paging, boolean isOr) {
        return search(criterions, aliases, paging, isOr, false);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> searchWithProjection(LinkedList<Criterion> criterionList, Projection projection, Paging paging) {
        return searchWithProjection(criterionList, projection, null, paging);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> searchWithProjection(LinkedList<Criterion> criterionList, Projection projection,
                              Map<String, String> aliases, Paging paging) {
        return searchWithProjection(criterionList, projection, aliases, paging, true);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> searchWithProjection(final LinkedList<Criterion> criterionList, final Projection projection,
                              final Map<String, String> aliases, final Paging paging, final boolean isOr) {
        return searchWithProjection(criterionList, projection, aliases, paging, true, false);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> searchWithProjection(final LinkedList<Criterion> criterionList, final Projection projection,
                              final Map<String, String> aliases, final Paging paging, final boolean isOr, final boolean isLeft) {
        return getHibernateTemplate().execute(new HibernateCallback<List<T>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = prepareCriteria(session, criterionList, aliases, paging, isOr, isLeft);
                if (projection != null) {
                    criteria.setProjection(projection);
                }
                return criteria.list();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T searchUniqueWithProjection(LinkedList<Criterion> criterionList, Projection projection, Paging paging) {
        return searchUniqueWithProjection(criterionList, projection, null, paging);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T searchUniqueWithProjection(LinkedList<Criterion> criterionList, Projection projection, Map<String, String> aliases, Paging paging) {
        return searchUniqueWithProjection(criterionList, projection, aliases, paging, true);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T searchUniqueWithProjection(final LinkedList<Criterion> criterions, final Projection projection,
                              final Map<String, String> aliases, final Paging paging, final boolean isOr) {
        return getHibernateTemplate().execute(new HibernateCallback<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = prepareCriteria(session, criterions, aliases, paging, isOr, false);
                if (projection != null) {
                    criteria.setProjection(projection);
                }
                /*List<T> itemList = (List<T>) criteria.uniqueResult();
                return itemList == null || itemList.isEmpty() ? null : itemList.get(0);*/
                return (T) criteria.uniqueResult();
            }
        });
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

    protected Criteria createCriteriaForFilter(Session session, SpecifiedIdsFilter filter) {
        Criteria criteria = session.createCriteria(getClazz());
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

    protected Criterion createFilterCriterion(SpecifiedIdsFilter filter) {
        Criterion filterCriterion = null;
        if (filter != null) {
            if (filter.getAll() == Boolean.TRUE) {
                if (!filter.getUnnecessaryIds().isEmpty()) {
                    filterCriterion = Restrictions.not(Restrictions.in("id", filter.getUnnecessaryIds()));
                }
            } else {
                if (!filter.getNecessaryIds().isEmpty()) {
                    filterCriterion = Restrictions.in("id", filter.getNecessaryIds());
                }
            }
        }
        return filterCriterion;
    }

    public <ES> List<ES> findWithFilter(final List<Criterion> criterionList, final Map<String, String> aliases,
                                           final SpecifiedIdsFilter filter, final Paging paging) {
        return findWithFilter(criterionList, aliases, null, null, null, filter, paging);
    }

    @SuppressWarnings("unchecked")
    public <ES> List<ES> findWithFilter(final List<Criterion> criterions, final Map<String, String> aliases,
                                           final Projection projection, final List<String> fetchPaths,
                                           final Class<ES> projectionClazz, final SpecifiedIdsFilter filter, final Paging paging) {
        return getHibernateTemplate().execute(new HibernateCallback<List<ES>>() {
            public List<ES> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);
                if (paging != null) {
                    if (paging.getLimit() != null && paging.getLimit() > -1) {
                        criteria.setMaxResults(paging.getLimit());
                    }
                    if (paging.getOffset() != null && paging.getOffset() > -1) {
                        criteria.setFirstResult(paging.getOffset());
                    }
                    List<SortField> sortFields = paging.getSortFields();
                    if (sortFields != null && sortFields.size() > 0) {
                        for (SortField sortField : sortFields) {
                            if (StringUtils.isNotBlank(sortField.getSortColumn())) {
                                SortOrder sortDirection = sortField.getSortDirection();
                                Order order = sortDirection == null || sortDirection == SortOrder.ASC ?
                                        Order.asc(sortField.getSortColumn()) : Order.desc(sortField.getSortColumn());
                                criteria.addOrder(order);
                            }
                        }
                    }
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

                return (List<ES>) criteria.list();
            }
        });
    }

    @Override
	@Transactional(readOnly = true)
	public Integer searchCount(LinkedList<Criterion> criterions) {
		return searchCount(criterions, null);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer searchCount(LinkedList<Criterion> criterions, Map<String, String> aliases) {
		return searchCount(criterions, aliases, true);
	}

    @Override
    @Transactional(readOnly = true)
    public Integer searchCount(LinkedList<Criterion> criterions, Map<String, String> aliases, boolean isOr) {
        return searchCount(criterions, aliases, isOr, false);
    }

    @Override
	@Transactional(readOnly = true)
	public E findById(ID id) {
		return getHibernateTemplate().get(clazz, id);
	}

	@Override
	@Transactional
	public void deleteById(ID id) {
		E entity = findById(id);
		if (entity != null) {
			delete(entity);
		}
	}

	@Override
	@Transactional
	public void delete(E entity) {
		getHibernateTemplate().delete(entity);
	}

    @Transactional(readOnly = true)
    public List<E> simpleSearch(String term, List<String> fields, Paging paging, SpecifiedIdsFilter filter) {
        List<List<SearchQuery>> searchQueries = new ArrayList<List<SearchQuery>>();
        if (StringUtils.isNotBlank(term)) {
            term = URLDecoder.decode(term.trim()).trim();
            for (String field: fields) {
                List<SearchQuery> searchQueryList = new ArrayList<SearchQuery>();
                SearchQuery searchQuery = new SearchQuery();
                searchQuery.setField(field);
                searchQuery.setOperation(QueryOperation.LIKECS);
                searchQuery.setValue(term);
                searchQueryList.add(searchQuery);
                searchQueries.add(searchQueryList);
            }
        }

        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        Map<String, String> aliases = new LinkedHashMap<String, String>();

        parseAdvancedSearchRequest(searchQueries, criterions, aliases, paging, true);

        if (filter != null) {
            addFilterCriterion(criterions, filter);
        }

        return search(criterions, aliases, paging, true, true);
    }

    @Transactional(readOnly = true)
    public Integer simpleSearchCount(String term, List<String> fields, SpecifiedIdsFilter filter) {
        List<List<SearchQuery>> searchQueries = new ArrayList<List<SearchQuery>>();
        if (StringUtils.isNotBlank(term)) {
            term = URLDecoder.decode(term.trim()).trim();
            for (String field: fields) {
                List<SearchQuery> searchQueryList = new ArrayList<SearchQuery>();
                SearchQuery searchQuery = new SearchQuery();
                searchQuery.setField(field);
                searchQuery.setOperation(QueryOperation.LIKECS);
                searchQuery.setValue(term);
                searchQueryList.add(searchQuery);
                searchQueries.add(searchQueryList);
            }
        }

        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        Map<String, String> aliases = new LinkedHashMap<String, String>();

        parseAdvancedSearchRequest(searchQueries, criterions, aliases, null, true);

        if (filter != null) {
            addFilterCriterion(criterions, filter);
        }

        return searchCount(criterions, aliases, true, true);
    }

    @Transactional(readOnly = true)
    public List<E> advancedSearch(List<List<SearchQuery>> searchQueries, Paging paging) {
        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        Map<String, String> aliases = new LinkedHashMap<String, String>();

        parseAdvancedSearchRequest(searchQueries, criterions, aliases, paging);

        return search(criterions, aliases, paging, true, true);
    }

    @Transactional(readOnly = true)
    public Integer advancedSearchCount(List<List<SearchQuery>> searchQueries) {
        return advancedSearchCount(searchQueries, null);
    }

    @Override
    public List<E> advancedSearchWithIdsFilter(List<List<SearchQuery>> searchQueries, Paging paging, SpecifiedIdsFilter filter) {
        LinkedList<Criterion> criterionList = new LinkedList<Criterion>();
        Map<String, String> aliases = new LinkedHashMap<String, String>();

        Criterion filterCriterion = createFilterCriterion(filter);

        if (searchQueries == null || searchQueries.isEmpty()) {
            criterionList.add(filterCriterion);
        } else {
            for (List<SearchQuery> queries : searchQueries) {
                Criterion criterion = parseAdvancedSearchRequest(queries, aliases, false);
                if (criterion == null && filterCriterion != null) {
                    criterionList.add(filterCriterion);
                } else if (criterion != null) {
                    criterionList.add(filterCriterion == null ? criterion : Restrictions.and(filterCriterion, criterion));
                }
            }
        }

        if (paging != null) {
            List<SortField> sortFields = paging.getSortFields();
            for (SortField sortField : sortFields) {
                parseSorting(sortField, getClazz(), aliases);
            }
        }

        return search(criterionList, aliases, paging, true, true);
    }

    @Override
    public Integer advancedSearchCountWithIdsFilter(List<List<SearchQuery>> searchQueries, SpecifiedIdsFilter filter) {
        LinkedList<Criterion> criterionList = new LinkedList<Criterion>();
        Map<String, String> aliases = new LinkedHashMap<String, String>();

        Criterion filterCriterion = createFilterCriterion(filter);

        if (searchQueries == null || searchQueries.isEmpty()) {
            criterionList.add(filterCriterion);
        } else {
            for (List<SearchQuery> queries : searchQueries) {
                Criterion criterion = parseAdvancedSearchRequest(queries, aliases, false);
                if (criterion == null && filterCriterion != null) {
                    criterionList.add(filterCriterion);
                } else if (criterion != null) {
                    criterionList.add(filterCriterion == null ? criterion : Restrictions.and(filterCriterion, criterion));
                }
            }
        }

        return searchCount(criterionList, aliases, true, true);
    }

    @Transactional(readOnly = true)
    public List<E> advancedSearchWithProjection(List<List<SearchQuery>> searchQueries, Projection projection, Map<String, String> aliases, Paging paging) {
        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        if (aliases == null) {
            aliases = new HashMap<String, String>();
        }

        parseAdvancedSearchRequest(searchQueries, criterions, aliases, paging);

        return searchWithProjection(criterions, projection, aliases, paging, true);
    }

    @Transactional(readOnly = true)
    public Integer advancedSearchCount(List<List<SearchQuery>> searchQueries, Paging paging) {
        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        Map<String, String> aliases = new LinkedHashMap<String, String>();

        parseAdvancedSearchRequest(searchQueries, criterions, aliases, paging);

        return searchCount(criterions, aliases, true, true);
    }

    protected Criteria prepareCriteria(Session session, LinkedList<Criterion> criterionList,
                                       Map<String, String> aliases, Paging paging, boolean isOr, boolean isLeft) {
        Criteria criteria = session.createCriteria(clazz);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (aliases != null && !aliases.isEmpty()) {
            for (Map.Entry<String, String> alias : aliases.entrySet()) {
                criteria.createAlias(alias.getKey(), alias.getValue(),
                        isLeft ? CriteriaSpecification.LEFT_JOIN : CriteriaSpecification.INNER_JOIN);
            }
        }

        if (criterionList != null) {
            Criterion left = null;
            for (Criterion criterion : criterionList) {
                left = criterionList.getFirst() == criterion ?
                        criterion :
                        isOr ? Restrictions.or(left, criterion) : Restrictions.and(left, criterion);

            }
            if (left != null) criteria.add(left);
        }

        if (paging != null) {
            if (paging.getLimit() != null && paging.getLimit() > -1) {
                criteria.setMaxResults(paging.getLimit());
            }
            if (paging.getOffset() != null && paging.getOffset() > -1) {
                criteria.setFirstResult(paging.getOffset());
            }
            if (paging.getSortFields() != null) {
                for (SortField sortField : paging.getSortFields()) {
                    if (sortField.getSortDirection().equals(SortOrder.ASC)) {
                        criteria.addOrder(Order.asc(sortField.getSortColumn()));
                    } else {
                        criteria.addOrder(Order.desc(sortField.getSortColumn()));
                    }
                }
            }
        }
        return criteria;
    }

    protected List<E> search(final LinkedList<Criterion> criterions, final Map<String, String> aliases, final Paging paging, final boolean isOr, final boolean isLeft) {
        return getHibernateTemplate().execute(new HibernateCallback<List<E>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<E> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = prepareCriteria(session, criterions, aliases, paging, isOr, isLeft);
                return criteria.list();
            }
        });
    }

    protected Integer searchCount(final LinkedList<Criterion> criterions, final Map<String, String> aliases, final boolean isOr, final boolean isLeft) {
        return getHibernateTemplate().execute(new HibernateCallback<Integer>() {
            @Override
            public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(clazz);

                if (aliases != null && !aliases.isEmpty()) {
                    for (Map.Entry<String, String> alias : aliases.entrySet()) {
                        criteria.createAlias(alias.getKey(), alias.getValue(),
                                isLeft ? CriteriaSpecification.LEFT_JOIN : CriteriaSpecification.INNER_JOIN);
                    }
                }

                if (criterions != null) {
                    Criterion left = null;
                    for (Criterion criterion : criterions) {
                        left = criterions.getFirst() == criterion ?
                                criterion :
                                isOr ? Restrictions.or(left, criterion) : Restrictions.and(left, criterion);
                    }
                    if (left != null) criteria.add(left);
                }

                return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
            }
        });
    }

    protected void parseAdvancedSearchRequest(List<List<SearchQuery>> allSearchQueries, LinkedList<Criterion> criterions, Map<String, String> aliases, Paging paging) {
        parseAdvancedSearchRequest(allSearchQueries, criterions, aliases, paging, false);
    }

    protected void parseAdvancedSearchRequest(List<List<SearchQuery>> allSearchQueries, LinkedList<Criterion> criterions, Map<String, String> aliases, Paging paging, boolean skipNotValidValues) {
        for (List<SearchQuery> searchQueries : allSearchQueries) {
            Criterion criterion = parseAdvancedSearchRequest(searchQueries, aliases, skipNotValidValues);
            if (criterion != null) {
                criterions.add(criterion);
            }
        }

        if (paging != null) {
            List<SortField> sortFields = paging.getSortFields();
            for (SortField sortField : sortFields) {
	            parseSorting(sortField, getClazz(), aliases);
            }
        }
    }

	protected void findAlias(Class<?> clazz, String path, Map<String, String> aliases) {
		String[] fieldNames = path.split("\\.");
		if (fieldNames.length > 1) {
			for (int i = 0; i < fieldNames.length; i++) {
				String fieldName = fieldNames[i];
				PropertyDescriptor propertyDescriptor = ClassUtils.getPropertyDescriptor(clazz, fieldName);
				if (propertyDescriptor != null) {
					Method readMethod = propertyDescriptor.getReadMethod();
					if (readMethod != null) {
						Class<?> returnType = readMethod.getReturnType();
						if (Collection.class.isAssignableFrom(returnType)) {
							returnType = (Class<?>) ((ParameterizedTypeImpl) readMethod.getGenericReturnType()).getActualTypeArguments()[0];
						}
						if (AbstractModel.class.isAssignableFrom(returnType)) {
							clazz = returnType;
							if (i == 0) {
								aliases.put(fieldName, fieldName);
							} else {
								aliases.put(fieldNames[i - 1] + "." + fieldName, fieldName);
							}
						}
					} else {
						throw new BusinessFunctionException("The field '" + fieldName + "' has not read method in class '" + clazz + "'");
					}
				} else {
					throw new BusinessFunctionException("The field '" + fieldName + "' does not exist in class '" + clazz + "'");
				}
			}
		}
	}

    protected void parseSorting(SortField sortField, Class<?> clazz, Map<String, String> aliases) {
        String path = sortField.getSortColumn();
        findAlias(getClazz(), path, aliases);

        String[] fieldNames = path.split("\\.");
        int fieldNamesLength = fieldNames.length;
        if (fieldNamesLength > 2) {
            sortField.setSortColumn(fieldNames[fieldNamesLength - 2] + "." + fieldNames[fieldNamesLength - 1]);
        }
    }

	@SuppressWarnings("unchecked")
    protected Criterion parseAdvancedSearchRequest(List<SearchQuery> searchQueries, Map<String, String> aliases, boolean skipNotValidValues) {
        int index = 0;
        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        for (SearchQuery searchQuery : searchQueries) {
            Criterion criterion;
            String field = searchQuery.getField();
            if (field == null) {
                criterions.add(Restrictions.sqlRestriction("1 = 1"));
            } else {
                String[] fieldNames = field.split("\\.");
                if (fieldNames.length == 1) {
                    String fieldName = fieldNames[0];
                    PropertyDescriptor propertyDescriptor = ClassUtils.getPropertyDescriptor(getClazz(), fieldName);
                    if (propertyDescriptor != null) {
                        Method readMethod = propertyDescriptor.getReadMethod();
                        if (readMethod != null) {
                            Class<?> returnType = readMethod.getReturnType();
                            try {
                                criterion = getRestrictions(searchQuery, returnType);
                                criterions.add(criterion);
                            } catch (IllegalArgumentException e) {
                                if (!skipNotValidValues) {
                                    throw new BusinessFunctionException("The field '" + fieldName + "' has type '" + returnType.getName() + "', but value '" + searchQuery.getValue() + "' is incorrect");
                                }
                            }
                        } else {
                            throw new BusinessFunctionException("The field '" + fieldName + "' has not read method in class '" + getClazz().getName() + "'");
                        }
                    } else {
                        throw new BusinessFunctionException("The field '" + fieldName + "' does not exist in class '" + getClazz().getName() + "'");
                    }
                } else {
                    Class<E> aClass = getClazz();
                    String indexedFieldName = null;
                    for (int i = 0; i < fieldNames.length; i++) {
                        String fieldName = fieldNames[i];
                        PropertyDescriptor propertyDescriptor = ClassUtils.getPropertyDescriptor(aClass, fieldName);
                        if (propertyDescriptor != null) {
                            Method readMethod = propertyDescriptor.getReadMethod();
                            if (readMethod != null) {
                                Class<?> returnType = readMethod.getReturnType();
                                if (Collection.class.isAssignableFrom(returnType)) {
                                    returnType = (Class<?>) ((ParameterizedTypeImpl)readMethod.getGenericReturnType()).getActualTypeArguments()[0];
                                }
                                if (AbstractModel.class.isAssignableFrom(returnType)) {
                                    aClass = (Class) returnType;
                                    String alias = i == 0 ? fieldName : indexedFieldName + "." + fieldName;
                                    indexedFieldName = aliases.get(alias);
                                    if (indexedFieldName == null) {
                                        indexedFieldName = fieldName + index++;
                                        aliases.put(alias, indexedFieldName);
                                    }
                                } else {
                                    if (i == (fieldNames.length - 1)) {
                                        String queryFieldName = indexedFieldName + "." + fieldName;
                                        try {
                                            criterion = getRestrictions(new SearchQuery(queryFieldName, searchQuery.getOperation(), searchQuery.getValue()), returnType);
                                            criterions.add(criterion);
                                        } catch (IllegalArgumentException e) {
                                            if (!skipNotValidValues) {
                                                throw new BusinessFunctionException("The field '" + fieldName + "' has type '" + returnType.getName() + "', but value '" + searchQuery.getValue() + "' is incorrect");
                                            }
                                        }
                                    } else {
                                        throw new BusinessFunctionException("Field name: '" + fieldName + "' is not correct in query: '" + field + "'");
                                    }
                                }
                            } else {
                                throw new BusinessFunctionException("The field '" + fieldName + "' has not read method in class '" + aClass + "'");
                            }
                        } else {
                            throw new BusinessFunctionException("The field '" + fieldName + "' does not exist in class '" + aClass + "'");
                        }
                    }
                }
            }
        }

        Criterion andCriterion = null;
        for (Criterion criterion : criterions) {
            andCriterion = criterions.getFirst() == criterion ?
                    criterion :
                    Restrictions.and(andCriterion, criterion);
        }
        return andCriterion;
    }

    protected Criterion getRestrictions(SearchQuery query, Class<?> type) {
        Criterion criterion;
        Object value = query.getValue();
        QueryOperation operation = query.getOperation();
        if (value != null && !(
                        QueryOperation.FIELDEQUALS.equals(operation) ||
                        QueryOperation.FIELDNOTEQUALS.equals(operation) ||
                        QueryOperation.FIELDGREATERTHAN.equals(operation) ||
                        QueryOperation.FIELDLESSTHAN.equals(operation))) {
            if (value instanceof Collection) {
                Collection values = (Collection) value;
                if (Integer.class.equals(type)) {
                    List<Integer> list = new ArrayList<Integer>();
                    for (Object item : values) {
                        list.add(Integer.parseInt(item.toString()));
                    }
                    value = list;
                } else if (Long.class.equals(type)) {
                    List<Long> list = new ArrayList<Long>();
                    for (Object item : values) {
                        list.add(Long.parseLong(item.toString()));
                    }
                    value = list;
                } else if (java.sql.Date.class.equals(type) || Date.class.equals(type)) {
                    List<Date> list = new ArrayList<Date>();
                    for (Object item : values) {
                        Tuple<Date, QueryOperation> tuple = convertToDate(item, operation);
                        operation = tuple.getValue();
                        list.add(tuple.getKey());
                    }
                    value = list;
                } else if (Enum.class.isAssignableFrom(type)) {
                    List<Enum> enumValues = new ArrayList<Enum>(values.size());
                    for (Object item : values) {
                        Enum enumItem = prepareEnumFromSearchCriteria((Class<? extends Enum>)type, item);
                        enumValues.add(enumItem);
                    }
                    value = enumValues;
                }
            } else {
                if (Integer.class.equals(type)) {
                    value = Integer.parseInt(value.toString());
                } else if (Long.class.equals(type)) {
                    value = Long.parseLong(value.toString());
                } else if (Double.class.equals(type)) {
                    value = Double.parseDouble(value.toString());
                } else if (java.sql.Date.class.equals(type) || Date.class.equals(type)) {
                    Tuple<Date, QueryOperation> tuple = convertToDate(value, operation);
                    value = tuple.getKey();
                    operation = tuple.getValue();
                } else if (Enum.class.isAssignableFrom(type)) {
                    value = prepareEnumFromSearchCriteria((Class<? extends Enum>)type, value);
                }
            }
        }

        if (!String.class.equals(type) && (QueryOperation.LIKECS.equals(operation) ||
                                           QueryOperation.LIKECSFIRST.equals(operation) ||
                                           QueryOperation.LIKE.equals(operation) ||
                                           QueryOperation.LIKEFIRST.equals(operation))) {
            operation = QueryOperation.EQUALS;
        }

        switch (operation) {
            case LIKECS:
                criterion = Restrictions.like(query.getField(), "%" + value + "%");
                break;
            case LIKECSFIRST:
                criterion = Restrictions.like(query.getField(), value + "%");
                break;
            case LIKE:
                criterion = Restrictions.ilike(query.getField(), "%" + value + "%");
                break;
            case LIKEFIRST:
                criterion = Restrictions.ilike(query.getField(), value + "%");
                break;
            case EQUALS:
                criterion = Restrictions.eq(query.getField(), value);
                break;
            case LESSTHAN:
                criterion = Restrictions.lt(query.getField(), value);
                break;
            case GREATERTHAN:
                criterion = Restrictions.gt(query.getField(), value);
                break;
            case ISNULL:
                criterion = Restrictions.isNull(query.getField());
                break;
            case ISNOTNULL:
                criterion = Restrictions.isNotNull(query.getField());
                break;
            case ISEMPTY:
                criterion = Restrictions.isEmpty(query.getField());
                break;
            case ISNOTEMPTY:
                criterion = Restrictions.isNotEmpty(query.getField());
                break;
            case NOTEQUALS:
                criterion = Restrictions.ne(query.getField(), value);
                break;
            case IN:
                criterion = generateInRestriction(query.getField(), (Collection) value);
                break;
            case NOTIN:
                criterion = Restrictions.not(generateInRestriction(query.getField(), (Collection) value));
                break;
            case FIELDEQUALS:
                criterion = Restrictions.eqProperty(query.getField(), value != null ? value.toString() : "");
                break;
            case FIELDNOTEQUALS:
                criterion = Restrictions.neProperty(query.getField(), value != null ? value.toString() : "");
                break;
            case FIELDGREATERTHAN:
                criterion = Restrictions.gtProperty(query.getField(), value != null ? value.toString() : "");
                break;
            case FIELDLESSTHAN:
                criterion = Restrictions.ltProperty(query.getField(), value != null ? value.toString() : "");
                break;
            default:
                throw new RuntimeException("Operation " + query.getField() + " is not a valid operation");
        }
        return criterion;
    }

    private <T extends Enum<T>> T prepareEnumFromSearchCriteria(Class<T> type, Object item) {
        T enumItem;
        if (item instanceof String) {
            try {
                enumItem = Enum.valueOf(type, (String) item);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new IllegalArgumentException("Failed to convert enum value from the search criteria.");
            }
        } else if (item instanceof Enum) {
            enumItem = (T) item;
        } else {
            throw new IllegalArgumentException("Failed to convert enum value from the search criteria.");
        }
        return enumItem;
    }

    private Tuple<Date, QueryOperation> convertToDate(Object value, QueryOperation operation) {
        Date date = null;
        if (value instanceof Long) {
            date = new Date((Long) value);
        } else if (value instanceof String) {
            for (String dateFormat : SUPPORTED_DATETIME_FORMATS) {
                SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
                try {
                    date = formatter.parse((String) value);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Not supported date format: '" + value + "', " +
                            "should be: [" + StringUtils.join(SUPPORTED_DATETIME_FORMATS, ", ") + "].", e);
                }
            }
        } else if (value instanceof Integer && QueryOperation.PAST.equals(operation)) {
            Integer numberOfDays = Integer.parseInt(value.toString());
            numberOfDays++;  // required to account for today (which is not part of the past)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -numberOfDays);
            date = calendar.getTime();
            operation = QueryOperation.GREATERTHAN;
        } else if (value instanceof Integer && QueryOperation.OLDER.equals(operation)) {
            Integer numberOfDays = Integer.parseInt(value.toString());
            //Commenting next line of code, because search result is not quite correct. For instance, if user enters Older Than 0,
            //then user expects to get all records, including those which were created today.
            //numberOfDays++;  // required to account for today (which is not part of the past)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -numberOfDays);
            date = calendar.getTime();
            operation = QueryOperation.LESSTHAN;
        }
        return new Tuple<Date, QueryOperation>(date, operation);
    }

    protected Criterion generateInRestriction(String fieldName, Collection collection) {
        Criterion criterion = null;
        int size = collection.size();
        if (size <= 1000) {
            criterion = Restrictions.in(fieldName, collection);
        } else {
            List list = new ArrayList(collection);
            int iterations = (int) Math.ceil((double) size / 1000);
            for (int i = 0; i < iterations; i++) {
                int fromIndex = i * 1000;
                int toIndex = (size - (i * 1000))  > 1000 ? ((i + 1) * 1000) : size;
                List subList = list.subList(fromIndex, toIndex);
                if (i == 0) {
                    criterion = Restrictions.in(fieldName, subList);
                } else {
                    criterion = Restrictions.or(criterion, Restrictions.in(fieldName, subList));
                }
            }
        }
        return criterion;
    }

	@Override
	@Transactional(readOnly = true)
	public <I extends Serializable> Map<I, ID> searchKeys(String key, Collection<I> values) {
		if (!values.isEmpty()) {
			Criteria criteria = getSession().createCriteria(getClazz());
			criteria.add(generateInRestriction(key, values));
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property(key));
			projectionList.add(Projections.id());

			criteria.setProjection(projectionList);

			List<Object[]> list = (List<Object[]>) criteria.list();

			Map<I, ID> search = new HashMap<I, ID>(list.size());
			for (Object[] item : list) {
				search.put((I) item[0], (ID) item[1]);
			}
			return search;
		}
		return Collections.emptyMap();
	}

    protected LinkedList<Criterion> addFilterCriterion(LinkedList<Criterion> criterionList, SpecifiedIdsFilter filter) {
        LinkedList<Criterion> resultCriterionList;
        Criterion filterCriterion = createFilterCriterion(filter);
        if (criterionList.isEmpty()) {
            criterionList.add(filterCriterion);
            resultCriterionList = criterionList;
        } else {
            resultCriterionList = new LinkedList<Criterion>();
            Criterion criterion = null;
            for (Criterion cr : criterionList) {
                criterion = criterion == null ? cr : Restrictions.or(criterion, cr);
            }
            resultCriterionList.add(Restrictions.and(filterCriterion, criterion));
        }
        return resultCriterionList;
    }
}
