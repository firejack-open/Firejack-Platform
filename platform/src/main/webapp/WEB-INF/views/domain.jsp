<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements.  See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership.  The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License.  You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>

<tags:manager4>
    <jsp:attribute name="head">
        <!-- include the required UI component libraries here -->
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/select-file-field.css">

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/PackageVersionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/AssetFileModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/UserActorModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/FieldModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/VersionsStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/AssetFilesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/StandardEntitiesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/TypeEntitiesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/ActionParametersStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/AssignedGroupsStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/AvailableGroupsStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/AssignedRolesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/AvailableRolesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/AssignedUsersStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/AvailableUsersStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/DatabasesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/AssociatedPackagesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/ActivitiesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/ActivityActorsStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/ExplanationsStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/ProcessFieldsStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/StatusesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/RegistryNodeFieldStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/TargetEntitiesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/store/ContextRolesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/dialog/DatabaseMigrationDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/dialog/PackageInstallationDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/dialog/PackageUninstallationDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/entity/ContextRoleGridFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/entity/ContextRoleAdditionDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/entity/ReferenceHtmlEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/entity/ReferenceObjectFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/EntityEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/RelationshipEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/PackageEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/ReportEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/BIReportEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/WizardEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/DomainView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/action/ActionParameterRowEditorDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/action/ActionParametersFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/action/ActionEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/process/ActorFieldSets.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/process/ActorEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/process/StatusesFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/process/ExplanationsFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/process/ActivitiesFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/process/CustomFieldsFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/process/ProcessEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/system/AssociatedPackagesFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/system/SystemEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/system/DatabaseEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/system/ServerAssociatedPackagesFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/system/ServerEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/system/FilestoreEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/controller/DomainController.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/controller/PackageController.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/controller/EntityController.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/controller/ActionController.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/controller/ActorController.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/controller/ProcessController.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/controller/SystemController.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/controller/RelationshipController.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/Application.js"></script>
    </jsp:attribute>
	<jsp:attribute name="title">Domain Manager</jsp:attribute>
</tags:manager4>