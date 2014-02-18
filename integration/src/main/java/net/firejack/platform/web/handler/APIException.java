/**
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
