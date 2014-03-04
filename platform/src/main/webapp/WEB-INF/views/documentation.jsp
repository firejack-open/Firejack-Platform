<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="opf" uri="http://openflame.firejack.net/jsp/website/funcs" %>

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

<tags:page4 useProgressBar="false">
    <jsp:attribute name="title">Documentation</jsp:attribute>
    <jsp:attribute name="head">
        <!-- include the OpenFlame Console js file -->
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/documentation.css" />

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/manager/DocumentationConfig.js"></script>

        <script type="text/javascript">
            OPF.DCfg.DOC_URL = '${docBaseUrl}';
            OPF.DCfg.HAS_ADD_PERMISSION = ${hasAddPermission};
            OPF.DCfg.HAS_EDIT_PERMISSION = ${hasEditPermission};
            OPF.DCfg.HAS_DELETE_PERMISSION = ${hasDeletePermission};

            OPF.Cfg.PAGE_TYPE = 'DOCUMENTATION';
        </script>

        <c:if test="${hasAddPermission || hasEditPermission}">
            <link rel="stylesheet" type="text/css" href="${baseUrl}/css/simple-html-editor.css" />

            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/htmleditor/SimpleHtmlEditor.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/htmleditor/plugins/HtmlEditorPlugins.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/htmleditor/plugins/SpecialHeadingFormats.js"></script>

            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/HrefClick.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/model/CultureModel.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/resource/CultureComboBox.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/resource/ImageResourceEditor.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/resource/TextResourceEditor.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/resource/ResourceControl.js"></script>

            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/manager/DocumentationImageEditor.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/manager/DocumentationEditorField.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/manager/DocumentationElement.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/manager/DocumentationResourceElement.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/manager/DocumentationCollectionElement.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/manager/DocumentationNewResourceDialog.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/manager/DocumentationEditor.js"></script>
        </c:if>
        <c:if test="${hasAddPermission || hasEditPermission || hasDeletePermission}">
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/manager/DocumentationManager.js"></script>
        </c:if>
        <c:if test="${!(hasAddPermission || hasEditPermission || hasDeletePermission)}">
            <style type="text/css">
                .collection {
                    border: none;
                    padding: 0;
                    margin-bottom: 0;
                }
            </style>
        </c:if>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/manager/DocumentationTreePanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/view/DocumentationView.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/controller/DocumentationController.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/documentation/Application.js"></script>
    </jsp:attribute>
    <jsp:attribute name="center">
        <div id="doc-header"></div>
        <div id="documentation">
            <jsp:include page="documentation-template.jsp"/>
        </div>
    </jsp:attribute>
</tags:page4>