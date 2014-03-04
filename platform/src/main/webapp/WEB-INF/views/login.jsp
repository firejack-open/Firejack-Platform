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

<tags:main4 useProgressBar="false">
    <jsp:attribute name="head">
        <script type="text/javascript">
            var pageType = '${pageType}';
            var showUserCreateButton = ${showUserCreateButton};
            var oauthProcessing = '${oauthActive}';
            OPF.Cfg.EXTRA_PARAMS = {
                filterMessages: ${filterMessages},
                publicKey: '${publicKey}',
                facebookLoginEnable: '${facebookLoginEnable}',
                twitterLoginEnable: '${twitterLoginEnable}',
                linkedinLoginEnable: '${linkedinLoginEnable}'
            };
        </script>

        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/login.css" />

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/rsa/JSBN.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/rsa/RSA.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/NavigationMenuRegister.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/AuthenticationProviders.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/authority/model/RoleModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/directory/model/UserModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/model/DashboardModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/model/SignUpModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/model/UserProfileModel.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/RegistryNodeType.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/site/model/NavigationElementModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/model/NavigationMenuModel.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/home/view/SignUpView.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/login/view/LoginView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/login/controller/LoginController.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/login/Application.js"></script>
    </jsp:attribute>
	<jsp:attribute name="title">Firejack Platform</jsp:attribute>
    <jsp:attribute name="center">
        <div id="content"></div>
        <%--<div id="welcome-message" ><p style="color: #6f6e6e;">${welcomeMessage}</p></div>--%>
    </jsp:attribute>
</tags:main4>