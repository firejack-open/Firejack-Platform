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

package net.firejack.platform.core.utils;

public interface OpenFlame {
	public static final String ROOT_DOMAIN = "net.firejack";
	public static final String PACKAGE = "net.firejack.platform";
	public static final String SYSTEM = "net.firejack.opf-local";
	public static final String DATABASE = "net.firejack.opf-local.db";

	public static final String USER_ROLE_NAME = "user";
	public static final String ADMIN_ROLE_NAME = "admin";

	public static final String SIGN_UP_ROLE = "net.firejack.platform.user";
	public static final String SIGN_UP_DIRECTORY = "net.firejack.platform.accounts";

	public static final String ROLE_GUEST = "net.firejack.platform.guest";
	public static final String ROLE_SYSTEM = "net.firejack.platform.system";
	public static final String ROLE_ADMIN = "net.firejack.platform.admin";

	public static final String FILESTORE_BASE = "net.firejack.opf-local.base";
	public static final String FILESTORE_CONTENT = "net.firejack.opf-local.content";
	public static final String FILESTORE_CONFIG = "net.firejack.opf-local.config";

	public static final String SMTP_HOST = "net.firejack.platform.process.mail.server";
	public static final String SMTP_PORT = "net.firejack.platform.process.mail.port";
	public static final String SMTP_TLS = "net.firejack.platform.process.mail.tls";
	public static final String SMTP_USERNAME = "net.firejack.platform.process.mail.username";
	public static final String SMTP_PASSWORD = "net.firejack.platform.process.mail.password";
	public static final String SMTP_EMAIL = "net.firejack.platform.process.mail.email";
	public static final String SMTP_SENDER = "net.firejack.platform.process.mail.sender";
	public static final String SMTP_DEFAULT_EMAIL = "net.firejack.platform.process.mail.email-address-to-send-unknown-notifications";

	public static final String TASK_ASSIGN_TEMPLATE = "net.firejack.platform.process.mail.task-assign-template";
	public static final String TASK_CREATE_TEMPLATE = "net.firejack.platform.process.mail.task-create-template";
	public static final String RESET_PASSWORD_TEMPLATE = "net.firejack.platform.directory.mail.reset-password-template";
	public static final String NEW_PASSWORD_TEMPLATE = "net.firejack.platform.directory.mail.new-password-template";


	public static final String WELCOME_MESSAGE_RESOURCE = "net.firejack.platform.site.welcome-message";

	public static final String DOCUMENTATION_ADD="net.firejack.platform.content.abstract-resource.resource.add-documentation";
	public static final String DOCUMENTATION_EDIT="net.firejack.platform.content.abstract-resource.resource.edit-documentation";
	public static final String DOCUMENTATION_DELETE="net.firejack.platform.content.abstract-resource.resource.delete-documentation";

	public static final String SIGN_OUT_ENTITY="net.firejack.platform.sign-out-entry-point";
}
