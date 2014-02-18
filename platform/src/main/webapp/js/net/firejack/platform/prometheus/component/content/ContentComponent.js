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

Ext.define('OPF.prometheus.component.content.ContentComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.content-component',

    margin: '5 10 5 0',
    cls: 'content-panel',
    border: true,

    entityLookup: null,
    titleLookup: null,
    contentLookup: null,
    maxTitleLength: null,
    maxContentLength: null,

    additionalComponents: null,

    initComponent: function() {
        var me = this;

        this.titleLookup = this.titleLookup || this.entityLookup + '.panel-title';
        this.contentLookup = this.contentLookup || this.entityLookup + '.panel-text';

        this.titleControl = Ext.ComponentMgr.create({
            xtype: 'text-resource-control',
            textResourceLookup: this.titleLookup,
            cls: 'content-panel-title',
            textInnerCls: 'content-panel-title-text',
            maxTextLength: this.maxTitleLength
        });

        this.contentControl = Ext.ComponentMgr.create({
            xtype: 'text-resource-control',
            textResourceLookup: this.contentLookup,
            cls: 'content-panel-content',
            textInnerCls: 'content-panel-content-text',
            maxTextLength: this.maxContentLength
        });

        this.items = [
            this.titleControl,
            this.contentControl
        ];

        if (Ext.isArray(this.additionalComponents)) {
            Ext.each(this.additionalComponents, function(additionalComponent) {
                me.items.push(additionalComponent);
            });
        }

        this.callParent(arguments);
    }
});