/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
