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

package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.annotation.Lookup;
import net.firejack.platform.core.model.registry.ISortable;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Class represents status entity
 */
@Entity
@Lookup(parent = RegistryNodeModel.class)
@Table(name = "opf_status")
public class StatusModel extends LookupModel<ProcessModel> implements ISortable {

    public static final String STATUS_STARTED = "Started";
    public static final String STATUS_FINISHED = "Finished";

	private static final long serialVersionUID = 1083887944959971449L;

	private Integer sortPosition;

	/**
	 * Gets the order position
	 *
	 * @return - position of the status within the process
	 */
	@Override
	@Column(name = "order_position", nullable = false, columnDefinition = "INTEGER UNSIGNED DEFAULT 0")
	public Integer getSortPosition() {
		return this.sortPosition;
	}

	/**
	 * Sets the order position
	 *
	 * @param sortPosition - position of the status within the process
	 */
	@Override
	public void setSortPosition(Integer sortPosition) {
		this.sortPosition = sortPosition;
	}

	/**
	 * Gets the type
	 *
	 * @return registry node type
	 */
	@Override
	@Transient
	public RegistryNodeType getType() {
		return RegistryNodeType.STATUS;
	}
}