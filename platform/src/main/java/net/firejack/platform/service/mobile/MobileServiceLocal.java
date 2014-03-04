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

package net.firejack.platform.service.mobile;


import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.mobile.IMobileService;
import net.firejack.platform.api.mobile.domain.MenuItem;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.service.mobile.broker.LoadMenuBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class is an implementation of net.firejack.platform.api.mail.IMobileService
 * Business layer is invoked to serve the requests locally
 */
@SuppressWarnings("unused")
@Component(APIConstants.BEAN_NAME_MOBILE_SERVICE)
public class MobileServiceLocal implements IMobileService {

	@Autowired
	private LoadMenuBroker loadMenuBroker;

	@Override
	public ServiceResponse<MenuItem> loadMenu(String lookup) {
		return loadMenuBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(lookup)));
	}
}