package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.UserActor;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.UserActorModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.IUserActorStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Class encapsulates the functionality of assigning a team to a case
 */
@TrackDetails
@Component("assignTeamToCaseBroker")
public class AssignTeamToCaseBroker extends ServiceBroker<ServiceRequest<UserActor>, ServiceResponse> {

    @Autowired
    @Qualifier("userActorStore")
    protected IUserActorStore userActorStore;

    /**
     * Assigns team to the case
     *
     * @param request service request containing user actor value objects
     * @return service response containing information about the success of the assignment
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse perform(ServiceRequest<UserActor> request) throws Exception {
        ServiceResponse response;
        if (request.getDataList() == null || request.getDataList().isEmpty()) {
            response = new ServiceResponse("User actors were not specified for save operation.", false);
        } else {
//        List<UserActorModel> userActorModels = factory.convertFrom(UserActorModel.class, request.getDataList()); / DOESN'T WORK
            List<UserActorModel> userActorModels = new ArrayList<UserActorModel>();
            for (UserActor userActor : request.getDataList()) {
                if (userActor != null && isValid(userActor.getActor()) &&
                        isValid(userActor.getUser()) && isValid(userActor.getProcessCase())) {
                    UserActorModel userActorModel = factory.convertFrom(UserActorModel.class, userActor);
                    userActorModel.setCase(factory.convertFrom(CaseModel.class, userActor.getProcessCase()));
                    userActorModels.add(userActorModel);
                }
            }

            try {
                userActorStore.saveOrUpdateAll(userActorModels);
                response = new ServiceResponse("Team assigned to the case successfully.", true);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse("Failed to save actors team to the case. Reason: " + e.getMessage(), false);
            }
        }
        return response;
    }

    private boolean isValid(BaseEntity entity) {
        return entity != null && entity.getId() != null;
    }
    
}
