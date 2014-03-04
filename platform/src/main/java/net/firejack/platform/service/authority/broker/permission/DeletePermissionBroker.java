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

package net.firejack.platform.service.authority.broker.permission;

import net.firejack.platform.core.broker.DeleteLookupModelBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.registry.IPermissionStore;
import net.firejack.platform.core.validation.ValidationMessage;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@TrackDetails
@Component("deletePermissionBrokerEx")
public class DeletePermissionBroker extends DeleteLookupModelBroker<PermissionModel> {

    @Autowired
    @Qualifier("permissionStore")
    private IPermissionStore permissionStore;

    @Override
    protected List<ValidationMessage> specificArgumentsValidation(
            ServiceRequest<SimpleIdentifier<Long>> request) throws RuleValidationException {
        List<ValidationMessage> validationMessages = super.specificArgumentsValidation(request);

        validationMessages = validationMessages == null ?
                new ArrayList<ValidationMessage>() : new ArrayList<ValidationMessage>(validationMessages);

        Long id = request.getData().getIdentifier();
        PermissionModel permission = permissionStore.findByIdWithRolesAndNavElements(id);
        if (permission.getRoles() != null && !permission.getRoles().isEmpty()) {
            ValidationMessage validationMessage = new ValidationMessage(null, "delete.failure.permission.associate.with.roles", id);
            validationMessages.add(validationMessage);
        }
        if (permission.getNavigationElements() != null && !permission.getNavigationElements().isEmpty()) {
            ValidationMessage validationMessage = new ValidationMessage(null, "delete.failure.permission.associate.with.nav.elements", id);
            validationMessages.add(validationMessage);
        }
        return validationMessages;
    }

    @Override
    protected IStore<PermissionModel, Long> getStore() {
        return permissionStore;
    }
}