package net.firejack.platform.core.broker.workflow;

import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;

public interface IWorkflowHandler {

    void handleWorkflowRecord(ServiceRequest request, ServiceResponse brokerResponse);

}
