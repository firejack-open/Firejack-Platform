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

package net.firejack.platform.service.content.broker.documentation;

import net.firejack.platform.api.content.domain.ImageResourceVersion;
import net.firejack.platform.api.content.domain.ResourceContent;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.resource.ImageResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.ResourceFileUtil;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("UpdateDocumentationImageBroker")
public class UpdateDocumentationImageBroker extends ServiceBroker<ServiceRequest<ImageResourceVersion>, ServiceResponse<ResourceContent>> {

	@Autowired
	@Qualifier("resourceFileUtil")
	private ResourceFileUtil imageFileUtil;

	@Autowired
	@Qualifier("imageResourceVersionStore")
	private IResourceVersionStore<ImageResourceVersionModel> resourceVersionStore;

	@Override
	protected ServiceResponse<ResourceContent> perform(ServiceRequest<ImageResourceVersion> request) throws Exception {
		ImageResourceVersion vo = request.getData();
		ImageResourceVersionModel imageResourceVersion = resourceVersionStore.findById(vo.getId());

		String temporaryUploadedFileName = vo.getResourceFileTemporaryName();

		try {
			imageFileUtil.processTempFile(temporaryUploadedFileName, imageResourceVersion);
			imageResourceVersion.setOriginalFilename(vo.getResourceFileOriginalName());
			resourceVersionStore.saveOrUpdate(imageResourceVersion);
			return new ServiceResponse<ResourceContent>("Image updated successfully", true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ServiceResponse<ResourceContent>("Error updating image!", false);
		}
	}
}
