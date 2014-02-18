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

package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.NavigationElementType;
import net.firejack.platform.core.model.registry.ProcessType;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.process.*;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.*;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;


@TrackDetails
@Component
public class CreateWorkflowBroker extends ServiceBroker<ServiceRequest<Process>, ServiceResponse<Process>> {

    @Autowired
    private IPackageStore packageStore;
    @Autowired
    private INavigationElementStore navigationElementStore;
    @Autowired
    @Qualifier("processStore")
    private IProcessStore processStore;
    @Autowired
    private IEntityStore entityStore;
    @Autowired
    @Qualifier("registryNodeStore")
    protected IRegistryNodeStore<RegistryNodeModel> registryNodeStore;
    @Autowired
    private IRoleStore roleStore;
    @Autowired
    private IPermissionStore permissionStore;
    @Autowired
    private IUserStore userStore;

    private ThreadLocal<List<NavigationElementModel>> navElementsHolder =
            new InheritableThreadLocal<List<NavigationElementModel>>();

    @Override
    protected ServiceResponse<Process> perform(ServiceRequest<Process> request) throws Exception {
        Process process = request.getData();
        ServiceResponse<Process> response;
        if (process == null || process.getEntity() == null || process.getEntity().getId() == null) {
            response = new ServiceResponse<Process>("Wrong workflow configuration.", false);
        } else {
            EntityModel entityModel = entityStore.findById(process.getEntity().getId());
            if (entityModel == null) {
                response = new ServiceResponse<Process>("Workflow configuration error, - main entity information is incorrect.", false);
            } else {
                List<Activity> activities = process.getActivities();
                if (activities == null || activities.isEmpty()) {
                    response = new ServiceResponse<Process>("Workflow steps are not specified.", false);
                } else {
                    Map<String, StatusModel> statuses = new HashMap<String, StatusModel>();
                    Map<String, ActorModel> actorModelByName = new HashMap<String, ActorModel>();
                    Map<ActivityModel, List<ActivityActionModel>> activityActions = new HashMap<ActivityModel, List<ActivityActionModel>>();
                    Map<String, ActivityModel> activityByName = new HashMap<String, ActivityModel>();

                    int activityOrderPosition = 0;
                    for (Activity activity : activities) {
                        ActivityModel activityModel = new ActivityModel();
                        activityModel.setName(activity.getName());
                        activityModel.setDescription(activity.getDescription());
                        activityModel.setActivityType(ActivityType.HUMAN);
                        activityModel.setNotify(Boolean.FALSE);
                        activityModel.setSortPosition(activityOrderPosition++);
                        activityModel.setActivityOrder(activity.getActivityOrder());
                        activityModel.setActivityForm(activity.getActivityForm());

                        List<ActivityFieldModel> activityFieldModels = new ArrayList<ActivityFieldModel>();
                        for (ActivityField activityField : activity.getFields()) {
                            ActivityFieldModel activityFieldModel = factory.convertFrom(ActivityFieldModel.class, activityField);
                            activityFieldModel.setActivity(activityModel);
                            activityFieldModels.add(activityFieldModel);
                        }
                        activityModel.setFields(activityFieldModels);

                        Actor actor = activity.getActor();
                        ActorModel actorModel;
                        if (actor.getId() == null) {
                            actorModel = actorModelByName.get(actor.getName());
                            if (actorModel == null) {
                                actorModel = new ActorModel();
                                actorModel.setName(actor.getName());
                                actorModel.setDescription(actor.getDescription());

                                List<UserActor> userActors = actor.getUserActors();
                                List<UserActorModel> userActorModels = new ArrayList<UserActorModel>();
                                for (UserActor userActor : userActors) {
                                    UserModel userModel = new UserModel();
                                    userModel.setId(userActor.getUser().getId());
                                    UserActorModel userActorModel = new UserActorModel();
                                    userActorModel.setUser(userModel);
                                    userActorModels.add(userActorModel);
                                }

                                actorModel.setUserActors(userActorModels);
                                actorModelByName.put(actorModel.getName(), actorModel);
                            }
                        } else {
                            actorModel = new ActorModel();
                            actorModel.setId(actor.getId());
                        }
                        activityModel.setActor(actorModel);

                        activityByName.put(activity.getName(), activityModel);
                    }
                    for (Activity activity : activities) {
                        List<ActivityAction> actions = activity.getActivityActions();
                        ActivityModel activityFrom = activityByName.get(activity.getName());
                        List<ActivityActionModel> actionModels;
                        if (actions == null) {
                            actionModels = null;
                        } else {
                            actionModels = new ArrayList<ActivityActionModel>();
                            for (ActivityAction action : actions) {
                                Status status = action.getStatus();
                                StatusModel statusModel = statuses.get(status.getName());
                                if (statusModel == null) {
                                    statusModel = new StatusModel();
                                    statusModel.setName(status.getName());
                                    statusModel.setDescription(status.getDescription());

                                    statuses.put(statusModel.getName(), statusModel);
                                }
                                ActivityActionModel activityActionModel = new ActivityActionModel();
                                activityActionModel.setName(action.getName());
                                activityActionModel.setDescription(action.getDescription());
                                activityActionModel.setActivityFrom(activityFrom);
                                ActivityModel activityTo = activityByName.get(action.getActivityTo().getName());
                                if (StatusModel.STATUS_FINISHED.equals(statusModel.getName())) {
                                    activityTo.setActivityOrder(ActivityOrder.END);
                                }
                                activityActionModel.setActivityTo(activityTo);
                                activityActionModel.setStatus(statusModel);
                                actionModels.add(activityActionModel);
                            }
                        }
                        activityActions.put(activityFrom, actionModels);
                    }
                    StatusModel finalStatus = statuses.get(StatusModel.STATUS_FINISHED);
                    if (finalStatus == null) {
                        response = new ServiceResponse<Process>("Final Status is not set.", false);
                    } else {
                        StatusModel startStatus = statuses.get(StatusModel.STATUS_STARTED);
                        int statusOrderPosition = 1;
                        if (startStatus == null) {
                            startStatus = new StatusModel();
                            startStatus.setName(StatusModel.STATUS_STARTED);
                            startStatus.setSortPosition(statusOrderPosition++);
                            statuses.put(startStatus.getName(), startStatus);
                        }
                        for (StatusModel statusModel : statuses.values()) {
                            if (statusModel != startStatus && statusModel != finalStatus) {
                                statusModel.setSortPosition(statusOrderPosition++);
                            }
                        }
                        finalStatus.setSortPosition(statusOrderPosition);

                        ProcessModel processModel = factory.convertFrom(ProcessModel.class, process);
                        processModel.setMain(entityModel);
                        processModel.setActivities(null);

                        ProcessModel workflowModel = processStore.createWorkflow(
                                processModel, statuses, activityActions, actorModelByName);
                        Process result = factory.convertTo(Process.class, workflowModel);
                        String packageLookup = StringUtils.getPackageLookup(workflowModel.getLookup());
                        prepareNavigationElementsAndRoles(packageLookup);
                        response = new ServiceResponse<Process>(result, "Workflow created.", true);
                    }
                }
            }
        }
        return response;
    }

