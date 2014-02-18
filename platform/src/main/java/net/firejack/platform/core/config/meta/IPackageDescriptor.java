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
