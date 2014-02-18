/*
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