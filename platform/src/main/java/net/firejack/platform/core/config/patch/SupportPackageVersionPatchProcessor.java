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

package net.firejack.platform.core.config.patch;


import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.construct.DomainConfigElement;
import net.firejack.platform.core.config.meta.diff.*;
import net.firejack.platform.core.config.meta.element.FolderElement;
import net.firejack.platform.core.config.meta.element.NavigationConfigElement;
import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.config.meta.element.authority.PermissionElement;
import net.firejack.platform.core.config.meta.element.authority.ResourceLocationElement;
import net.firejack.platform.core.config.meta.element.authority.RoleAssignmentElement;
import net.firejack.platform.core.config.meta.element.authority.RoleElement;
import net.firejack.platform.core.config.meta.element.conf.ConfigReference;
import net.firejack.platform.core.config.meta.element.directory.DirectoryElement;
import net.firejack.platform.core.config.meta.element.directory.GroupElement;
import net.firejack.platform.core.config.meta.element.directory.UserElement;
import net.firejack.platform.core.config.meta.element.process.ActorElement;
import net.firejack.platform.core.config.meta.element.process.ProcessElement;
import net.firejack.platform.core.config.meta.element.resource.CollectionElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceElement;
import net.firejack.platform.core.config.meta.element.schedule.ScheduleElement;
import net.firejack.platform.core.utils.StringUtils;

import java.util.*;


public class SupportPackageVersionPatchProcessor extends UIDPatchProcessor {

    private static ThreadLocal<Map<String, IDomainElement>> oldDomainsHolder =
            new InheritableThreadLocal<Map<String, IDomainElement>>();

    @Override
    public IElementDiffInfoContainer processDifferences(
            IPackageDescriptor oldPackageDescriptor, IPackageDescriptor newPackageDescriptor) {
        ElementDiffInfoContainer diffInfoContainer;
        if (oldPackageDescriptor == null || newPackageDescriptor == null ||
                oldPackageDescriptor.getVersion().compareTo(newPackageDescriptor.getVersion()) > 0) {
            throw new IllegalArgumentException("The selected version should be less than current version.");
        } else {
            oldDomainsHolder.set(new HashMap<String, IDomainElement>());
            try {
                diffInfoContainer = (ElementDiffInfoContainer) super.processDifferences(
                        oldPackageDescriptor, newPackageDescriptor);
                processDifferencesInternal(diffInfoContainer,
                        oldPackageDescriptor, newPackageDescriptor);
            } finally {
                oldDomainsHolder.remove();
            }
        }
        return diffInfoContainer;
    }

