/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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
