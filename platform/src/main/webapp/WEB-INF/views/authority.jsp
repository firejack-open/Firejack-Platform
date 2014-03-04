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
        <!-- include the page layout library that initializes the page -->
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/editor/RoleEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/editor/PermissionEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/editor/ResourceLocationFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/editor/ResourceLocationEditor.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/view/RoleGridView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/view/PermissionGridView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/view/ResourceLocationGridView.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/view/AuthorityView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/controller/AuthorityController.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/Application.js"></script>
    </jsp:attribute>
	<jsp:attribute name="title">Authorization Manager</jsp:attribute>
</tags:manager4>