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
package net.firejack.platform.api.filestore;

import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.filestore.domain.FileStoreInfo;
import net.firejack.platform.core.response.ServiceResponse;
import org.springframework.beans.BeansException;

import java.io.InputStream;
import java.util.Map;

public interface IFileStoreService {

	void valid() throws BeansException;

	/**
	 * Service creates the directory
	 *
	 *
	 *
	 * @param lookup
	 * @param path - directory path
	 *
	 * @return information about the success of the creation operation
	 */
	ServiceResponse createDirectory(String lookup, String... path);

	/**
	 * Service removes the directory
	 *
	 *
	 * @param lookup
	 * @param path - directory path
	 *
	 * @return information about the success of the removal operation
	 */
	ServiceResponse deleteDirectory(String lookup, String... path);

	/**
	 * Service modifies the directory name
	 *
	 *
	 *
	 *
	 *
	 * @param lookup
	 * @param name
	 * @param path    - directory path
	 * @return information about the success of the modification operation
	 */
	ServiceResponse renameDirectory(String lookup, String name, String... path);

	/**
	 * Service searches the directory for the file/directory that conform to the search criteria
	 *
	 *
	 *
	 * @param lookup
	 * @param term - term to search by
	 *
	 * @param path - directory path
	 * @return found file/directory
	 */
	ServiceResponse<FileInfo> search(String lookup, String term, String... path);

	/**
	 * Service retrieves the file
	 *
	 *
	 *
	 * @param lookup
	 * @param filename - file name
	 *
	 * @param path     - file path
	 * @return file to be downloaded
	 */
	InputStream download(String lookup, String filename, String... path);

	/**
	 * Service updates the file
	 *
	 *
	 * @param lookup
	 * @param filename    - file name
	 * @param inputStream - new file content
	 *
	 * @param path        - file path
	 * @return information about the success of the modification operation
	 */
	ServiceResponse upload(String lookup, String filename, InputStream inputStream, String... path);

	/**
	 * Service removes the file
	 *
	 *
	 * @param lookup
	 * @param filename - file name
	 *
	 * @param path     - file path
	 * @return information about the success of the deletion operation
	 */
	ServiceResponse deleteFile(String lookup, String filename, String... path);

	/**
	 * Service reads information about the filestore
	 *
	 * @return filestore value object
	 */
	ServiceResponse<FileStoreInfo> readFileStoreInfo();

	ServiceResponse<FileInfo> getInfo(String lookup, String... path);

	ServiceResponse zip(String lookup, Map<String, InputStream> stream, String... path);

	ServiceResponse updatezip(String lookup, Map<String, InputStream> stream, String... path);

	ServiceResponse unzip(String lookup, InputStream stream, String... path);

    ServiceResponse<FileInfo> unzipTemp(String lookup, InputStream stream, String... path);

}
