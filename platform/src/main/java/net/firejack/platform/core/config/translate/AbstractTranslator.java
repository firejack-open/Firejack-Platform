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

package net.firejack.platform.core.config.translate;

import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.diff.*;
import net.firejack.platform.core.config.meta.element.FolderElement;
import net.firejack.platform.core.config.meta.element.NavigationConfigElement;
import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.config.meta.element.authority.PermissionElement;
import net.firejack.platform.core.config.meta.element.authority.ResourceLocationElement;
import net.firejack.platform.core.config.meta.element.authority.RoleElement;
import net.firejack.platform.core.config.meta.element.conf.ConfigReference;
import net.firejack.platform.core.config.meta.element.directory.DirectoryElement;
import net.firejack.platform.core.config.meta.element.directory.GroupElement;
import net.firejack.platform.core.config.meta.element.directory.UserElement;
import net.firejack.platform.core.config.meta.element.process.ActorElement;
import net.firejack.platform.core.config.meta.element.process.ProcessElement;
import net.firejack.platform.core.config.meta.element.resource.CollectionElement;
import net.firejack.platform.core.config.meta.element.resource.CollectionMemberElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.patch.EntityElementManager;
import net.firejack.platform.core.utils.ClassUtils;
import net.firejack.platform.core.utils.OpenFlameSpringContext;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import org.apache.log4j.Logger;

import java.util.*;


