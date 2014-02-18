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

import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.domain.*;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.Factory;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.documentation.DocumentationEntryType;
import net.firejack.platform.generate.VelocityGenerator;
import net.firejack.platform.service.content.utils.ResourceProcessor;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import net.firejack.platform.web.security.model.context.ContextManager;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unused")
@Component("actionStore")
public class ActionStore extends RegistryNodeStore<ActionModel> implements IActionStore, IResourceAccessFieldsStore<ActionModel> {

    private static final String PARAM_TERM = "terms";
    private static final String PARAM_ID = "id";
    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_LIMIT = "limit";
    private static final String PARAM_PARENT_VALUE = "parentNodeValues";
    private static final String PARAM_SORT_COLUMN = "sortColumn";
    private static final String PARAM_SORT_DIRECTION = "sortDirection";

    private static final String PARAM_QUERY = "queryParameters";
    private static final String PARAM_SORT = "sortOrders";

    public static final String URL_PREFIX_REST = "/rest";
    public static final String URL_PREFIX_WS = "/ws";

    @Autowired
    private IActionParameterStore actionParameterStore;

    @Autowired
    private IPermissionStore permissionStore;

    @Autowired
    @Qualifier("packageStore")
    private IPackageStore packageStore;

    @Autowired
    @Qualifier("collectionStore")
    private ICollectionStore collectionStore;

    @Autowired
    @Qualifier("cacheProcessor")
    private ICacheDataProcessor cacheProcessor;

    @Autowired
    private Factory factory;

	@Autowired
	@Qualifier("registryNodeStore")
	private RegistryNodeStore registryNodeStore;

	@Autowired
    @Qualifier("resourceProcessor")
    private ResourceProcessor resourceProcessor;

    @Autowired
	protected VelocityGenerator generator;

    @Autowired
    private IRoleStore roleStore;

