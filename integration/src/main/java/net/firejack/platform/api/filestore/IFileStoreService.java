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
