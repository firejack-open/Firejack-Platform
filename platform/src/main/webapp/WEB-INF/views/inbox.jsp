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

        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/inbox.css">

        <!-- include the required UI component libraries here -->
        <script type="text/javascript">
            var FIELD_TYPE_INTEGER = '${FIELD_TYPE_INTEGER}';
            var FIELD_TYPE_STRING = '${FIELD_TYPE_STRING}';
            var FIELD_TYPE_DATE = '${FIELD_TYPE_DATE}';
            var FIELD_TYPE_BOOLEAN = '${FIELD_TYPE_BOOLEAN}';
            var FIELD_TYPE_LONG = '${FIELD_TYPE_LONG}';
            var FIELD_TYPE_DOUBLE = '${FIELD_TYPE_DOUBLE}';
        </script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/ExplanationsStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/AssigneesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/UsersStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/CaseActionsStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/CaseAttachmentsStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/CaseNotesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/NextPreviousAssigneesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/ProcessActivitiesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/ProcessStatusesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/TasksCasesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/MyProcessesStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/AssigneeTeamStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/store/PreviousActivities.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/DetailsCardPanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/process/ProcessInfoPanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/AbstractPerformRollbackDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/AssociatedObjectPanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/NotesPanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/task/AssignTaskDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/task/ClaimTaskDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/task/TaskHistoryPanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/task/PerformTaskDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/task/RollbackTaskDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/team/AssignTeamDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/processcase/CaseAttachmentsPanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/processcase/PerformCaseDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/processcase/RollbackCaseDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/processcase/StopCaseDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/processcase/StartCaseDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/task/TaskDetailsPanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/task/TaskFilterPanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/task/TaskGridPanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/view/ProcessManagerView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/controller/InboxController.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/inbox/Application.js"></script>
    </jsp:attribute>
	<jsp:attribute name="title">Inbox</jsp:attribute>
</tags:manager4>