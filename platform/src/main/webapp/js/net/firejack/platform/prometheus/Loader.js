/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
