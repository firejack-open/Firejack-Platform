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

package net.firejack.platform.core.validation.service;

import com.sun.jersey.spi.resource.Singleton;
import net.firejack.platform.core.broker.rule.ReadRuleBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.validation.constraint.vo.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


@Component("ruleService")
@Singleton
@Path("rule")
public class RuleService {

    @Autowired
    @Qualifier("readRuleBroker")
    private ReadRuleBroker readRuleBroker;

    /**
     * @param constraintSourceId
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Field> formConstructor(@QueryParam(value = "id") String constraintSourceId) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(constraintSourceId);
        return readRuleBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

}