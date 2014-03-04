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

package net.firejack.platform.service.content.broker.resource.html;

import net.firejack.platform.api.content.domain.HtmlResource;
import net.firejack.platform.core.model.registry.resource.HtmlResourceModel;
import net.firejack.platform.core.model.registry.resource.HtmlResourceVersionModel;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.service.content.broker.resource.AbstractCreateNewResourceVersionsBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component("createNewHtmlResourceVersionsBroker")
@TrackDetails
public class CreateNewHtmlResourceVersionsBroker
        extends AbstractCreateNewResourceVersionsBroker<HtmlResourceModel, HtmlResourceVersionModel, HtmlResource> {

    @Autowired
    @Qualifier("htmlResourceStore")
    private IResourceStore<HtmlResourceModel> htmlResourceStore;

    @Autowired
    @Qualifier("htmlResourceVersionStore")
    private IResourceVersionStore<HtmlResourceVersionModel> htmlResourceVersionStore;


    @Override
    public IResourceStore<HtmlResourceModel> getResourceStore() {
        return htmlResourceStore;
    }

    @Override
    public IResourceVersionStore<HtmlResourceVersionModel> getResourceVersionStore() {
        return htmlResourceVersionStore;
    }

    @Override
    public String getResourceName() {
        return "Html";
    }

}