public abstract class AbstractTranslator
        <R, TR extends ITranslationResult<R>>
        implements IPackageDescriptorTranslator<R> {

    private Class<TR> translationResultClass;
    private Map<INamedPackageDescriptorElement, String> lookupMap = new HashMap<INamedPackageDescriptorElement, String>();
    protected Map<String, INamedPackageDescriptorElement> cache = new HashMap<String, INamedPackageDescriptorElement>();
    protected EntityElementManager oldEntitiesManager;
    protected EntityElementManager newEntitiesManager;
    protected ManuallyProgress progressBar;
    protected TR resultState;

    protected AbstractTranslator(Class<TR> translationResultClass) {
        this.translationResultClass = translationResultClass;
    }

    @Override
    public ITranslationResult<R> translate(IElementDiffInfoContainer elementDiffContainer) {
        if (OpenFlameSpringContext.getContext().containsBean("progressAspect")) {
            progressBar = OpenFlameSpringContext.getBean(ManuallyProgress.class);
        }
        lookupMap.clear();
        resultState = prepareResultWrapper();
        beforeTranslate(elementDiffContainer, resultState);
        translateInternal(elementDiffContainer);
        afterTranslate(elementDiffContainer);
        progressBar = null;
        return getResultState();
    }

    /**
     * @return result state
     */
    public TR getResultState() {
        return resultState;
    }

    protected TR prepareResultWrapper() {
        return ClassUtils.populate(translationResultClass);
    }

    protected void beforeTranslate(IElementDiffInfoContainer elementDiffContainer, TR resultState) {
        this.oldEntitiesManager = elementDiffContainer.getOldEntitiesManager();
        this.newEntitiesManager = elementDiffContainer.getNewEntitiesManager();
    }

    protected void afterTranslate(IElementDiffInfoContainer elementDiffContainer) {
    }

    protected void translateInternal(IElementDiffInfoContainer elementDiffContainer) {
        showProgress("Pre-process data before Import...", 1);
        beforeDiffProcessing(elementDiffContainer);

        Map<Boolean, SortedSet<DomainsDiff>> domainDiffs = prepare(elementDiffContainer.getDomainDiffs());
        Map<Boolean, SortedSet<EntitiesDiff>> entityDiffs = prepare(elementDiffContainer.getEntityDiffs());
        Map<Boolean, SortedSet<ReportDiff>> reportDiffs = prepare(elementDiffContainer.getReportDiffs());
        Map<Boolean, SortedSet<BIReportDiff>> biReportDiffs = prepare(elementDiffContainer.getBiReportDiffs());
        Map<Boolean, SortedSet<WizardDiff>> wizardDiffs = prepare(elementDiffContainer.getWizardDiffs());
        Map<Boolean, SortedSet<RelationshipsDiff>> relationshipDiffs = prepare(elementDiffContainer.getRelationshipDiffs());
        Map<Boolean, SortedSet<FieldsDiff>> fieldDiffs = prepare(elementDiffContainer.getFieldDiffs());
        Map<Boolean, SortedSet<IndexesDiff>> indexesDiffs = prepare(elementDiffContainer.getIndexesDiffs());

        showProgress("Preparing items for Import Process...", 5);
        Map<Boolean, SortedSet<IPackageDescriptorElementDiff
                <?, ? extends INamedPackageDescriptorElement>>> map =
                new HashMap<Boolean, SortedSet<IPackageDescriptorElementDiff
                        <?, ? extends INamedPackageDescriptorElement>>>();
        map.put(Boolean.TRUE,
                new TreeSet<IPackageDescriptorElementDiff
                        <?, ? extends INamedPackageDescriptorElement>>(
                        new StructureElementsComparator<IPackageDescriptorElementDiff
                                <?, ? extends INamedPackageDescriptorElement>>()));
        map.put(Boolean.FALSE,
                new TreeSet<IPackageDescriptorElementDiff
                        <?, ? extends INamedPackageDescriptorElement>>(
                        new StructureElementsComparator<IPackageDescriptorElementDiff
                                <?, ? extends INamedPackageDescriptorElement>>()));


        putInOrder(elementDiffContainer.getActionDiffs(), map);
        putInOrder(elementDiffContainer.getNavigationElementDiffs(), map);
        putInOrder(elementDiffContainer.getResourceLocationDiffs(), map);
        putInOrder(elementDiffContainer.getPermissionDiffs(), map);
        putInOrder(elementDiffContainer.getRoleDiffs(), map);
        putInOrder(elementDiffContainer.getFolderDiffs(), map);
        putInOrder(elementDiffContainer.getConfigDiffs(), map);
        putInOrder(elementDiffContainer.getDirectoryDiffs(), map);
        putInOrder(elementDiffContainer.getGroupDiffs(), map);
        putInOrder(elementDiffContainer.getScheduleDiffs(), map);
        putInOrder(elementDiffContainer.getUserDiffs(), map);
        putInOrder(elementDiffContainer.getProcessDiffs(), map);
        putInOrder(elementDiffContainer.getActorDiffs(), map);
        putInOrder(elementDiffContainer.getResourceDiffs(), map);
        putInOrder(elementDiffContainer.getCollectionDiffs(), map);
        putInOrder(elementDiffContainer.getUserProfileFieldGroupsDiffs(), map);
        putInOrder(elementDiffContainer.getUserProfileFieldsDiffs(), map);

        //===================================Process ADDED diffs======================================
        SortedSet<DomainsDiff> domainDiffSet;
        if ((domainDiffSet = domainDiffs.get(Boolean.TRUE)) != null) {
            showProgress("Importing/Updating Domains...", 2);
            for (DomainsDiff diff : domainDiffSet) {
                processDomainsDiff(diff);
            }
        }

        SortedSet<EntitiesDiff> entityDiffSet;
        if ((entityDiffSet = entityDiffs.get(Boolean.TRUE)) != null) {
            showProgress("Importing/Updating Entities...", 4);
            for (EntitiesDiff diff : entityDiffSet) {
                processEntityDiff(diff);
            }
        }

        afterEntityProcessing(elementDiffContainer);

        SortedSet<RelationshipsDiff> relationshipDiffSet;
        if ((relationshipDiffSet = relationshipDiffs.get(Boolean.TRUE)) != null) {
            showProgress("Importing/Updating Relationships...", 2);
            for (RelationshipsDiff diff : relationshipDiffSet) {
                processRelationshipsDiff(diff);
            }
        }

        SortedSet<FieldsDiff> fieldDiffSet;
        if ((fieldDiffSet = fieldDiffs.get(Boolean.TRUE)) != null) {
            showProgress("Importing/Updating Fields...", 4);
            for (FieldsDiff diff : fieldDiffSet) {
                processFieldDiff(diff);
            }
        }

        SortedSet<IndexesDiff> indexesDiffSet;
        if ((indexesDiffSet = indexesDiffs.get(Boolean.TRUE)) != null) {
            showProgress("Importing/Updating Indexes...", 2);
            for (IndexesDiff diff : indexesDiffSet) {
                processIndexDiff(diff);
            }
        }

        SortedSet<ReportDiff> reportDiffSet;
        if ((reportDiffSet = reportDiffs.get(Boolean.TRUE)) != null) {
            showProgress("Importing/Updating Reports...", 2);
            for (ReportDiff diff : reportDiffSet) {
                processReportDiff(diff);
            }
        }

        SortedSet<BIReportDiff> biReportDiffSet;
        if ((biReportDiffSet = biReportDiffs.get(Boolean.TRUE)) != null) {
            showProgress("Importing/Updating BI Reports...", 2);
            for (BIReportDiff diff : biReportDiffSet) {
                processBIReportDiff(diff);
            }
        }

        SortedSet<WizardDiff> wizardDiffSet;
        if ((wizardDiffSet = wizardDiffs.get(Boolean.TRUE)) != null) {
            showProgress("Importing/Updating Wizards...", 2);
            for (WizardDiff diff : wizardDiffSet) {
                processWizardDiff(diff);
            }
        }

        processDiffs(map.get(Boolean.TRUE), "Importing/Updating Data...", 4);
        //===================================Process Role Assignments======================================

        if (elementDiffContainer.getRoleAssignmentDiffs() != null) {
            showProgress("Importing/Updating Role Assignments...", 3);
            for (RoleAssignmentsDiff diff : elementDiffContainer.getRoleAssignmentDiffs()) {
                processRoleAssignmentsDiff(diff);
            }
        }

        //===================================Process REMOVED diffs======================================
        processDiffs(map.get(Boolean.FALSE), "Replacing Old Data...", 4);

        if ((indexesDiffSet = indexesDiffs.get(Boolean.FALSE)) != null) {
            showProgress("Replacing Indexes...", 2);
            for (IndexesDiff diff : indexesDiffSet) {
                processIndexDiff(diff);
            }
        }

        if ((fieldDiffSet = fieldDiffs.get(Boolean.FALSE)) != null) {
            showProgress("Replacing old Fields...", 2);
            for (FieldsDiff diff : fieldDiffSet) {
                processFieldDiff(diff);
            }
        }

        if ((relationshipDiffSet = relationshipDiffs.get(Boolean.FALSE)) != null) {
            showProgress("Replacing old Relationships...", 3);
            for (RelationshipsDiff diff : relationshipDiffSet) {
                processRelationshipsDiff(diff);
            }
        }

        if ((entityDiffSet = entityDiffs.get(Boolean.FALSE)) != null) {
            showProgress("Replacing old Entities...", 3);
            for (EntitiesDiff diff : entityDiffSet) {
                processEntityDiff(diff);
            }
        }
        //afterEntityProcessing(elementDiffContainer);

        if ((domainDiffSet = domainDiffs.get(Boolean.FALSE)) != null) {
            showProgress("Replacing old Domains...", 3);
            for (DomainsDiff diff : domainDiffSet) {
                processDomainsDiff(diff);
            }
        }
    }

    protected void processDomainsDiff(DomainsDiff diff) {
    }

    protected void processActionsDiff(ActionsDiff diff) {
    }

    protected void processNavigationElementsDiff(NavigationElementsDiff diff) {
    }

    protected void processResourceLocationsDiff(ResourceLocationsDiff diff) {
    }

    protected void processPermissionsDiff(PermissionsDiff diff) {
    }

    protected void processRolesDiff(RolesDiff diff) {
    }

    protected void processProcessesDiff(ProcessesDiff diff) {
    }

    protected void processActorsDiff(ActorsDiff diff) {
    }

    protected void processFoldersDiff(FoldersDiff diff) {
    }

    protected void processConfigsDiff(ConfigsDiff diff) {
    }

    protected void processDirectoriesDiff(DirectoriesDiff diff) {
    }

    protected void processGroupsDiff(GroupsDiff diff) {
    }

    protected void processSchedulesDiff(SchedulesDiff diff) {
    }

    protected void processUsersDiff(UsersDiff diff) {
    }

    protected void processResourcesDiff(ResourcesDiff diff) {
    }

    protected void processCollectionsDiff(CollectionsDiff diff) {
    }

    protected void processRoleAssignmentsDiff(RoleAssignmentsDiff diff) {
    }

    protected void processUserProfileFieldsDiff(UserProfileFieldsDiff diff) {

    }

    protected void processUserProfileFieldGroupsDiff(UserProfileFieldGroupsDiff diff) {

    }

    protected void processReportDiff(ReportDiff diff) {
    }

    protected void processBIReportDiff(BIReportDiff diff) {
    }

    protected void processWizardDiff(WizardDiff diff) {

    }

    protected void afterEntityProcessing(IElementDiffInfoContainer elementDiffContainer) {
    }

    protected void beforeDiffProcessing(IElementDiffInfoContainer elementDiffContainer) {

    }

    protected void showProgress(String message, int weight) {
        if (progressBar != null) {
            progressBar.status(message, weight);
        }
    }

    protected abstract void processFieldDiff(FieldsDiff diff);

    protected abstract void processIndexDiff(IndexesDiff diff);

    protected abstract void processEntityDiff(EntitiesDiff diff);

    protected abstract void processRelationshipsDiff(RelationshipsDiff diff);

    protected abstract Logger getLogger();

    private <T extends IPackageDescriptorElementDiff<?, ? extends INamedPackageDescriptorElement>> Map
            <Boolean, SortedSet<T>> prepare(List<T> diffList) {
        Map<Boolean, SortedSet<T>> map = new HashMap<Boolean, SortedSet<T>>();
        if (diffList != null) {
            for (T diff : diffList) {
                SortedSet<T> diffSet = map.get(diff.getType() != DifferenceType.REMOVED);
                if (diffSet == null) {
                    diffSet = new TreeSet<T>(new StructureElementsComparator<T>());
                    map.put(diff.getType() != DifferenceType.REMOVED, diffSet);
                }
                diffSet.add(diff);
            }
        }
        return map;
    }

    private class StructureElementsComparator
            <T extends IPackageDescriptorElementDiff<?, ? extends INamedPackageDescriptorElement>>
            implements Comparator<T> {

        @Override
        public int compare(T o1, T o2) {
            int result;
            if (o1.getType() == DifferenceType.REMOVED ^ o2.getType() == DifferenceType.REMOVED) {
                result = o1.getType() == DifferenceType.REMOVED ? -1 : 1;
            } else {
                String refPath1;
                String refPath2;
                if (o1.getType() == DifferenceType.REMOVED && o2.getType() == DifferenceType.REMOVED) {
                    refPath1 = getNormalizedPath(o2.getDiffTarget());
                    refPath2 = getNormalizedPath(o1.getDiffTarget());
                } else {
                    refPath1 = getNormalizedPath(
                            o1.getType() == DifferenceType.ADDED ?
                                    o1.getDiffTarget() : o1.getNewElement());
                    refPath2 = getNormalizedPath(
                            o2.getType() == DifferenceType.ADDED ?
                                    o2.getDiffTarget() : o2.getNewElement());
                }
                result = refPath1.compareTo(refPath2);
                result = result == 0 ? 1 : result;
            }
            return result;
        }

    }

    private String getNormalizedPath(INamedPackageDescriptorElement item) {
        String path = lookupMap.get(item);
        if (path == null) {
            if (item instanceof IRelationshipElement) {
                IRelationshipElement rel = (IRelationshipElement) item;
                path = DiffUtils.lookupByRefPath(
                        rel.getSource().getRefPath());
                path = DiffUtils.lookup(path, rel.getName());
            } else if (item instanceof IFieldElement || item instanceof IDomainElement ||
                    item instanceof IEntityElement) {
                path = DiffUtils.lookup(item);
            } else if (item instanceof GroupElement) {
                GroupElement group = (GroupElement) item;
                path = StringUtils.isBlank(group.getDirectoryRef()) ?
                        group.getPath() : group.getDirectoryRef();
                path = DiffUtils.lookup(path, group.getName());
            } else if (item instanceof CollectionElement) {
                CollectionElement collection = (CollectionElement) item;
                String longestPath = DiffUtils.lookup(collection.getPath(), collection.getName());
                if (collection.getCollectionMembers() != null) {
                    for (CollectionMemberElement member : collection.getCollectionMembers()) {
                        if (member.getReference().compareTo(longestPath) > 0) {
                            longestPath = member.getReference();
                        }
                    }
                }
                //as soon as collection may depend on resources or collections that have path "greater"
                //than the current collection's path
                path = longestPath;
            } else {
                path = DiffUtils.lookup(item.getPath(), item.getName());
            }
            cache.put(path, item);
            path = weightPrefix(item) + path;
            lookupMap.put(item, path);
        }
        return path;
    }

    private String weightPrefix(INamedPackageDescriptorElement item) {
        String prefix;
        if (item instanceof ActionElement || item instanceof NavigationConfigElement ||
                item instanceof ResourceLocationElement) {
            prefix = "1.";
        } else if (item instanceof PermissionElement) {
            prefix = "2.";
        } else if (item instanceof RoleElement) {
            prefix = "3.";
        } else if (item instanceof DirectoryElement || item instanceof GroupElement || item instanceof UserElement) {
            prefix = "4.";
        } else if (item instanceof ActorElement) {
            prefix = "5.";
        } else if (item instanceof ProcessElement) {
            prefix = "6.";
        } else if (item instanceof ResourceElement || item instanceof CollectionElement ||
                item instanceof FolderElement || item instanceof ConfigReference) {
            prefix = "7.";
        } else {
            prefix = "8.";
        }

        return prefix;
    }

    private void putInOrder(
            List<? extends IPackageDescriptorElementDiff<?, ? extends INamedPackageDescriptorElement>> diffList,
            Map<Boolean, SortedSet<IPackageDescriptorElementDiff<?, ? extends INamedPackageDescriptorElement>>> map) {
        if (diffList != null) {
            for (IPackageDescriptorElementDiff<?, ? extends INamedPackageDescriptorElement> diff : diffList) {
                SortedSet<IPackageDescriptorElementDiff<?, ? extends INamedPackageDescriptorElement>> diffSet =
                        map.get(diff.getType() != DifferenceType.REMOVED);
                diffSet.add(diff);
            }
        }
    }

    private void processDiffs(
            SortedSet<IPackageDescriptorElementDiff
                    <?, ? extends INamedPackageDescriptorElement>> diffSet,
            String progressBarMessage, int operationWeight) {
        showProgress(progressBarMessage, operationWeight);
        for (IPackageDescriptorElementDiff
                <?, ? extends INamedPackageDescriptorElement> diff : diffSet) {
            if (diff instanceof ActionsDiff) {
                processActionsDiff((ActionsDiff) diff);
            } else if (diff instanceof NavigationElementsDiff) {
                processNavigationElementsDiff((NavigationElementsDiff) diff);
            } else if (diff instanceof ResourceLocationsDiff) {
                processResourceLocationsDiff((ResourceLocationsDiff) diff);
            } else if (diff instanceof PermissionsDiff) {
                processPermissionsDiff((PermissionsDiff) diff);
            } else if (diff instanceof RolesDiff) {
                processRolesDiff((RolesDiff) diff);
            } else if (diff instanceof FoldersDiff) {
                processFoldersDiff((FoldersDiff) diff);
            } else if (diff instanceof ConfigsDiff) {
                processConfigsDiff((ConfigsDiff) diff);
            } else if (diff instanceof DirectoriesDiff) {
                processDirectoriesDiff((DirectoriesDiff) diff);
            } else if (diff instanceof UsersDiff) {
                processUsersDiff((UsersDiff) diff);
            } else if (diff instanceof ProcessesDiff) {
                processProcessesDiff((ProcessesDiff) diff);
            } else if (diff instanceof ActorsDiff) {
                processActorsDiff((ActorsDiff) diff);
            } else if (diff instanceof ResourcesDiff) {
                processResourcesDiff((ResourcesDiff) diff);
            } else if (diff instanceof CollectionsDiff) {
                processCollectionsDiff((CollectionsDiff) diff);
            } else if (diff instanceof GroupsDiff) {
                processGroupsDiff((GroupsDiff) diff);
            } else if (diff instanceof SchedulesDiff) {
                processSchedulesDiff((SchedulesDiff) diff);
            } else if (diff instanceof UserProfileFieldsDiff) {
                processUserProfileFieldsDiff((UserProfileFieldsDiff) diff);
            } else if (diff instanceof UserProfileFieldGroupsDiff) {
                processUserProfileFieldGroupsDiff((UserProfileFieldGroupsDiff) diff);
            } else if (diff instanceof ReportDiff) {
                processReportDiff((ReportDiff) diff);
            } else if (diff instanceof WizardDiff) {
                processWizardDiff((WizardDiff) diff);
            }
        }
    }

}