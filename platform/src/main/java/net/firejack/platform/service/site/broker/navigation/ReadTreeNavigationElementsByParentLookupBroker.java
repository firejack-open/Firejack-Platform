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
import net.firejack.platform.core.broker.ServiceBroker;
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

import java.util.*;

@Component("readTreeNavigationElementsByParentLookupBroker")
@TrackDetails
public class ReadTreeNavigationElementsByParentLookupBroker
        extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<NavigationElement>> {

    public static final String MSG_WRONG_LOOKUP = "Wrong lookup format for parent navigation element.";
    public static final String MSG_APPROPRIATE_NAVIGATION_ELEMENTS_WERE_FOUND = "No appropriate navigation element were found by specified lookup.";
    public static final String MSG_NO_SUCH_PARENT_NAVIGATION_WAS_FOUND = "No navigation element found for specified lookup.";

    @Autowired
    private ReadTextResourceByLookupBroker readTextResourceByLookupBroker;

    @Autowired
    private ReadImageResourceByLookupBroker readImageResourceByLookupBroker;

    @Override
    public ServiceResponse<NavigationElement> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        String lookup = request.getData().getIdentifier();
        List<NavigationElement> resultNavElements = new LinkedList<NavigationElement>();
        if (ConfigContainer.isAppInstalled() && StringUtils.isNotBlank(lookup)) {
            String packageLookup = StringUtils.getPackageLookup(lookup);
            if (packageLookup == null) {
                logger.warn(MSG_WRONG_LOOKUP);
            } else {
                OPFContext context = OPFContext.getContext();
                INavElementContainer navElementContainer = context.getNavElementContainer();
                List<NavigationElement> navElementListByPackage =
                        navElementContainer.getNavElementListByPackage(packageLookup);
                if (navElementListByPackage == null || navElementListByPackage.isEmpty()) {
                    logger.warn(MSG_APPROPRIATE_NAVIGATION_ELEMENTS_WERE_FOUND);
                } else {
                    NavigationElement parent = null;
                    for (NavigationElement vo : navElementListByPackage) {
                        if (vo.getLookup().equalsIgnoreCase(lookup)) {
                            parent = vo;
                            break;
                        }
                    }
                    if (parent == null) {
                        logger.warn(MSG_NO_SUCH_PARENT_NAVIGATION_WAS_FOUND);
                    } else {
                        Map<NavigationElement, SortedSet<NavigationElement>> childParents =
                                sortNavElementsByParentLookup(parent, navElementListByPackage);
                        SortedSet<NavigationElement> childNavSet = childParents.get(parent);
                        if (childNavSet != null) {
                            for (NavigationElement child : childNavSet) {
                                buildTree(child, resultNavElements, childParents);
                            }
                        }
                    }
                }
            }
        }

        //Now I use resource brokers because they are cached. But maybe need to change it and use memcached?
        for (NavigationElement resultNavElement : resultNavElements) {
            SimpleIdentifier<String> textIdentifier = new SimpleIdentifier<String>(resultNavElement.getLookup() + ".top-menu-name");
            ServiceRequest<SimpleIdentifier<String>> textResourceRequest = new ServiceRequest<SimpleIdentifier<String>>(textIdentifier);
            ServiceResponse<TextResource> textResourceResponse = readTextResourceByLookupBroker.perform(textResourceRequest);
            if (textResourceResponse.isSuccess()) {
                resultNavElement.setTextResourceVersion(textResourceResponse.getItem().getResourceVersion());
            }

            SimpleIdentifier<String> imageIdentifier = new SimpleIdentifier<String>(resultNavElement.getLookup() + ".top-menu-icon");
            ServiceRequest<SimpleIdentifier<String>> imageResourceRequest = new ServiceRequest<SimpleIdentifier<String>>(imageIdentifier);
            ServiceResponse<ImageResource> imageResourceResponse = readImageResourceByLookupBroker.perform(imageResourceRequest);
            if (imageResourceResponse.isSuccess()) {
                resultNavElement.setImageResourceVersion(imageResourceResponse.getItem().getResourceVersion());
            }
        }
        return new ServiceResponse<NavigationElement>(resultNavElements, "Return tree navigation elements.", true);
    }

    private void buildTree(NavigationElement parent, List<NavigationElement> upperLevelNavElements,
                           Map<NavigationElement, SortedSet<NavigationElement>> childParents) {
        OpenFlamePrincipal principal = OPFContext.getContext().getPrincipal();
        if (principal.checkUserPermission(parent)) {
            NavigationElement nav = convert(parent);
            nav.setNavigationElements(new LinkedList<NavigationElement>());
            upperLevelNavElements.add(nav);

            SortedSet<NavigationElement> childNavSet = childParents.get(parent);
            if (childNavSet != null && !childNavSet.isEmpty()) {
                for (NavigationElement vo : childNavSet) {
                    buildTree(vo, nav.getNavigationElements(), childParents);
                }
            }
        }
    }

    private SortedMap<NavigationElement, SortedSet<NavigationElement>> sortNavElementsByParentLookup(
            NavigationElement parent, List<NavigationElement> navElementList) {
        Map<String, NavigationElement> navLookupMap = new HashMap<String, NavigationElement>();
        for (NavigationElement nav : navElementList) {
            if (nav.getLookup().startsWith(parent.getLookup())) {
                navLookupMap.put(nav.getLookup(), nav);
            }
        }
        TreeMap<NavigationElement, SortedSet<NavigationElement>> result =
                new TreeMap<NavigationElement, SortedSet<NavigationElement>>(comparator);
        for (NavigationElement nav : navElementList) {
            if (nav != parent && nav.getLookup().startsWith(parent.getLookup())) {
                int lastDot = nav.getLookup().lastIndexOf(StringUtils.PATH_SEPARATOR);
                String parentLookup = nav.getLookup().substring(0, lastDot);
                if (navLookupMap.containsKey(parentLookup)) {
                    NavigationElement parentNav = navLookupMap.get(parentLookup);
                    SortedSet<NavigationElement> voSet = result.get(parentNav);
                    if (voSet == null) {
                        voSet = new TreeSet<NavigationElement>(comparator);
                        result.put(parentNav, voSet);
                    }
                    voSet.add(nav);
                }
            }
        }
        return result;
    }

    public static NavigationElement convert(NavigationElement vo) {
        NavigationElement nav = new NavigationElement();
        nav.setParentPath(vo.getParentPath());
        nav.setLookup(vo.getLookup());
        nav.setPath(vo.getPath());
        nav.setName(vo.getName());
        nav.setUrlPath(vo.getUrlPath());
        nav.setUrlParams(vo.getUrlParams());
        return nav;
    }

    public static final NavComparator comparator = new NavComparator();
    private static class NavComparator implements Comparator<NavigationElement> {
        @Override
        public int compare(NavigationElement n1, NavigationElement n2) {
            if (n1 == null && n2 == null || n1 == n2) {
                return 0;
            } else if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            } else if (n1.getPath().equals(n2.getPath())) {
                return n1.getSortPosition() - n2.getSortPosition();
            } else {
                return -1;
            }
        }
    }

}