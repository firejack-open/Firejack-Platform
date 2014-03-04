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

package net.firejack.platform.service.registry.broker.domain;

import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.registry.IRootDomainStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.service.registry.broker.DeleteRegistryNodeBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("deleteRootDomainBroker")
public class DeleteRootDomainBroker extends DeleteRegistryNodeBroker<RootDomainModel> {

	@Autowired
	private IRootDomainStore store;

	@Override
	protected String getSuccessMessage() {
		return "Root domain has deleted successfully";
	}

	@Override
	protected IStore<RootDomainModel, Long> getStore() {
		return store;
	}

    @Override
    protected void delete(Long id) {
        RootDomainModel rootDomainModel = store.findById(id);
        if (DiffUtils.extractPathFromLookup(OpenFlame.PACKAGE).equals(rootDomainModel.getLookup())) {
            throw new BusinessFunctionException("Can't delete 'net.firejack' root domain.");
        } else {
            super.delete(id);
        }
    }

}

