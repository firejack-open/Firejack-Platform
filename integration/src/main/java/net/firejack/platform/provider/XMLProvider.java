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

package net.firejack.platform.provider;

import com.sun.jersey.core.provider.jaxb.AbstractRootElementProvider;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ArrayUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@Provider
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class XMLProvider extends AbstractRootElementProvider {
	private JAXBContext request;
	private JAXBContext response;

	public XMLProvider(@Context Providers ps, @Context Class[] beans) {
		super(ps, MediaType.APPLICATION_XML_TYPE);

		Class[] requestClasses = (Class[]) ArrayUtils.add(beans, ServiceRequest.class);
		Class[] responseClasses = (Class[]) ArrayUtils.add(beans, ServiceResponse.class);
		try {
			this.request = JAXBContext.newInstance(requestClasses);
			this.response = JAXBContext.newInstance(responseClasses);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected JAXBContext getStoredJAXBContext(Class type) throws JAXBException {
		if (type == ServiceResponse.class) {
			return response;
		} else if (type == ServiceRequest.class) {
			return request;
		}
		return super.getStoredJAXBContext(type);
	}
}