    protected void processDifferencesInternal(
            ElementDiffInfoContainer diffInfoContainer,
            IPackageDescriptor oldPackageDescriptor, IPackageDescriptor newPackageDescriptor) {

        List<DomainsDiff> domainDiffs = diffInfoContainer.getDomainDiffs();

        List<DomainsDiff> resultDomainDiffs = new ArrayList<DomainsDiff>();
        diffInfoContainer.setDomainDiffList(resultDomainDiffs);

        StringBuilder sb = new StringBuilder(newPackageDescriptor.getPath());
        sb.append('.').append(newPackageDescriptor.getName());
        final String basePackageRefPath = sb.toString();
        String packageRefPathPrefix = sb.append('.').toString();
        if (domainDiffs != null) {
            Set<String> pathsToAdd = new TreeSet<String>();
            //we assume that package name was not changed
            for (DomainsDiff diff : domainDiffs) {
                if (diff.getType() == DifferenceType.REMOVED) {
                    IDomainElement domainToAdd = diff.getDiffTarget();
                    processDomainPathsSet(packageRefPathPrefix, domainToAdd.getPath(), pathsToAdd);
                    resultDomainDiffs.add(new DomainsDiff(DifferenceType.ADDED, domainToAdd));
                } else if (diff.getType() == DifferenceType.UPDATED) {
                    IDomainElement domainToAdd = diff.getNewElement();
                    processDomainPathsSet(packageRefPathPrefix, domainToAdd.getPath(), pathsToAdd);
                    resultDomainDiffs.add(new DomainsDiff(DifferenceType.ADDED, domainToAdd));
                }
            }
            Set<String> domainsRefPath = new HashSet<String>();
            for (DomainsDiff diff : resultDomainDiffs) {
                IDomainElement domain = diff.getDiffTarget();
                domainsRefPath.add(domain.getPath() + '.' + domain.getName());
            }
            for (String pathToAdd : pathsToAdd) {
                if (!domainsRefPath.contains(pathToAdd)) {
                    IDomainElement domainToAdd = oldDomainsHolder.get().get(pathToAdd);
                    if (domainToAdd == null) {
                        throw new IllegalStateException("Failed to find domain by path [" + pathToAdd + "]");
                    }
                    domainDiffs.add(new DomainsDiff(DifferenceType.ADDED, domainToAdd));
                }
            }
        }

        //add version sub-domain that will be used as base parent for diff elements
        DomainConfigElement versionDomain = new DomainConfigElement(
                StringUtils.normalize(oldPackageDescriptor.getVersion()));
        versionDomain.setParent(newPackageDescriptor);
        versionDomain.setPath(basePackageRefPath);
        versionDomain.setVersionSubDomain(Boolean.TRUE);

        final String pathPart = versionDomain.getName() + '.';
        for (DomainsDiff diff : resultDomainDiffs) {
            DomainConfigElement domain = (DomainConfigElement) diff.getDiffTarget();
            if (basePackageRefPath.equals(domain.getPath())) {
                domain.setParent(versionDomain);
                versionDomain.add(domain);
                domain.setPath(packageRefPathPrefix + versionDomain.getName());
            } else {
                sb = new StringBuilder(domain.getPath());
                sb.insert(packageRefPathPrefix.length(), pathPart);
                domain.setPath(sb.toString());
            }
        }
        DomainsDiff versionDomainDiff = new DomainsDiff(DifferenceType.ADDED, versionDomain);
        resultDomainDiffs.add(versionDomainDiff);

        DescriptorElementDiffComparator diffComparator = new DescriptorElementDiffComparator();
        Collections.sort(resultDomainDiffs, diffComparator);

        List<EntitiesDiff> entityDiffs = diffInfoContainer.getEntityDiffs();
        if (entityDiffs != null) {
            SortedMap<String, IDomainElement> entityDomains = new TreeMap<String, IDomainElement>();
            List<EntitiesDiff> newEntityDiffs = null;
            for (EntitiesDiff diff : entityDiffs) {
                if (diff.getType() == DifferenceType.REMOVED) {
                    if (newEntityDiffs == null) {
                        newEntityDiffs = new ArrayList<EntitiesDiff>();
                    }
                    newEntityDiffs.add(new EntitiesDiff(DifferenceType.ADDED, diff.getDiffTarget()));

                    IParentReferenceOwner elementWithParent = diff.getDiffTarget();
                    while (elementWithParent != null) {
                        INamedPackageDescriptorElement parent = elementWithParent.getParent();
                        if (parent != null) {
                            if (parent instanceof IDomainElement || parent instanceof IEntityElement) {
                                if (parent instanceof IDomainElement) {
                                    entityDomains.put(parent.getPath() + '.' + parent.getName(), (IDomainElement) parent);
                                    elementWithParent = (IParentReferenceOwner) parent;
                                }
                            } else {
                                elementWithParent = null;
                            }
                        } else {
                            elementWithParent = null;
                        }
                    }
                } else if (diff.getType() == DifferenceType.UPDATED) {
                    if (newEntityDiffs == null) {
                        newEntityDiffs = new ArrayList<EntitiesDiff>();
                    }
                    newEntityDiffs.add(new EntitiesDiff(
                            DifferenceType.ADDED, diff.getNewElement()));
                }
            }
            for (Map.Entry<String, IDomainElement> entry : entityDomains.entrySet()) {
                DomainConfigElement domainElement = (DomainConfigElement) entry.getValue();
                DomainsDiff domainsDiff = new DomainsDiff(DifferenceType.ADDED, domainElement);
                sb = new StringBuilder(domainElement.getPath());
                if (basePackageRefPath.equals(domainElement.getPath())) {
                    sb.append('.').append(versionDomain.getName());
                } else {
                    sb.insert(packageRefPathPrefix.length(), pathPart);
                }
                domainElement.setPath(sb.toString());
                resultDomainDiffs.add(domainsDiff);
            }
            if (newEntityDiffs != null && !newEntityDiffs.isEmpty()) {
                for (EntitiesDiff diff : newEntityDiffs) {
                    IEntityElement entity = diff.getDiffTarget();
                    if (basePackageRefPath.equals(entity.getPath())) {
                        factory.assignParent(entity, versionDomain);
                        factory.attachPath(entity, packageRefPathPrefix + versionDomain.getName());
                        versionDomain.add(entity);
                    } else {
                        sb = new StringBuilder(entity.getPath());
                        sb.insert(packageRefPathPrefix.length(), pathPart);
                        factory.attachPath(entity, sb.toString());
                    }
                }
                Collections.sort(newEntityDiffs, diffComparator);
            }
            diffInfoContainer.setEntityDiffList(newEntityDiffs);
        }

        List<ActionsDiff> actionDiffs = diffInfoContainer.getActionDiffs();
        if (actionDiffs != null) {
            List<ActionsDiff> newActionDiffs = null;
            for (ActionsDiff diff : actionDiffs) {
                if (diff.getType() == DifferenceType.REMOVED) {
                    if (newActionDiffs == null) {
                        newActionDiffs = new ArrayList<ActionsDiff>();
                    }
                    newActionDiffs.add(new ActionsDiff(Boolean.TRUE, diff.getDiffTarget()));
                } else if (diff.getType() == DifferenceType.UPDATED) {
                    if (newActionDiffs == null) {
                        newActionDiffs = new ArrayList<ActionsDiff>();
                    }
                    newActionDiffs.add(new ActionsDiff(Boolean.TRUE, diff.getNewElement()));
                }
            }
            if (newActionDiffs != null && !newActionDiffs.isEmpty()) {
                for (ActionsDiff diff : newActionDiffs) {
                    ActionElement action = diff.getDiffTarget();
                    sb = new StringBuilder(action.getPath());
                    sb.insert(packageRefPathPrefix.length(), pathPart);
                    action.setPath(sb.toString());
                }
            }
            diffInfoContainer.setActionDiffList(newActionDiffs);
        }

        List<FieldsDiff> fieldDiffs = diffInfoContainer.getFieldDiffs();
        if (fieldDiffs != null) {
            List<FieldsDiff> newFieldDiffs = null;
            for (FieldsDiff diff : fieldDiffs) {
                if (diff.getType() == DifferenceType.REMOVED) {
                    FieldsDiff newDiff = new FieldsDiff(
                            DifferenceType.ADDED, diff.getTargetParent(), diff.getDiffTarget());
                    if (newFieldDiffs == null) {
                        newFieldDiffs = new ArrayList<FieldsDiff>();
                    }
                    newFieldDiffs.add(newDiff);
                } else if (diff.getType() == DifferenceType.UPDATED) {
                    FieldsDiff newDiff = new FieldsDiff(
                            DifferenceType.ADDED, diff.getTargetParent(), diff.getDiffTarget());
                    if (newFieldDiffs == null) {
                        newFieldDiffs = new ArrayList<FieldsDiff>();
                    }
                    newFieldDiffs.add(newDiff);
                }
            }
            if (newFieldDiffs != null && !newFieldDiffs.isEmpty()) {
                for (FieldsDiff diff : newFieldDiffs) {
                    IFieldElement field = diff.getDiffTarget();
                    sb = new StringBuilder(field.getPath());
                    sb.insert(packageRefPathPrefix.length(), pathPart);
                    factory.attachPath(field, sb.toString());
                }
                Collections.sort(newFieldDiffs, diffComparator);
            }
            diffInfoContainer.setFieldDiffList(newFieldDiffs);
        }
        
    }

