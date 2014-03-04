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

<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="opf" uri="http://openflame.firejack.net/jsp/website/funcs" %>

<tags:manager4>
    <jsp:attribute name="head">
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/search/model/SearchModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/search/store/SearchResultStore.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/search/view/SearchResultView.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/search/controller/SearchController.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/console/search/Application.js"></script>

        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/search.css" />

        <script type="text/javascript">
            var DOC_ASSET_TYPE_DATA = [
                [
                    'ALL',
                    '<fmt:message key="language.registry-node-type.ALL"/>',
                    'all'
                ],
                <c:forEach var="documentationAssetType" items="${assetTypes}">
                    [
                        '${documentationAssetType.name}',
                        '<fmt:message key="language.registry-node-type.${documentationAssetType.name}"/>',
                        '${fn:toLowerCase(documentationAssetType.name)}'
                    ],
                </c:forEach>
            ];
            var SEARCH_PHRASE = '${term}';
        </script>
    </jsp:attribute>
	<jsp:attribute name="title">Resource Manager</jsp:attribute>
</tags:manager4>