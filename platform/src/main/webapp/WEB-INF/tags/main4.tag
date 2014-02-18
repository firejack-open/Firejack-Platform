<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ attribute name="title" fragment="true" %>
<%@ attribute name="head" fragment="true" %>
<%@ attribute name="center" fragment="true" %>
<%@ attribute name="useProgressBar" type="java.lang.Boolean" required="true" %>

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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <script type="text/javascript">
        var currentUrl = document.URL;
        var baseUrl    = '${baseUrl}';
        // base url doesn't match real platform url
        if (window.name.indexOf('afterReset') == -1 && currentUrl.indexOf(baseUrl) == -1) {
            var baseUrlWithoutTrailingSlashes = baseUrl.replace(/\/+$/, "");
            var arr = baseUrlWithoutTrailingSlashes.split("/");
            var context = arr[3] ? "/"+arr[3] : "";
            // go to reset url to update base url property
            var resetUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '')+context+'/reset';
            window.name += 'afterReset'; // to prevent cyclic redirect to reset page
            window.location.href = resetUrl;
        }
    </script>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Firejack Technology - Firejack Platform Console: <jsp:invoke fragment="title"/></title>
        <base href="${baseUrl}" />

        <style type="text/css">
            #loading-mask{
                position:absolute;
                top: 0;
                bottom: 0;
                left:0;
                right: 0;
                background-color: #ffffff;
                z-index:1;
            }

            #loading {
                position: absolute;
                height: 60px;
                width: 220px;
                top: 50%;
                left: 50%;
                margin-top: -30px;
                margin-left: -110px;
                border: 1px solid #ccc;
                -webkit-border-radius: 5px;
                -moz-border-radius: 5px;
                border-radius: 5px;
                text-align: center;
                background: url(${baseUrl}/images/opf-loader.gif) no-repeat center center;
                z-index:2;
            }
        </style>
    </head>
    <body>
        <div id="loading-mask" style=""></div>
        <div id="loading"></div>

        <!-- Ext 4.0 -->
        <link rel="stylesheet" type="text/css" href="${baseUrl}/ext-4.0.7/resources/css/ext-all.css" />
        <link rel="stylesheet" type="text/css" href="${baseUrl}/ext-4.0.7/resources/css/ext-all-gray.css" />

        <script type="text/javascript" src="${baseUrl}/ext-4.0.7/ext-all-debug.js"></script>
        <link rel="stylesheet" type="text/css" href="${baseUrl}/ext-4.0.7/ux/CheckHeader.css" />
        <script type="text/javascript" src="${baseUrl}/ext-4.0.7/ux/CheckColumn.js"></script>


        <c:if test="${useProgressBar}">
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/progressbar/OpenFlameSocketEngine.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/progressbar/ProgressBarDialog.js"></script>
        </c:if>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/plugin/TreeViewDragDrop.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/Config.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/OpenFlameConnection.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/OpenflameUtils.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/CommonUtils.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/DateUtils.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/MessageUtils.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/fix/Override.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/utils/fix/Scroller.js"></script>

        <script type="text/javascript">
            OPF.Cfg.OPF_CONSOLE_URL = '${baseUrl}';
            OPF.Cfg.BASE_URL = '${baseUrl}';
            OPF.Cfg.DEBUG_MODE = ${debugMode};
            OPF.Cfg.DEFAULT_LOGIN = '${defaultLogin}';
            OPF.Cfg.DEFAULT_PASSWORD = '${defaultPassword}';
            OPF.Cfg.CAN_EDIT_RESOURCE = ('${canEditResource}' == 'true');
            OPF.Cfg.PAGE_UID = '${pageUID}';
            OPF.Cfg.PLATFORM_VERSION = '${version}';
            OPF.Cfg.BUILD_NUMBER = '${buildNumber}';
            OPF.Cfg.LOAD_TIMESTAMP = ${loadTimestamp};
        </script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/validation/MessageLevel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/validation/Validator.js"></script>

        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/SubLabelable.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/Components.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/HrefClick.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/LinkButton.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/model/FileTreeModel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/ErrorContainer.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/notice/NoticeContainer.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/SelectFileField.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/FileManagerDialog.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/RegistryNodeDropPanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/FileResourcePanel.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/net/firejack/platform/core/component/ToolTipOnClick.js"></script>

        <jsp:invoke fragment="head"/>

        <script type="text/javascript">
            Ext.Ajax.defaultHeaders = {
                'Page-UID': OPF.Cfg.PAGE_UID,
                'Accept': 'application/json'
            };

            Ext.onReady(function(){
                var loadingMask = Ext.get('loading-mask');
                var loading = Ext.get('loading');
                loadingMask.fadeOut({ duration: 0.1, remove: true });
                loading.hide();
            });
        </script>

        <jsp:invoke fragment="center"/>
        <c:if test="${useProgressBar}">
            <div id="swfsocket"></div>
        </c:if>

        <c:if test="${not empty googleAnalyticsId}">
            <script type="text/javascript">
                var _gaq = _gaq || [];
                _gaq.push(['_setAccount', '${googleAnalyticsId}']);
                //            _gaq.push(['_setDomainName', 'firejack.net']);
                _gaq.push(['_trackPageview']);

                (function() {
                    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
                    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
                })();
            </script>
        </c:if>
    </body>
</html>
