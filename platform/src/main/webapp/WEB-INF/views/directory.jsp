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