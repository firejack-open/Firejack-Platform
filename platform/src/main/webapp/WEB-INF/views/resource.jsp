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