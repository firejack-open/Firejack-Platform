package net.firejack.platform.service.authority.broker;

import net.firejack.platform.api.authority.domain.TypeFilter;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.IdFilter;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@TrackDetails
@Component("readTypeFiltersForUserBroker")
public class ReadTypeFiltersForUserBroker extends ServiceBroker
        <ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<TypeFilter>> {

    @Override
    protected ServiceResponse<TypeFilter> perform(ServiceRequest<SimpleIdentifier<Long>> request)
		    throws Exception {
        Long userId = request.getData().getIdentifier();
        Map<String, IdFilter> idFiltersForUser = CacheManager.getInstance().getIdFiltersForUser(userId);
        List<TypeFilter> typeFilterList = new ArrayList<TypeFilter>();
        if (idFiltersForUser != null) {
            for (Map.Entry<String, IdFilter> idFilterEntry : idFiltersForUser.entrySet()) {
                IdFilter idFilter = idFilterEntry.getValue();

                TypeFilter typeFilter = new TypeFilter();
                typeFilter.setType(idFilterEntry.getKey());
                typeFilter.setIdFilter(idFilter);

                typeFilterList.add(typeFilter);
            }
        }
        return new ServiceResponse<TypeFilter>(typeFilterList, null, true);
    }
}