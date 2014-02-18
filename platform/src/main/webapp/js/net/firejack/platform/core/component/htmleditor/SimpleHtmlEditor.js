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