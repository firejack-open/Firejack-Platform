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