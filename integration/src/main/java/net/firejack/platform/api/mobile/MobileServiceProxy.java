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

package net.firejack.platform.api.mobile;


import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.mobile.domain.MenuItem;
import net.firejack.platform.core.response.ServiceResponse;

/**
 * Class is an implementation of net.firejack.platform.api.process.IMailService
 * The services are invoked in RESTful manner
 */
public class MobileServiceProxy extends AbstractServiceProxy implements IMobileService {

	public MobileServiceProxy(Class[] classes) {
		super(classes);
	}

	@Override
    public String getServiceUrlSuffix() {
        return "/mobile";
    }

	@Override
	public ServiceResponse<MenuItem> loadMenu(String lookup) {
		return get("menu/"+lookup);
	}
}