    private void prepareNavigationElementsAndRoles(String packageLookup) {
        PackageModel pkg = packageStore.findWithSystemByLookup(packageLookup);
        if (pkg != null) {
            navElementsHolder.set(new ArrayList<NavigationElementModel>());
            try {
                processNavigationTree(pkg);
                processRoles(pkg);
            } finally {
                navElementsHolder.remove();
            }

        }
    }

    private void processNavigationTree(PackageModel pkg) {
        List<NavigationElementModel> navigationElements = navElementsHolder.get();

        String lookup = DiffUtils.lookup(pkg.getLookup(), "gateway");
        NavigationElementModel gatewayNav = navigationElementStore.findByLookup(lookup);
        if (gatewayNav == null) {
            gatewayNav = new NavigationElementModel();
        }
        gatewayNav.setName(StringUtils.capitalize("Gateway"));
        gatewayNav.setDescription("This is an auto generated navigation element for code generation.");
        gatewayNav.setPath(pkg.getLookup());

        gatewayNav.setLookup(lookup);
        gatewayNav.setParent(pkg);
        gatewayNav.setParentPath("/" + pkg.getName());
        gatewayNav.setUrlPath("/");
        gatewayNav.setPageUrl("/home");
        SystemModel system = pkg.getSystem();
        if (system != null) {
            gatewayNav.setServerName(system.getServerName());
            gatewayNav.setProtocol(system.getProtocol());
            gatewayNav.setPort(system.getPort());
        }
        gatewayNav.setSortPosition(1);
        navigationElements.add(gatewayNav);

        prepareNavigationElements(pkg, gatewayNav, new Class[]{
                DomainModel.class,
                ProcessModel.class
        }, 5);

        for (NavigationElementModel element : navigationElements) {
            navigationElementStore.save(element);
        }

        /*List<NavigationElementModel> allNavigationElements = navigationElementStore.findAllByLikeLookupPrefix(
                gatewayNav.getLookup());
        for (NavigationElementModel navigationElement : allNavigationElements) {
            boolean isExist = false;
            for (NavigationElementModel correctNavigationElement : navigationElements) {
                if (correctNavigationElement.getLookup().equals(navigationElement.getLookup())) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                navigationElementStore.deleteRecursiveById(navigationElement.getId());
            }
        }*/
    }

