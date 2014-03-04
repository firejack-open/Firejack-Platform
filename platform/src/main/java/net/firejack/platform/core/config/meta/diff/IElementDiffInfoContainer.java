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