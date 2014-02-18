<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%--
  ~ Firejack Firejack Platform - Copyright (c) 2012 Firejack Technologies
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

<tags:main4 useProgressBar="false">
    <jsp:attribute name="head">
        <script type="text/javascript">
            var pageType = '${pageType}';
        </script>
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/login.css" />

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/NavigationMenuRegister.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/RegistryNodeType.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/AuthenticationProviders.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/model/RoleModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/model/UserModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/model/DashboardModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/model/SignUpModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/model/UserProfileModel.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/site/model/NavigationElementModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/model/NavigationMenuModel.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/view/SignUpView.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/forgotpassword/view/ForgotPasswordView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/forgotpassword/controller/ForgotPasswordController.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/forgotpassword/Application.js"></script>
    </jsp:attribute>
	<jsp:attribute name="title">Firejack Platform</jsp:attribute>
    <jsp:attribute name="center">
        <div id="content"></div>
        <%--<div id="welcome-message" ><p style="color: #6f6e6e;">${welcomeMessage}</p></div>--%>
    </jsp:attribute>
</tags:main4>