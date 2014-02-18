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

Ext.define('OPF.Ui', {});

OPF.Ui.createBtn = function(text, width, action, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.button.Button', Ext.apply({
        text: text,
        width: width,
        action: action
    }, cfg))
};

OPF.Ui.createMenu = function(text, action, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.menu.Item', Ext.apply({
        cls: 'main-controls-menu',
        text: text,
        action: action
    }, cfg))
};

OPF.Ui.createToggleButton = function(title, toggleGroup, action, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.button.Button', Ext.apply({
        text: title,
        enableToggle: true,
        toggleGroup: toggleGroup,
        action: action
    }, cfg));
};

OPF.Ui.xSpacer = function(width, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.toolbar.Spacer', Ext.apply({width: width}, cfg))
};

OPF.Ui.ySpacer = function(height, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.toolbar.Spacer', Ext.apply({height: height}, cfg))
};

OPF.Ui.spacer = function(width, height, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.toolbar.Spacer', Ext.apply({width: width, height: height}, cfg))
};

OPF.Ui.populateColumn = function(dataIndex, header, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Column', Ext.apply({
        dataIndex: dataIndex,
        text: header,
        sortable: true
    }, cfg));
};

OPF.Ui.populateHiddenColumn = function(dataIndex, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Column', Ext.apply({
        dataIndex: dataIndex,
        hidden: true
    }, cfg));
};

OPF.Ui.populateNumberColumn = function(dataIndex, header, width, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Number', Ext.apply({
        dataIndex: dataIndex,
        text: header,
        sortable: true,
        width: width
    }, cfg));
};

OPF.Ui.populateBooleanColumn = function(dataIndex, header, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Boolean', Ext.apply({
        dataIndex: dataIndex,
        text: header,
        sortable: true
    }, cfg));
};

OPF.Ui.populateCheckBoxColumn = function(dataIndex, header, width, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.ux.CheckColumn', Ext.apply({
        text: header,
        dataIndex: dataIndex,
        width: width
    }, cfg));
};

OPF.Ui.populateDateColumn = function(dataIndex, header, width, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Date', Ext.apply({
        dataIndex: dataIndex,
        text: header,
        sortable: true,
        width: width,
        format: 'M j, Y g:i A'
    }, cfg));
};

OPF.Ui.populateIconColumn16 = function(width, image, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Column', Ext.apply({
        text: '!',
        sortable: true,
        align: 'center',
        width: width,
        renderer: function() {
            return '<img src="' + OPF.Ui.icon16(image) + '">';
        }
    }, cfg));
};

OPF.Ui.populateActionColumn = function(header, width, items, cfg) {
    cfg = cfg || {};
    items = Ext.isArray(items) ? items : [];
    return Ext.create('OPF.core.component.ActionColumn', Ext.apply({
        text: header,
        sortable: false,
        width: width,
        items: items
    }, cfg));
};

OPF.Ui.createActionColumnBtn = function(iconPath, tooltip, handler, cfg) {
    cfg = cfg || {};
    return Ext.apply({
        icon   : OPF.Cfg.OPF_CONSOLE_URL + iconPath,
        tooltip: tooltip,
        handler: handler,
        visible: true
    }, cfg);
};

OPF.Ui.createActionColumnBtn16 = function(icon, tooltip, handler, cfg) {
    cfg = cfg || {};
    return Ext.apply({
        icon   : OPF.Ui.icon16(icon),
        tooltip: tooltip,
        handler: handler,
        visible: true
    }, cfg);
};

OPF.Ui.createGridBtn = function(value, id, record, icon, cfg) {
    return new Ext.Button(
        Ext.apply({
            id: 'btn-' + id,
            renderTo: id,
            text: value,
            icon: OPF.Ui.icon16(icon),
            cls: 'grid-btn'
        }, cfg)
    );
};

OPF.Ui.textFormField = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.ComponentManager.create(Ext.apply({
        xtype: 'opf-textfield',
        name: name,
        anchor: '100%',
        fieldLabel: label
    }, cfg));
};

OPF.Ui.comboFormField = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.ComponentManager.create(Ext.apply({
        xtype: 'opf-combo',
        name: name,
        fieldLabel: label,
        anchor: '100%'
    }, cfg));
};

OPF.Ui.textFormArea = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.ComponentManager.create(Ext.apply({
        xtype: 'opf-textarea',
        name: name,
        anchor: '100%',
        fieldLabel: label
    }, cfg));
};

OPF.Ui.formCheckBox = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.ComponentManager.create(Ext.apply({
        xtype: 'opf-checkbox',
        name: name,
        fieldLabel: label,
        anchor: '100%'
    }, cfg));
};

OPF.Ui.displayField = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.create('OPF.core.component.Display', Ext.apply({
        name: name,
        fieldLabel: label,
        anchor: '100%'
    }, cfg));
};

OPF.Ui.formDate = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.form.field.Date', Ext.apply({
        name: name,
        fieldLabel: label,
        anchor: '100%'
    }, cfg));
};

OPF.Ui.hiddenField = function(name, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.form.field.Hidden', Ext.apply({
        name: name
    }, cfg));
};

OPF.Ui.getCmp = function(cmpQuery) {
    var cmpArray = Ext.ComponentQuery.query(cmpQuery);
    return cmpArray == null || !Ext.isArray(cmpArray) ||
        cmpArray.length == 0 ? null : cmpArray[0];
};

OPF.Ui.icon128 = function(icon) {
    return OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/128/' + icon;
};

OPF.Ui.icon16 = function(icon) {
    return OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/16/' + icon;
};