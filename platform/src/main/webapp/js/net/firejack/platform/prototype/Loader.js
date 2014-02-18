/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

(function() {

    var scripts = [
        {
            path: OPF_CONSOLE_URL + '/ext-4.0.7/ux/',
            items: [
                'CheckColumn'
            ]
        },
        {
            path: OPF_CONSOLE_URL + '/js/net/firejack/platform/',
            items: [
                'core/utils/Config',
                'core/utils/CommonUtils',
                'core/utils/MessageUtils',
                'core/utils/DateUtils',
                'core/utils/ExportStore',
                'core/utils/ui/InputTextMask',
                'core/utils/fix/Scroller',

                'core/utils/progressbar/OpenFlameSocketEngine',
                'core/utils/progressbar/ProgressBarDialog',
                'core/utils/rsa/JSBN',
                'core/utils/rsa/RSA',

                'core/validation/MessageLevel',
                'core/validation/Validator',
                'core/validation/vtype/PasswordVType',

                'core/component/SubLabelable',
                'core/component/LabelContainer',
                'core/component/Components',
                'core/component/NumberField',
                'core/component/DateTime',
                'core/component/NavigationMenu',
                'core/component/UploadFileDialog',
                'core/component/ImageContainer',
                'core/component/ErrorContainer',
                'core/component/notice/NoticeContainer',

                'core/component/htmleditor/SimpleHtmlEditor',
                'core/component/htmleditor/plugins/HtmlEditorPlugins',
                'core/component/htmleditor/plugins/SpecialHeadingFormats',
                'core/component/HrefClick',

                'core/model/CultureModel',
                'core/component/resource/DisplayComponent',
                'core/component/resource/CultureComboBox',
                'core/component/resource/ImageResourceEditor',
                'core/component/resource/TextResourceEditor',
                'core/component/resource/ResourceControl',
                'core/component/resource/TextResourceControl',
                'core/component/resource/ImageResourceControl'
            ]
        }

    ];

    for (var i = 0; i < scripts.length; i++) {
        var script = scripts[i];
        for (var j = 0; j < script.items.length; j++) {
            document.write('<script type="text/javascript" src="' + script.path + script.items[j] + '.js?v=' + CACHE_VERSION + '"></script>');
        }
    }

    var styles = [
        {
            path: OPF_CONSOLE_URL + '/ext-4.0.7/resources/css/',
            items: [
                'ext-all',
                'ext-all-gray'
            ]
        },
        {
            path: OPF_CONSOLE_URL + '/ext-4.0.7/ux/',
            items: [
                'CheckHeader'
            ]
        },
        {
            path: OPF_CONSOLE_URL + '/css/',
            items: [
                'simple-html-editor',
                'prototype',
                'calendar'
            ]
        }
    ];

    for (var m = 0; m < styles.length; m++) {
        var style = styles[m];
        for (var n = 0; n < style.items.length; n++) {
            document.write('<link rel="stylesheet" type="text/css" href="' + style.path + style.items[n] + '.css?v=' + CACHE_VERSION + '"/>');
        }
    }

})();
