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

package net.firejack.platform.service.content.broker.resource.document;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.resource.DocumentResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;


@Component("getDocumentResourceVersionFileBroker")
@TrackDetails
public class GetDocumentResourceVersionFileBroker
        extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<FileInfo>> {

	@Autowired
	private FileHelper helper;

    @Autowired
    @Qualifier("documentResourceVersionStore")
    private IResourceVersionStore<DocumentResourceVersionModel> resourceVersionStore;

	@Override
	public ServiceResponse<FileInfo> perform(ServiceRequest<NamedValues> request) throws Exception {
		Long resourceId = (Long) request.getData().get("resourceId");
		String documentFilename = (String) request.getData().get("documentFilename");
		ServiceResponse<FileInfo> response = new ServiceResponse<FileInfo>();
		InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_CONTENT, documentFilename, helper.getDocument(), String.valueOf(resourceId));

		FileInfo fileInfo = new FileInfo();
		fileInfo.setStream(stream);
		setOriginalFilename(documentFilename, fileInfo);
		response.addItem(fileInfo);
		response.setSuccess(true);
		return response;
	}

	private void setOriginalFilename(String documentFilename, FileInfo fileInfo) {
        String sId = documentFilename.substring(0, documentFilename.indexOf("_"));
        if (StringUtils.isNumeric(sId)) {
            Long resourceVersionId = Long.parseLong(sId);
            DocumentResourceVersionModel resourceVersion = resourceVersionStore.findById(resourceVersionId);
            if (resourceVersion != null) {
                fileInfo.setOrgFilename(resourceVersion.getOriginalFilename());
            }
        }
    }

}
