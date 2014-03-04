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

public class PrepareTemplateEvent extends TemplateEvent {
	private static final long serialVersionUID = 5621614614699721619L;

	public PrepareTemplateEvent(Object source) {
		super(source);
	}

	public PrepareTemplateEvent(Object source, String path, String name) {
		super(source);
		this.path = path;
		this.name = name;
	}
}
