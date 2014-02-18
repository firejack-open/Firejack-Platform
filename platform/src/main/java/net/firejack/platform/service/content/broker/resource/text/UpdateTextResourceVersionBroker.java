/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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
