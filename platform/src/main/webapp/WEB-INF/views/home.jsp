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

<tags:page4 useProgressBar="false">
    <jsp:attribute name="head">
        <script type="text/javascript">
            var pageType = '${pageType}';
            var showUserCreateButton = ${showUserCreateButton};
            var oauthProcessing = '${oauthActive}';
        </script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/model/RoleModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/model/UserModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/model/DashboardModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/model/SignUpModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/model/UserProfileModel.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/site/model/NavigationElementModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/model/NavigationMenuModel.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/AuthenticationProviders.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/store/DashboardStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/view/LoginView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/view/ProfileView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/view/DashboardView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/view/SignUpView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/view/HomeView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/controller/HomeController.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/Application.js"></script>
    </jsp:attribute>
	<jsp:attribute name="title">OPEN FLAME</jsp:attribute>
    <jsp:attribute name="center"></jsp:attribute>
    <jsp:attribute name="infoContent"></jsp:attribute>
</tags:page4>