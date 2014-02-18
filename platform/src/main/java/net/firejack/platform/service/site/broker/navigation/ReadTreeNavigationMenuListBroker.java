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

package net.firejack.platform.service.site.broker.navigation;

import net.firejack.platform.api.content.domain.ImageResource;
import net.firejack.platform.api.content.domain.TextResource;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.api.site.domain.NavigationElementTree;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.service.content.broker.resource.image.ReadImageResourceByLookupBroker;
import net.firejack.platform.service.content.broker.resource.text.ReadTextResourceByLookupBroker;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.navigation.INavElementContainer;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TrackDetails
@Component("readTreeNavigationMenuListBroker")
public class ReadTreeNavigationMenuListBroker
		extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<NavigationElementTree>> {

    public static final String MSG_WRONG_LOOKUP = "Wrong lookup format for parent navigation element.";
    public static final String MSG_APPROPRIATE_NAVIGATION_ELEMENTS_WERE_FOUND = "No appropriate navigation element were found by specified lookup.";
    public static final String MSG_NO_SUCH_PARENT_NAVIGATION_WAS_FOUND = "No navigation element found for specified lookup.";

    @Autowired
    private ReadTextResourceByLookupBroker readTextResourceByLookupBroker;

    @Autowired
    private ReadImageResourceByLookupBroker readImageResourceByLookupBroker;

    @Override
    public ServiceResponse<NavigationElementTree> perform(ServiceRequest<NamedValues> request) throws Exception {
        String lookup = (String) request.getData().get("lookup");
	    boolean lazyLoadResource = (Boolean) request.getData().get("lazyLoadResource");

	    List<NavigationElementTree> navigationElementTrees = new ArrayList<NavigationElementTree>();
        if (ConfigContainer.isAppInstalled() && StringUtils.isNotBlank(lookup)) {
            String packageLookup = StringUtils.getPackageLookup(lookup);
            if (packageLookup == null) {
                logger.warn(MSG_WRONG_LOOKUP);
            } else {
                OPFContext context = OPFContext.getContext();
                INavElementContainer navElementContainer = context.getNavElementContainer();
                List<NavigationElement> navElementListByPackage = navElementContainer.getNavElementListByPackage(packageLookup);
                if (navElementListByPackage == null || navElementListByPackage.isEmpty()) {
                    logger.warn(MSG_APPROPRIATE_NAVIGATION_ELEMENTS_WERE_FOUND);
                } else {
                    NavigationElementTree navigationElementTree = null;
                    NavigationElement parent = null;
                    for (NavigationElement vo : navElementListByPackage) {
                        if (vo.getLookup().equalsIgnoreCase(lookup)) {
                            parent = vo;
                            navigationElementTree = convert(parent);
                            break;
                        }
                    }
                    if (parent == null) {
                        logger.warn(MSG_NO_SUCH_PARENT_NAVIGATION_WAS_FOUND);
                    } else {
                        buildTree(navigationElementTree, parent, navElementListByPackage, lazyLoadResource);
                        navigationElementTrees = navigationElementTree.getChildren();
                    }
                }
            }
        }
        return new ServiceResponse<NavigationElementTree>(navigationElementTrees, "Return tree navigation elements.", true);
    }

    private void buildTree(NavigationElementTree parentNavigationElementTree, NavigationElement parent, List<NavigationElement> navigationElementList, boolean lazyLoadResource) {
        List<NavigationElementTree> children = parentNavigationElementTree.getChildren();
        for (NavigationElement navigationElement : navigationElementList) {
            if (parent.getLookup().endsWith(navigationElement.getPath())) {
                OpenFlamePrincipal principal = OPFContext.getContext().getPrincipal();
                if (principal.checkUserPermission(navigationElement)) {
	                if (!lazyLoadResource) {
		                initResourceContents(navigationElement);
	                }
	                NavigationElementTree navigationElementTree = convert(navigationElement);
                    children.add(navigationElementTree);
                    buildTree(navigationElementTree, navigationElement, navigationElementList, lazyLoadResource);
                }
            }
        }
        Collections.sort(children, NavigationElementTree.SORT_POSITION);
    }

	private void initResourceContents(NavigationElement navigationElement) {
		try {
            SimpleIdentifier<String> textIdentifier = new SimpleIdentifier<String>(navigationElement.getLookup() + ".menu-name");
            ServiceRequest<SimpleIdentifier<String>> textResourceRequest = new ServiceRequest<SimpleIdentifier<String>>(textIdentifier);
            ServiceResponse<TextResource> textResourceResponse = readTextResourceByLookupBroker.perform(textResourceRequest);
			if (textResourceResponse.isSuccess()) {
				navigationElement.setTextResourceVersion(textResourceResponse.getItem().getResourceVersion());
			}

			SimpleIdentifier<String> imageIdentifier = new SimpleIdentifier<String>(navigationElement.getLookup() + ".menu-icon");
			ServiceRequest<SimpleIdentifier<String>> imageResourceRequest = new ServiceRequest<SimpleIdentifier<String>>(imageIdentifier);
			ServiceResponse<ImageResource> imageResourceResponse = readImageResourceByLookupBroker.perform(imageResourceRequest);
			if (imageResourceResponse.isSuccess()) {
				navigationElement.setImageResourceVersion(imageResourceResponse.getItem().getResourceVersion());
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private NavigationElementTree convert(NavigationElement navigationElement) {
        NavigationElementTree navigationElementTree = new NavigationElementTree();
        String name = navigationElement.getName();
        if (navigationElement.getTextResourceVersion() != null) {
            name = navigationElement.getTextResourceVersion().getText();
        }
        navigationElementTree.setName(name);
        navigationElementTree.setUrlPath(navigationElement.getUrlPath());
        navigationElementTree.setPageUrl(navigationElement.getPageUrl());
        navigationElementTree.setElementType(navigationElement.getElementType());
        navigationElementTree.setPath(navigationElement.getPath());
        navigationElementTree.setLookup(navigationElement.getLookup());
        navigationElementTree.setSortPosition(navigationElement.getSortPosition());
        navigationElementTree.setChildren(new ArrayList<NavigationElementTree>());
        navigationElementTree.setAllowDrag(false);
        navigationElementTree.setAllowDrop(false);
        navigationElementTree.setExpandable(true);
        navigationElementTree.setExpanded(false);
        navigationElementTree.setHidden(navigationElement.getHidden() != null && navigationElement.getHidden());
        return navigationElementTree;
    }

}
