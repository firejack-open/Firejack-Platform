package net.firejack.platform.service.authority.broker;

import net.firejack.platform.api.authority.domain.MappedPermissions;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.sr.CachedSecuredRecordDataLoader;
import net.firejack.platform.web.security.sr.ISecuredRecordDataLoader;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@TrackDetails
@Component("readSRPermissionsBrokerEx")
public class ReadSRPermissionsBroker extends ServiceBroker
        <ServiceRequest, ServiceResponse<MappedPermissions>> {

    private ISecuredRecordDataLoader securedRecordDataLoader;

    @Override
    protected ServiceResponse<MappedPermissions> perform(ServiceRequest request)
		    throws Exception {
        OPFContext context = OPFContext.getContext();
        String sessionToken = context.getSessionToken();
        List<MappedPermissions> mappedPermissionsList;
        if (StringUtils.isBlank(sessionToken) || context.getPrincipal().isGuestPrincipal()) {
            mappedPermissionsList = new ArrayList<MappedPermissions>();
        } else {
            Long userId = context.getPrincipal().getUserInfoProvider().getId();
            Map<Long, List<UserPermission>> srContextPermissions =
                    getSecuredRecordDataLoader().loadSecuredRecordContextPermissions(userId);
            if (srContextPermissions == null) {
                mappedPermissionsList = new ArrayList<MappedPermissions>();
            } else {
                mappedPermissionsList = new ArrayList<MappedPermissions>();
                for (Map.Entry<Long, List<UserPermission>> contextPermissionsBySR : srContextPermissions.entrySet()) {
                    MappedPermissions mappedPermissions = new MappedPermissions();
                    mappedPermissions.setMappedId(contextPermissionsBySR.getKey());
                    mappedPermissions.setPermissions(
                            contextPermissionsBySR.getValue() == null ? new LinkedList<UserPermission>() :
                            contextPermissionsBySR.getValue());

                    mappedPermissionsList.add(mappedPermissions);
                }
            }
        }
        return new ServiceResponse<MappedPermissions>(mappedPermissionsList, null, true);
    }

    protected ISecuredRecordDataLoader getSecuredRecordDataLoader() {
        if (securedRecordDataLoader == null) {
            securedRecordDataLoader = new CachedSecuredRecordDataLoader();
        }
        return securedRecordDataLoader;
    }
}