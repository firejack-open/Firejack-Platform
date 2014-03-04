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
