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


Ext.define('OPF.console.documentation.manager.DocumentationImageEditor', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.imageEditor',

    editor: null,

    plain: true,

    initComponent: function() {
        var instance = this;

        this.imageWidth = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'imageWidth'
        });

        this.imageHeight = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'imageHeight'
        });

        this.resourceFileTemporaryName = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'resourceFileTemporaryName'
        });

        this.resourceFileOriginalName = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'resourceFileOriginalName'
        });

        this.imageContainer = Ext.ComponentMgr.create({
            xtype: 'container'
        });

//        Ext.WindowMgr.zseed = 12000; // default window group for all new windows (must be in front of editor (z-index 11000))

        this.items = [
            this.imageContainer
        ];

        this.callParent(arguments);
    },

    loadImageContainer: function(url, origWidth, origHeight) {
        var width = origWidth;
        var height = origHeight;
        if (origWidth > OPF.DCfg.MAX_IMAGE_WIDTH) {
            var widthCoof = origWidth / OPF.DCfg.MAX_IMAGE_WIDTH;
            width = origWidth / widthCoof;
            height = origHeight / widthCoof;
        }
        var padding = Math.floor((OPF.DCfg.MAX_IMAGE_WIDTH - width) / 2 + 4);
        var imageHtml = '<img class="image-edit" ' +
                             'src="' + url + '" ' +
                             'width="' + width + '" ' +
                             'height="' + height + '" ' +
                             'style="padding-left:' + padding + 'px; padding-right:' + padding + 'px;"/>';
        this.imageContainer.update(imageHtml);

        var editorWidth = this.getSize().width;
        var editorHeight = height + 50;

        this.setSize(editorWidth, editorHeight);
        this.editor.field.container.setSize(editorWidth, editorHeight);
        this.editor.boundEl.setSize(editorWidth, editorHeight);
        this.editor.realign(true);
    },

    setImageContainerToNotProvided: function() {
        var html = '<div class="image-edit">No image provided</div>';
        this.imageContainer.update(html);
    },

    getValue: function() {
        
    },

    setValue: function() {

    },

    reset: function() {

    },

    getJsonData: function() {
        return {
            width: this.imageWidth.getValue(),
            height: this.imageHeight.getValue(),
            resourceFileTemporaryName: this.resourceFileTemporaryName.getValue(),
            resourceFileOriginalName: this.resourceFileOriginalName.getValue()
        };
    }

});