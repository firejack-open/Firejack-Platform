/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.web.controller;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.content.domain.ActionServiceLink;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.ActionParameterModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.resource.CollectionStore;
import net.firejack.platform.core.store.registry.resource.FolderStore;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.service.content.broker.documentation.service.DocumentationService;
import net.firejack.platform.service.content.broker.documentation.service.Property;
import net.firejack.platform.service.content.broker.documentation.service.ReferenceWrapper;
import net.firejack.platform.service.content.broker.documentation.service.RelatedEntityProperty;
import net.firejack.platform.web.controller.aop.SetAuthorizedUser;
import net.firejack.platform.web.controller.aop.SetInitData;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller("documentationController")
@RequestMapping(value = {"/console/documentation"})
public class DocumentationController extends BaseController {

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Autowired
    @Qualifier("packageStore")
    private IPackageStore packageStore;

    @Autowired
    @Qualifier("collectionStore")
    private ICollectionStore collectionStore;

    @Autowired
    private DocumentationService documentationService;


    /**
     * @param model
     * @param request
     * @param country
     * @param lookup
     * @return
     */
    @SetInitData
    @SetAuthorizedUser
    @RequestMapping(params = {"country", "lookup"}, method = RequestMethod.GET)
    public String init(Model model, HttpServletRequest request, String country, String lookup) {
        model.addAttribute("docBaseUrl", Env.FIREJACK_URL.getValue() + "/console/documentation");

        String lookupUrlSuffix = lookup;
        lookup = lookup.replace("/", ".");
        RegistryNodeModel registryNode = registryNodeStore.findByLookup(lookup);

        if (registryNode != null) {
            UserPermission addDocumentationPermission = new UserPermission(OpenFlame.DOCUMENTATION_ADD);
            UserPermission editDocumentationPermission = new UserPermission(OpenFlame.DOCUMENTATION_EDIT);
            UserPermission deleteDocumentationPermission = new UserPermission(OpenFlame.DOCUMENTATION_DELETE);
            boolean hasAddDocPermission = OPFContext.getContext().getPrincipal().checkUserPermission(addDocumentationPermission);
            boolean hasEditDocPermission = OPFContext.getContext().getPrincipal().checkUserPermission(editDocumentationPermission);
            boolean hasDeleteDocPermission = OPFContext.getContext().getPrincipal().checkUserPermission(deleteDocumentationPermission);
            model.addAttribute("hasAddPermission", hasAddDocPermission);
            model.addAttribute("hasEditPermission", hasEditDocPermission);
            model.addAttribute("hasDeletePermission", hasDeleteDocPermission);

            model.addAttribute("registryNode", registryNode);
            model.addAttribute("lookup", lookup);
            model.addAttribute("lookupUrlSuffix", lookupUrlSuffix);
            model.addAttribute("country", country);
            model.addAttribute("stsBaseUrl", Env.FIREJACK_URL.getValue());
            model.addAttribute("maxImageWidth", 550);

            PackageModel rnPackage = packageStore.findWithSystemByChildrenId(registryNode.getId());
            if (rnPackage != null) {
                String contextName = StringUtils.normalize(rnPackage.getName());
                model.addAttribute("contextName", contextName);
            }

            String collectionLookup = registryNode.getLookup() + "." + FolderStore.DEFAULT_DOC_FOLDER_NAME + "." + CollectionStore.DEFAULT_COLLECTION_DOC_NAME;
            CollectionModel collection = collectionStore.findByLookup(collectionLookup);

            Cultures[] cultures;
            if (hasAddDocPermission || hasEditDocPermission || hasDeleteDocPermission) {
                cultures = Cultures.values();
            } else {
                if (collection != null) {
                    List<Cultures> cultureList = documentationService.findAvailableCultures(collection.getId());
                    cultures = cultureList.toArray(new Cultures[cultureList.size()]);
                } else {
                    cultures = new Cultures[]{ Cultures.AMERICAN };
                }
            }
            model.addAttribute("cultures", cultures);

            Cultures culture = null;
            if (StringUtils.isNotBlank(country)) {
                culture = Cultures.findByCountry(country);
            }
            if (culture == null) {
                culture = Cultures.AMERICAN;
            }

            Long registryNodeId = registryNode.getId();

            List<RegistryNodeModel> parentRegistryNodes = documentationService.findParentRegistryNodes(registryNodeId);
            model.addAttribute("parentRegistryNodes", parentRegistryNodes);

            RegistryNodeModel currentRootRegistryNodes = parentRegistryNodes.get(0);
            List<RegistryNodeModel> beforeParentRegistryNodes = new ArrayList<RegistryNodeModel>();
            List<RegistryNodeModel> afterParentRegistryNodes = new ArrayList<RegistryNodeModel>();
            List<RootDomainModel> rootRegistryNodes = documentationService.findRootDomains();
            boolean isBefore = true;
            for (RegistryNodeModel rootRegistryNode: rootRegistryNodes) {
                if (rootRegistryNode.getId().equals(currentRootRegistryNodes.getId())) {
                    isBefore = false;
                } else {
                    if (isBefore) {
                        beforeParentRegistryNodes.add(rootRegistryNode);
                    } else {
                        afterParentRegistryNodes.add(rootRegistryNode);
                    }
                }
            }
            model.addAttribute("beforeParentRegistryNodes", beforeParentRegistryNodes);
            model.addAttribute("afterParentRegistryNodes", afterParentRegistryNodes);

            List<RegistryNodeModel> childrenRegistryNodes = documentationService.findChildrenRegistryNodes(registryNodeId);
            model.addAttribute("childrenRegistryNodes", childrenRegistryNodes);

            List<ActionServiceLink> links = documentationService.findLinks(registryNodeId);
            model.addAttribute("links", links);

            if (collection != null) {
                ReferenceWrapper collectionWrapper = documentationService.findReferences(collection, culture);
                model.addAttribute("collectionWrapper", collectionWrapper);
            }

            List<Property<FieldModel>> properties = documentationService.findProperties(registryNodeId, culture);
            model.addAttribute("properties", properties);

            List<RelatedEntityProperty> relatedEntities = documentationService.findRelatedEntities(registryNodeId, culture);
            model.addAttribute("relatedEntities", relatedEntities);

            boolean isEntity = RegistryNodeType.ENTITY.equals(registryNode.getType());
            model.addAttribute("isEntity", isEntity);

            List<Property<ActionModel>> actions = documentationService.findActions(registryNode, culture);
            model.addAttribute("actions", actions);

            List<Property<ActionParameterModel>> parameters = documentationService.findParameterProperties(registryNode, culture);
            model.addAttribute("parameters", parameters);
        } else {
            // TODO: Need to use message localization
            model.addAttribute("errorMessage", "Documentation not found by lookup [" + lookup + "]");
        }
        return "documentation";
    }

    /**
     * @param model
     * @param request
     * @return
     * @throws org.springframework.web.bind.ServletRequestBindingException
     *
     */
    @RequestMapping(value = "/template", /*params = {"country", "lookup"},*/ method = RequestMethod.POST)
    public String generate(Model model, HttpServletRequest request) throws ServletRequestBindingException {
        String country = ServletRequestUtils.getStringParameter(request, "country", null);
        String lookup = ServletRequestUtils.getStringParameter(request, "lookup", null);
        Integer maxImageWidth = ServletRequestUtils.getIntParameter(request, "maxImageWidth");
        init(model, request, country, lookup);
        model.addAttribute("baseUrl", "${baseUrl}");
        model.addAttribute("docBaseUrl", "${docBaseUrl}");
        model.addAttribute("maxImageWidth", maxImageWidth);

        return "documentation-template";
    }

}
