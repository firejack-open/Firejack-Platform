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

package net.firejack.platform.web.progress;

import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.mina.aop.ProgressReadStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("progress")
public class ProgressEndpoint {


    @Autowired
	private ProgressReadStatus progressReadStatus;

    /**
	 * Read progress status
	 *
	 * @param uid  uid
	 * @return founded progress statuses
	 */
	@GET
	@Path("/status")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse progress(@HeaderParam("Page-UID") String  uid) {
		List progress = progressReadStatus.getProgress(uid);
		return new ServiceResponse(progress, "Load progress", true);
	}

}
