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
package net.firejack.platform.service.filestore;

import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.filestore.IFileStoreService;
import net.firejack.platform.api.filestore.domain.FileStoreInfo;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IFileStore;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.service.filestore.broker.directory.CreateFileDirectoryBroker;
import net.firejack.platform.service.filestore.broker.directory.DeleteFileDirectoryBroker;
import net.firejack.platform.service.filestore.broker.directory.RenameFileDirectoryBroker;
import net.firejack.platform.service.filestore.broker.directory.SearchFileStoreBroker;
import net.firejack.platform.service.filestore.broker.file.*;
import net.firejack.platform.service.filestore.broker.zip.CreateZipBroker;
import net.firejack.platform.service.filestore.broker.zip.UnZipBroker;
import net.firejack.platform.service.filestore.broker.zip.UnZipTempBroker;
import net.firejack.platform.service.filestore.broker.zip.UpdateZipBroker;
import net.firejack.platform.utils.OpenFlameConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component(APIConstants.BEAN_NAME_FILESTORE_SERVICE)
public class FileStoreServiceLocal implements IFileStoreService {

	@Autowired
	private CreateFileDirectoryBroker createDirectoryBroker;
	@Autowired
	private DeleteFileDirectoryBroker removeDirectoryBroker;
	@Autowired
	private RenameFileDirectoryBroker renameDirectoryBroker;
	@Autowired
	private SearchFileStoreBroker searchDirectoryBroker;
	@Autowired
	private DownloadFileBroker downloadFileBroker;
	@Autowired
	private UploadFileBroker updateFileBroker;
	@Autowired
	private DeleteFileBroker deleteFileBroker;
	@Autowired
	private ReadFileStoreBroker readFilestoreBroker;
	@Autowired
	private ReadFileInfoBroker readFileInfoBroker;
	@Autowired
	private CreateZipBroker createZipBroker;
	@Autowired
	private UpdateZipBroker updateZipBroker;
	@Autowired
	private UnZipBroker unZipBroker;
    @Autowired
	private UnZipTempBroker unZipTempBroker;
	@Autowired
	private IFileStore fileStore;

	@Override
	public void valid() throws BeansException {
		if (ConfigContainer.isAppInstalled()) {
			List<FileStoreModel> list = fileStore.findAllByLookupList(Arrays.asList(
					OpenFlame.FILESTORE_BASE,
					OpenFlame.FILESTORE_CONTENT,
					OpenFlame.FILESTORE_CONFIG));

			for (FileStoreModel model : list) {
				File file = new File(model.getServerDirectory());
				if (!file.exists() || !file.isDirectory() || !file.canWrite()) {
					throw new BeanCreationNotAllowedException(APIConstants.BEAN_NAME_FILESTORE_SERVICE, "Not valid file store :" + model.getServerDirectory());
				}
			}
		} else if (OpenFlameConfig.MASTER_URL.exist()) {
			throw new BeanCreationNotAllowedException(APIConstants.BEAN_NAME_FILESTORE_SERVICE, "");
		}
	}

	@Override
	public ServiceResponse createDirectory(String lookup, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("path", path);
		return createDirectoryBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse deleteDirectory(String lookup, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("path", path);
		return removeDirectoryBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse renameDirectory(String lookup, String name, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("name", name);
		values.put("path", path);
		return renameDirectoryBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse<FileInfo> search(String lookup, String term, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("path", path);
		values.put("term", term);
		return searchDirectoryBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public InputStream download(String lookup, String filename, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("filename", filename);
		values.put("path", path);
		ServiceResponse<FileInfo> execute = downloadFileBroker.execute(new ServiceRequest<NamedValues>(values));
		return execute.getItem().getStream();
	}

	@Override
	public ServiceResponse upload(String lookup, String filename, InputStream inputStream, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("filename", filename);
		values.put("path", path);
		values.put("inputStream", inputStream);
		return updateFileBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse deleteFile(String lookup, String filename, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("filename", filename);
		values.put("path", path);
		return deleteFileBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse<FileStoreInfo> readFileStoreInfo() {
		return readFilestoreBroker.execute(new ServiceRequest());
	}

	@Override
	public ServiceResponse<FileInfo> getInfo(String lookup, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("path", path);
		return readFileInfoBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse zip(String lookup, Map<String, InputStream> stream, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("path", path);
		values.put("streams", stream);
		return createZipBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse updatezip(String lookup, Map<String, InputStream> stream, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("path", path);
		values.put("streams", stream);
		return updateZipBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse unzip(String lookup, InputStream stream, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("path", path);
		values.put("stream", stream);
		return unZipBroker.execute(new ServiceRequest<NamedValues>(values));
	}

    @Override
	public ServiceResponse<FileInfo> unzipTemp(String lookup, InputStream stream, String... path) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("path", path);
		values.put("stream", stream);
		return unZipTempBroker.execute(new ServiceRequest<NamedValues>(values));
	}
}
