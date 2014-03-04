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

package net.firejack.platform.api.filestore;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import net.firejack.platform.api.ServiceProxyExecutor;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.web.handler.Builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

public abstract class FileStoreServiceProxyExecutor<T> extends ServiceProxyExecutor<T> {

    private FileStoreServiceProxy serviceProxy;

    public FileStoreServiceProxyExecutor(FileStoreServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    public T request(Builder builder) {
        T response = null;
        try {
            response = doRequest(builder);
        } catch (Exception e) {
            if (e instanceof UndeclaredThrowableException) {
                Throwable undeclaredThrowable = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
                if (undeclaredThrowable instanceof InvocationTargetException) {
                    Throwable targetException = ((InvocationTargetException) undeclaredThrowable).getTargetException();
                    if (targetException instanceof UniformInterfaceException) {
                        ClientResponse clientResponse = ((UniformInterfaceException) targetException).getResponse();
                        if (ClientResponse.Status.FORBIDDEN.getStatusCode() == clientResponse.getStatus()) {
                            response = doRequestException(builder);
                        }
                    }
                }
            }
            if (response == null) {
                throw new BusinessFunctionException(e);
            }
        }
        return response;
    }

    public T doRequestException(Builder builder) {
        serviceProxy.resetSessionToken();
        serviceProxy.addCookie(builder);
        return doRequest(builder);
    }

}
