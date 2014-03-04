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