/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.config.meta.element.BaseNavigableRegistryNodeElement;
import net.firejack.platform.core.config.meta.element.NavigationConfigElement;
import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.config.meta.element.action.ActionParameterElement;
import net.firejack.platform.core.config.meta.element.authority.ResourceLocationElement;
import net.firejack.platform.core.model.registry.INavigable;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.ActionParameterModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;

import java.util.ArrayList;
import java.util.List;


public class NavigableElementFactory<Ent extends RegistryNodeModel, RN extends BaseNavigableRegistryNodeElement>
        extends PackageDescriptorConfigElementFactory<Ent, RN> {

    @Override
    protected void initEntitySpecific(Ent entity, RN rnElement) {
        super.initEntitySpecific(entity, rnElement);
        if (entity instanceof INavigable) {
            initNavigableProperties(rnElement, (INavigable) entity);
            if (entity instanceof ActionModel && rnElement instanceof ActionElement) {
                ActionModel action = (ActionModel) entity;
                ActionElement actionElement = (ActionElement) rnElement;
                action.setMethod(actionElement.getMethod());
                action.setSoapUrlPath(actionElement.getSoapUrlPath());
                action.setSoapMethod(actionElement.getSoapMethod());
                List<ActionParameterElement> parameterElementList = actionElement.getParameters();
                if(parameterElementList != null && !parameterElementList.isEmpty()) {
                    List<ActionParameterModel> parameters = new ArrayList<ActionParameterModel>();
                    for (ActionParameterElement parameterElement : parameterElementList) {
                        ActionParameterModel parameter = new ActionParameterModel();
                        parameter.setName(parameterElement.getName());
                        parameter.setDescription(parameterElement.getDescription());
                        parameter.setFieldType(parameterElement.getType());
                        parameter.setLocation(parameterElement.getLocation());
                        parameter.setOrderPosition(parameterElement.getOrderPosition());
                        parameter.setParent(action);
                        PackageDescriptorConfigElementFactory.initializeModelUID(
                                parameter, parameterElement);
                        parameters.add(parameter);
                    }
                    action.setActionParameters(parameters);
                }
            } else if (entity instanceof NavigationElementModel && rnElement instanceof NavigationConfigElement) {
                NavigationElementModel navElement = (NavigationElementModel) entity;
                NavigationConfigElement navigationConfigElement = (NavigationConfigElement) rnElement;
                navElement.setProtocol(navigationConfigElement.getProtocol());
                navElement.setSortPosition(navigationConfigElement.getOrder());
                navElement.setPageUrl(navigationConfigElement.getPageUrl());
                navElement.setUrlParams(navigationConfigElement.getUrlParams());
                navElement.setElementType(navigationConfigElement.getType());
                Reference reference = navigationConfigElement.getReference();
                if (reference != null) {
                    RegistryNodeModel mainModel = new RegistryNodeModel();
                    mainModel.setName(reference.getRefName());
                    mainModel.setPath(reference.getRefPath());
                    navElement.setMain(mainModel);
                }
                if (Boolean.TRUE.equals(navigationConfigElement.getHidden())) {
                    navElement.setHidden(navigationConfigElement.getHidden());
                }
            } else if (entity instanceof ResourceLocationModel && rnElement instanceof ResourceLocationElement) {
                ResourceLocationModel resourceLocation = (ResourceLocationModel) entity;
                ResourceLocationElement resourceLocationElement = (ResourceLocationElement) rnElement;
                resourceLocation.setWildcardStyle(resourceLocationElement.getWildcardStyle());
            }
        }
    }

    @Override
    protected void initDescriptorElementSpecific(RN rnElement, Ent entity) {
        super.initDescriptorElementSpecific(rnElement, entity);
        if (entity instanceof INavigable) {
            initNavigableProperties((INavigable) entity, rnElement);
            if (entity instanceof ActionModel && rnElement instanceof ActionElement) {
                ActionModel action = (ActionModel) entity;
                ActionElement actionElement = (ActionElement) rnElement;
                actionElement.setMethod(action.getMethod());
                actionElement.setSoapUrlPath(action.getSoapUrlPath());
                actionElement.setSoapMethod(action.getSoapMethod());

                if (action.getInputVOEntity() != null) {
                    actionElement.setInputVOEntityLookup(action.getInputVOEntity().getLookup());
                }
                if (action.getOutputVOEntity() != null) {
                    actionElement.setOutputVOEntityLookup(action.getOutputVOEntity().getLookup());
                }

                List<ActionParameterModel> actionParameters = action.getActionParameters();
                if (actionParameters != null) {
                    List<ActionParameterElement> parameters = new ArrayList<ActionParameterElement>();
                    for (ActionParameterModel actionParameter : actionParameters) {
                        ActionParameterElement parameter = new ActionParameterElement();
                        parameter.setName(actionParameter.getName());
                        parameter.setDescription(actionParameter.getDescription());
                        parameter.setType(actionParameter.getFieldType());
                        parameter.setLocation(actionParameter.getLocation());
                        parameter.setOrderPosition(actionParameter.getOrderPosition());
                        PackageDescriptorConfigElementFactory.initializeConfigElementUID(
                                parameter, actionParameter, registryNodeStore);
                        parameters.add(parameter);
                    }
                    actionElement.setParameters(parameters);
                }
            } else if (entity instanceof NavigationElementModel && rnElement instanceof NavigationConfigElement) {
                NavigationElementModel navElement = (NavigationElementModel) entity;
                NavigationConfigElement navigationConfigElement = (NavigationConfigElement) rnElement;
                navigationConfigElement.setProtocol(navElement.getProtocol());
                navigationConfigElement.setOrder(navElement.getSortPosition());
                navigationConfigElement.setPageUrl(navElement.getPageUrl());
                navigationConfigElement.setUrlParams(navElement.getUrlParams());
                navigationConfigElement.setType(navElement.getElementType());
                if (Boolean.TRUE.equals(navElement.getHidden())) {
                    navigationConfigElement.setHidden(navElement.getHidden());
                }

                RegistryNodeModel registryNode = navElement.getMain();
                if (registryNode != null ) {
                    Reference reference = new Reference(registryNode.getName(), registryNode.getPath());
                    navigationConfigElement.setReference(reference);
                }
            } else if (entity instanceof ResourceLocationModel && rnElement instanceof ResourceLocationElement) {
                ResourceLocationModel resourceLocation = (ResourceLocationModel) entity;
                ResourceLocationElement resourceLocationElement = (ResourceLocationElement) rnElement;
                resourceLocationElement.setWildcardStyle(resourceLocation.getWildcardStyle());
            }
        }
    }

    @Override
    protected String getRefPath(Ent entity) {
        return entity.getPath();
    }

    private void initNavigableProperties(INavigable sourceNavigable, INavigable targetNavigable) {
        if (sourceNavigable == null || targetNavigable == null) {
            return;
        }
        targetNavigable.setServerName(sourceNavigable.getServerName());
        targetNavigable.setPort(sourceNavigable.getPort());
        targetNavigable.setParentPath(sourceNavigable.getParentPath());
        targetNavigable.setUrlPath(sourceNavigable.getUrlPath());
    }

}