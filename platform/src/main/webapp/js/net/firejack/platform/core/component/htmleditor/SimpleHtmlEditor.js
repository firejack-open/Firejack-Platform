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



Ext.define('OPF.core.component.htmleditor.SimpleHtmlEditor', {
    extend: 'Ext.form.field.HtmlEditor',
    alias: 'widget.simplehtmleditor',

    enableAlignments: false,
    enableColors: false,
    enableFont: false,
    enableFontSize: false,
    enableLinks: false,

    sourceEditMode: true,


    initComponent: function() {
        this.plugins = OPF.core.component.htmleditor.plugins.HtmlEditorPlugins.plugins();

        this.callParent(arguments);
    },

//    listeners: {
//        initialize: function(editor) {
//            var cssLink = editor.iframe.contentDocument.createElement("link");
//            cssLink.href = OPF.Cfg.OPF_CONSOLE_URL + "/css/simple-html-editor.css";
//            cssLink.rel = "stylesheet";
//            cssLink.type = "text/css";
//            editor.iframe.contentDocument.body.appendChild(cssLink);
//        }
//    },

    getValue: function() {
        var value = OPF.core.component.htmleditor.SimpleHtmlEditor.superclass.getValue.call(this);
        value = value.replace(/<link[^<>]*?\/?>/g, '');
        return value;
    },

    setValue: function(value) {
        value = '<link type="text/css" rel="stylesheet" href="' + OPF.Cfg.OPF_CONSOLE_URL + '/css/simple-html-editor.css">' + value;
        OPF.core.component.htmleditor.SimpleHtmlEditor.superclass.setValue.call(this, value);
    }

});