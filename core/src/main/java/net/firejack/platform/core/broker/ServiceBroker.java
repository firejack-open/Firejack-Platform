/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.core.broker;

import net.firejack.platform.core.broker.securedrecord.ISecuredRecordHandler;
import net.firejack.platform.core.broker.security.SecurityHandler;
import net.firejack.platform.core.broker.workflow.IWorkflowHandler;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Factory;
import net.firejack.platform.core.validation.ValidationMessage;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.core.validation.exception.ValidationException;
import net.firejack.platform.core.validation.processor.IValidationProcessor;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public abstract class ServiceBroker<RQ extends ServiceRequest, RS extends ServiceResponse> implements IServiceBroker<RQ, RS> {

    private static final String DEFAULT_DETAIL_MESSAGE_KEY = "broker.detailedMessage.common";

    protected static final Logger logger = Logger.getLogger(ServiceBroker.class);

    @Autowired(required = false)
    private IValidationProcessor validationProcessor;

    @Autowired
    protected Factory factory;

    @Autowired(required = false)
    protected ISecuredRecordHandler securedRecordHandler;

    @Autowired(required = false)
    protected IWorkflowHandler workflowHandler;

    @Autowired(required = false)
    protected SecurityHandler securityHandler;

    public RS execute(RQ request) {
        if (request == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        RS response;
        try {
            processArguments(request);
            validateArguments(request);
            authorizeBeforePerform(request);
            response = getExternal().perform(request);
            authorizeAfterPerform(request, response);
        } catch (RuleValidationException e) {
            if (e.getValidationMessages() != null) {
                logger.error(e.getMessage());
                throw new ValidationException(e.getValidationMessages());
            }
            throw new WebApplicationException(e);
        } catch (BusinessFunctionException e) {
            throw e;
        } catch (SecurityException e) {
            return populateErrorResponse(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessFunctionException("server.error", new String[]{e.getMessage()}, null);
        }

        processSecuredRecords(request, response);
        processWorkflow(request, response);
        notify(request, response);

        return response;
    }

    /**
     * Validates the data being submitted for the operation (read or write) against
     * all known constraints. This includes simple data validation and complex business
     * rules to maintain data integrity.
     *
     * @param request - request
     * @throws net.firejack.platform.core.validation.exception.RuleValidationException throws RuleValidationException
     */
    protected void validateArguments(RQ request) throws RuleValidationException {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        validationMessages.addAll(validationProcessor.validateArguments(request));
        validationMessages.addAll(specificArgumentsValidation(request));

        if (!validationMessages.isEmpty()) {
            throw new RuleValidationException(validationMessages);
        }
    }

    /**
     * Performs the actual transaction for the business function, updating all data or
     * retrieving all data necessary from the data access layer. This method assumes authorization
     * and validation (deep and shallow) have been passed using the provided context.
     * <p/>
     * This method is also responsible for translating the message to a domain object and the resulting
     * domain object(s) to value object for serialization to the client. Service facades will be able
     * to serialize the objects to fit the appropriate protocol and format (XML, JSON, REST, SOAP, etc).
     *
     *
     * @param request I the message passed to the business function with all data required
     * @return R result of the message or a generic success / failure message as applicable.
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     *          throws BusinessFunctionException
     */
    protected abstract RS perform(RQ request) throws Exception;

    /**
     * Sends a request using the result, notifying all asynchronous listeners
     * registered for the operation. Sends no notification if there are no registered
     * listeners. This method is most often used for create, update, and delete transactions.
     *
     * @param request message to notify
     * @param response  R Result of the method (read or write)
     */
    @SuppressWarnings("unused")
    public void notify(RQ request, RS response) {
        //override this method in child classes if necessary
    }

    protected void processArguments(RQ request) {
        //override this method in child classes if necessary
    }

    protected List<ValidationMessage> specificArgumentsValidation(RQ request) throws RuleValidationException {
        return new ArrayList<ValidationMessage>();
    }

    public Object getDetailedMessageArgs(RQ request) {
        Object identifier = null;

        if (request.getData() instanceof SimpleIdentifier) {
            identifier = ((SimpleIdentifier) request.getData()).getIdentifier();
        } else if (request.getData() instanceof NamedValues) {
            NamedValues values = (NamedValues) request.getData();
            identifier = values.get("id");
        } else if (request.getData() instanceof BaseEntity) {
            BaseEntity vo = (BaseEntity) request.getData();
            identifier = vo.getId();
        }

        if (identifier == null) {
            return null;
        }

        return identifier;
    }

    protected void processSecuredRecords(RQ request, RS response) {
        ISecuredRecordHandler srHandler = getSecuredRecordHandler();
        if (srHandler != null) {
            srHandler.handleSecuredRecord(request, response);
        }
    }

    protected ISecuredRecordHandler getSecuredRecordHandler() {
        return securedRecordHandler;
    }

    protected void authorizeBeforePerform(RQ request) throws SecurityException {
        SecurityHandler handler = getSecurityHandler();
        if (handler != null && !handler.handleSecurityBeforeRequest(request)) {
            throw new SecurityException("Access restricted for the action.");
        }
    }

    protected void authorizeAfterPerform(RQ request, RS response) throws SecurityException {
        SecurityHandler handler = getSecurityHandler();
        if (handler != null && !handler.handleSecurityAfterRequest(request, response)) {
            throw new SecurityException("Access restricted after request executed.");
        }
    }

    protected SecurityHandler getSecurityHandler() {
        return securityHandler;
    }

    @SuppressWarnings("unchecked")
    protected RS populateErrorResponse(String msg) {
        logger.warn("Populating error response with message: " + msg);
        return (RS) new ServiceResponse(msg, false);
    }

	protected ServiceBroker<RQ, RS> getExternal() {
		ServiceBroker external = this;
		try {
			external = (ServiceBroker<RQ, RS>) AopContext.currentProxy();
		} catch (IllegalStateException e) {
			logger.debug(e.getMessage());
		}
		return external;
	}

    protected void processWorkflow(RQ request, RS response) {
        IWorkflowHandler wfHandler = getWorkflowHandler();
        if (wfHandler != null) {
            wfHandler.handleWorkflowRecord(request, response);
        }
    }

    public IWorkflowHandler getWorkflowHandler() {
        return workflowHandler;
    }
}
