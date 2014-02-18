package net.firejack.platform.service.authority.broker.role;

import net.firejack.platform.api.authority.domain.AssignedRole;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@TrackDetails
public class SearchContextRolesAvailableForUser extends ServiceBroker
        <ServiceRequest<UserRole>, ServiceResponse<AssignedRole>> {

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IRoleStore roleStore;
    @Autowired
    private IUserRoleStore userRoleStore;

    @Override
    protected ServiceResponse<AssignedRole> perform(ServiceRequest<UserRole> request) throws Exception {
        UserRole pattern = request.getData();
        ServiceResponse<AssignedRole> response;
        if (pattern == null || pattern.getUser() == null || pattern.getUser().getId() == null ||
                StringUtils.isBlank(pattern.getTypeLookup()) || (pattern.getModelId() == null &&
                StringUtils.isBlank(pattern.getComplexPK()))) {
            response = new ServiceResponse<AssignedRole>("Not all required input parameters are specified.", false);
        } else {
            EntityModel contextType = entityStore.findByLookup(pattern.getTypeLookup());
            if (contextType == null) {
                response = new ServiceResponse<AssignedRole>(
                        "Failed to find context type information by specified lookup parameter.", false);
            } else {
                //Map<String, String> aliases = new HashMap<String, String>();
                LinkedList<Criterion> restrictions = new LinkedList<Criterion>();
                restrictions.add(Restrictions.eq("path", pattern.getTypeLookup()));
                List<RoleModel> foundRoles = roleStore.search(restrictions, null);
                if (foundRoles.isEmpty()) {
                    response = new ServiceResponse<AssignedRole>(
                            "There are no roles available for the specified context.", true);
                } else {
                    List<Long> roleIdList = new ArrayList<Long>(foundRoles.size());
                    for (RoleModel roleModel : foundRoles) {
                        roleIdList.add(roleModel.getId());
                    }
                    restrictions.clear();
                    restrictions.add(Restrictions.eq("user.id", pattern.getUser().getId()));
                    restrictions.add(Restrictions.eq("type", pattern.getTypeLookup()));
                    if (pattern.getModelId() == null) {
                        restrictions.add(Restrictions.eq("externalId", pattern.getComplexPK()));
                    } else {
                        restrictions.add(Restrictions.eq("internalId", pattern.getModelId()));
                    }
                    restrictions.add(Restrictions.in("role.id", roleIdList));
                    List<UserRoleModel> assignedUserRoles = userRoleStore.search(restrictions, null, null, false);
                    Map<Long, AssignedRole> roleByIdMap = new HashMap<Long, AssignedRole>();
                    for (RoleModel roleModel : foundRoles) {
                        AssignedRole role = new AssignedRole();
                        role.setId(roleModel.getId());
                        role.setName(roleModel.getName());
                        role.setPath(roleModel.getPath());
                        role.setLookup(roleModel.getLookup());
                        role.setAssigned(Boolean.FALSE);
                        roleByIdMap.put(roleModel.getId(), role);
                    }
                    for (UserRoleModel userRoleModel : assignedUserRoles) {
                        AssignedRole role = roleByIdMap.get(userRoleModel.getRole().getId());
                        role.setAssigned(Boolean.TRUE);
                    }
                    List<AssignedRole> availableRoles = new LinkedList<AssignedRole>();
                    availableRoles.addAll(roleByIdMap.values());
                    response = new ServiceResponse<AssignedRole>(availableRoles, "Roles are found for the specified seach pattern", true);
                }
            }
        }
        return response;
    }

}