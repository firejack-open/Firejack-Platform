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

package net.firejack.platform.web.security.extension.sr;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.sr.CachedSecuredRecordDataLoader;

import java.util.List;
import java.util.Map;

@Deprecated
public class ConsoleSecuredRecordDataLoader extends CachedSecuredRecordDataLoader {

    @Override
    protected Map<Long, List<UserPermission>> loadSRContextPermissionsForLocalUse() {
        OPFContext context = OPFContext.getContext();
        OpenFlamePrincipal principal = context.getPrincipal();
        return principal.isGuestPrincipal() ? null :
                CacheManager.getInstance().getSecuredRecordContextPermissions(
                        principal.getUserInfoProvider().getId());
    }

    @Override
    protected Map<Long, SecuredRecordNode> loadSecuredRecordsForLocalUse() {
        return CacheManager.getInstance().getSecuredRecords();
    }

}