    @Autowired
    private IScheduleStore scheduleStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(ActionModel.class);
    }

	@Transactional
	public List<ActionModel> findByLookupForExample(String lookup) {
		List<ActionModel> result = new ArrayList<ActionModel>();

		LookupModel model = registryNodeStore.findByLookup(lookup, true);
		if (model != null) {
			if (model.getType().equals(RegistryNodeType.ACTION)) {
				result.add((ActionModel) model);
			} else {
				result = super.findAllByLikeLookupPrefix(model.getLookup());
			}

			for (ActionModel item : result) {
				Hibernate.initialize(item.getActionParameters());
				initParent(item);
			}
		}

		return result;
	}

	private void initParent(RegistryNodeModel model) {
		if (model != null) {
			Hibernate.initialize(model.getParent());
			initParent(model.getParent());
		}
	}

	@Override
    @Transactional(readOnly = true)
    public ActionModel findById(final Long id) {
        final ActionModel example = instantiate();
        ActionModel action = getHibernateTemplate().execute(new HibernateCallback<ActionModel>() {
            @Override
            public ActionModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(example.getClass());
                criteria.add(Restrictions.eq("id", id));
                criteria.setFetchMode("actionParameters", FetchMode.JOIN);
                criteria.setFetchMode("inputVOEntity", FetchMode.JOIN);
                criteria.setFetchMode("outputVOEntity", FetchMode.JOIN);
                return (ActionModel) criteria.uniqueResult();
            }
        });
        if (action != null) {
            Hibernate.initialize(action.getPermissions());
        }
        return action;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionModel> findAllWithFilterWithoutFields(SpecifiedIdsFilter<Long> filter) {
        List<ActionModel> actions = findAllWithFilter(filter);
        for (ActionModel action : actions) {
            action.setActionParameters(null);
        }
        return actions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionModel> findAllWithPermissions() {
        List<ActionModel> actionList = findAll();
        processParametersAndLoadPermissions(actionList);
        return actionList;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<ActionModel>> findAllWithPermissionsByPackage() {
        List<PackageModel> packageList = packageStore.findAll();
        Map<String, List<ActionModel>> actionsByPackage = new HashMap<String, List<ActionModel>>();
        if (packageList != null) {
            for (PackageModel p : packageList) {
                List<ActionModel> actions = findAllByLikeLookupPrefix(p.getLookup() + '.');
                if (actions != null) {
                    processParametersAndLoadPermissions(actions);
                    actionsByPackage.put(p.getLookup(), actions);
                }
            }
        }
        return actionsByPackage;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionModel> findAllWithPermissions(final String baseLookup) {
        List<ActionModel> actionList = getHibernateTemplate().execute(new HibernateCallback<List<ActionModel>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<ActionModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.like("lookup", baseLookup + ".%"));
                return criteria.list();
            }
        });
        processParametersAndLoadPermissions(actionList);
        return actionList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionModel> findAllByLikeLookupPrefix(String lookupPrefix) {
        List<ActionModel> actions = super.findAllByLikeLookupPrefix(lookupPrefix);
        for (ActionModel action : actions) {
            if (action.getInputVOEntity() != null) {
                Hibernate.initialize(action.getInputVOEntity());
            }
            if (action.getOutputVOEntity() != null) {
                Hibernate.initialize(action.getOutputVOEntity());
            }
            if (action.getActionParameters() != null) {
                Hibernate.initialize(action.getActionParameters());
                for (ActionParameterModel actionParameter : action.getActionParameters()) {
                    Hibernate.initialize(actionParameter);
                }
            }
        }
        return actions;
    }


    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<ActionModel> findAllByLikeLookupAndName(final String lookupPrefix, final String name) {
        if (StringUtils.isBlank(lookupPrefix)) {
            return Collections.emptyList();
        }

        List<ActionModel> actions = getHibernateTemplate().execute(new HibernateCallback<List<ActionModel>>() {
            public List<ActionModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.like("lookup", lookupPrefix + '%'));
                criteria.add(Restrictions.eq("name", name));
                return criteria.list();
            }
        });

        for (ActionModel action : actions) {
            if (action.getActionParameters() != null) {
                Hibernate.initialize(action.getActionParameters());
                for (ActionParameterModel actionParameter : action.getActionParameters()) {
                    Hibernate.initialize(actionParameter);
                }
            }
        }
        return actions;
    }

    @Override
    @Transactional(readOnly = true)
    public ActionModel findByUrlPath(final String urlPath, EntityProtocol protocol) {
        final ActionModel example = instantiate();
        example.setProtocol(protocol);
        /*List<Action> actionList = findAll();
                  urlPath = urlPath.toLowerCase();
                  Action urlAction = null;
                  if (actionList != null && !actionList.isEmpty()) {
                      for (Action action : actionList) {
                          String actionUrl = action.getUrlPath().toLowerCase();
                          if (urlPath.contains(actionUrl)) {
                              urlAction = action;
                              break;
                          }
                      }
                  }
                  return urlAction;*/
        return getHibernateTemplate().execute(new HibernateCallback<ActionModel>() {
            @Override
            public ActionModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(example.getClass());
                /*criteria.setFetchMode("actionParameters", FetchMode.JOIN);*/
                ActionModel resultAction = null;
                @SuppressWarnings("unchecked")
                List<ActionModel> candidateList = (List<ActionModel>) criteria.list();
                if (candidateList != null && !candidateList.isEmpty()) {
                    for (ActionModel action : candidateList) {
                        String actionUrl = action.getUrlPath().toLowerCase();
                        if (urlPath.contains(actionUrl)) {
                            resultAction = action;
                            break;
                        }
                    }
                }
                return resultAction;
            }
        });
    }

    @Override
    @Transactional
    public void save(ActionModel action) {
        saveWithGeneratingExamples(action, true);
        if (action.getId() == null) {
            PermissionModel permissionModel = permissionStore.findByLookup(action.getLookup());
            roleStore.addPermissionsToCurrentPackageRoles(action.getLookup(), ContextManager.getUserInfoProvider().getId(), Arrays.asList(permissionModel));
        }
    }

    @Override
    @Transactional
    public void saveWithGeneratingExamples(ActionModel action, boolean requiredToRegenerateExamples) {
        boolean isNew = action.getId() == null;
        List<ActionParameterModel> parameters = action.getActionParameters();
        action.setActionParameters(null);

        ActionModel oldAction = findById(action.getId());

        super.saveForGenerator(action);

	    updateActionPath(action);

	    if (ConfigContainer.isAppInstalled()) {
            Action actionInfo = factory.convertTo(Action.class, action);
            if (isNew) {
                cacheProcessor.addAction(actionInfo);
            } else {
                cacheProcessor.updateAction(actionInfo);
            }
        }

        List<ActionParameterModel> newActionParameters = new ArrayList<ActionParameterModel>();
        Map<Long, ActionParameterModel> restActionParameters = new HashMap<Long, ActionParameterModel>();
        if (parameters != null) {
            for (ActionParameterModel parameter : parameters) {
                if (parameter.getId() == null) {
                    newActionParameters.add(parameter);
                } else {
                    restActionParameters.put(parameter.getId(), parameter);
                }
            }
        }
        Map<ActionParameterModel, Boolean> parametersDescriptionsToProcess = new HashMap<ActionParameterModel, Boolean>();
        if (!newActionParameters.isEmpty()) {
            for (ActionParameterModel parameter : newActionParameters) {
                parametersDescriptionsToProcess.put(parameter, true);
            }
        }
        if (isNew) {
            permissionStore.createPermissionByAction(action);
        } else {
            List<ActionParameterModel> updateActionParameters = new ArrayList<ActionParameterModel>();
            List<ActionParameterModel> actionParametersWithUpdatedDescription = new ArrayList<ActionParameterModel>();
            List<ActionParameterModel> removeActionParameters = new ArrayList<ActionParameterModel>();
            List<ActionParameterModel> existActionParameters = actionParameterStore.findAllByActionId(action.getId());
            for (ActionParameterModel existActionParameter : existActionParameters) {
                ActionParameterModel updatedActionParameter = restActionParameters.get(existActionParameter.getId());
                if (updatedActionParameter != null) {
                    if (updatedActionParameter.getDescription() != null &&
                            !updatedActionParameter.getDescription().equals(existActionParameter.getDescription())) {
                        parametersDescriptionsToProcess.put(updatedActionParameter, null);
                    }
                    updateActionParameters.add(updatedActionParameter);
                } else {
                    parametersDescriptionsToProcess.put(existActionParameter, false);
                    removeActionParameters.add(existActionParameter);
                }
            }

            for (ActionParameterModel parameter : removeActionParameters) {
                actionParameterStore.deleteById(parameter.getId());
            }
            for (ActionParameterModel parameter : updateActionParameters) {
                parameter.setParent(action);
                actionParameterStore.merge(parameter);
            }
        }

        for (ActionParameterModel parameter : newActionParameters) {
            parameter.setParent(action);
            actionParameterStore.saveOrUpdate(parameter);
        }
        CollectionModel collection = null;

	    String description = action.getDescription();

	    if (StringUtils.isNotBlank(description)) {
		    collection = collectionStore.findOrCreateCollection(action);
		    getHtmlResourceStore().createDescription(action, collection);
	    }

	    if (requiredToRegenerateExamples) {
	        if (collection == null) {
	            collection = collectionStore.findOrCreateCollection(action);
	        }
            getHtmlResourceStore().createRestExample(action, collection);
            getHtmlResourceStore().createSoapExample(action, collection);
	    }

        if (!parametersDescriptionsToProcess.isEmpty()) {
            if (collection == null) {
                collection = collectionStore.findOrCreateCollection(action);
            }
            resourceProcessor.processActionParametersDescription(collection, parametersDescriptionsToProcess);
        }
    }

    @Override
    @Transactional
    public void saveForGenerator(ActionModel action) {
        if (action.getMethod() != null) {
            action.setProtocol(EntityProtocol.HTTP);
        }
        List<ActionParameterModel> parameters = action.getActionParameters();
        action.setActionParameters(null);

        super.saveForGenerator(action);

        if (parameters != null) {
            for (ActionParameterModel parameter : parameters) {
                parameter.setParent(action);
            }

            actionParameterStore.saveOrUpdateAll(parameters);
            action.setActionParameters(parameters);
        }
    }

    @Override
    @Transactional
    public void saveForGenerator(ActionModel action, Set<DocumentationEntryType> examplesToCreate) {//todo: refactor using batch updates to improve cache synchronization
        if (action.getMethod() != null) {
            action.setProtocol(EntityProtocol.HTTP);
        }
        List<ActionParameterModel> parameters = action.getActionParameters();
        action.setActionParameters(null);

        super.save(action);

        CollectionModel collection = collectionStore.findOrCreateCollection(action);

        if (parameters != null) {
            for (ActionParameterModel parameter : parameters) {
                parameter.setParent(action);
            }

            actionParameterStore.saveOrUpdateAll(parameters);
            action.setActionParameters(parameters);
        }

        if (examplesToCreate != null && !examplesToCreate.isEmpty()) {
            if (examplesToCreate.contains(DocumentationEntryType.REST_EXAMPLE)) {
                getHtmlResourceStore().createRestExample(action, collection);
            }
            if (examplesToCreate.contains(DocumentationEntryType.SOAP_EXAMPLE)) {
                getHtmlResourceStore().createSoapExample(action, collection);
            }
        }
    }

	@Override
	public void updateResourceAccessFields(ActionModel action) {
		action.setServerName(null);
		action.setParentPath(null);
		action.setPort(null);

		boolean urlPathWasNotSpecified = action.getUrlPath() == null;

		List<RegistryNodeModel> parentRegistryNodes = ((RegistryNodeStore) getRegistryNodeStore()).findAllParentsForEntityLookup(action.getLookup());
		for (RegistryNodeModel parentRegistryNode : parentRegistryNodes) {
            parentRegistryNode = lazyInitializeIfNeed(parentRegistryNode);
			if (parentRegistryNode instanceof PackageModel) {
				SystemModel system = ((PackageModel) parentRegistryNode).getSystem();
				action.setParentPath(((PackageModel) parentRegistryNode).getUrlPath());
				if (system != null) {
					action.setServerName(system.getServerName());
					action.setPort(system.getPort());
				}
			}
			if (urlPathWasNotSpecified && parentRegistryNode instanceof EntityModel) {
				updateActionPath(action);
				urlPathWasNotSpecified = false;
			}
		}
	}

    @Override
    @Transactional
    public ActionModel createWithPermissionByEntity(IEntity entity, RESTMethod restMethod) {
        ActionModel action = new ActionModel();
        action.setParent((RegistryNodeModel) entity);
        action.setChildCount(0);
        action.setName(restMethod.getActionName());
        action.setPath(entity.getLookup());
        action.setLookup(entity.getLookup() + '.' + StringUtils.normalize(restMethod.name()));
        action.setMethod(restMethod.getMethod());
        action.setServerName(entity.getServerName());
        action.setParentPath(entity.getParentPath());
        action.setPort(entity.getPort());

	    String capitalize = StringUtils.capitalize(entity.getName()).replaceAll("\\p{Space}","");
	    if (restMethod.equals(RESTMethod.READ_ALL)) {
		    action.setSoapMethod("readAll" + capitalize);
	    } else if (restMethod.equals(RESTMethod.ADVANCED_SEARCH)) {
		    action.setSoapMethod("advancedSearch" + capitalize);
	    } else {
		    action.setSoapMethod(restMethod.getActionName() + capitalize);
	    }

	    int index = 0;
	    if (restMethod == RESTMethod.READ || restMethod == RESTMethod.UPDATE || restMethod == RESTMethod.DELETE) {
		    ActionParameterModel parameter = new ActionParameterModel();
		    parameter.setParent(action);
		    parameter.setOrderPosition(index++);
		    parameter.setLocation(ParameterTransmissionType.PATH);
		    parameter.setName(PARAM_ID);
		    parameter.setFieldType(FieldType.NUMERIC_ID);

		    action.addActionParameters(parameter);
	    } else if (restMethod == RESTMethod.SEARCH) {
		    ActionParameterModel parameter = new ActionParameterModel();
		    parameter.setParent(action);
		    parameter.setOrderPosition(index++);
		    parameter.setLocation(ParameterTransmissionType.PATH);
		    parameter.setName(PARAM_TERM);
		    parameter.setFieldType(FieldType.SHORT_TEXT);

		    action.addActionParameters(parameter);
	    } else if (restMethod == RESTMethod.ADVANCED_SEARCH) {
		    ActionParameterModel parameter = new ActionParameterModel();
		    parameter.setParent(action);
		    parameter.setOrderPosition(index++);
		    parameter.setLocation(ParameterTransmissionType.QUERY);
		    parameter.setName(PARAM_QUERY);
		    parameter.setFieldType(FieldType.MEDIUM_TEXT);

		    action.addActionParameters(parameter);
	    }

	    if (restMethod == RESTMethod.READ_ALL || restMethod == RESTMethod.SEARCH || restMethod == RESTMethod.ADVANCED_SEARCH) {
		    ActionParameterModel parameter = new ActionParameterModel();
		    parameter.setParent(action);
		    parameter.setOrderPosition(index++);
		    parameter.setLocation(ParameterTransmissionType.QUERY);
		    parameter.setName(PARAM_OFFSET);
		    parameter.setFieldType(FieldType.INTEGER_NUMBER);

		    action.addActionParameters(parameter);

		    parameter = new ActionParameterModel();
		    parameter.setParent(action);
		    parameter.setOrderPosition(index++);
		    parameter.setLocation(ParameterTransmissionType.QUERY);
		    parameter.setName(PARAM_LIMIT);
		    parameter.setFieldType(FieldType.INTEGER_NUMBER);

		    action.addActionParameters(parameter);

		    parameter = new ActionParameterModel();
		    parameter.setParent(action);
		    parameter.setOrderPosition(index++);
		    parameter.setLocation(ParameterTransmissionType.QUERY);
		    if (restMethod == RESTMethod.ADVANCED_SEARCH) {
			    parameter.setName(PARAM_SORT);
			    parameter.setFieldType(FieldType.MEDIUM_TEXT);
		    } else {
			    parameter.setName(PARAM_SORT_COLUMN);
			    parameter.setFieldType(FieldType.SHORT_TEXT);
		    }

		    action.addActionParameters(parameter);

		    if (restMethod != RESTMethod.ADVANCED_SEARCH) {
			    parameter = new ActionParameterModel();
			    parameter.setParent(action);
			    parameter.setOrderPosition(index);
			    parameter.setLocation(ParameterTransmissionType.QUERY);
			    parameter.setName(PARAM_SORT_DIRECTION);
			    parameter.setFieldType(FieldType.SHORT_TEXT);

			    action.addActionParameters(parameter);
		    }
	    }

        if (entity.getType() == RegistryNodeType.BI_REPORT) {
            ActionParameterModel parameter = new ActionParameterModel();
            parameter.setParent(action);
            parameter.setOrderPosition(index);
            parameter.setLocation(ParameterTransmissionType.QUERY);
            parameter.setName(PARAM_PARENT_VALUE);
            parameter.setFieldType(FieldType.SHORT_TEXT);

            action.addActionParameters(parameter);
        }

        EntityProtocol protocol = entity.getProtocol();
        if (protocol == null) {
            protocol = EntityProtocol.HTTP;
        }
        action.setProtocol(protocol);
        action.setStatus(entity.getStatus());
        if (HTTPMethod.POST.equals(restMethod.getMethod()) || HTTPMethod.PUT.equals(restMethod.getMethod())) {
            action.setInputVOEntity((EntityModel) entity);
        }
        action.setOutputVOEntity((RegistryNodeModel)entity);

	    generateDescription(action, restMethod);

        saveWithGeneratingExamples(action, true);
        return action;
    }

	@Override
	@Transactional
	public void updateActionPath(ActionModel action) {
		action.setUrlPath(URL_PREFIX_REST + createRestUrl(action, null));
		action.setSoapUrlPath(URL_PREFIX_WS + createSOAPUrl(action.getParent()));
	}

	private String createRestUrl(RegistryNodeModel model, RegistryNodeModel child) {
		if (model != null && !model.getType().equals(RegistryNodeType.ROOT_DOMAIN)) {
			if (child != null && model.getType().equals(RegistryNodeType.ENTITY) && child.getType().equals(RegistryNodeType.ENTITY)) {
				//return createRestUrl(model.getParent(), model);
				return createRestUrl(model.getParent(), model) + "/" + StringUtils.normalize(model.getName());
			} else if (model.getParent().getType().equals(RegistryNodeType.PACKAGE) && model.getType().equals(RegistryNodeType.ENTITY)) {
				return "/" + StringUtils.normalize(model.getParent().getName()) + "/" + StringUtils.normalize(model.getName());
			} else if (model.getType().equals(RegistryNodeType.PACKAGE) && child.getType().equals(RegistryNodeType.ENTITY)) {
				return "/" + StringUtils.normalize(model.getName());
			} else if (model.getType().equals(RegistryNodeType.DOMAIN) || model.getType().equals(RegistryNodeType.ENTITY) || model.getType().equals(RegistryNodeType.REPORT) || model.getType().equals(RegistryNodeType.BI_REPORT) || (model.getType().equals(RegistryNodeType.ACTION) && !isSimply(model.getName()))) {
				return createRestUrl(model.getParent(), model) + "/" + StringUtils.normalize(model.getName());
			} else {
				return createRestUrl(model.getParent(), model);
			}
		}
		return "";
	}

	private boolean isSimply(String name) {
		return name.matches("create|update|read|read-all|delete");
	}

	private String createSOAPUrl(RegistryNodeModel model) {
		if (model != null && !model.getType().equals(RegistryNodeType.PACKAGE)) {
			if (model.getType().equals(RegistryNodeType.DOMAIN)) {
				return createSOAPUrl(model.getParent()) + "/" + StringUtils.normalize(model.getName());
			} else if(model.getType().equals(RegistryNodeType.ENTITY) && model.getParent().getType().equals(RegistryNodeType.PACKAGE)) {
				return "/" + StringUtils.normalize(model.getParent().getName());
			} else {
				return createSOAPUrl(model.getParent());
			}
		}
		return "";
	}

	private void generateDescription(ActionModel action, RESTMethod method) {
		String name = method.getActionName();
		String compose = generator.compose("/templates/actions/" + name + ".vsl", action.getParent());
		action.setDescription(compose);
	}

	@Override
    @Transactional
    public void delete(ActionModel action) {
        actionParameterStore.deleteByActionId(action.getId());
        List<PermissionModel> permissions = action.getPermissions();
        for (PermissionModel permission : permissions) {
            getPermissionStore().delete(permission);
        }
        scheduleStore.deleteByActionId(action.getId());
        super.delete(action);
        if (ConfigContainer.isAppInstalled()) {
            Action actionToDelete = factory.convertTo(Action.class, action);
            cacheProcessor.removeAction(actionToDelete);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ActionModel findByUID(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Empty UID parameter.");
        }
        Criteria criteria = getSession().createCriteria(getClazz());
        criteria.createAlias("uid", "uid");
        criteria.add(Restrictions.eq("uid.uid", uid));
        criteria.setFetchMode("actionParameters", FetchMode.JOIN);
        ActionModel action = (ActionModel) criteria.uniqueResult();
        if (action != null) {
            Hibernate.initialize(action.getPermissions());
            for (ActionParameterModel parameter : action.getActionParameters()) {
                Hibernate.initialize(parameter.getUid());
            }
        }
        return action;
    }

    @Override
    @Transactional
    public ActionModel deleteByUID(String uid) {
        ActionModel action = super.findByUID(uid);
        if (action != null) {
            deleteRecursively(action);
        }
        return action;
    }

    private void processParametersAndLoadPermissions(List<ActionModel> actionList) {
        if (actionList != null && !actionList.isEmpty()) {
            for (ActionModel action : actionList) {
                action.setActionParameters(null);
                if (action.getPermissions() != null) {
                    Hibernate.initialize(action.getPermissions());
                    for (PermissionModel permission : action.getPermissions()) {
                        Hibernate.initialize(permission);
                    }
                }
            }
        }
    }

}
