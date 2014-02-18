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


/**
 *
 */
Ext.define('OPF.console.resource.view.resource.HtmlResourceEditor', {
    extend: 'OPF.console.resource.view.resource.BaseResourceEditor',
    alias: 'widget.html-resource-editor',

    title: 'HTML: [New]',

    infoResourceLookup: 'net.firejack.platform.content.abstract-resource.resource.html-resource',

    noContentDefined: '<font title="empty-text" color="#c0c0c0">No Content Defined</font>',

    /**
     *
     */
    initComponent: function() {
        var instance = this;

        this.resourceVersionHtmlField = new OPF.core.component.tinymce.TinyMCEEditor(null, {
            hideLabel: true,
            height: 200,
            name: 'resourceVersionHtml'
        });

        this.additionalFieldSet = Ext.ComponentMgr.create({
            xtype: 'label-container',
            fieldLabel: 'Html',
            subFieldLabel: '',
            layout: 'fit',
            height: 200,
            items: [
                this.resourceVersionHtmlField
            ]
        });

        this.callParent(arguments);
    },

    onAfterSetSpecificValue: function(jsonData) {
        this.resourceVersionHtmlField.setValue(jsonData.resourceVersion.html);
    },

    onBeforeSpecificDataSave: function(formData) {
        if (formData.resourceVersionHtml != this.noContentDefined) {
            formData.resourceVersion.html = formData.resourceVersionHtml;
        }
        delete formData.resourceVersionHtml;
    },

    onReloadResourceVersionFailure: function() {
        this.resourceVersionHtmlField.setValue(this.noContentDefined);
    },

    onSuccessDeleteResourceVersion: function() {
        this.resourceVersionHtmlField.setValue(this.noContentDefined);
    }

});