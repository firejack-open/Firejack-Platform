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
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/CollectionMembershipModel.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/FolderEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/ScheduleEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/CollectionMembershipFieldSet.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/CollectionEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/resource/ResourceLanguageBar.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/resource/BaseResourceEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/resource/TextResourceEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/resource/HtmlResourceEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/resource/ImageResourceEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/resource/AudioResourceEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/resource/VideoResourceEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/resource/DocumentResourceEditor.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/resource/FileResourceEditor.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/model/ScheduleHistoryModel.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/ResourceGridView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/ConfigurationGridView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/ScheduleGridView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/ScheduleHistoryGridView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/view/ResourceView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/controller/ResourceController.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/resource/Application.js"></script>
    </jsp:attribute>
	<jsp:attribute name="title">Site Manager</jsp:attribute>
</tags:manager4>