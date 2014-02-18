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

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

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
