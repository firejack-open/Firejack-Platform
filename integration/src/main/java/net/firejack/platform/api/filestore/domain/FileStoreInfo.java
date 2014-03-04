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

package net.firejack.platform.api.filestore.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/** Class represents filestore value object and is passed among broker and services */

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class FileStoreInfo extends AbstractDTO {
	private static final long serialVersionUID = 2763278190846006476L;

	private Long availableStorage;
	private Long totalStorage;
	private Long usedDiskSpace;

	/** Constructor without arguments */
	public FileStoreInfo() {
	}

	/**
	 * Constructor setting the storage properties
	 *
	 * @param availableStorage - available storage space
	 * @param totalStorage     - total storage space
	 * @param usedDiskSpace    - used space
	 */
	public FileStoreInfo(Long availableStorage, Long totalStorage, Long usedDiskSpace) {
		this.availableStorage = availableStorage;
		this.totalStorage = totalStorage;
		this.usedDiskSpace = usedDiskSpace;
	}

	/**
	 * Gets available storage
	 *
	 * @return available storage
	 */
	public Long getAvailableStorage() {
		return availableStorage;
	}

	/**
	 * Sets available storage
	 *
	 * @param availableStorage - available storage
	 */
	public void setAvailableStorage(Long availableStorage) {
		this.availableStorage = availableStorage;
	}

	/**
	 * Gets total storage
	 *
	 * @return total storage
	 */
	public Long getTotalStorage() {
		return totalStorage;
	}

	/**
	 * Sets total storage
	 *
	 * @param totalStorage - total storage
	 */
	public void setTotalStorage(Long totalStorage) {
		this.totalStorage = totalStorage;
	}

	/**
	 * Gets used disk space
	 *
	 * @return used disk space
	 */
	public Long getUsedDiskSpace() {
		return usedDiskSpace;
	}

	/**
	 * Sets used disk space
	 *
	 * @param usedDiskSpace - used disk space
	 */
	public void setUsedDiskSpace(Long usedDiskSpace) {
		this.usedDiskSpace = usedDiskSpace;
	}
}
