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

package net.firejack.platform.core.model;

import net.firejack.platform.core.model.registry.RegistryNodeType;

public class SearchModel extends BaseEntityModel {
	private static final long serialVersionUID = -4054727351470594525L;

	private String name;
	private String path;
	private String lookup;
	private String description;
	private RegistryNodeType type;

	public SearchModel() {
	}

	public SearchModel(Object... args) {
		super(((Number) args[0]).longValue());
		this.name = (String) args[1];
		this.path = (String) args[2];
		this.lookup = (String) args[3];
		this.description = (String) args[4];
		this.type = RegistryNodeType.find((String) args[5]);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RegistryNodeType getType() {
		return type;
	}

	public void setType(RegistryNodeType type) {
		this.type = type;
	}
}
