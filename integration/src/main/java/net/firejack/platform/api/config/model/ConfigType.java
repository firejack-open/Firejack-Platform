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

package net.firejack.platform.api.config.model;


import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.registry.domain.Database;
import net.firejack.platform.api.registry.domain.Filestore;
import net.firejack.platform.api.registry.domain.System;

public enum ConfigType {
	STRING(Config.class),
	SYSTEM(System.class),
	DATABASE(Database.class),
	FILESTORE(Filestore.class)/*,
    OPEN_FLAME_CONNECTION(OpenFlameConnectionConfig.class)*/;

	private Class clazz;

	private ConfigType(Class clazz) {
		this.clazz = clazz;
	}

	/** @return  */
	public Class getClazz() {
		return clazz;
	}
}
