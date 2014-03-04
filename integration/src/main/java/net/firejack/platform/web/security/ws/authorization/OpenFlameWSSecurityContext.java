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

package net.firejack.platform.web.security.ws.authorization;

import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import org.apache.cxf.security.SecurityContext;
import org.apache.log4j.Logger;

public class OpenFlameWSSecurityContext implements SecurityContext {

    private static final Logger logger = Logger.getLogger(OpenFlameWSSecurityContext.class);

    @Override
    public OpenFlamePrincipal getUserPrincipal() {
        OpenFlamePrincipal principal;
        try {
            OPFContext context = OPFContext.getContext();
            principal = context.getPrincipal();
        } catch (ContextLookupException e) {
            logger.error(e.getMessage(), e);
            principal = null;
        }
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

}