/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.exception;

import net.firejack.platform.core.utils.MessageResolver;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.Response;
import java.util.Locale;


public class BusinessFunctionException extends OpenFlameRuntimeException {
    private static final long serialVersionUID = -8655192038335011513L;

    private String msgKey;
    private Object[] messageArguments;
    private Response.Status status;

    /***/
    public BusinessFunctionException() {
    }

    /**
     * @param message
     */
    public BusinessFunctionException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public BusinessFunctionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public BusinessFunctionException(Throwable cause) {
        super(cause);
    }

    /**
     * @param msgKey
     * @param messageArguments
     * @param status
     */
    public BusinessFunctionException(String msgKey, Object[] messageArguments, Response.Status status) {
        this.msgKey = msgKey;
        this.messageArguments = messageArguments;
        this.status = (status == null ? Response.Status.INTERNAL_SERVER_ERROR : status);
    }

    /**
     * @param msgKey
     * @param messageArguments
     */
    public BusinessFunctionException(String msgKey, Object... messageArguments) {
        this.msgKey = msgKey;
        this.messageArguments = messageArguments;
        this.status = (status == null ? Response.Status.INTERNAL_SERVER_ERROR : status);
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (StringUtils.isBlank(msg) && StringUtils.isNotBlank(getMsgKey())) {
            msg = MessageResolver.messageFormatting(getMsgKey(), Locale.ENGLISH, getMessageArguments());
        }
        return msg;
    }

    /**
     * @return
     */
    public String getMsgKey() {
        return msgKey;
    }

    /**
     * @param msgKey
     */
    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    /**
     * @return
     */
    public Object[] getMessageArguments() {
        return messageArguments;
    }

    /**
     * @param messageArguments
     */
    public void setMessageArguments(Object[] messageArguments) {
        this.messageArguments = messageArguments;
    }

    /**
     * @return
     */
    public Response.Status getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(Response.Status status) {
        this.status = status;
    }

}