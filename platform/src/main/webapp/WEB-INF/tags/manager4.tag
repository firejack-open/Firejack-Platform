<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ attribute name="title" fragment="true" %>
<%@ attribute name="head" fragment="true" %>
<%@ attribute name="infoContent" fragment="true" %>

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

<tags:page4 useProgressBar="true">
    <jsp:attribute name="head">
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/ext-ux-grid-row-editor.css" />
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/ui/UIUtils.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/LabelContainer.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/BaseGridView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/RegistryNodeButton.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/CloudNavigation.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/editor/BasicInfoFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/editor/DescriptionInfoFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/editor/ResourceFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/editor/PermissionFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/editor/FieldAllowedValueGrid.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/editor/FieldEditorDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/editor/FieldGridFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/editor/BaseEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/editor/BaseSupportedPermissionEditor.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/RootDomainEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/domain/view/DomainEditor.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/security/store/AssignedUserStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/security/store/AvailableRolesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/security/store/DirectoryStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/security/store/KnownUserStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/security/SecurityWindow.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/license/LicenseWindow.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/PageLayout.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/NavigationPageLayout.js"></script>

        <script type="text/javascript">
            OPF.Cfg.PAGE_TYPE = '${pageType}';
        </script>

        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/simple-html-editor.css" />

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/htmleditor/SimpleHtmlEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/htmleditor/plugins/HtmlEditorPlugins.js"></script>
        <%--<script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/htmleditor/plugins/SpecialHeadingFormat.js"></script>--%>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/htmleditor/plugins/SpecialHeadingFormats.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/model/CultureModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/resource/DisplayComponent.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/resource/CultureComboBox.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/resource/ImageResourceEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/resource/TextResourceEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/resource/ResourceControl.js"></script>

        <jsp:invoke fragment="head"/>
    </jsp:attribute>
	<jsp:attribute name="title"><jsp:invoke fragment="title"/></jsp:attribute>
    <jsp:attribute name="infoContent"><jsp:invoke fragment="infoContent"/></jsp:attribute>
</tags:page4>