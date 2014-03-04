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

package net.firejack.platform.web.handler;

import net.firejack.platform.core.domain.ServerError;

import java.util.List;

public class APIException extends RuntimeException {
	private static final long serialVersionUID = 6393570217803708517L;

	private String message;
	private List<ServerError> errors;

	public APIException(String message, List<ServerError> errors) {
		this.message = message;
		this.errors = errors;
	}

	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		if (message != null) {
			builder.append(message).append(":");
		}
		for (ServerError error : errors) {
			builder.append("\n");
			if (error.getName() != null) {
				builder.append("Field = ").append(error.getName()).append(",");
			}
			builder.append(" Error = ").append(error.getMsg());
		}
		return builder.toString();
	}
}
