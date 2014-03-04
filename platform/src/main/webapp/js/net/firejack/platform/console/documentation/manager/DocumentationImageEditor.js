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