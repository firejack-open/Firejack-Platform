<%--
  ~ Firejack Platform - Copyright (c) 2011 Firejack Technologies
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