<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="title" fragment="true" %>
<%@ attribute name="head" fragment="true" %>
<%@ attribute name="center" fragment="true" %>
<%@ attribute name="infoContent" fragment="true" %>

<%@ attribute name="useProgressBar" type="java.lang.Boolean" %>

<%--
  ~ Firejack Platform - Copyright (c) 2012 Firejack Technologies
  ~
  ~ This source code is the product of the Firejack Technologies
  ~ Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
  ~ Asanov) and licensed only under valid, executed license agreements
  ~ between Firejack Technologies and its customers. Modification and / or
  ~ re-distribution of this source code is allowed only within the terms
  ~ of an executed license agreement.
  ~
  ~ Any modification of this code voids any and all warranties and indemnifications
  ~ for the component in question and may interfere with upgrade path. Firejack Technologies
  ~ encourages you to extend the core framework and / or request modifications. You may
  ~ also submit and assign contributions to Firejack Technologies for consideration
  ~ as improvements or inclusions to the platform to restore modification
  ~ warranties and indemnifications upon official re-distributed in patch or release form.
  --%>

<tags:main4 useProgressBar="${useProgressBar}">
    <jsp:attribute name="head">
        <!-- include the Firejack Platform Console css file -->
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/clds4.css">
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/style.css">

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/NavigationMenuRegister.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/NavigationMenu.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/BreadCrumbs.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/tinymce/tiny_mce_src.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/tinymce/TinyMCEEditor.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/model/FieldModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/model/RegistryNodeTreeModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/RootDomainModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/PackageModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/DatabaseModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/ServerModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/FilestoreModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/SystemModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/DomainModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/EntityModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/RelationshipModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/ActionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/ActionParameterModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/ActorModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/ActivityModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/StatusModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/CaseExplanationModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/ProcessModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/ReportModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/BIReportModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/WizardModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/site/model/NavigationElementModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/model/RoleModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/model/PermissionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/model/ResourceLocationModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/model/DirectoryModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/model/GroupModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/model/UserModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/model/SystemUserModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/model/UserProfileFieldModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/model/UserProfileFieldGroupModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/statistics/model/LogEntryModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/statistics/model/LogTransactionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/statistics/model/MetricsEntryModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/security/model/AssignedRoleModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/model/UserRoleModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/FolderModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/ConfigModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/CollectionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/ScheduleModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/ResourceModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/TextResourceVersionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/TextResourceModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/HtmlResourceVersionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/HtmlResourceModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/ImageResourceVersionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/ImageResourceModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/AudioResourceVersionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/AudioResourceModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/VideoResourceVersionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/VideoResourceModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/DocumentResourceVersionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/DocumentResourceModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/FileResourceVersionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/FileResourceModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/model/CaseAttachmentModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/model/CaseActionModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/model/CaseNoteModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/model/CaseModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/model/TaskModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/model/ProcessFieldModel.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/RegistryNodeType.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/ManagerLayout.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/UploadFileDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/GridDragDropRowOrder.js"></script>

        <script type="text/javascript">
            OPF.Cfg.USER_INFO = {
                id: ${not empty currentUser.id ? currentUser.id : 'null'},
                <c:if test="${not empty currentUser.username}">
                username: '${currentUser.username}',
                </c:if>
                <c:if test="${empty currentUser.username}">
                username: null,
                </c:if>
                isLogged: ${not empty currentUser.username}
            };
            OPF.core.utils.RegistryNodeType.AllowTypes = ${allowTypes};
        </script>
        <jsp:invoke fragment="head"/>
    </jsp:attribute>
	<jsp:attribute name="title"><jsp:invoke fragment="title"/></jsp:attribute>
    <jsp:attribute name="center">
        <div id="content">
            <jsp:invoke fragment="center"/>
        </div>

        <!-- The content for the information panel -->
        <div style="display:none;">
            <div id="info-content" class="info-content"><jsp:invoke fragment="infoContent"/></div>
        </div>
    </jsp:attribute>
</tags:main4>