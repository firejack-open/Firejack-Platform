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

package net.firejack.platform.service.content.broker.resource;

import net.firejack.platform.api.content.domain.Culture;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component("readAllCulturesBroker")
@TrackDetails
public class ReadAllCulturesBroker
        extends ServiceBroker<ServiceRequest, ServiceResponse<Culture>> {

    @Override
    public ServiceResponse<Culture> perform(ServiceRequest message) throws Exception {
        List<Culture> cultures = new ArrayList<Culture>();
        for (Cultures cult : Cultures.values()) {
            Culture culture = new Culture(cult, cult.getLocale().getCountry());
            cultures.add(culture);
        }
        return new ServiceResponse<Culture>(cultures, "Return list of all cultures.", true);
    }
    
}