    @Override
    protected Map<String, IDomainElement> processDomains(
            IPackageDescriptor packageDescriptor, boolean newDescriptor) {
        Map<String, IDomainElement> domainsByUIDMap =
                super.processDomains(packageDescriptor, newDescriptor);
        if (!newDescriptor) {
            Map<String, IDomainElement> domainsMap = oldDomainsHolder.get();
            for (IDomainElement domain : domainsByUIDMap.values()) {
                domainsMap.put(domain.getPath(), domain);
            }
        }
        return domainsByUIDMap;
    }

    @Override
    protected List<PermissionsDiff> processPermissionDiffs(
            PermissionElement[] oldPermissions, PermissionElement[] newPermissions) {
        //todo: gather all permissions corresponded to action diffs with type UPDATED
        return null;
    }

    @Override
    protected List<ProcessesDiff> handleProcessDiffs(
            ProcessElement[] oldProcesses, ProcessElement[] newProcesses) {
        return null;
    }

    @Override
    protected List<ActorsDiff> processActorDiffs(
            ActorElement[] oldActors, ActorElement[] newActors) {
        return null;
    }

    @Override
    protected List<ConfigsDiff> processConfigDiffs(
            ConfigReference[] oldConfigs, ConfigReference[] newConfigs) {
        return null;
    }

