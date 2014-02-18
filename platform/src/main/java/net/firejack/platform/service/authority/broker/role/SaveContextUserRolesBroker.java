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

package net.firejack.platform.service.authority.broker.role;


import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.BaseUser;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IPermissionStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.registry.ISecuredRecordStore;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.store.user.UserStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@TrackDetails
public class SaveContextUserRolesBroker extends ServiceBroker<ServiceRequest<NamedValues<Object>>, ServiceResponse<UserRole>> {

    public static final String PARAM_USER_ROLES = "PARAM_USER_ROLES";
    public static final String PARAM_SYNC_ROLES = "PARAM_SYNC_ROLES";

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IPermissionStore permissionStore;
    @Autowired
    private UserStore userStore;
    @Autowired
    private IRoleStore roleStore;
    @Autowired
    private IUserRoleStore userRoleStore;
    @Autowired
    private ISecuredRecordStore securedRecordsStore;

    @Override
    protected ServiceResponse<UserRole> perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        @SuppressWarnings("unchecked")
        List<UserRole> userRoles = (List<UserRole>) request.getData().get(PARAM_USER_ROLES);
        Boolean syncRoles = (Boolean) request.getData().get(PARAM_SYNC_ROLES);

        ServiceResponse<UserRole> response;
        if (userRoles == null || userRoles.isEmpty()) {
            response = new ServiceResponse<UserRole>("No user role information is specified.", false);
        } else {
            syncRoles = syncRoles == null ? Boolean.TRUE : syncRoles;
            String typeLookup = userRoles.get(0).getTypeLookup();
            EntityModel entityModel = entityStore.findByLookup(typeLookup);
            if (entityModel == null) {
                response = new ServiceResponse<UserRole>("Role context is unknown.", false);
            } else {
                LinkedList<String> lookupList = new LinkedList<String>();
                for (UserRole userRole : userRoles) {
                    String roleLookup = userRole.getRole().getLookup();
                    lookupList.add(roleLookup);
                }
                /*Map<String, String> aliases = new HashMap<String, String>();
                aliases.put("permissions", "perms");
                LinkedList<Criterion> restrictions = new LinkedList<Criterion>();
                restrictions.add(Restrictions.in("lookup", lookupList));
                List<RoleModel> roleModelList = roleStore.search(restrictions, aliases, null);*/
                List<RoleModel> roleModelList = roleStore.findContextRolesByLookupListWithPermissions(lookupList);
                Map<String, RoleModel> rolesByLookup = new HashMap<String, RoleModel>();
                for (RoleModel role : roleModelList) {
                    rolesByLookup.put(role.getLookup(), role);
                }
                LinkedList<Criterion> restrictions = new LinkedList<Criterion>();
                if (syncRoles) {
                    for (UserRole userRole : userRoles) {
                        Role role = userRole.getRole();
                        RoleModel roleModel = rolesByLookup.get(role.getLookup());
                        if (roleModel == null) {
                            roleModel = new RoleModel();
                            roleModel.setName(role.getName());
                            roleModel.setPath(role.getPath());
                            roleModel.setLookup(role.getLookup());
                            roleModel.setParent(entityModel);
                            lookupList.clear();
                            if (role.getPermissions() != null) {
                                for (Permission permission : role.getPermissions()) {
                                    lookupList.add(permission.getLookup());
                                }
                            }
                            updateRolePermissions(restrictions, lookupList, roleModel);
                            rolesByLookup.put(roleModel.getLookup(), roleModel);
                        } else {
                            List<Permission> permissions = role.getPermissions();
                            List<PermissionModel> permissionModels = roleModel.getPermissions();
                            boolean permissionsEmpty = permissions == null || permissions.isEmpty();
                            boolean permissionModelsEmpty = permissionModels == null || permissionModels.isEmpty();
                            if (permissionsEmpty && !permissionModelsEmpty) {
                                roleModel.setPermissions(null);
                                roleStore.save(roleModel);
                            } else if (permissionModelsEmpty && !permissionsEmpty) {
                                lookupList.clear();
                                for (Permission permission : permissions) {
                                    lookupList.add(permission.getLookup());
                                }
                                updateRolePermissions(restrictions, lookupList, roleModel);
                            } else if (!permissionsEmpty) {
                                Set<String> permissionLookupSet = new HashSet<String>();
                                for (Permission permission : permissions) {
                                    permissionLookupSet.add(permission.getLookup());
                                }
                                Set<String> permissionModelLookupSet = new HashSet<String>();
                                for (PermissionModel permissionModel : permissionModels) {
                                    permissionModelLookupSet.add(permissionModel.getLookup());
                                }
                                if (!permissionLookupSet.containsAll(permissionModelLookupSet) ||
                                        !permissionModelLookupSet.containsAll(permissionLookupSet)) {
                                    updateRolePermissions(restrictions, permissionLookupSet, roleModel);
                                }
                            }
                        }
                    }
                }


                List<UserRole> userRolesToGrant = new ArrayList<UserRole>();
                List<UserRole> userRolesToReduce = new ArrayList<UserRole>();
                for (UserRole userRole : userRoles) {
                    Boolean reduceRole = userRole.getReduced();
                    if (reduceRole == null || !reduceRole) {
                        userRolesToGrant.add(userRole);
                    } else {
                        userRolesToReduce.add(userRole);
                    }
                }

                for (UserRole roleToReduce : userRolesToReduce) {
                    UserRoleModel userRoleModel = populateRestrictions(rolesByLookup, restrictions, roleToReduce);
                    if (userRoleModel != null) {
                        List<UserRoleModel> roles = userRoleStore.search(restrictions, null, null, false);
                        if (roles.isEmpty()) {
                            logger.info("Database does not have user role to reduce for {user[id = " + roleToReduce.getId() +
                                    "], type[" + typeLookup + "], ID[" + (roleToReduce.getModelId() == null ?
                                    roleToReduce.getComplexPK() : roleToReduce.getModelId()) + "]}");
                        } else {
                            userRoleStore.deleteAll(roles);
                        }
                    }
                }

                List<UserRoleModel> userRoleModelsCreated = new LinkedList<UserRoleModel>();
                for (UserRole userRole : userRolesToGrant) {
                    UserRoleModel userRoleModel = populateRestrictions(rolesByLookup, restrictions, userRole);
                    if (userRoleModel == null) {
                        logger.info("Wrong user role data {user[id = " +
                                (userRole.getUser() == null ? null : userRole.getUser().getId()) +
                                "], type[" + typeLookup + "], ID[" + (userRole.getModelId() == null ?
                                userRole.getComplexPK() : userRole.getModelId()) + "]}");
                    } else {
                        Integer count = userRoleStore.searchCount(restrictions, null, false);
                        if (count == 0) {
                            SecuredRecordModel srModel;
                            if (userRole.getSecuredRecordId() == null) {
                                if (userRole.getModelId() == null) {
                                    logger.info("userRole.getModelId() == null");
                                    userRoleModel.setExternalId(userRole.getComplexPK());
                                    Map<String, String> aliases = new HashMap<String, String>();
                                    aliases.put("registryNode", "rn");
                                    restrictions.clear();
                                    restrictions.add(Restrictions.eq("externalStringId", userRole.getComplexPK()));
                                    restrictions.add(Restrictions.eq("rn.lookup", typeLookup));
                                    List<SecuredRecordModel> srList = securedRecordsStore.search(restrictions, aliases, null, false);
                                    srModel = srList.isEmpty() ? null : srList.get(0);
                                } else {
                                    logger.info("userRole.getModelId() != null");
                                    userRoleModel.setInternalId(userRole.getModelId());
                                    srModel = securedRecordsStore.findByIdAndType(userRole.getModelId(), typeLookup);
                                }
                            } else {
                                srModel = securedRecordsStore.findById(userRole.getSecuredRecordId());
                            }
                            userRoleModel.setSecuredRecord(srModel);
                            userRoleModel.setType(typeLookup);
                            if (userRole.getModelId() == null) {
                                userRoleModel.setExternalId(userRole.getComplexPK());
                            } else {
                                userRoleModel.setInternalId(userRole.getModelId());
                            }
                            userRoleStore.saveOrUpdate(userRoleModel);
                            userRoleModelsCreated.add(userRoleModel);
                        } else {
                            logger.info("Database already has user role for {user[id = " + userRoleModel.getUser().getId() +
                                    "], type[" + typeLookup + "], ID[" + (userRole.getModelId() == null ?
                                    userRole.getComplexPK() : userRole.getModelId()) + "]}");
                        }
                    }
                }
                List<UserRole> userRolesCreated = factory.convertTo(UserRole.class, userRoleModelsCreated);
                response = new ServiceResponse<UserRole>(userRolesCreated, "User roles are saved successfully.", true);
            }
        }
        return response;
    }

    private UserRoleModel populateRestrictions(
            Map<String, RoleModel> rolesByLookup, LinkedList<Criterion> restrictions, UserRole userRole) {
        restrictions.clear();
        UserRoleModel model = null;
        BaseUser user = userRole.getUser();
        if (user != null && user.getId() != null &&
                (userRole.getModelId() != null || StringUtils.isNotBlank(userRole.getComplexPK())) &&
                StringUtils.isNotBlank(userRole.getTypeLookup())) {
            UserModel userModel = userStore.findById(user.getId());
            if (userModel != null) {
                RoleModel role = rolesByLookup.get(userRole.getRole().getLookup());
                if (role != null) {
                    restrictions.add(Restrictions.eq("user.id", userModel.getId()));
                    restrictions.add(Restrictions.eq("role.id", role.getId()));
                    restrictions.add(Restrictions.eq("type", userRole.getTypeLookup()));
                    if (userRole.getModelId() == null) {
                        restrictions.add(Restrictions.eq("externalId", userRole.getComplexPK()));
                    } else {
                        restrictions.add(Restrictions.eq("internalId", userRole.getModelId()));
                    }
                    model = new UserRoleModel();
                    model.setRole(role);
                    model.setUser(userModel);
                }
            }
        }
        return model;
    }

    private void updateRolePermissions(LinkedList<Criterion> restrictions, Collection<String> lookupList, RoleModel roleModel) {
        restrictions.clear();
        restrictions.add(Restrictions.in("lookup", lookupList));
        List<PermissionModel> foundPermissions = permissionStore.search(restrictions, null);
        roleModel.setPermissions(foundPermissions);
        roleStore.save(roleModel);
    }

}