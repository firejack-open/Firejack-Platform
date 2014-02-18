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

package net.firejack.platform.core.config.meta.diff;

import net.firejack.platform.core.config.patch.EntityElementManager;

import java.util.List;


public interface IElementDiffInfoContainer {

    /**
     * @return
     */
    List<EntitiesDiff> getEntityDiffs();

    /**
     * @return
     */
    List<FieldsDiff> getFieldDiffs();

    /**
     * @return
     */
    List<IndexesDiff> getIndexesDiffs();

    /**
     * @return
     */
    List<RelationshipsDiff> getRelationshipDiffs();

    /**
     * @return
     */
    List<ActionsDiff> getActionDiffs();

    /**
     * @return
     */
    List<PermissionsDiff> getPermissionDiffs();

    /**
     * @return
     */
    List<RolesDiff> getRoleDiffs();

    /**
     * @return
     */
    List<NavigationElementsDiff> getNavigationElementDiffs();

    /**
     * @return
     */
    List<ResourceLocationsDiff> getResourceLocationDiffs();

    /**
     * @return
     */
    List<DomainsDiff> getDomainDiffs();

    /**
     * @return
     */
    List<FoldersDiff> getFolderDiffs();

    /**
     * @return
     */
    List<ConfigsDiff> getConfigDiffs();

    /**
     * @return
     */
    List<ProcessesDiff> getProcessDiffs();

    /**
     * @return
     */
    List<ActorsDiff> getActorDiffs();

    /**
     * @return
     */
    List<ResourcesDiff> getResourceDiffs();

    /**
     * @return
     */
    List<CollectionsDiff> getCollectionDiffs();

    /**
     * @return
     */
    List<RoleAssignmentsDiff> getRoleAssignmentDiffs();

    /**
     * @return
     */
    List<DirectoriesDiff> getDirectoryDiffs();

    /**
     * @return
     */
    List<UsersDiff> getUserDiffs();

    /**
     * @return
     */
    List<GroupsDiff> getGroupDiffs();

    List<SchedulesDiff> getScheduleDiffs();

    List<UserProfileFieldsDiff> getUserProfileFieldsDiffs();

    List<UserProfileFieldGroupsDiff> getUserProfileFieldGroupsDiffs();

    List<ReportDiff> getReportDiffs();

    List<BIReportDiff> getBiReportDiffs();

    List<WizardDiff> getWizardDiffs();

    /**
     * @return
     */
    EntityElementManager getNewEntitiesManager();

    /**
     * @return
     */
    EntityElementManager getOldEntitiesManager();

}