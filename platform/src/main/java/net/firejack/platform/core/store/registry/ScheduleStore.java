package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.schedule.ScheduleManager;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.Paging;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleStore extends RegistryNodeStore<ScheduleModel> implements IScheduleStore {

    @Autowired
    private IRoleStore roleStore;
    @Autowired
    private IPermissionStore permissionStore;
    @Autowired
    private IActionStore actionStore;
    @Autowired
    private ScheduleManager scheduleManager;

	@PostConstruct
	public void init() {
		setClazz(ScheduleModel.class);
	}


    @Override
    @Transactional(readOnly = true)
    public List<ScheduleModel> findAll() {
        List<ScheduleModel> scheduleModels = super.findAll();
        for (ScheduleModel scheduleModel : scheduleModels) {
            Hibernate.initialize(scheduleModel.getAction());
        }
        return scheduleModels;
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleModel findById(Long id) {
        ScheduleModel scheduleModel = super.findById(id);
        Hibernate.initialize(scheduleModel.getAction());
        return scheduleModel;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleModel> findAllByParentIdsWithFilter(List<Long> ids, SpecifiedIdsFilter<Long> filter, Paging paging) {
        List<ScheduleModel> scheduleModels = super.findAllByParentIdsWithFilter(ids, filter, paging);
        for (ScheduleModel scheduleModel : scheduleModels) {
            Hibernate.initialize(scheduleModel.getAction());
        }
        return scheduleModels;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion nameCriterion = Restrictions.like("lookup", "%" + term + "%");
        criterions.add(nameCriterion);
        return findAllWithFilter(criterions, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleModel> findAllBySearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdCriterion = Restrictions.in("parent.id", registryNodeIds);
        Criterion nameCriterion = Restrictions.like("lookup", "%" + term + "%");
        LogicalExpression expressionAll = Restrictions.and(registryNodeIdCriterion, nameCriterion);
        criterions.add(expressionAll);
        return findAllWithFilter(criterions, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleModel> findAllByLikeLookupPrefix(String lookupPrefix) {
        List<ScheduleModel> scheduleModels = super.findAllByLikeLookupPrefix(lookupPrefix);
        for (ScheduleModel scheduleModel : scheduleModels) {
            if (scheduleModel.getAction() != null) {
                Hibernate.initialize(scheduleModel.getAction());
            }
        }
        return scheduleModels;
    }

    @Override
    @Transactional
    public void delete(ScheduleModel scheduleModel) {
        super.delete(scheduleModel);

        RoleModel systemRole = roleStore.findByLookup(OpenFlame.ROLE_SYSTEM);
        if (systemRole != null) {
            List<PermissionModel> rolePermissions = permissionStore.findRolePermissions(systemRole.getId());
            ActionModel actionModel = actionStore.findById(scheduleModel.getAction().getId());
            PermissionModel actionPermissionModel = permissionStore.findByLookup(actionModel.getLookup());
            if (actionPermissionModel != null) {
                rolePermissions.remove(actionPermissionModel);
                roleStore.saveRolePermissions(systemRole.getId(), rolePermissions);
            }
        }
        scheduleManager.deleteScheduleJob(scheduleModel);
    }

    @Override
    @Transactional
    public void deleteAllByRegistryNodeId(Long registryNodeId) {
        List<ScheduleModel> scheduleModels = findAllByParentIdWithFilter(registryNodeId, null);
        super.deleteAll(scheduleModels);
    }

    @Override
    @Transactional
    public void deleteByActionId(Long actionId) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        criterions.add(Restrictions.eq("action.id", actionId));
        List<ScheduleModel> scheduleModels = findAllWithFilter(criterions, null);
        for (ScheduleModel scheduleModel : scheduleModels) {
            delete(scheduleModel);
        }
    }

    @Override
    @Transactional
    public void save(ScheduleModel scheduleModel) {
        ScheduleModel oldScheduleModel = null;
        if (scheduleModel.getId() != null) {
            oldScheduleModel = findById(scheduleModel.getId());
        }

        saveWithoutUpdateChildren(scheduleModel);
        ActionModel actionModel = actionStore.findById(scheduleModel.getAction().getId());
        scheduleModel.setAction(actionModel);

        if (oldScheduleModel == null) {
            RoleModel systemRole = roleStore.findByLookup(OpenFlame.ROLE_SYSTEM);
            if (systemRole != null) {
                List<PermissionModel> rolePermissions = permissionStore.findRolePermissions(systemRole.getId());

                PermissionModel actionPermissionModel = permissionStore.findByLookup(actionModel.getLookup());
                if (actionPermissionModel != null) {
                    rolePermissions.add(actionPermissionModel);
                    roleStore.saveRolePermissions(systemRole.getId(), rolePermissions);
                }
            }
            scheduleManager.createScheduleJob(scheduleModel);
        } else {
            if (!oldScheduleModel.getAction().getId().equals(scheduleModel.getAction().getId())) {
                RoleModel systemRole = roleStore.findByLookup(OpenFlame.ROLE_SYSTEM);
                if (systemRole != null) {
                    List<PermissionModel> rolePermissions = permissionStore.findRolePermissions(systemRole.getId());
                    ActionModel oldActionModel = actionStore.findById(oldScheduleModel.getAction().getId());
                    PermissionModel oldActionPermissionModel = permissionStore.findByLookup(oldActionModel.getLookup());
                    if (oldActionPermissionModel != null) {
                        rolePermissions.remove(oldActionPermissionModel);
                    }
                    PermissionModel actionPermissionModel = permissionStore.findByLookup(actionModel.getLookup());
                    if (actionPermissionModel != null) {
                        rolePermissions.add(actionPermissionModel);
                    }
                    roleStore.saveRolePermissions(systemRole.getId(), rolePermissions);
                }
            }
            scheduleManager.updateScheduleJob(scheduleModel);
        }
    }

}