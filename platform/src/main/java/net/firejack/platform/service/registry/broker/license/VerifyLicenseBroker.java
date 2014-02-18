/**
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
package net.firejack.platform.service.registry.broker.license;

import net.firejack.platform.api.registry.domain.License;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.web.security.license.LicenceHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@TrackDetails
@Component
public class VerifyLicenseBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<License>> {

	@Override
	protected ServiceResponse<License> perform(ServiceRequest<NamedValues> request) throws Exception {
		InputStream stream = (InputStream) request.getData().get("stream");

        try {
            License license = FileUtils.readJAXB(License.class, stream);
            LicenceHelper.verify(license);
            LicenceHelper.save(license);
        } catch (Exception e) {
            return new ServiceResponse<License>(e.getMessage(), false);
        }

        return new ServiceResponse<License>("Import xml successfully", true);
	}
}
