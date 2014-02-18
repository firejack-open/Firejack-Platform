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
            path: OPF_CONSOLE_URL + '/js/net/firejack/platform/',
            items: [
                'core/utils/PrototypeUtils',
                'core/utils/Config',
                'core/utils/CommonUtils',
                'core/utils/MessageUtils',
                'core/utils/DateUtils',
                'core/utils/ExportStore',
                'core/utils/ui/InputTextMask',
                'core/utils/ui/UIUtils',
                'core/utils/ModelHelper',
                'core/utils/StoreHelper',

                'core/utils/progressbar/OpenFlameSocketEngine',
                'core/utils/progressbar/ProgressBarDialog',
                'core/utils/rsa/JSBN',
                'core/utils/rsa/RSA',

                'core/validation/MessageLevel',
                'core/validation/FormValidator',
                'core/validation/vtype/PasswordVType',
                'core/validation/vtype/VTypes',

                'core/component/MenuItem',
                'core/component/LabelContainer',
                'core/component/NavigationMenu',
                'core/component/UploadFileDialog',
                'core/component/ImageContainer',
                'core/component/ErrorContainer',
                'core/component/ToolTipOnClick',
                'core/component/notice/NoticeContainer',

                'core/component/form/SubLabelable',
                'core/component/form/ErrorHandler',
                'core/component/form/FieldContainer',
                'core/component/form/Text',
                'core/component/form/Hidden',
                'core/component/form/Checkbox',
                'core/component/form/ComboBox',
                'core/component/form/BoxSelect',
                'core/component/form/Date',
                'core/component/form/DateTime',
                'core/component/form/Display',
                'core/component/form/File',
                'core/component/form/HtmlEditor',
                'core/component/form/Number',
                'core/component/form/TextArea',
                'core/component/form/Time',
                'core/component/form/SearchComboBox',
                'core/component/form/Rating',
                'core/component/form/ReferenceHtmlEditor',

                'core/component/grid/Column',
                'core/component/grid/RatingColumn',
                'core/component/grid/RowToolbarGrid',

                'core/component/htmleditor/SimpleHtmlEditor',
                'core/component/htmleditor/plugins/HtmlEditorPlugins',
                'core/component/htmleditor/plugins/SpecialHeadingFormats',
                'core/component/HrefClick',
                'core/component/plugin/TreeViewDragDrop',

                'core/model/CultureModel',
                'core/model/RegistryNodeTreeModel',

                'console/domain/model/DomainModel',
                'console/domain/model/EntityModel',
                'console/domain/model/RelationshipModel',
                'console/domain/model/ReportModel',
                'console/domain/model/FieldModel',
                'console/domain/model/ReportFieldModel',

                'core/component/resource/DisplayComponent',
                'core/component/resource/CultureComboBox',
                'core/component/resource/ImageResourceEditor',
                'core/component/resource/TextResourceEditor',
                'core/component/resource/ResourceControl',
                'core/component/resource/TextResourceControl',
                'core/component/resource/ImageResourceControl',

                'core/utils/fix/Override'
            ]
        }

    ];

    for (var i = 0; i < scripts.length; i++) {
        var script = scripts[i];
        for (var j = 0; j < script.items.length; j++) {
            document.write('<script type="text/javascript" src="' + script.path + script.items[j] + '.js?v=' + CACHE_VERSION + '"></script>');
        }
    }

})();
