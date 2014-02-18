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

import javax.ws.rs.core.Response;


public class WebAuthorizationException extends RuntimeException {
    private static final long serialVersionUID = 3360285999590916766L;

    private String msgKey;
    private Object[] messageArguments;
    private Response.Status status;

    /**
     * @param message
     */
    public WebAuthorizationException(String message) {
        super(message);
        this.status = Response.Status.UNAUTHORIZED;
    }

    /**
     * @param message
     * @param status
     */
    public WebAuthorizationException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    /**
     * @param msgKey
     * @param messageArguments
     */
    public WebAuthorizationException(String msgKey, Object... messageArguments) {
        this.msgKey = msgKey;
        this.messageArguments = messageArguments;
        this.status = Response.Status.UNAUTHORIZED;
    }

    /**
     * @param msgKey
     * @param messageArguments
     * @param status
     */
    public WebAuthorizationException(String msgKey, Object[] messageArguments, Response.Status status) {
        this.msgKey = msgKey;
        this.messageArguments = messageArguments;
        this.status = status;
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
