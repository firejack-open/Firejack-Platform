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

package net.firejack.platform.service.registry.helper;

import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.NavigationElementType;
import net.firejack.platform.core.model.registry.ProcessType;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.bi.BIReportModel;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.model.registry.report.ReportModel;
import net.firejack.platform.core.model.registry.resource.FolderModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.model.registry.wizard.WizardModel;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.registry.*;
import net.firejack.platform.core.store.registry.resource.IFolderStore;
import net.firejack.platform.core.store.registry.resource.IImageResourceStore;
import net.firejack.platform.core.store.registry.resource.ITextResourceStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.MessageResolver;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;
import org.apache.velocity.util.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class GatewayService implements IGatewayService {

    private static final String ID_LOOKUP_KEY = "GatewayService-id-lookup-key";
    private static final Logger logger = Logger.getLogger(GatewayService.class);

    @Autowired
	@Qualifier("registryNodeStore")
	protected IRegistryNodeStore<RegistryNodeModel> registryNodeStore;
    @Autowired
    private IPackageStore packageStore;
    @Autowired
    private INavigationElementStore navigationElementStore;
    @Autowired
    private IRoleStore roleStore;
    @Autowired
    private IPermissionStore permissionStore;
    @Autowired
    private IUserStore userStore;
    @Autowired
    private IFolderStore folderStore;
    @Autowired
    private ITextResourceStore textResourceStore;
    @Autowired
    private IImageResourceStore imageResourceStore;

    private ThreadLocal<Map<String,Object>> cache;
    private List<NavigationElementModel> navigationElements;

    public void setCache(ThreadLocal<Map<String, Object>> cache) {
        this.cache = cache;
    }

    @ProgressStatus(weight = 5, description = "Preparation for code generation")
    public void prepareGateway(Long packageId) {
        PackageModel pkg = packageStore.findWithSystemById(packageId);
        if (pkg != null) {

            prepareNavigationElements(pkg);

            prepareRoles(pkg);

            prepareResourceContents(pkg);

        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Generate necessary Navigation Elements for package before generate code
     * @param pkg package
     */
    private void prepareNavigationElements(PackageModel pkg) {
        navigationElements = new ArrayList<NavigationElementModel>();

        // create Gateway navigation element
        NavigationElementModel mainNavigationElement = createNavigationElement("Gateway", pkg, 1);

        // create Login navigation element
        createNavigationElement("Login", mainNavigationElement, 1);
        // create Forgot Password navigation element
        createNavigationElement("Forgot Password", mainNavigationElement, 2);
        // create Home navigation element
        createNavigationElement("Home", mainNavigationElement, 3);
        // create Inbox navigation element
        createNavigationElement("Inbox", mainNavigationElement, 4);

        Class[] registryNodeClasses = new Class[] {
                DomainModel.class,
                EntityModel.class,
                ReportModel.class,
                BIReportModel.class,
                WizardModel.class,
                ProcessModel.class
        };

        prepareNavigationElements(pkg, mainNavigationElement, registryNodeClasses, 5);


        Map<String, Object> map = cache.get();
        Map<Long, String> idLookupMap = (Map<Long, String>) map.get(ID_LOOKUP_KEY);
        for (NavigationElementModel element : navigationElements) {
            navigationElementStore.save(element);
            if (element.getMain() != null) {
                Long registryNodeId = element.getMain().getId();
                String lookup = idLookupMap.get(registryNodeId);
                if (StringUtils.isNotBlank(lookup)) {
                    map.put(lookup, element.getUrlPath());
                }
            }

        }

        List<NavigationElementModel> allNavigationElements = navigationElementStore.findAllByLikeLookupPrefix(mainNavigationElement.getLookup());
        for (NavigationElementModel navigationElement : allNavigationElements) {
            boolean isExist = false;
            for (NavigationElementModel correctNavigationElement : navigationElements) {
                if (correctNavigationElement.getLookup().equals(navigationElement.getLookup())) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                navigationElementStore.deleteRecursiveById(navigationElement.getId());
            }
        }
	}

    private void prepareNavigationElements(RegistryNodeModel parentRegistryNode, NavigationElementModel parentNavigationElement, Class[] registryNodeClasses, Integer shift) {
        Map<String, Object> map = cache.get();
        Map<Long, String> idMap = (Map<Long, String>) map.get(ID_LOOKUP_KEY);
        if (idMap == null) {
            idMap = new HashMap<Long, String>();
            map.put(ID_LOOKUP_KEY, idMap);
        }
        List<RegistryNodeModel> registryNodeModels =
                registryNodeStore.findChildrenByParentIdAndTypes(parentRegistryNode.getId(), null, registryNodeClasses);
        for (int i = 0, registryNodeModelsSize = registryNodeModels.size(); i < registryNodeModelsSize; i++) {
            RegistryNodeModel registryNode = registryNodeModels.get(i);
            if (parentRegistryNode instanceof PackageModel || parentRegistryNode instanceof DomainModel) {
                if (registryNode instanceof EntityModel) {
                    EntityModel entityModel = (EntityModel) registryNode;
                    if (!entityModel.getAbstractEntity() && !entityModel.getTypeEntity()) {
                        NavigationElementModel entityNavElement = createNavigationElement(registryNode.getName(), parentNavigationElement, i + shift);
                        entityNavElement.setMain(entityModel);
                        //map.put(entityModel.getLookup(), entityNavElement.getUrlPath());
                        idMap.put(entityModel.getId(), entityModel.getLookup());
                    }
                    prepareNavigationElements(registryNode, parentNavigationElement, registryNodeClasses, 1);
                } else if (registryNode instanceof WizardModel) {
                    NavigationElementModel entityNavElement = createNavigationElement(registryNode.getName(), parentNavigationElement, i + shift);
                    entityNavElement.setElementType(NavigationElementType.WIZARD);
                    entityNavElement.setPageUrl(registryNode.getLookup());
                    entityNavElement.setMain(registryNode);
                    //map.put(registryNode.getLookup(), entityNavElement.getUrlPath());
                    idMap.put(registryNode.getId(), registryNode.getLookup());
                } else if (registryNode instanceof ProcessModel) {
//                    if (ProcessType.CREATABLE.equals(((ProcessModel) registryNode).getProcessType())) {
                        EntityModel entityModel = (EntityModel) registryNode.getMain();
                        if (entityModel != null) {
                            if (!entityModel.getAbstractEntity() && !entityModel.getTypeEntity()) {
                                NavigationElementModel entityNavElement = createNavigationElement(registryNode.getName(), parentNavigationElement, i + shift);
                                entityNavElement.setElementType(NavigationElementType.WORKFLOW);
                                entityNavElement.setMain(entityModel);
                                entityNavElement.setUrlParams(registryNode.getLookup());
                                ProcessModel processModel = (ProcessModel) registryNode;
                                if (ProcessType.RELATIONAL.equals(processModel.getProcessType())) {
                                    entityNavElement.setHidden(Boolean.TRUE);
                                }
                                //map.put(registryNode.getLookup(), entityNavElement.getUrlPath());
                                idMap.put(registryNode.getId(), registryNode.getLookup());
                            }
                        }
//                    }
                } else {
                    NavigationElementModel navigationElement = createNavigationElement(registryNode.getName(), parentNavigationElement, i + shift);
                    prepareNavigationElements(registryNode, navigationElement, registryNodeClasses, 1);
                }
            } else if (parentRegistryNode instanceof EntityModel){
                if (registryNode instanceof EntityModel) {
                    EntityModel entityModel = (EntityModel) registryNode;
                    if (!entityModel.getAbstractEntity() && !entityModel.getTypeEntity()) {
                        NavigationElementModel entityNavElement = createNavigationElement(registryNode.getName(), parentNavigationElement, i + shift);
                        entityNavElement.setMain(registryNode);
                        //map.put(entityModel.getLookup(), entityNavElement.getUrlPath());
                        idMap.put(entityModel.getId(), entityModel.getLookup());
                    }
                    prepareNavigationElements(registryNode, parentNavigationElement, registryNodeClasses, 1);
                } else if (registryNode instanceof ReportModel || registryNode instanceof BIReportModel) {
                    NavigationElementModel entityNavElement = createNavigationElement(registryNode.getName(), parentNavigationElement, i + shift);
                    entityNavElement.setMain(registryNode);
                    //map.put(registryNode.getLookup(), entityNavElement.getUrlPath());
                    idMap.put(registryNode.getId(), registryNode.getLookup());
                }
            }
        }
    }


    private NavigationElementModel createNavigationElement(String name, PackageModel pkg, Integer order) {
        String lookup = DiffUtils.lookup(pkg.getLookup(), name);
        NavigationElementModel navigationElementModel = navigationElementStore.findByLookup(lookup);
        if (navigationElementModel == null) {
            navigationElementModel = new NavigationElementModel();
        }
        navigationElementModel.setName(StringUtils.capitalize(name));
        navigationElementModel.setDescription("This is an auto generated navigation element for code generation.");
        navigationElementModel.setPath(pkg.getLookup());

        navigationElementModel.setLookup(lookup);
        navigationElementModel.setParent(pkg);
        navigationElementModel.setParentPath("/" + pkg.getName());
        navigationElementModel.setUrlPath("/");
        navigationElementModel.setPageUrl("/home");
        SystemModel system = pkg.getSystem();
        if (system != null) {
            navigationElementModel.setServerName(system.getServerName());
            navigationElementModel.setProtocol(system.getProtocol());
            navigationElementModel.setPort(system.getPort());
        }
        navigationElementModel.setSortPosition(order);

        navigationElements.add(navigationElementModel);
        return navigationElementModel;
    }

    private NavigationElementModel createNavigationElement(String name, NavigationElementModel parentNavigationElement, Integer order) {
        String lookup = DiffUtils.lookup(parentNavigationElement.getLookup(), name);
        NavigationElementModel navigationElementModel = navigationElementStore.findByLookup(lookup);
        if (navigationElementModel == null) {
            navigationElementModel = new NavigationElementModel();
        }
        navigationElementModel.setName(StringUtils.capitalize(name));
        navigationElementModel.setDescription("This is an auto generated navigation element for code generation.");
        navigationElementModel.setPath(parentNavigationElement.getLookup());
        navigationElementModel.setLookup(lookup);
        navigationElementModel.setParent(parentNavigationElement);
        navigationElementModel.setParentPath(parentNavigationElement.getParentPath());
        navigationElementModel.setServerName(parentNavigationElement.getServerName());
        navigationElementModel.setProtocol(parentNavigationElement.getProtocol());
        navigationElementModel.setPort(parentNavigationElement.getPort());
        navigationElementModel.setElementType(NavigationElementType.PAGE);
        navigationElementModel.setSortPosition(order);

        navigationElements.add(navigationElementModel);
        return navigationElementModel;
    }

    private RoleModel populateRole(String roleName, PackageModel pkg) {
        RoleModel roleModel = new RoleModel();
        roleModel.setName(roleName);
        roleModel.setDescription("This is an auto generated role for code generation.");
        roleModel.setParent(pkg);
        return roleModel;
    }

    private void prepareRoles(PackageModel pkg) {
        String packageLookup = pkg.getLookup();

        RoleModel adminRoleModel = roleStore.findByLookup(packageLookup + ".admin");
        if (adminRoleModel == null) {
            adminRoleModel = populateRole("admin", pkg);
        }
        RoleModel userRole = roleStore.findByLookup(packageLookup + ".user");
        if (userRole == null) {
            userRole = populateRole("user", pkg);
        }
        List<PermissionModel> guestPermissions = new ArrayList<PermissionModel>();
        List<PermissionModel> permissionModels = permissionStore.findAllBySearchTermWithFilter(packageLookup, null);
        for (PermissionModel permissionModel : permissionModels) {
            if (permissionModel.getLookup().equals(packageLookup + ".gateway.login") ||
                    permissionModel.getLookup().equals(packageLookup + ".gateway.forgot-password")) {
                guestPermissions.add(permissionModel);
            }
        }
        permissionModels.removeAll(guestPermissions);
        adminRoleModel.setPermissions(permissionModels);
        roleStore.save(adminRoleModel);

        userRole.setPermissions(permissionModels);
        roleStore.save(userRole);

        RoleModel guestRoleModel = roleStore.findByLookup(packageLookup + ".guest");
        if (guestRoleModel == null) {
            guestRoleModel = new RoleModel();
            guestRoleModel.setName("guest");
            guestRoleModel.setDescription("This is an auto generated role for code generation.");
            guestRoleModel.setParent(pkg);
        }

        if (guestRoleModel.getPermissions() == null) {
            guestRoleModel.setPermissions(guestPermissions);
        } else {
            guestPermissions.removeAll(guestRoleModel.getPermissions());
            guestRoleModel.getPermissions().addAll(guestPermissions);
        }
        roleStore.save(guestRoleModel);

        if (OPFContext.isInitialized()) {
            long adminId;
            try {
                IUserInfoProvider currentUserInfoProvider = ContextManager.getUserInfoProvider();
                adminId = currentUserInfoProvider.getId();
            } catch (ContextLookupException e) {
                UserModel admin = userStore.findUserByUsername("admin");
                adminId = admin.getId();
            }

            UserModel currentUser = userStore.findByIdWithRoles(adminId);
            UserRoleModel userRoleModel = new UserRoleModel(currentUser, adminRoleModel);
            currentUser.getUserRoles().add(userRoleModel);
            userStore.save(currentUser);
        }
    }

    private void prepareResourceContents(PackageModel pkg) {
        RegistryNodeModel rootDomain = registryNodeStore.findById(pkg.getParent().getId());
        String rootDomainName = rootDomain.getName();
        createTextResourceContext(pkg, "Site Name", "gateway.site-name", rootDomainName, StringUtils.capitalize(pkg.getName()));
        createTextResourceContext(pkg, "Site Description", "gateway.site-description");
        createLogoResourceContent(pkg, "Site Logo");

        int year = Calendar.getInstance().get(Calendar.YEAR);
        createTextResourceContext(pkg, "Copyright", "gateway.copyright", String.valueOf(year), rootDomainName);
        createLogoResourceContent(pkg, "Footer Site Logo");

        FolderModel folderModel = folderStore.findByLookup(pkg.getLookup() + ".general");
        if (folderModel == null) {
            folderModel = new FolderModel();
            folderModel.setName("general");
            folderModel.setDescription("General folder is specific folder for store general resources like Site name, Site logo and etc.");
            folderModel.setParent(pkg);
            folderStore.save(folderModel);
        }

        createTextResourceContext(folderModel, "Login Welcome Title", "gateway.login.welcome-title");
        createTextResourceContext(folderModel, "Login Welcome Message", "gateway.login.welcome-message", rootDomainName);

        createTextResourceContext(folderModel, "Forgot Password Title", "gateway.forgot-password.title");
        createTextResourceContext(folderModel, "Forgot Password Message", "gateway.forgot-password.message", rootDomainName);

        createTextResourceContext(folderModel, "Home Page Title", "gateway.home.page-title");
        createTextResourceContext(folderModel, "Home Panel Title", "gateway.home.panel-title");
        createTextResourceContext(folderModel, "Home Panel Text", "gateway.home.panel-text", rootDomainName, pkg.getName());

        generateResourceContents(pkg);
    }

    private void generateResourceContents(RegistryNodeModel registryNodeModel) {
        Class[] registryNodeClasses = new Class[] {
                DomainModel.class,
                EntityModel.class,
                ReportModel.class,
                BIReportModel.class
        };
        List<RegistryNodeModel> registryNodeModels =
                registryNodeStore.findChildrenByParentIdAndTypes(registryNodeModel.getId(), null, registryNodeClasses);
        for (RegistryNodeModel registryNode : registryNodeModels) {
            String name = StringUtils.capitalize(registryNode.getName());
            if (RegistryNodeType.DOMAIN.equals(registryNode.getType())) {
                createTextResourceContext(registryNode, "Page Title", "gateway.domain.page-title", name);
                createTextResourceContext(registryNode, "Panel Title", "gateway.domain.panel-title", name);
                createTextResourceContext(registryNode, "Panel Text", "gateway.domain.panel-text", name);
            } else if (RegistryNodeType.ENTITY.equals(registryNode.getType())) {
                createTextResourceContext(registryNode, "Page Title", "gateway.entity.page-title", name);
                createTextResourceContext(registryNode, "Panel Title", "gateway.entity.panel-title", name);

                String description = registryNode.getDescription();
                if (StringUtils.isNotBlank(description) && !description.trim().endsWith(".")) {
                    description = description.trim() + ". ";
                }
                if (StringUtils.isBlank(description)) {
                    description = "";
                }
                createTextResourceContext(registryNode, "Panel Text", "gateway.entity.panel-text", description);
            } else if (RegistryNodeType.REPORT.equals(registryNode.getType())) {
                createTextResourceContext(registryNode, "Page Title", "gateway.report.page-title", name);
                createTextResourceContext(registryNode, "Panel Title", "gateway.report.panel-title", name);

                String description = registryNode.getDescription();
                if (StringUtils.isNotBlank(description) && !description.trim().endsWith(".")) {
                    description = description.trim() + ". ";
                }
                if (StringUtils.isBlank(description)) {
                    description = "";
                }
                createTextResourceContext(registryNode, "Panel Text", "gateway.report.panel-text", description);
            } else if (RegistryNodeType.BI_REPORT.equals(registryNode.getType())) {
                createTextResourceContext(registryNode, "Page Title", "gateway.bi-report.page-title", name);
                createTextResourceContext(registryNode, "Panel Title", "gateway.bi-report.panel-title", name);

                String description = registryNode.getDescription();
                if (StringUtils.isNotBlank(description) && !description.trim().endsWith(".")) {
                    description = description.trim() + ". ";
                }
                if (StringUtils.isBlank(description)) {
                    description = "";
                }
                createTextResourceContext(registryNode, "Panel Text", "gateway.bi-report.panel-text", description);
            }
            generateResourceContents(registryNode);
        }
    }

    private void createTextResourceContext(RegistryNodeModel parent, String name, String messageKey, String... args) {
        String text = MessageResolver.messageFormatting(messageKey, Locale.ENGLISH, args);
        textResourceStore.saveTextResource(parent, name, text);
    }

    private void createLogoResourceContent(PackageModel pkg, String name) {
        InputStream inputStream = ClassUtils.getResourceAsStream(this.getClass(), "templates/code/web/images/logo.png");
        FileInfo fileInfo = new FileInfo("logo.png", inputStream);
        try {
            imageResourceStore.saveImageResource(pkg, name, "Prometheus Logo", fileInfo);
        } catch (IOException e) {
            logger.error("Can't save Gateway Logo.", e);
        }
    }

}
