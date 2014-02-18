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

package net.firejack.platform.web.security.ws.interceptor;

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.ws.WSContextDataHolder;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.SoapVersion;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenFlameWSS4JOutInterceptor extends WSS4JOutInterceptor {

	private static final Logger logger = Logger.getLogger(OpenFlameWSS4JOutInterceptor.class);

	public OpenFlameWSS4JOutInterceptor() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("action", "NoSecurity");
		setProperties(map);
	}

	@Override
	public void handleMessage(SoapMessage mc) throws Fault {
		List<Header> headers = mc.getHeaders();
		SoapVersion version = mc.getVersion();
		try {
			String token = WSContextDataHolder.getCurrentToken();
			if (StringUtils.isNotBlank(token)) {
				Header header = new Header(
						WSContextDataHolder.AUTHENTICATION_HEADER,
						token, new JAXBDataBinding(String.class));
				headers.add(header);
			} else {
				throw new SoapFault("Authentication token was not found.", version.getSender());
			}
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new SoapFault(e.getMessage(), e, version.getSender());
		} finally {
			WSContextDataHolder.cleanup();
		}
		super.handleMessage(mc);
	}

	/*@Override
		public Object getOption(String key) {
			if (WSHandlerConstants.USER.equals(key)) {
				return WSContextDataHolder.getCurrentUserName();
			}
			return super.getOption(key);
		}*/
}