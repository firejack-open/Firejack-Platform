/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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