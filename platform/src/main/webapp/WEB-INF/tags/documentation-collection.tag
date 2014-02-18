<%@tag description="display the whole collection with resources" pageEncoding="UTF-8"%>
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

<%@attribute name="collectionWrapper" type="net.firejack.platform.service.content.broker.documentation.service.ReferenceWrapper" required="true" %>
<%@attribute name="mainCollectionId" type="java.lang.Long" required="true" %>
<%@attribute name="isMainCollection" type="java.lang.Boolean" required="true" %>

<c:set var="collectionId" value="${collectionWrapper.reference.id}"/>
<div id="collection_${isMainCollection ? 'main' : opf:randomValue(collectionId)}" class="collection content collection-${mainCollectionId} ${isMainCollection ? '' : 'sortable'}" src="{collectionId: ${mainCollectionId}, resourceId: ${collectionId}}">
    <c:forEach var="referenceWrapper" items="${collectionWrapper.childrenReferences}" varStatus="vs">
        <c:choose>
            <c:when test="${fn:contains(referenceWrapper.type.name, '_RESOURCE')}">
                <c:set var="resource" value="${referenceWrapper.reference}"/>
                <div id="content_resource_${opf:randomValue(resource.id)}" class="editor-container content collection-${collectionId}" src="{collectionId: ${collectionId}, resourceId: ${resource.id}}">
                    <h2><a name="${opf:anchorEncode(resource.name)}">${resource.name}</a></h2>
                    <div class="control info">
                        <c:choose>
                            <c:when test="${resource.resourceType == 'HTML'}">
                                <c:choose>
                                    <c:when test="${not empty resource.resourceVersion}">
                                        <div class="description editable editor-html" src="{resourceVersionId: ${resource.resourceVersion.id}}">${not empty resource.resourceVersion.html ? resource.resourceVersion.html : 'No description provided'}</div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="description editable editor-html" src="{}">No description provided</div>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:when test="${resource.resourceType == 'TEXT'}">
                                <c:choose>
                                    <c:when test="${not empty resource.resourceVersion}">
                                        <pre class="description editable editor-text" src="{resourceVersionId: ${resource.resourceVersion.id}}">${not empty resource.resourceVersion.text ? opf:htmlEncode(opf:wrapText(resource.resourceVersion.text, 70)) : 'No description provided'}</pre>
                                    </c:when>
                                    <c:otherwise>
                                        <pre class="description editable editor-text" src="{}">No description provided</pre>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:when test="${resource.resourceType == 'IMAGE'}">
                                <c:set var="imageUrl" value="${stsBaseUrl}/rest/content/resource/image/by-filename/${resource.id}/${resource.resourceVersion.id}_${resource.resourceVersion.version}_${resource.resourceVersion.culture}"/>
                                <div class="description editable editor-image" src="{resourceVersionId: ${resource.resourceVersion.id}, imageUrl: '${imageUrl}'}">
                                    <c:choose>
                                        <c:when test="${not empty resource.resourceVersion.originalFilename}">
                                            <c:choose>
                                                <c:when test="${resource.resourceVersion.width >= maxImageWidth}">
                                                    <img width="${maxImageWidth}" src="${imageUrl}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="padding">
                                                        <fmt:formatNumber value="${(maxImageWidth - resource.resourceVersion.width) / 2 + 4}" maxFractionDigits="0"/>
                                                    </c:set>

                                                    <img style="padding-left:${padding}px; padding-right:${padding}px;" width="${resource.resourceVersion.width}" src="${imageUrl}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            No image provided
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:when>
                        </c:choose>
                        <div class="action-component buttons-updown" style="display: none;"></div>
                    </div>
                </div>
            </c:when>
            <c:when test="${referenceWrapper.type.name == 'COLLECTION'}">
                <tags:documentation-collection collectionWrapper="${referenceWrapper}" mainCollectionId="${collectionId}" isMainCollection="false"/>
            </c:when>
            <c:otherwise>Don't support this type '${referenceWrapper.type.name}'.</c:otherwise>
        </c:choose>
    </c:forEach>
</div>