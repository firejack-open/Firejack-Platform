package net.firejack.platform.core.validation.annotation;
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


public enum DomainType {
	ROOT_DOMAIN,
	DOMAIN,
	SUB_DOMAIN,
	SYSTEM,
	SERVER,
	DATABASE,
	FILESTORE,
	PACKAGE,
	ENTITY,
	SUB_ENTITY,
	BI_REPORT,
	REPORT,
    WIZARD,
	FIELD(true),
	INDEX(true),
	RELATIONSHIP,
	ACTION,
	ACTION_PARAMETER(true),
	DIRECTORY,
	GROUP,
	USER(true),
	SYSTEM_USER(true),
	USER_PROFILE_FIELD(true),
	USER_PROFILE_FIELD_GROUP(true),
	PERMISSION(true),
	ROLE(true),
	RESOURCE_LOCATION(true),
	FOLDER(true),
	COLLECTION,
	NAVIGATION_ELEMENT,
	TEXT_RESOURCE,
	HTML_RESOURCE,
	IMAGE_RESOURCE,
	AUDIO_RESOURCE,
	VIDEO_RESOURCE,
	DOCUMENT_RESOURCE,
	FILE_RESOURCE,
	CONFIG(true),
	PROCESS,
	ACTOR,
	ACTIVITY(true),
	STATUS(true),
	SCHEDULE(true);

	private boolean external;

	private DomainType() {
	}

	private DomainType(boolean external) {
		this.external = external;
	}

	public boolean isExternal() {
		return external;
	}
}
