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


Ext.define('OPF.core.component.resource.ResourceControl', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.resource-control',

    cls: 'info-resource-content',

    innerCls: 'info-content',

    textResourceLookup: null,
    textInnerCls: 'info-text',
    textMaxLength: null,

    htmlResourceLookup: null,
    htmlInnerCls: 'info-html',

    imgResourceLookup: null,
    imgInnerCls: 'info-image',
    imageWidth: null,
    imageHeight: null,

    border: false,

    initComponent: function() {
        var instance = this;

        this.innerLoadingCmp = new Ext.Component({
            html: 'Loading...'
        });

        var buttonsArray = [];
        var resourcesArray = [this.innerLoadingCmp];

        if (this.imgResourceLookup) {
            this.innerImgCnt = new Ext.Component({
                cls: this.imgInnerCls
            });
            resourcesArray.push(this.innerImgCnt);

            buttonsArray.push({
                xtype: 'hrefclick',
                cls: 'resourceEditLink',
                html: 'EDIT IMAGE',
                onClick: function() {
                    var winId = 'ImageEditorWinId9320';
                    var window = Ext.getCmp(winId);
                    if (window == null) {
                        window = new OPF.core.component.resource.ImageResourceEditor(winId, {
                            imageWidth: instance.imageWidth,
                            imageHeight: instance.imageHeight
                        });
                    }
                    window.setEditingEl(instance.innerImgCnt);
                    window.setIdData(instance.imageIdData);
                    window.show();
                }
            });
        }

        if (this.textResourceLookup) {

            if (OPF.isNotEmpty(this.textMaxLength)) {
                this.innerTextCnt = new OPF.core.component.resource.DisplayComponent({
                    cls: this.textInnerCls,
                    maxTextLength: this.textMaxLength
                });
            } else {
                this.innerTextCnt = new Ext.Component({
                    cls: this.textInnerCls
                });
            }
            resourcesArray.push(this.innerTextCnt);

            buttonsArray.push({
                xtype: 'hrefclick',
                cls: 'resourceEditLink',
                html: 'EDIT TEXT',
                onClick: function() {
                    var winId = 'TextEditorWinId5823';
                    var window = Ext.getCmp(winId);
                    if (window == null) {
                        window = new OPF.core.component.resource.TextResourceEditor(winId, 'textarea');
                    }
                    window.setEditingEl(instance.innerTextCnt);
                    window.setIdData(instance.textIdData);
                    window.show();
                }
            });
        }
        
        if (this.htmlResourceLookup) {
            this.innerHtmlCnt = new Ext.Component({
                cls: this.htmlInnerCls
            });
            resourcesArray.push(this.innerHtmlCnt);

            buttonsArray.push({
                xtype: 'hrefclick',
                cls: 'resourceEditLink',
                html: 'EDIT HTML',
                onClick: function() {
                    var winId = 'HtmlEditorWinId9420';
                    var window = Ext.getCmp(winId);
                    if (window == null) {
                        window = new OPF.core.component.resource.TextResourceEditor(winId, 'tinymceeditor');
                    }
                    window.setEditingEl(instance.innerHtmlCnt);
                    window.setIdData(instance.htmlIdData);
                    window.show();
                }
            });
        }

        var renderMouseOverListener = {
            render: function(cnt) {
                cnt.getEl().on({
                    'mouseover': function() {
                        instance.actionsCnt.show();
                    },
                    'mouseout': function() {
                        instance.actionsCnt.hide();
                    }
                });
            }
        };

        this.innerCnt = new Ext.Container({
            cls: this.innerCls,
            items: resourcesArray,
            listeners: renderMouseOverListener
        });

        this.actionsCnt = new Ext.Container({
            xtype: 'container',
            cls: 'actions',
            hidden: true,
            items: buttonsArray,
            listeners: renderMouseOverListener
        });

        this.items = [
            this.innerCnt
        ];

        this.callParent(arguments);

        this.numUpdates = 0;

        if (this.textResourceLookup) {
            this.numUpdates++;
        }
        if (this.htmlResourceLookup) {
            this.numUpdates++;
        }
        if (this.imgResourceLookup) {
            this.numUpdates++;
        }

        if (this.textResourceLookup) {

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/text/by-lookup/' + this.textResourceLookup),
                method: 'GET',
                success: function(response){
                    var jsonData = Ext.decode(response.responseText);
                    if (jsonData.data) {
                        var data = jsonData.data[0];
                        instance.innerTextCnt.update(data.resourceVersion.text);
                        instance.textIdData = {
                            resourceId: data.id,
                            version: data.resourceVersion.version
                        };
                    } else {
                        instance.innerTextCnt.update('Not defined');
                    }
                    instance.finishUpdate();
                },
                failure: null
            });
        }

        if (this.htmlResourceLookup) {

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/html/by-lookup/' + this.htmlResourceLookup),
                method: 'GET',
                success: function(response){
                    var jsonData = Ext.decode(response.responseText);
                    if (jsonData.data) {
                        var data = jsonData.data[0];
                        instance.innerHtmlCnt.update(data.resourceVersion.html);
                        instance.htmlIdData = {
                            resourceId: data.id,
                            version: data.resourceVersion.version
                        };
                    } else {
                        instance.innerHtmlCnt.update('Not defined');
                    }
                    instance.finishUpdate();
                },
                failure: null
            });
        }

        if (this.imgResourceLookup) {

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/image/by-lookup/' + this.imgResourceLookup),
                method: 'GET',
                success: function(response){
                    var jsonData = Ext.decode(response.responseText);
                    if (jsonData.data) {
                        var data = jsonData.data[0];
                        var urlSuffix = 'content/resource/image/by-filename/' + data.id + '/' + data.resourceVersion.storedFilename + '?_dc=' + new Date().getTime();
                        var url = OPF.Cfg.restUrl(urlSuffix);

                        var imageHtml = OPF.core.component.resource.ResourceControl.getImgHtml(url, instance.imageWidth, instance.imageHeight);

                        instance.innerImgCnt.update(imageHtml);
                        instance.finishUpdate();

                        instance.imageIdData = {
                            resourceId: data.id,
                            version: data.resourceVersion.version
                        };
                    }
                },
                failure: null
            });
        }

    },

    finishUpdate: function() {
        this.numUpdates--;
        if (this.numUpdates == 0) {
            this.innerLoadingCmp.update('');
            if (OPF.Cfg.CAN_EDIT_RESOURCE) {
                this.insert(1, this.actionsCnt);
            }
            this.doLayout();
        }
    }
});

