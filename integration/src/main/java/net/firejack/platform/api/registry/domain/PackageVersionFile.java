package net.firejack.platform.api.registry.domain;

import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.registry.model.PackageFileType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class PackageVersionFile extends FileInfo {
	private static final long serialVersionUID = -4519670996422774869L;
	private PackageFileType type;

	public PackageVersionFile() {
	}

	public PackageVersionFile(String filename, Long updated, PackageFileType type) {
	    super(filename, updated);
	    this.type = type;
	}

	public PackageFileType getType() {
		return type;
	}

	public void setType(PackageFileType type) {
		this.type = type;
	}
}
