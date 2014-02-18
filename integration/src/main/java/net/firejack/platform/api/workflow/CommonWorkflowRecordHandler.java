package net.firejack.platform.api.workflow;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.core.broker.workflow.IWorkflowHandler;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ClassUtils;
import net.firejack.platform.core.validation.exception.PropertyNotFoundException;
import net.firejack.platform.web.security.action.StandardAction;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CommonWorkflowRecordHandler implements IWorkflowHandler {

    private static final Logger logger = Logger.getLogger(CommonWorkflowRecordHandler.class);

    @Override
    public void handleWorkflowRecord(ServiceRequest request, ServiceResponse response) {
        if (response != null && response.isSuccess() && OPFContext.isInitialized()) {
            try {
                OPFContext context = OPFContext.getContext();
                if (context != null) {
                    Action currentAction = context.getCurrentAction();
                    if (currentAction != null) {
                        StandardAction actionType = StandardAction.detectStandardAction(currentAction);
                        if (/*StandardAction.CREATE.equals(actionType) || */StandardAction.DELETE.equals(actionType)) {
                            Long entityId = currentAction.getParentId();
                            /*if (StandardAction.CREATE.equals(actionType)) {
                                AbstractDTO dto = response.getItem();
                                Long recordId = (Long) getId(dto, true);
                                if (recordId != null) {
                                    ServiceResponse<Task> workflowResponse = OPFEngine.ProcessService.createWorkflowTasksForRecord(recordId, entityId);
                                    if (workflowResponse.isSuccess()) {
                                        logger.debug(workflowResponse.getMessage());
                                    } else {
                                        logger.warn(workflowResponse.getMessage());
                                    }
                                } else {
                                    logger.warn("Couldn't detect record id for entity: [" + currentAction.getPath() + "]");
                                }
                            } else*/ if (StandardAction.DELETE.equals(actionType)) {
                                AbstractDTO dto = request.getData();
                                Long recordId = (Long) getId(dto, false);
                                if (recordId != null) {
                                    ServiceResponse<Task> workflowResponse = OPFEngine.ProcessService.deleteWorkflowTasksForRecord(recordId, entityId);
                                    if (workflowResponse.isSuccess()) {
                                        logger.debug(workflowResponse.getMessage());
                                    } else {
                                        logger.warn(workflowResponse.getMessage());
                                    }
                                } else {
                                    logger.warn("Couldn't detect record id for entity: [" + currentAction.getPath() + "]");
                                }
                            }
                        }
                    }
                }
            } catch (ContextLookupException e) {
                logger.debug(e.getMessage(), e);
            }
        }
    }

    protected Serializable getId(AbstractDTO dto, boolean create) {
        Serializable id;
        if (dto == null) {
            id = null;
        } else {
            if (create) {
                id = getPropertyValue(dto, "id", Long.class); //TODO: refactor
            } else {
                if (dto instanceof SimpleIdentifier) {
                    SimpleIdentifier idHolder = (SimpleIdentifier) dto;
                    id = (Serializable) idHolder.getIdentifier();
                } else if (dto instanceof NamedValues) {
                    NamedValues namedValues = (NamedValues) dto;
                    id = (Serializable) namedValues.get("id");
                } else {
                    logger.warn("The id info is not retrieved.");
                    id = null;
                }
            }
        }
        return id;
    }

    private <T> T getPropertyValue(AbstractDTO dto, String propertyName, Class<T> clazz) {
        T result;
        try {
            result = ClassUtils.getPropertyValue(dto, propertyName, clazz);
        } catch (PropertyNotFoundException e) {
            result = null;
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
        return result;
    }

}
