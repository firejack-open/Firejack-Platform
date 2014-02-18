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
