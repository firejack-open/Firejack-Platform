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

package net.firejack.platform.service.content.broker.resource.audio;

import net.firejack.platform.api.content.domain.AudioResource;
import net.firejack.platform.core.model.registry.resource.AudioResourceModel;
import net.firejack.platform.core.model.registry.resource.AudioResourceVersionModel;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.service.content.broker.resource.AbstractReadResourceByLookupBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component("readAudioResourceByLookupBroker")
@TrackDetails
public class ReadAudioResourceByLookupBroker
        extends AbstractReadResourceByLookupBroker<AudioResourceModel, AudioResourceVersionModel, AudioResource> {

    @Autowired
    @Qualifier("audioResourceStore")
    private IResourceStore<AudioResourceModel> resourceStore;

    @Autowired
    @Qualifier("audioResourceVersionStore")
    private IResourceVersionStore<AudioResourceVersionModel> resourceVersionStore;

    @Override
    public IResourceStore<AudioResourceModel> getResourceStore() {
        return resourceStore;
    }

    @Override
    public IResourceVersionStore<AudioResourceVersionModel> getResourceVersionStore() {
        return resourceVersionStore;
    }

}