OPF.core.component.resource.ResourceControl.getImgHtml = function(url, width, height, title) {
    var imageHtml = '<img src="' + url + '"';
    if (width) {
        imageHtml += ' width="' + width + '"';
    }
    if (height) {
        imageHtml += ' height="' + height + '"';
    }
    if (title) {
        imageHtml += ' title="' + title + '"';
        imageHtml += ' alt="' + title + '"';
    }
    imageHtml += ' />';
    return imageHtml;
};

OPF.core.component.resource.ResourceControl.getImgHtmlWrapper = function(url, orgWidth, orgHeight, title, maxWidth, maxHeight) {
    var widthCoof = 1;
    var heightCoof = 1;
    if (orgWidth > maxWidth) {
        widthCoof = orgWidth / maxWidth;
    }
    if (orgHeight > maxHeight) {
        heightCoof = orgHeight / maxHeight;
    }
    var width;
    var height;
    if (heightCoof > widthCoof) {
        width = orgWidth / heightCoof;
        height = orgHeight / heightCoof;
    } else if (heightCoof < widthCoof) {
        width = orgWidth / widthCoof;
        height = orgHeight / widthCoof;
    } else {
        width = orgWidth;
        height = orgHeight
    }

    var widthPadding = (maxWidth - width) / 2;
    var xPadding = widthPadding > 0 ? widthPadding + 'px' : '0';
    var heightPadding = (maxHeight - height) / 2;
    var yPadding = heightPadding > 0 ? heightPadding + 'px' : '0';

    var html = '<div style="width: ' + maxWidth + 'px; height: ' + maxHeight + 'px; padding: ' + yPadding + ' ' + xPadding + ' ' + yPadding + ' ' + xPadding + ';">';
    html += '<img src="' + url + '"';
    html += ' width="' + width + '"';
    html += ' height="' + height + '"';
    if (title) {
        html += ' title="' + title + '"';
        html += ' alt="' + title + '"';
    }
    html += ' /></div>';
    return html;
};

OPF.core.component.resource.ResourceControl.getStubHtml = function(title, width, height) {
    var imageHtml = '<div style="display: table-cell; border: 1px solid #999999; text-align: center; vertical-align: middle;';
    if (width) {
        imageHtml += 'width: ' + width + 'px;';
    }
    if (height) {
        imageHtml += 'height: ' + height + 'px;';
    }
    imageHtml += '">';
    if (title) {
        imageHtml += title;
    }
    imageHtml += '</div>';
    return imageHtml;
};

