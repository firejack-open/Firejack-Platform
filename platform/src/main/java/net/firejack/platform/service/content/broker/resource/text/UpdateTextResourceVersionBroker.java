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

package net.firejack.platform.service.content.broker.resource.text;

import net.firejack.platform.api.content.domain.TextResourceVersion;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.model.registry.resource.TextResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component("updateTextResourceVersionBroker")
@TrackDetails
public class UpdateTextResourceVersionBroker
        extends ServiceBroker<ServiceRequest<TextResourceVersion>, ServiceResponse<TextResourceVersion>> {

    @Autowired
    @Qualifier("textResourceVersionStore")
    private IResourceVersionStore<TextResourceVersionModel> textResourceVersionStore;

    @Override
    protected ServiceResponse<TextResourceVersion> perform(ServiceRequest<TextResourceVersion> request) throws Exception {
        Long resourceId = request.getData().getResourceId();
        Integer version = request.getData().getVersion();
        Cultures culture = request.getData().getCulture();

        try {
            TextResourceVersionModel textResourceVersionModel = textResourceVersionStore.findByResourceIdCultureAndVersion(resourceId, culture, version);
            if (textResourceVersionModel == null) {
                textResourceVersionModel = textResourceVersionStore.createNewResourceVersion(resourceId, version, culture);
            }

            textResourceVersionModel.setText(request.getData().getText());
            textResourceVersionStore.saveOrUpdate(textResourceVersionModel);

            TextResourceVersion textResourceVersion = factory.convertTo(TextResourceVersion.class, textResourceVersionModel);

            return new ServiceResponse<TextResourceVersion>(textResourceVersion, "Text resource saved successfully.", true);
        } catch (Exception e) {
            logger.error("error saving text resource", e);
            return new ServiceResponse<TextResourceVersion>("Error saving text resource!", false);
        }
    }
}
