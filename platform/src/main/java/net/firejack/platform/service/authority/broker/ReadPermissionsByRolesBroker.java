package net.firejack.platform.service.authority.broker;


import net.firejack.platform.api.authority.domain.MappedPermissions;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.CollectionUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@TrackDetails
@Component("readPermissionsByRolesBrokerEx")
public class ReadPermissionsByRolesBroker extends ServiceBroker
        <ServiceRequest<NamedValues<String>>, ServiceResponse<MappedPermissions>> {

    public static final String PARAM_SESSION_TOKEN = "sessionToken";

    @Override
    protected ServiceResponse<MappedPermissions> perform(ServiceRequest<NamedValues<String>> request)
		    throws Exception {
        String sessionToken = request.getData().get(PARAM_SESSION_TOKEN);
        CacheManager cacheManager = CacheManager.getInstance();
        Map<Long, List<UserPermission>> permissions;
        if (StringUtils.isBlank(sessionToken)) {
            permissions = cacheManager.getGuestPermissions();
        } else {
            permissions = cacheManager.getPermissions(sessionToken);
        }
        List<MappedPermissions> rolePermissions;
        if (CollectionUtils.isEmpty(permissions)) {
            rolePermissions = new ArrayList<MappedPermissions>();
        } else {
            rolePermissions = new ArrayList<MappedPermissions>(permissions.size());
            for (Map.Entry<Long, List<UserPermission>> rolePermissionsEntry : permissions.entrySet()) {
                MappedPermissions rp = new MappedPermissions();
                rp.setMappedId(rolePermissionsEntry.getKey());
                rp.setPermissions(rolePermissionsEntry.getValue());

                rolePermissions.add(rp);
            }
        }

        ServiceResponse<MappedPermissions> response = new ServiceResponse<MappedPermissions>();
        response.setSuccess(Boolean.TRUE);
        response.setData(rolePermissions);

        return response;
    }
}