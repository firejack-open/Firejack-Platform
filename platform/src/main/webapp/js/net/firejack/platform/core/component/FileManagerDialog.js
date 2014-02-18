/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

Ext.define('OPF.core.component.FileTreePanel', {
    extend: 'Ext.tree.Panel',
    alias : 'widget.opf-filetreepanel',

    layout: 'fit',

    useArrows: true,
    autoScroll: true,
    animate: true,
    collapsible: false,
    split: true,
    rootVisible: false,

    selectedNode: null,

    managerLayout: null,

    fileServiceUrl: null,
    directoryOnly: false,

    constructor: function(managerLayout, directoryOnly, cfg) {
        cfg = cfg || {};
        OPF.core.component.FileTreePanel.superclass.constructor.call(this, Ext.apply({
            managerLayout: managerLayout,
            directoryOnly: directoryOnly
        }, cfg));
    },

    /**
     *
     */
    initComponent: function() {
        var me = this;

        this.store = Ext.create('Ext.data.TreeStore', {
            model: 'OPF.core.model.FileTreeModel',
            proxy: {
                type: 'ajax',
                url: me.fileServiceUrl,
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            root: {
                text: 'root',
                expanded: true,
                path: ''
            },
            folderSort: true,
            sorters: this.SORTER,
            clearOnLoad: false,
            listeners: {
                beforeload: function(store, operation, eOpts) {
                    store.proxy.url = me.fileServiceUrl + '?path=' + operation.node.get('path') + "&directoryOnly=" + me.directoryOnly;
                }
            }
        });

        this.delayedClick = new Ext.util.DelayedTask(function(doubleClick, record) {
            if(doubleClick) {
                this.fireDblClick(record);
            } else {
                this.fireClick(record);
            }
        }, this),

        this.callParent(arguments);
    },

    listeners: {
        itemclick: function(tree, record) {
            this.delayedClick.delay(200, null, this, [false, record]);
        },
        itemdblclick: function(tree, record) {
            this.delayedClick.delay(200, null, this, [true, record]);
        },
        containerclick: function(tree) {
            tree.getSelectionModel().deselect(this.selectedNode);
            this.selectedNode = null;
            this.managerLayout.selectButton.disable();
        }
    },

    fireClick: function(record) {
        this.selectedNode = record;
        this.managerLayout.selectButton.enable();
    },

    fireDblClick: function(record) {
        this.managerLayout.chooseFile(record);
    }

});

Ext.define('OPF.core.component.FileManagerDialog', {
    extend: 'Ext.window.Window',
    alias : 'widget.opf-filemanager-dlg',

    id: 'fileManagerDialog',
    title: 'File Explorer',

    modal: true,
    closable: true,
    closeAction: 'hide',

    layout: 'fit',
    resizable: false,
    width: 300,
    height: 400,

    directoryOnly: null,
    selectFileField: null,

    constructor: function(directoryOnly, cfg) {
        cfg = cfg || {};
        OPF.core.component.FileManagerDialog.superclass.constructor.call(this, Ext.apply({
            directoryOnly: directoryOnly
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.fileTreePanel = Ext.create('OPF.core.component.FileTreePanel', this, this.directoryOnly, {
            fileServiceUrl: OPF.Cfg.restUrl('/registry/filemanager/directory')
        });

        this.selectButton = new Ext.Button({
            text: 'Select',
            disabled: true,
            handler: function() {
                instance.chooseFile(instance.fileTreePanel.selectedNode);
            }
        });

        this.closeButton = new Ext.Button({
            text: 'Cancel',
            handler: function() {
                instance.close();
            }
        });

        this.items = [
            this.fileTreePanel
        ];

        this.fbar = [
            this.selectButton,
            this.closeButton
        ];

        this.callParent(arguments);
    },

    chooseFile: function(node) {
        this.selectFileField.setValue(node.get('path'));
        this.close();
    },

    setSelectFileField: function(field) {
        this.selectFileField = field;
    }

});

OPF.core.component.FileManagerDialog.init = function(field, directoryOnly) {
    var fileManagerDialog = Ext.WindowMgr.get('fileManagerDialog');
    if (isEmpty(fileManagerDialog)) {
        fileManagerDialog = Ext.create('OPF.core.component.FileManagerDialog', directoryOnly);
        Ext.WindowMgr.register(fileManagerDialog);
    }
    fileManagerDialog.setSelectFileField(field);
    return fileManagerDialog;
};