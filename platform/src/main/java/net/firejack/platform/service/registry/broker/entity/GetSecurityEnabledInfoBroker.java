package net.firejack.platform.service.registry.broker.entity;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;


@TrackDetails
@Component
public class GetSecurityEnabledInfoBroker extends ServiceBroker
        <ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<SimpleIdentifier<Boolean>>> {

    @Override
    protected ServiceResponse<SimpleIdentifier<Boolean>> perform(ServiceRequest<SimpleIdentifier<String>> request)
            throws Exception {
        String entityLookup = request.getData().getIdentifier();
        ServiceResponse<SimpleIdentifier<Boolean>> response;
        if (StringUtils.isBlank(entityLookup)) {
            response = new ServiceResponse<SimpleIdentifier<Boolean>>("entityLookup parameter should not be blank.", false);
        } else {
            Boolean securityEnabled = CacheManager.getInstance().checkIfEntitySecurityEnabled(entityLookup);
            securityEnabled = securityEnabled == null ? false : securityEnabled;
            response = new ServiceResponse<SimpleIdentifier<Boolean>>(
                    new SimpleIdentifier<Boolean>(securityEnabled), "Success", true);
        }
        return response;
    }
}
