/**
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
package net.firejack.platform.web.handler;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import net.firejack.platform.core.response.ServiceResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ErrorHandler implements InvocationHandler {

	private WebResource.Builder target;

	public ErrorHandler(WebResource.Builder target) {
		this.target = target;
	}

	public static Builder getProxy(WebResource.Builder resource) {
		return (Builder) Proxy.newProxyInstance(ErrorHandler.class.getClassLoader(), new Class[]{Builder.class}, new ErrorHandler(resource));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(target, args);
		} catch (InvocationTargetException e) {
			Throwable exception = e.getTargetException();
			if (exception instanceof UniformInterfaceException) {
				ClientResponse clientResponse = ((UniformInterfaceException) exception).getResponse();
				if (clientResponse.getClientResponseStatus().equals(ClientResponse.Status.PRECONDITION_FAILED)) {
					ServiceResponse response = clientResponse.getEntity(ServiceResponse.class);
					throw new APIException(response.getMessage(), response.getData());
				} else {
					throw e;
				}
			}
			throw exception;
		}
	}
}
