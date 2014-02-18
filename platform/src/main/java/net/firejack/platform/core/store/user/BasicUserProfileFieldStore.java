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

package net.firejack.platform.core.store.user;

import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.model.user.UserProfileFieldModel;
import net.firejack.platform.core.model.user.UserProfileFieldValueModel;
import net.firejack.platform.core.store.registry.RegistryNodeStore;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
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
@Component("userProfileFieldStore")
public class BasicUserProfileFieldStore extends RegistryNodeStore<UserProfileFieldModel> implements IUserProfileFieldStore {

    @Autowired
    @Qualifier("userStore")
    private IUserStore userStore;
    @Autowired
    @Qualifier("userProfileFieldValueStore")
    private IUserProfileFieldValueStore userProfileFieldValueStore;
    /***/
    @PostConstruct
    public void init() {
        setClazz(UserProfileFieldModel.class);
    }

    @Override
    public UserProfileFieldModel findById(final Long id, final boolean loadGroup) {
        return getHibernateTemplate().execute(new HibernateCallback<UserProfileFieldModel>() {
            @Override
            public UserProfileFieldModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(UserProfileFieldModel.class);
                if (loadGroup) {
                    criteria.setFetchMode("", FetchMode.JOIN);
                }
                criteria.add(Restrictions.eq("id", id));
	            return (UserProfileFieldModel) criteria.uniqueResult();
            }
        });
    }

    @SuppressWarnings({"JpaQueryApiInspection", "unchecked"})
    @Override
    @Transactional(readOnly = true)
    public List<UserProfileFieldModel> findUserProfileFieldsByRegistryNodeId(Long registryNodeId, Long userProfileFieldGroupId) {
        List<UserProfileFieldModel> userProfileFieldModels;
        if (new Long(0).equals(userProfileFieldGroupId)) {
            userProfileFieldModels = getHibernateTemplate().findByNamedQueryAndNamedParam(
                "UserProfileField.findByRegistryNodeIdAndGroupNull",
                "registryNodeId", registryNodeId);
        } else {
            userProfileFieldModels = getHibernateTemplate().findByNamedQueryAndNamedParam(
                "UserProfileField.findByRegistryNodeId",
                new String[]{"registryNodeId", "userProfileFieldGroupId"},
                new Object[]{registryNodeId, userProfileFieldGroupId});
        }

        return userProfileFieldModels;
    }

    @SuppressWarnings({"JpaQueryApiInspection"})
    @Override
    @Transactional(readOnly = true)
    public UserProfileFieldModel findUserProfileFieldByRegistryNodeIdAndName(Long registryNodeId, String name) {
        List result = getHibernateTemplate().findByNamedQueryAndNamedParam(
                "UserProfileField.findByRegistryNodeIdAndName",
                new String[]{"registryNodeId", "name"},
                new Object[]{registryNodeId, name}
        );
        return result.isEmpty() ? null : (UserProfileFieldModel) result.get(0);
    }

	@Override
    @Transactional
    public void deleteAllByRegistryNodeId(Long registryNodeId) {
        List<UserProfileFieldModel> fields = findAllByParentIdWithFilter(registryNodeId, null);
        super.deleteAll(fields);
    }

    @Override
    @Transactional
    public void mergeForGenerator(UserProfileFieldModel model) {
        UserProfileFieldModel mergedModel = mergeModel(model);
        if (mergedModel != null) {
            saveOrUpdate(mergedModel);
        }
    }

    @Override
    @Transactional
    public void mergeForGenerator(List<UserProfileFieldModel> modelList) {
        if (modelList != null && !modelList.isEmpty()) {
            List<UserProfileFieldModel> resultModels = new ArrayList<UserProfileFieldModel>();
            for (UserProfileFieldModel model : modelList) {
                UserProfileFieldModel mergedModel = mergeModel(model);
                if (mergedModel != null) {
                    resultModels.add(mergedModel);
                }
            }
            if (!resultModels.isEmpty()) {
                saveOrUpdateAll(resultModels);
            }
        }
    }

    @Override
    @Transactional
    public void saveForGenerator(List<UserProfileFieldModel> modelList) {
        if (modelList != null) {
            for (UserProfileFieldModel model : modelList) {
                saveForGenerator(model);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<UserProfileFieldModel> findGroupedProfileFields(String groupLookup) {
        Criteria criteria = getSession().createCriteria(UserProfileFieldModel.class);
        if (StringUtils.isBlank(groupLookup)) {
            criteria.add(Restrictions.isNull("userProfileFieldGroup"));
        } else {
            criteria.createAlias("userProfileFieldGroup", "group");
            criteria.add(Restrictions.eq("group.lookup", groupLookup));
        }
        return (List<UserProfileFieldModel>) criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<UserProfileFieldValueModel> findUserProfileFieldValuesByUserAndFields(
            Long userId, List<Long> profileFieldIdList) {
        Criteria criteria = getSession().createCriteria(UserProfileFieldValueModel.class);
        criteria.add(Restrictions.eq("user.id", userId));
        criteria.add(Restrictions.in("userProfileField.id", profileFieldIdList));
        return (List<UserProfileFieldValueModel>) criteria.list();
    }

    @Override
    @Transactional
    public List<UserProfileFieldValueModel> saveUserProfileFieldModelList(List<UserProfileFieldValueModel> valueModels) {
        List<UserProfileFieldValueModel> result;
        if (valueModels != null && !valueModels.isEmpty()) {
            result = new ArrayList<UserProfileFieldValueModel>();
            Map<Long, UserModel> cachedUsers = new HashMap<Long, UserModel>();
            Map<Long, UserProfileFieldModel> cachedFields = new HashMap<Long, UserProfileFieldModel>();
            Map<String, UserProfileFieldModel> cachedFieldsByLookup = new HashMap<String, UserProfileFieldModel>();
            for (UserProfileFieldValueModel valueModel : valueModels) {
                UserModel user = valueModel.getUser();
                UserProfileFieldModel profileField = valueModel.getUserProfileField();
                if (user != null && user.getId() != null && profileField != null &&
                        (profileField.getId() != null || StringUtils.isNotBlank(profileField.getLookup()))) {
                    if (cachedUsers.containsKey(user.getId())) {
                        user = cachedUsers.get(user.getId());
                    } else {
                        user = userStore.findById(user.getId());
                        if (user != null) {
                            cachedUsers.put(user.getId(), user);
                        }
                    }
                    if (profileField.getId() != null && cachedFields.containsKey(profileField.getId())) {
                        profileField = cachedFields.get(profileField.getId());
                    } else if (StringUtils.isNotBlank(profileField.getLookup()) &&
                            cachedFieldsByLookup.containsKey(profileField.getLookup())) {
                        profileField = cachedFieldsByLookup.get(profileField.getLookup());
                    } else {
                        profileField = profileField.getId() == null ?
                                findByLookup(profileField.getLookup()) : findById(profileField.getId());
                        if (profileField != null) {
                            cachedFields.put(profileField.getId(), profileField);
                            cachedFieldsByLookup.put(profileField.getLookup(), profileField);
                        }
                    }
                    if (user != null && profileField != null) {
                        valueModel.setUser(user);
                        valueModel.setUserProfileField(profileField);

                        userProfileFieldValueStore.saveOrUpdate(valueModel);
                        result.add(valueModel);
                    }
                }
            }
        } else {
            result = null;
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<UserProfileFieldValueModel>> findUserProfileFieldValues(
            List<Long> userIdList, Collection<Long> userProfileFields) {
        Map<Long, List<UserProfileFieldValueModel>> map;
        if (userIdList == null || userIdList.isEmpty()) {
            map = null;
        } else {
            map = new HashMap<Long, List<UserProfileFieldValueModel>>();
            for (Long userId : userIdList) {
                Criteria criteria = getSession().createCriteria(UserProfileFieldValueModel.class);
                criteria.add(Restrictions.eq("user.id", userId));
                criteria.add(Restrictions.in("userProfileField.id", userProfileFields));
                @SuppressWarnings("unchecked")
                List<UserProfileFieldValueModel> valuesList = (List<UserProfileFieldValueModel>) criteria.list();
                map.put(userId, valuesList);
            }
        }
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public String findRegistryNodeRefPath(Long registryNodeId) {
        UserProfileFieldModel profileFieldModel = findById(registryNodeId);
        String path;
        if (profileFieldModel == null) {
            path = null;
        } else {
            path = profileFieldModel.getPath();
        }
        return path;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileFieldModel findByUID(String uid) {
        Criteria criteria = getSession().createCriteria(UserProfileFieldModel.class);
        criteria.createAlias("uid", "uid");
        criteria.add(Restrictions.eq("uid.uid", uid));
        criteria.setFetchMode("userProfileFieldGroup", FetchMode.JOIN);
        return (UserProfileFieldModel) criteria.uniqueResult();
    }

    private UserProfileFieldModel mergeModel(UserProfileFieldModel model) {
        UserProfileFieldModel result = null;
        if (model.getUid() != null) {
            UserProfileFieldModel oldModel = findByUID(model.getUid().getUid());
            if (oldModel != null) {
                oldModel.setFieldType(model.getFieldType());
                oldModel.setUserProfileFieldGroup(model.getUserProfileFieldGroup());
                oldModel.setName(model.getName());
                oldModel.setDescription(model.getDescription());
                LookupModel parent = getRegistryNodeStore().findByLookup(model.getPath());
                if (parent != null) {
                    oldModel.setParent((RegistryNodeModel) parent);
                    oldModel.setPath(model.getPath());
                }
                oldModel.setLookup(oldModel.getPath() + '.' + StringUtils.normalize(oldModel.getName()));
                result = oldModel;
            }
        }
        return result;
    }

}
