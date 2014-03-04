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
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.resource.TextResourceModel;
import net.firejack.platform.core.model.registry.resource.TextResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.resource.TextResourceStore;
import net.firejack.platform.core.validation.ValidationMessage;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component("createTextResourceVersionBroker")
@TrackDetails
public class CreateTextResourceVersionBroker
        extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<TextResourceVersion>> {

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Autowired
    @Qualifier("textResourceStore")
    public TextResourceStore textResourceStore;

    @Override
    protected List<ValidationMessage> specificArgumentsValidation(ServiceRequest<NamedValues> request) throws RuleValidationException {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        String resourceLookup = (String) request.getData().get("resourceLookup");
        RegistryNodeModel registryNodeModel = registryNodeStore.findByLookup(resourceLookup);
        if (registryNodeModel != null) {
            ValidationMessage validationMessage = new ValidationMessage(
                    null, "resource.already.exist", resourceLookup);
            validationMessages.add(validationMessage);
        }
        return validationMessages;
    }


    @Override
    protected ServiceResponse<TextResourceVersion> perform(ServiceRequest<NamedValues> request) throws Exception {
        String resourceLookup = (String) request.getData().get("resourceLookup");
        TextResourceVersion textResourceVersion = (TextResourceVersion) request.getData().get("textResourceVersion");

        try {
            String parentLookup = DiffUtils.extractPathFromLookup(resourceLookup);
            RegistryNodeModel parentModel = registryNodeStore.findByLookup(parentLookup);
            if (parentModel != null) {
                String name = DiffUtils.humanNameFromLookup(resourceLookup);

                TextResourceModel textResourceModel = new TextResourceModel();
                textResourceModel.setName(name);
                textResourceModel.setParent(parentModel);

                TextResourceVersionModel textResourceVersionModel = factory.convertFrom(TextResourceVersionModel.class, textResourceVersion);
                textResourceVersionModel.setResource(textResourceModel);
                textResourceVersionModel.setVersion(1);

                textResourceModel.setResourceVersion(textResourceVersionModel);
                textResourceModel.setSelectedVersion(1);
                textResourceModel.setLastVersion(1);

                textResourceStore.save(textResourceModel);

                textResourceVersion = factory.convertTo(TextResourceVersion.class, textResourceVersionModel);
                return new ServiceResponse<TextResourceVersion>(textResourceVersion, "Text resource saved successfully.", true);
            } else {
                return new ServiceResponse<TextResourceVersion>("Can't find parent for created resource.", false);
            }
        } catch (Exception e) {
            logger.error("error saving text resource", e);
            return new ServiceResponse<TextResourceVersion>("Error saving text resource!", false);
        }
    }
}
