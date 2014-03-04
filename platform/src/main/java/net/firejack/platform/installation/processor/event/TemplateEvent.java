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

package net.firejack.platform.installation.processor.event;

import org.springframework.context.ApplicationEvent;

public class TemplateEvent extends ApplicationEvent {
	private static final long serialVersionUID = 5621614614699721619L;
	protected String name;
	protected String path;
	protected String lookup;

	public TemplateEvent(Object source) {
		super(source);
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public boolean isModify() {
		return path != null && name != null;
	}
}
