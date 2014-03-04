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

package net.firejack.platform.web.security.directory;

import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.web.security.directory.exception.UserAlreadyExistsException;

import java.util.Map;

public interface IDirectoryService {

    /**
     * @param userName
     * @param password
     * @return
     */
    IUserInfoProvider authenticate(String userName, String password);

    /**
     * @param userName
     * @param password
     * @param email
     * @param additionalProperties
     * @return
     * @throws net.firejack.platform.web.security.directory.exception.UserAlreadyExistsException
     *
     */
    IUserInfoProvider createUser(String userName, String password, String email,
                                 Map<String, String> additionalProperties) throws UserAlreadyExistsException;


    //add other methods
}