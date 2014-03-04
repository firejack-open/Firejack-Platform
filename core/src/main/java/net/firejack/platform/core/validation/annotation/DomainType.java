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

package net.firejack.platform.core.validation.annotation;


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
