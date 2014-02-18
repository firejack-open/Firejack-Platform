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

package net.firejack.platform.core.store.user;

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.user.BaseUserModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.store.registry.AuthorizationVOFactory;
import net.firejack.platform.core.store.registry.ISecuredRecordStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class BaseUserStore<M extends BaseUserModel> extends BaseStore<M, Long> implements IBaseUserStore<M> {

    @Autowired
    @Qualifier("userRoleStore")
    private IUserRoleStore userRoleStore;

    @Autowired
    private ISecuredRecordStore securedRecordStore;

	@PostConstruct
	public void init() {
		setClazz((Class<M>) BaseUserModel.class);
	}

    @Autowired
    @Qualifier("cacheProcessor")
    private ICacheDataProcessor cacheProcessor;

    @Override
    @Transactional(readOnly = true)
    public M findUserByUsername(String username) {
        return findSingle("User.findByUsername",
                "username", username);
    }

    @Override
    @Transactional(readOnly = true)
    public M findUserByEmail(String email) {
        return findSingle("User.findByEmail",
                "email", email);
    }

	@Override
	@Transactional(readOnly = true)
	public List<M> findByRole(Long id) {
		return find(null, null, "User.findByRole", "id", id);
	}

    @Override
    @Transactional(readOnly = true)
    public M findByIdWithRoles(Long id) {
        return findSingle("User.findByIdWithRoles",
                "id", id);
    }

    @Override
    @Transactional(readOnly = true)
    public M findByIdWithRegistryNodeAndGlobalRoles(Long id) {
        return findSingle("User.findByIdWithRegistryNodeAndGlobalRoles",
                "id", id);
    }

    @Override
    @Transactional(readOnly = true)
    public M findUserByUsernameAndPassword(String username, String password) {
        return findSingle("User.findByUsernameAndPassword",
                "userName", username, "password", password);
    }

    @Override
    @Transactional(readOnly = true)
    public List<M> findAllByRegistryNodeIdsAndSearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion usernameCriterion = Restrictions.like("username", "%" + term + "%");
        Criterion emailCriterion = Restrictions.like("email", "%" + term + "%");
        Criterion firstNameCriterion = Restrictions.like("firstName", "%" + term + "%");
        Criterion lastNameCriterion = Restrictions.like("lastName", "%" + term + "%");
        LogicalExpression expression1 = Restrictions.or(usernameCriterion, emailCriterion);
        LogicalExpression expression2 = Restrictions.or(firstNameCriterion, lastNameCriterion);
        LogicalExpression expression = Restrictions.or(expression1, expression2);
        if (registryNodeIds != null) {
            Criterion registryNodeIdCriterion = Restrictions.in("registryNode.id", registryNodeIds);
            LogicalExpression expressionAll = Restrictions.and(registryNodeIdCriterion, expression);
            criterions.add(expressionAll);
        } else {
            criterions.add(expression);
        }
        return findAllWithFilter(criterions, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<M> findAllByRegistryNodeIdsAndSearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter,
            Integer offset, Integer limit, Order... orders) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion usernameCriterion = Restrictions.like("username", "%" + term + "%");
        Criterion emailCriterion = Restrictions.like("email", "%" + term + "%");
        Criterion firstNameCriterion = Restrictions.like("firstName", "%" + term + "%");
        Criterion lastNameCriterion = Restrictions.like("lastName", "%" + term + "%");
        LogicalExpression expression1 = Restrictions.or(usernameCriterion, emailCriterion);
        LogicalExpression expression2 = Restrictions.or(firstNameCriterion, lastNameCriterion);
        LogicalExpression expression = Restrictions.or(expression1, expression2);
        if (registryNodeIds != null) {
            Criterion registryNodeIdCriterion = Restrictions.in("registryNode.id", registryNodeIds);
            LogicalExpression expressionAll = Restrictions.and(registryNodeIdCriterion, expression);
            criterions.add(expressionAll);
        } else {
            criterions.add(expression);
        }
        return findAllWithFilter(offset, limit, criterions, filter, orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<M> findAllByRegistryNodeIdsWithFilter(List<Long> registryNodeIds, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdCriterion = Restrictions.in("registryNode.id", registryNodeIds);
        criterions.add(registryNodeIdCriterion);
        List<M> users = findAllWithFilter(criterions, filter);
        for (M user : users) {
            user.setRegistryNode(null);
        }
        return users;
    }

    @Override
    @Transactional
    public void save(M user) {
        List<UserRoleModel> userRoles = user.getUserRoles();
        user.setUserRoles(null);

        saveOrUpdate(user);

        List<UserRoleModel> existUserRoles = new ArrayList<UserRoleModel>();
        if (user.getId() != null) {
            existUserRoles = userRoleStore.findAllByUserId(user.getId());
        }
        for (UserRoleModel existUserRole : existUserRoles) {
            if (!(containsUserRole(existUserRole, userRoles) || isContextualRole(existUserRole))) {
                userRoleStore.delete(existUserRole);
            }
        }
        for (UserRoleModel userRole : userRoles) {
            if (!containsUserRole(userRole, existUserRoles)) {
                userRole.setUser(user);
                if (userRole.getInternalId() != null && StringUtils.isNotBlank(userRole.getType()) &&
                        userRole.getSecuredRecord() == null) {
                    //in case if secured record was not set in the broker, try to set it manually in the store
                    SecuredRecordModel securedRecordModel = securedRecordStore.findByIdAndType(
                            userRole.getInternalId(), userRole.getType());
                    userRole.setSecuredRecord(securedRecordModel);
                }
                userRoleStore.saveOrUpdate(userRole);
            }
        }
    }

	private boolean containsUserRole(UserRoleModel userRole, List<UserRoleModel> userRoles) {
        boolean result = false;
        if (userRoles != null) {
            for (UserRoleModel ur : userRoles) {
                if (ur.getRole().getId().equals(userRole.getRole().getId()) &&
                        ur.getUser().getId().equals(userRole.getUser().getId()) &&
                        StringUtils.equals(ur.getType(), userRole.getType()) &&
                        ((ur.getInternalId() == null && userRole.getInternalId() == null) ||
                                (ur.getInternalId() != null && userRole.getInternalId() != null &&
                                        ur.getInternalId().equals(userRole.getInternalId())))) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private boolean isContextualRole(UserRoleModel userRole) {
        return userRole.getInternalId() != null || StringUtils.isNotBlank(userRole.getType());
    }

//    @Override
//    @Transactional
//    public M saveExternalUser(M user, String directoryLookup, String roleLookup) {
//        M single = findUserByUsernameAndPassword(user.getUsername(), user.getPassword());
//        if (single == null) {
//            LookupModel directory = registryNodeStore.findByLookup(directoryLookup);
//            user.setRegistryNode((RegistryNodeModel) directory);
//
//            RoleModel role = roleStore.findByLookup(roleLookup);
//            user.setUserRoles(Arrays.asList(new UserRoleModel(user, role)));
//
//            save(user);
//            return user;
//        }
//        return single;
//    }

    @Override
    @Transactional
    public void saveUserRoles(Long userId, List<RoleModel> roles) {//todo: remake considering context roles!!!
        if (userId == null) {
            throw new IllegalArgumentException("userId parameter should not be null.");
        }
        if (roles != null && !roles.isEmpty()) {
            M user = findById(userId);
            if (user != null) {
                List<UserRoleModel> userRoles = new ArrayList<UserRoleModel>();
                List<Long> roleIdList = new ArrayList<Long>();
                for (RoleModel role : roles) {
                    UserRoleModel userRole = new UserRoleModel(user, role);
                    userRoles.add(userRole);
                    roleIdList.add(role.getId());
                }
                user.setUserRoles(userRoles);
                saveOrUpdate(user);

                cacheProcessor.setUserRole(AuthorizationVOFactory.convert(user), roleIdList);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countUsersByRole(Long id) {
        return count("User.countUsersByRole", true, "id", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<Long>> findAllRolesByUsers() {
        List<M> allUsers = find(null, null, "User.findAllWithRoles");
        Map<Long, List<Long>> rolesByUsers = new HashMap<Long, List<Long>>();
        if (allUsers != null) {
            for (M user : allUsers) {
                List<Long> userRoles = new ArrayList<Long>();
                rolesByUsers.put(user.getId(), userRoles);
                if (user.getUserRoles() != null) {
                    for (UserRoleModel userRole : user.getUserRoles()) {
                        if (userRole.getType() == null) {//not context role
                            userRoles.add(userRole.getRole().getId());
                        }
                    }
                }
            }
        }
        return rolesByUsers;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings({"unchecked"})
    public List<M> findAllUsersHaveContextRolesForRegistryNodeId(final Long objectId, final RegistryNodeModel registryNode, final SpecifiedIdsFilter<Long> filter) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public List<M> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria userCriteria = createCriteriaForFilter(session, filter);
                Criteria userRoleCriteria = userCriteria.createAlias("userRoles", "ur");
                userRoleCriteria.add(Restrictions.eq("ur.internalId", objectId));
                userRoleCriteria.add(Restrictions.eq("ur.type", registryNode.getLookup()));
                Criteria roleCriteria = userRoleCriteria.createAlias("ur.role", "r");
//	            Criteria registryNodeCriteria = roleCriteria.createAlias("r.parent", "rn");

                userCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return (List<M>) userRoleCriteria.list();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings({"unchecked"})
    public List<M> findLastCreatedUsers(final Integer count) {
        return find(null, count, "User.findLastCreated");
    }

    @Override
    @Transactional
    public void deleteAllByRegistryNodeId(Long registryNodeId) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdCriterion = Restrictions.eq("registryNode.id", registryNodeId);
        criterions.add(registryNodeIdCriterion);
        List<M> users = findAllWithFilter(criterions, null);
        for (M user : users) {
            delete(user);
        }
    }

    @Transactional(readOnly = true)
    public List<M> findUsersBelongingToActor(long actorId) {
        return find(null, null, "User.findUsersBelongingToActor", "actorId", actorId);
    }

    @Transactional(readOnly = true)
    public List<M> findUsersBelongingToActorNotInCase(long actorId) {
        return find(null, null, "User.findUsersBelongingToActorNotInCase", "actorId", actorId);
    }


}
