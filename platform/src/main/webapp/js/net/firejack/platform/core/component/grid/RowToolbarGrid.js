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

Ext.define('OPF.core.component.grid.RowToolbarGridPlugin', {
    extend: 'Ext.AbstractPlugin',
    alias: 'widget.opf-row-toolbar-grid-plugin',

    buttons: [],

    showBtnWidth: 39,
    showBtnHeight: 60,

    init: function(grid) {
        var me = this;

        grid.on('itemmouseenter', this.gridItemMouseEnter);
        grid.on('itemmouseleave', this.gridItemMouseLeave);

        grid.buttons = this.buttons;
        Ext.each(this.buttons, function(button) {
            grid[button.name + 'Fn'] = me[button.name + 'Fn'];
        });
        grid.showFn = this.showFn;
        grid.showBtnWidth = this.showBtnWidth;
        grid.showBtnHeight = this.showBtnHeight;
        grid.scope = this.scope;
    },

    gridItemMouseEnter: function(view, record, item, index, e) {
        var me = this;

        view.rowElement = Ext.get(item);
        view.rowElement.setStyle('position', 'relative');
        view.currentRecord = record;

        if (OPF.isEmpty(view.rowCmp)) {
            view.buttons = view.buttons || view.ownerCt.buttons;
            Ext.each(view.buttons, function(button) {
                view[button.name + 'RecordButton'] = Ext.create('Ext.button.Button', {
                    tooltip: button.tooltip || Ext.String.capitalize(button.name),
                    ui: 'action',
                    width: button.width || 26,
                    height: button.height || 26,
                    iconCls: button.iconCls || (button.name + '-btn'),
                    hidden: true
                });
            });

            view.showRowToolbarButton = Ext.create('Ext.button.Button', {
                text: '>>',
                width: this.showBtnWidth,
                height: this.showBtnHeight,
                handler: this.showFn,
                scope: view,
                hideMode: 'visibility'
            });

            var buttons = [
                view.showRowToolbarButton
            ];
            Ext.each(view.buttons, function(button) {
                buttons.push(view[button.name + 'RecordButton']);
            });

            view.rowCmp = Ext.create('Ext.container.Container', {
                cls: 'row-action-toolbar',
                renderTo: view.rowElement,
                items: buttons
            });
        } else {
            view.rowCmp.getEl().appendTo(view.rowElement);
        }
        var rowSize = view.rowElement.getSize();
        view.rowCmp.show();
        view.rowCmp.setHeight(rowSize.height);
        view.rowCmp.setPosition(0, rowSize.height * index);

        Ext.each(view.buttons, function(button) {
            view[button.name + 'RecordButton'].on('click', me[button.name + 'Fn'], me.scope, [view, record, index]);
        });
    },

    gridItemMouseLeave: function(view, record, item, index, e) {
        var me = this;
        if (view.rowCmp) {
            view.rowCmp.setWidth(view.showRowToolbarButton.getWidth());
            view.rowCmp.hide();
            view.showRowToolbarButton.show();

            Ext.each(view.buttons, function(button) {
                view[button.name + 'RecordButton'].hide();
                view[button.name + 'RecordButton'].un('click', me[button.name + 'Fn'], me.scope);
            });
        }
    },

    showFn: function() {
        var me = this;

        var rowSize = this.rowElement.getSize();
        this.rowCmp.setWidth(rowSize.width);
        this.showRowToolbarButton.hide();

        Ext.each(this.buttons, function(button) {
            me[button.name + 'RecordButton'].show();
        });
    }

});