    private void prepareNavigationElements(RegistryNodeModel parentRegistryNode, NavigationElementModel parentNavigationElement, Class[] registryNodeClasses, Integer shift) {
        List<RegistryNodeModel> registryNodeModels =
                registryNodeStore.findChildrenByParentIdAndTypes(parentRegistryNode.getId(), null, registryNodeClasses);
        for (int i = 0, registryNodeModelsSize = registryNodeModels.size(); i < registryNodeModelsSize; i++) {
            RegistryNodeModel registryNode = registryNodeModels.get(i);
            if (parentRegistryNode instanceof PackageModel || parentRegistryNode instanceof DomainModel) {
                if (registryNode instanceof ProcessModel) {
//                    if (ProcessType.CREATABLE.equals(((ProcessModel) registryNode).getProcessType())) {
                    EntityModel entityModel = (EntityModel) registryNode.getMain();
                    if (entityModel != null) {
                        if (!entityModel.getAbstractEntity() && !entityModel.getTypeEntity()) {
                            NavigationElementModel entityNavElement = createNavigationElement(registryNode.getName(), parentNavigationElement, i + shift);
                            entityNavElement.setElementType(NavigationElementType.WORKFLOW);
                            entityNavElement.setMain(entityModel);
                            entityNavElement.setUrlParams(registryNode.getLookup());
                            ProcessModel processModel = (ProcessModel) registryNode;
                            if (ProcessType.RELATIONAL.equals(processModel.getProcessType())) {
                                entityNavElement.setHidden(Boolean.TRUE);
                            }
                        }
                    }
//                    }
                } else {
                    NavigationElementModel navigationElement = createNavigationElement(registryNode.getName(), parentNavigationElement, i + shift);
                    prepareNavigationElements(registryNode, navigationElement, registryNodeClasses, 1);
                }
            }
        }
    }

    private NavigationElementModel createNavigationElement(String name, NavigationElementModel parentNavigationElement, Integer order) {
        String lookup = DiffUtils.lookup(parentNavigationElement.getLookup(), name);
        NavigationElementModel navigationElementModel = navigationElementStore.findByLookup(lookup);
        if (navigationElementModel == null) {
            navigationElementModel = new NavigationElementModel();
        }
        navigationElementModel.setName(StringUtils.capitalize(name));
        navigationElementModel.setDescription("This is an auto generated navigation element for code generation.");
        navigationElementModel.setPath(parentNavigationElement.getLookup());
        navigationElementModel.setLookup(lookup);
        navigationElementModel.setParent(parentNavigationElement);
        navigationElementModel.setParentPath(parentNavigationElement.getParentPath());
        navigationElementModel.setServerName(parentNavigationElement.getServerName());
        navigationElementModel.setProtocol(parentNavigationElement.getProtocol());
        navigationElementModel.setPort(parentNavigationElement.getPort());
        navigationElementModel.setElementType(NavigationElementType.PAGE);
        navigationElementModel.setSortPosition(order);

        List<NavigationElementModel> navigationElements = navElementsHolder.get();
        navigationElements.add(navigationElementModel);
        return navigationElementModel;
    }

    private RoleModel populateRole(String roleName, PackageModel pkg) {
        RoleModel roleModel = new RoleModel();
        roleModel.setName(roleName);
        roleModel.setDescription("This is an auto generated role for code generation.");
        roleModel.setParent(pkg);
        return roleModel;
    }

