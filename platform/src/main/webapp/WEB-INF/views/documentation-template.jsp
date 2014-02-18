<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="opf" uri="http://openflame.firejack.net/jsp/website/funcs" %>
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
<script type="text/javascript">
    OPF.DCfg.REGISTRY_NODE_ID = ${registryNode.id};
    OPF.DCfg.REGISTRY_NODE_TYPE = '${registryNode.type}';
    OPF.DCfg.LOOKUP_URL_SUFFIX = '${lookupUrlSuffix}';
    OPF.DCfg.LOOKUP = '${lookup}';
    OPF.DCfg.COUNTRY = '${country}';
    OPF.DCfg.MAX_IMAGE_WIDTH = ${maxImageWidth};
</script>

<c:set var="currentUrl" value='${docBaseUrl}/${country}/${fn:replace(registryNode.lookup, ".", "/")}'/>

<div id="left-doc-panel"></div>
<div id="middle-doc-panel">
    <div class="doc-container">
        <div class="languages">
            <img src="${baseUrl}/images/documentation/languages.jpg" width="31" /> <h4>Select Language</h4>
            <div id="language-list"></div>
            <script type="text/javascript">
                var CULTURE_DATA = [
                    <c:forEach var="culture" items="${cultures}">
                    [
                        '${fn:toLowerCase(culture.locale.country)}',
                        '${docBaseUrl}/${fn:toLowerCase(culture.locale.country)}/${fn:replace(lookupUrlSuffix, ".", "/")}',
                        '<fmt:message key="language.culture.${culture.name}"/>'
                    ],
                    </c:forEach>
                ];
                var CULTURE_CURRENT = '${country}';
            </script>
            <div id="cultureSelectorContainer" class="culture-selector"></div>
            <div id="addNewSectionButtonContainer"></div>
        </div>

        <h1><img src="${baseUrl}/images/icons/24/${fn:toLowerCase(registryNode.type)}_24.png" alt="${registryNode.name}" title="${registryNode.name}">${registryNode.name}</h1>

        <p class="docPath">
            <c:forEach var="parentRegistryNode" items="${parentRegistryNodes}" varStatus="vs">
                ${vs.first ? '' : '<b>»</b>'} <a href="${docBaseUrl}/${country}/${fn:replace(parentRegistryNode.lookup, ".", "/")}" class="greenp">${parentRegistryNode.name}</a>
            </c:forEach>
        </p>

        <c:if test="${fn:length(links) > 0}">
            <h2><a name="links">Protocols and Descriptors</a></h2>
            <table width="99%" class="docTable" cellpadding="0" cellspacing="0">
                <tr>
                    <th scope="col">
                        <h3>NAME</h3>
                    </th>
                    <th scope="col">
                        <h3>URI</h3>
                    </th>
                    <th scope="col" >
                        <h3>HTTP METHOD</h3>
                    </th>
                    <th scope="col">
                        <h3>LINK</h3>
                    </th>
                </tr>
                <c:forEach var="link" items="${links}" varStatus="vs">
                    <tr>
                        <td class="object" valign="top">${link.type}</td>
                        <td valign="top">${link.url}</td>
                        <c:choose>
                            <c:when test="${link.type == 'WSDL' || link.type == 'WADL'}">
                                <td valign="top">[GET]</td>
                                <td valign="top"><a href="${link.baseUrl}${link.url}" class="exampleBtn">VIEW</a></td>
                            </c:when>
                            <c:otherwise>
                                <td valign="top">[${link.method}]</td>
                                <td valign="top"><a class="exampleBtn" href="${currentUrl}#${fn:toLowerCase(link.type)}-example">EXAMPLE</a></td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
            </table>
        </c:if>

        <tags:documentation-collection collectionWrapper="${collectionWrapper}" mainCollectionId="0" isMainCollection="true"/>

        <c:if test="${fn:length(parameters) > 0}">
            <h2><a name="parameters">Parameters</a></h2>
            <table width="99%" class="docTable" cellpadding="0" cellspacing="0">
                <tr>
                    <th scope="col" width="120px">
                        <h3>Parameter</h3>
                    </th>
                    <th scope="col" width="50px">
                        <h3>Location</h3>
                    </th>
                    <th scope="col" width="70px">
                        <h3>Type</h3>
                    </th>
                    <th scope="col" >
                        <h3>Description</h3>
                    </th>
                    <th scope="col" width="80px">
                        <h3>Required</h3>
                    </th>
                </tr>
                <c:forEach var="parameter" items="${parameters}">
                    <tr>
                        <td class="object" valign="top">${parameter.entity.name}</td>
                        <c:choose>
                            <c:when test="${not empty parameter.entity.location}">
                                <td valign="top">${parameter.entity.location}</td>
                            </c:when>
                            <c:otherwise>
                                <td valign="top">DATA</td>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${not empty parameter.entity.location}">
                                <td valign="top"><fmt:message key="net.firejack.platform.api.registry.model.FieldType.${parameter.entity.fieldType.clazz.simpleName}.class"/></td>
                            </c:when>
                            <c:otherwise>
                                <td valign="top"><a href="${docBaseUrl}/${country}/${fn:replace(parameter.data.lookup, ".", "/")}">${parameter.data.name}</a></td>
                            </c:otherwise>
                        </c:choose>
                        <td valign="top">
                            <div class="editor-container simple-content">
                                <div class="control">
                                    <c:choose>
                                        <c:when test="${not empty parameter.resource && parameter.resource.resourceType == 'TEXT'}">
                                            <c:choose>
                                                <c:when test="${not empty parameter.resource.resourceVersion}">
                                                    <div class="small-description editable editor-text" src="{resourceVersionId: ${parameter.resource.resourceVersion.id}}">${parameter.resource.resourceVersion.text}</div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="small-description editable editor-text" src="{resourceId: ${parameter.resource.id}}">No description provided</div>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:when test="${not empty parameter.resource && parameter.resource.resourceType == 'HTML'}">
                                            <c:choose>
                                                <c:when test="${not empty parameter.resource.resourceVersion}">
                                                    <div class="small-description editable editor-html" src="{resourceVersionId: ${parameter.resource.resourceVersion.id}}">${parameter.resource.resourceVersion.html}</div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="small-description editable editor-html" src="{resourceId: ${parameter.resource.id}}">No description provided</div>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${not empty parameter.entity.location}">
                                                    <div class="small-description editable editor-text" src="{lookupSuffix: 'parameters.${fn:toLowerCase(parameter.entity.name)}'}">No description provided</div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="small-description">No description provided</div>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="action-component" style="display: none;"></div>
                                </div>
                            </div>
                        </td>
                        <td valign="top">True</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>

        <c:if test="${fn:length(properties) > 0}">
            <h2><a name="properties">Properties</a></h2>
            <table width="99%" class="docTable" cellpadding="0" cellspacing="0">
                <tr>
                    <th scope="col" width="160px">
                        <h3>Name</h3>
                    </th>
                    <th scope="col">
                        <h3>Description</h3>
                    </th>
                    <th scope="col" width="110px">
                        <h3>Data Type</h3>
                    </th>
                </tr>
                <c:forEach var="property" items="${properties}">
                    <tr>
                        <td class="object" valign="top">${property.entity.name}</td>
                        <td valign="top">
                            <div class="editor-container simple-content">
                                <div class="control">
                                    <c:choose>
                                        <c:when test="${not empty property.resource && property.resource.resourceType == 'TEXT'}">
                                            <c:choose>
                                                <c:when test="${not empty property.resource.resourceVersion}">
                                                    <div class="small-description editable editor-text" src="{resourceVersionId: ${property.resource.resourceVersion.id}}">${property.resource.resourceVersion.text}</div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="small-description editable editor-text" src="{resourceId: ${property.resource.id}}">No description provided</div>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="small-description editable editor-text" src="{lookupSuffix: 'fields.${fn:toLowerCase(property.entity.name)}'}">No description provided</div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="action-component" style="display: none;"></div>
                                </div>
                            </div>
                        </td>
                        <td valign="top"><fmt:message key="net.firejack.platform.api.registry.model.FieldType.${property.entity.fieldType}.name"/></td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>

        <c:if test="${fn:length(relatedEntities) > 0}">
            <h2><a name="related-entities">Related Entities</a></h2>
            <table width="99%" class="docTable" cellpadding="0" cellspacing="0">
                <tr>
                    <th scope="col" width="160px">
                        <h3>Name</h3>
                    </th>
                    <th scope="col">
                        <h3>Description</h3>
                    </th>
                    <th scope="col" width="110px">
                        <h3>Relationship</h3>
                    </th>
                </tr>
                <c:forEach var="property" items="${relatedEntities}">
                    <tr>
                        <td class="object" valign="top">
                            <a href="${docBaseUrl}/${country}/${fn:replace(property.entity.lookup, ".", "/")}">${property.entity.name}</a>
                        </td>
                        <td valign="top">
                            <div class="editor-container simple-content">
                                <div class="control">
                                    <c:choose>
                                        <c:when test="${not empty property.resource}">
                                            <c:choose>
                                                <c:when test="${not empty property.resource.resourceVersion}">
                                                    <c:choose>
                                                        <c:when test="${property.resource.resourceType == 'TEXT'}">
                                                            <div class="small-description editable editor-text" src="{resourceVersionId: ${property.resource.resourceVersion.id}}">${property.resource.resourceVersion.text}</div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="small-description editable editor-html" src="{resourceVersionId: ${property.resource.resourceVersion.id}}">${property.resource.resourceVersion.html}</div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="small-description editable editor-html" src="{resourceId: ${property.resource.id}}">No description provided</div>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="small-description editable editor-html" src="{registryNodeId: ${property.entity.id}, lookupSuffix: 'description'}">No description provided</div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="action-component" style="display: none;"></div>
                                </div>
                            </div>
                        </td>
                        <td valign="top"><fmt:message key="net.firejack.platform.api.registry.model.RelationshipType.${property.relationship.relationshipType}.name"/></td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>

        <c:if test="${fn:length(actions) > 0}">
            <h2><a name="supported-actions">Supported Actions</a></h2>
            <table width="99%" class="docTable" cellpadding="0" cellspacing="0">
                <tr>
                    <th scope="col" width="160px">
                        <h3>Name</h3>
                    </th>
                    <th scope="col">
                        <h3>Description</h3>
                    </th>
                    <th scope="col" width="110px">
                        <h3>Method</h3>
                    </th>
                </tr>
                <c:forEach var="property" items="${actions}">
                    <tr>
                        <td class="object" valign="top">
                            <a href="${docBaseUrl}/${country}/${fn:replace(property.entity.lookup, ".", "/")}">${property.entity.name}</a>
                        </td>
                        <td valign="top">
                            <div class="editor-container simple-content">
                                <div class="control">
                                    <c:choose>
                                        <c:when test="${not empty property.resource && property.resource.resourceType == 'HTML'}">
                                            <c:choose>
                                                <c:when test="${not empty property.resource.resourceVersion}">
                                                    <div class="small-description editable editor-html" src="{resourceVersionId: ${property.resource.resourceVersion.id}}">${property.resource.resourceVersion.html}</div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="small-description editable editor-html" src="{resourceId: ${property.resource.id}}">No description provided</div>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="small-description editable editor-html" src="{registryNodeId: ${property.entity.id}, lookupSuffix: 'description'}">No description provided</div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="action-component" style="display: none;"></div>
                                </div>
                            </div>
                        </td>
                        <td valign="top">${property.entity.method}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
    </div>
</div>