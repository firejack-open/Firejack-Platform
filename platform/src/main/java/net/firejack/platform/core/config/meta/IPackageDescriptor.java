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

package net.firejack.platform.core.config.meta;

import net.firejack.platform.core.config.meta.construct.BIReportElement;
import net.firejack.platform.core.config.meta.construct.ReportElement;
import net.firejack.platform.core.config.meta.construct.WizardElement;
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
import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldElement;
import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldGroupElement;
import net.firejack.platform.core.config.meta.element.resource.CollectionElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceElement;
import net.firejack.platform.core.config.meta.element.schedule.ScheduleElement;


public interface IPackageDescriptor extends IRootElementContainer {

	String getContextUrl();
    /**
     * @return
     */
    String getVersion();

    /**
     * @param version
     */
    void setVersion(String version);

    /**
     * @return
     */
    String getDependencies();

    /**
     * @return
     */
    IRelationshipElement[] getRelationships();

    /**
     * @return
     */
    ActionElement[] getActionElements();

    /**
     * @return
     */
    NavigationConfigElement[] getNavigationElements();

    /**
     * @return
     */
    DirectoryElement[] getDirectoryElements();

    /**
     * @return
     */
    GroupElement[] getGroupElements();

    ScheduleElement[] getScheduleElements();

    /**
     * @return
     */
    UserElement[] getUsers();

    /**
     * @return
     */
    RoleElement[] getRoles();

    /**
     * @return
     */
    RoleAssignmentElement[] getRoleAssignments();

    /**
     * @return
     */
    PermissionElement[] getPermissions();

    /**
     * @return
     */
    ResourceLocationElement[] getResourceLocationElements();

    /**
     * @return
     */
    FolderElement[] getFolderElements();

    /**
     * @return
     */
    ResourceElement[] getResources();

    /**
     * @return
     */
    ConfigReference[] getConfigsElements();

    /**
     * @return
     */
    ActorElement[] getActorElements();

	/**
	 *
	 * @return
	 */
	CollectionElement[] getCollectionElements();

    /**
     * @return
     */
    ProcessElement[] getProcessElements();

    UserProfileFieldElement[] getUserProfileFields();

    UserProfileFieldGroupElement[] getUserProfileFieldGroups();

	ReportElement[] getReportElements();

    BIReportElement[] getBiReportElements();

    WizardElement[] getWizardElements();

}