    private void processRoles(PackageModel pkg) {
        String packageLookup = pkg.getLookup();

        List<PermissionModel> guestPermissions = new ArrayList<PermissionModel>();
        List<PermissionModel> permissionModels = permissionStore.findAllBySearchTermWithFilter(packageLookup, null);

        Set<Long> permissionIdSet = new HashSet<Long>();
        for (PermissionModel permissionModel : permissionModels) {
            if (permissionModel.getLookup().equals(packageLookup + ".gateway.login") ||
                    permissionModel.getLookup().equals(packageLookup + ".gateway.forgot-password")) {
                guestPermissions.add(permissionModel);
            } else {
                permissionIdSet.add(permissionModel.getId());
            }
        }
        permissionModels.removeAll(guestPermissions);

        ///////////// Start> Populating/Updating admin role
        RoleModel adminRoleModel = roleStore.findByLookup(packageLookup + ".admin");
        if (adminRoleModel == null) {
            adminRoleModel = populateRole("admin", pkg);
        }
        saveIfNecessary(adminRoleModel, permissionModels, permissionIdSet);
        ///////////// End> Populating/Updating admin role

        ///////////// Start> Populating/Updating user role
        RoleModel userRole = roleStore.findByLookup(packageLookup + ".user");
        if (userRole == null) {
            userRole = populateRole("user", pkg);
        }
        saveIfNecessary(userRole, permissionModels, permissionIdSet);
        ///////////// End> Populating/Updating user role

        ///////////// Start> Populating/Updating guest role
        boolean updateIsRequired = false;
        RoleModel guestRoleModel = roleStore.findByLookup(packageLookup + ".guest");
        if (guestRoleModel == null) {
            guestRoleModel = populateRole("guest", pkg);
            guestRoleModel.setPermissions(guestPermissions);
            updateIsRequired = true;
        } else {
            List<PermissionModel> guestCurrentPermissions = guestRoleModel.getPermissions();
            if (guestCurrentPermissions == null || guestCurrentPermissions.isEmpty() ||
                    guestCurrentPermissions.size() != guestPermissions.size()) {
                updateIsRequired = true;
                guestRoleModel.setPermissions(guestPermissions);
            } else {
                Set<Long> idSet = new HashSet<Long>();
                for (PermissionModel guestPermissionModel : guestPermissions) {
                    idSet.add(guestPermissionModel.getId());
                }
                for (PermissionModel currentPermission : guestCurrentPermissions) {
                    if (!idSet.contains(currentPermission.getId())) {
                        updateIsRequired = true;
                        break;
                    }
                }

                if (updateIsRequired) {
                    guestPermissions.removeAll(guestCurrentPermissions);
                    guestCurrentPermissions.addAll(guestPermissions);
                }
            }
        }
        if (updateIsRequired) {
            roleStore.save(guestRoleModel);
        }
        ///////////// End> Populating/Updating guest role
        if (OPFContext.isInitialized()) {
            long adminId;
            try {
                IUserInfoProvider currentUserInfoProvider = ContextManager.getUserInfoProvider();
                adminId = currentUserInfoProvider.getId();
            } catch (ContextLookupException e) {
                UserModel admin = userStore.findUserByUsername("admin");
                adminId = admin.getId();
            }

            UserModel currentUser = userStore.findByIdWithRoles(adminId);
            UserRoleModel userRoleModel = new UserRoleModel(currentUser, adminRoleModel);
            currentUser.getUserRoles().add(userRoleModel);
            userStore.save(currentUser);
        }
    }

    private void saveIfNecessary(RoleModel roleModel, List<PermissionModel> permissionModels, Set<Long> permissionIdSet) {
        List<PermissionModel> currentPermissions = roleModel.getPermissions();
        boolean updateIsRequired = false;
        if (currentPermissions == null || currentPermissions.isEmpty() || currentPermissions.size() != permissionIdSet.size()) {
            updateIsRequired = true;
        } else {
            for (PermissionModel permissionModel : currentPermissions) {
                if (!permissionIdSet.contains(permissionModel.getId())) {
                    updateIsRequired = true;
                    break;
                }
            }
        }
        if (updateIsRequired) {
            roleModel.setPermissions(permissionModels);
            roleStore.save(roleModel);
        }
    }
}