    @Override
    protected List<DirectoriesDiff> processDirectoryDiffs(
            DirectoryElement[] oldDirectories, DirectoryElement[] newDirectories) {
        return null;
    }

    @Override
    protected List<GroupsDiff> processGroupDiffs(GroupElement[] oldGroups, GroupElement[] newGroups) {
        return null;
    }

    @Override
    protected List<SchedulesDiff> processScheduleDiffs(ScheduleElement[] oldSchedules, ScheduleElement[] newSchedules) {
        return null;
    }

    @Override
    protected List<FoldersDiff> processFolderDiffs(
            FolderElement[] oldFolderElements, FolderElement[] newFolderElements) {
        return null;
    }

    @Override
    protected List<NavigationElementsDiff> processNavigationDiffs(
            NavigationConfigElement[] oldActions, NavigationConfigElement[] newActions) {
        return null;
    }

    @Override
    protected RelationshipsDiffContainer processRelationshipDiffs(List<DomainsDiff> domainDiffList,
            IRelationshipElement[] oldRelationships, IRelationshipElement[] newRelationships) {
        return null;
    }

    @Override
    protected List<ResourcesDiff> processResourceDiffs(
            ResourceElement[] oldResources, ResourceElement[] newResources) {
        return null;
    }

    @Override
    protected List<ResourceLocationsDiff> processResourceLocationDiffs(
            ResourceLocationElement[] oldResourceLocations, ResourceLocationElement[] newResourceLocations) {
        return null;
    }

    @Override
    protected List<RoleAssignmentsDiff> processRoleAssignmentDiffs(
            RoleAssignmentElement[] oldAssignments, RoleAssignmentElement[] newAssignments) {
        return null;
    }

    @Override
    protected List<RolesDiff> processRoleDiffs(
            RoleElement[] oldRoles, PermissionElement[] oldPermissionElements,
            RoleElement[] newRoles, PermissionElement[] newPermissionElements) {
        return null;
    }

    @Override
    protected List<UsersDiff> processUserDiffs(
            UserElement[] oldUsers, UserElement[] newUsers) {
        return null;
    }

    @Override
    protected List<CollectionsDiff> processCollectionDiffs(
            CollectionElement[] oldCollections, CollectionElement[] newCollections) {
        return null;
    }

    private void processDomainPathsSet(String packageRefPathPrefix, String domainPath, Set<String> pathsToAdd) {
        if (packageRefPathPrefix.length() < domainPath.length()) {
            String[] pathEntries = domainPath.substring(packageRefPathPrefix.length()).split("\\.");
            if (pathEntries != null && pathEntries.length != 0) {
                StringBuilder sb = new StringBuilder(packageRefPathPrefix);
                for (String pathEntry : pathEntries) {
                    sb.append(pathEntry);
                    pathsToAdd.add(sb.toString());
                }
            }
        }
    }

}