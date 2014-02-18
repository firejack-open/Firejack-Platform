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

package net.firejack.platform.service.content.broker.resource.image;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.resource.ImageResourceModel;
import net.firejack.platform.core.model.registry.resource.ImageResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;


@Component
@TrackDetails
public class ReadResourceImageStreamBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<SimpleIdentifier<InputStream>>> {

	@Autowired
	private FileHelper helper;
	@Autowired
	@Qualifier("imageResourceStore")
	private IResourceStore<ImageResourceModel> resourceStore;

	@Autowired
	@Qualifier("imageResourceVersionStore")
	private IResourceVersionStore<ImageResourceVersionModel> resourceVersionStore;


	@Override
	protected ServiceResponse<SimpleIdentifier<InputStream>> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
		String lookup = request.getData().getIdentifier();
		ImageResourceModel resourceModel = resourceStore.findByLookup(lookup);
		if (resourceModel != null) {
			ImageResourceVersionModel resourceVersionModel = resourceVersionStore.findLastVersionByResourceId(resourceModel.getId());

			resourceModel = resourceVersionModel.getResource();
			resourceModel.setResourceVersion(resourceVersionModel);
			resourceModel.setSelectedVersion(resourceVersionModel.getVersion());

			String imageFilename = resourceVersionModel.getId() + "_" +
					resourceVersionModel.getVersion() + "_" +
					resourceVersionModel.getCulture().name();

			InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_CONTENT, imageFilename, helper.getImage(), String.valueOf(resourceModel.getId()));

			return new ServiceResponse<SimpleIdentifier<InputStream>>(new SimpleIdentifier<InputStream>(stream), "Read successfully", true);
		}

		return new ServiceResponse<SimpleIdentifier<InputStream>>("Read image :" + lookup + " failed", false);
	}
}