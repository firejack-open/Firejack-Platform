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

package net.firejack.platform.core.broker.security;

import net.firejack.platform.api.authority.CommonSecurityHandler;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;

public class DeleteLookupModelSecurityHandler<L extends LookupModel> extends CommonSecurityHandler {

    private IStore<L, Long> store;

    public DeleteLookupModelSecurityHandler(IStore<L, Long> store) {
        if (store == null) {
            Logger.getLogger(this.getClass()).warn("Specified store is null.");
        }
        this.store = store;
    }

    @Override
    protected String getItemPath(Action currentAction, AbstractDTO requestData) {
        String path = null;
        if (requestData instanceof SimpleIdentifier) {
            SimpleIdentifier simpleIdentifier = (SimpleIdentifier) requestData;
            Object identifier = simpleIdentifier.getIdentifier();
            if (identifier != null) {
                Long id = null;
                String lookup = null;
                if (identifier instanceof Long) {
                    id = (Long) identifier;
                } else if (identifier instanceof String) {
                    lookup = (String) identifier;
                }
                if (id == null) {
                    if (lookup != null) {
                        path = StringUtils.getPackageLookup(lookup);
                    }
                } else {
                    L foundItem = this.store.findById(id);
                    if (foundItem != null) {
                        path = foundItem.getPath();
                    }
                }
            }
        }
        return path;
    }

}