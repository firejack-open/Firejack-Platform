<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

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

<tags:manager4>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/model/SecuredEntityModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/DirectoryFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/DirectoryEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/ldap/SchemaEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/ldap/AuthenticationChecker.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/ldap/GroupsMappingTab.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/ldap/LdapManagerDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/UserRoleAssignmentDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/UserRoleGridFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/UserBasicInfoFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/UserInfoFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/OAuthInfoFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/BaseRoleFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/UserRoleFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/UserEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/SystemUserEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/GroupRoleFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/editor/GroupEditor.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/model/UserProfileFieldTreeModel.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/view/DirectoryGridView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/view/UserGridView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/view/SystemUserGridView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/view/UserProfileFieldGroupViewDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/view/UserProfileFieldViewDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/view/UserProfileFieldGridView.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/view/DirectoryView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/controller/DirectoryController.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/Application.js"></script>

        <style type="text/css">
            .upf_empty_row {
                display: none;
            }
        </style>
    </jsp:attribute>
	<jsp:attribute name="title">Directory Manager</jsp:attribute>
</tags:manager4>