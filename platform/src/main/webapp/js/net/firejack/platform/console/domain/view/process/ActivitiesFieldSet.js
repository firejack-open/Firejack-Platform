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


Ext.define('OPF.console.domain.view.process.ActivityEditorDialog', {
    extend: 'Ext.window.Window',
    alias: 'widget.activity-editor-dlg',

    title: 'Activity Editor',
    modal: true,
    closable: true,
    closeAction: 'hide',

    grid: null,
    rowIndex: null,

    autoHeight: true,
    autoWidth: true,
    layout: 'fit',
    resizable: false,

    constructor: function(id, grid, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.ActivityEditorDialog.superclass.constructor.call(this, Ext.apply({
            id: id,
            grid: grid
        }, cfg));
    },

    initComponent: function(){
        var me = this;

        this.pathField = OPF.Ui.displayField('path', 'Path');
        this.nameField = OPF.Ui.textFormField('name', 'Name', {
            labelAlign: 'top',
            subFieldLabel: '',
            enableKeyEvents: true
        });
        this.lookupField = OPF.Ui.displayField('lookup', 'Lookup');
        this.activityTypeField = OPF.Ui.comboFormField('activityType', 'Type', {
            labelAlign: 'top',
            subFieldLabel: '',
            selectOnFocus: true,
            editable: false,
            typeAhead: true,
            triggerAction: 'all'
        });
        this.descriptionField = OPF.Ui.textFormArea('description', 'Description',{
            labelAlign: 'top',
            subFieldLabel: ''
        });

        this.actorField = OPF.Ui.comboFormField('actor', 'Actor', {
            labelAlign: 'top',
            subFieldLabel: '',
            store: 'ActivityActors',
            queryParam: 'term',
            valueField: 'id',
            displayField:'lookup',
            typeAhead: true,
            //hideTrigger:true,
            mode: 'remote',
            minChars: 2,
            forceSelection: true,
            emptyText:'Select an actor...',
            selectOnFocus: true
        });

        this.statusField = OPF.Ui.comboFormField('status', 'Status', {
            labelAlign: 'top',
            valueField: 'id',
            displayField:'name',
            queryMode: 'local',
            emptyText:'Select a status...',
            listeners: {
                change: function() {
                    me.form.getForm().checkValidity();
                }
            }
        });

        this.notifyField = OPF.Ui.formCheckBox('notify', 'Notify');

        this.form = Ext.create('Ext.form.Panel', {
            flex: 1,
            width: 450,
            height: 420,
            padding: 10,
            border: false,
            headerAsText: false,
            monitorValid: true,
            frame: true,
            layout: 'anchor',
            defaults: {
                bodyStyle: 'padding 5px;'
            },
            fbar: [
                OPF.Ui.createBtn('Save', 50, 'save-activity', {formBind: true}),
                OPF.Ui.createBtn('Reset', 60, 'reset-activity'),
                OPF.Ui.createBtn('Cancel', 60, 'cancel-activity')
            ],
            items: [
                this.pathField,
                this.nameField,
                this.lookupField,
                this.activityTypeField,
                this.actorField,
                this.descriptionField,
                this.statusField,
                this.notifyField
            ]
        });

        this.items = [
            this.form
        ];

        this.callParent(arguments);

        var constraints = new OPF.core.validation.FormInitialisation(OPF.core.utils.RegistryNodeType.ACTIVITY.getConstraintName());
        constraints.initConstraints(this.form, null);
    },

    initStatusField: function() {
        var statusesGridStore = this.grid.editPanel.statusesFieldSet.statusesGrid.store;
        var statusData = [];
        Ext.each(statusesGridStore.data.items, function(item, index) {
            var data = item.data;
            var id = OPF.isNotEmpty(data.id) ? data.id : - data.sortPosition;
            var statusModel = Ext.create('OPF.console.domain.model.StatusModel');
            statusModel.set('id', id);
            statusModel.set('name', data.name);
            statusData.push(statusModel);
        });

        if (this.statusField.getStore() == null) {
            this.statusField.store = Ext.create('Ext.data.ArrayStore', {data: statusData});
        } else {
            this.statusField.store.loadData(statusData);
        }
    },

    fillEditor: function(baseLookup, rowIndex) {
        this.baseLookup = baseLookup;
        this.rowIndex = rowIndex;

        this.initStatusField();

        if (OPF.isNotEmpty(rowIndex)) {
            var model = this.grid.store.getAt(rowIndex);
            if (OPF.isNotEmpty(model)) {

                var activityActorModel = Ext.create('OPF.console.domain.model.ActorModel');
                activityActorModel.set('id', model.get('actorId'));
                activityActorModel.set('name', model.get('actorName'));
                var activityActorsStore = Ext.StoreManager.lookup('ActivityActors');
                activityActorsStore.add(activityActorModel);
                this.pathField.setValue(this.baseLookup);
                this.nameField.setValue(model.get('name'));
                this.descriptionField.setValue(model.get('description'));
                this.actorField.setValue(model.get('actorId'));

                if (this.statusField.getStore().getById(model.get('statusId'))) { // set status field only if the status hasn't been removed
                    this.statusField.setValue(model.get('statusId'));
                } else {
                    this.statusField.setValue('');
                }
                this.activityTypeField.setValue(model.get('activityType'));
                this.notifyField.setValue(model.get('notify'));
                this.updateLookup();
                return;
            }
        }

        this.pathField.setValue(this.baseLookup);
        this.nameField.setValue('');
        this.descriptionField.setValue('');
        this.activityTypeField.setValue('');
        this.actorField.setValue('');
        this.statusField.setValue('');
        this.notifyField.setValue(true);
        this.updateLookup();
    },

    startEditing: function(baseLookup, rowIndex) {
        this.fillEditor(baseLookup, rowIndex);
        this.show();
    },

    save: function() {
        var model;

        var actorId = this.actorField.getValue();
        var actorComboRec = this.actorStore.getById(actorId);
        var actorName = actorComboRec.data.name;
        var statusId = this.statusField.getValue();
        var statusName;

        var statusStore = this.grid.editPanel.statusesFieldSet.statusesGrid.store;

        if (typeof statusId == 'number') { // selection of one of the statuses made
            var statusGridRec;
            if (statusId > 0) {
                statusGridRec = statusStore.getById(statusId);
                statusName = statusGridRec.data.name;
            } else {
                var statusRecIndex = statusStore.findExact('sortPosition', -statusId);
                statusGridRec = statusStore.getAt(statusRecIndex);
                statusName = statusGridRec.data.name;
            }
        } else { // new status to be created with the typed in name
            var nextSortPosition = statusStore.getCount() + 1; // add record at the end

            var statusModel = Ext.create('OPF.console.domain.model.StatusModel');
            statusModel.set('name', statusId); // if user typed in text instead of selecting predefined value, this is that text
            statusModel.set('path', this.pathField.getValue()); // same path as activity's path
            statusModel.set('lookup', calculateLookup(this.pathField.getValue(), statusId));
            statusModel.set('description', '');
            statusModel.set('sortPosition', nextSortPosition);
            statusStore.add(statusModel);

            statusName = statusId; // if user typed in text instead of selecting predefined value, this is that text
            statusId = - nextSortPosition; // id is calculated as negative order for new status records
        }

        model = OPF.isEmpty(this.rowIndex) ?
            Ext.create('OPF.console.domain.model.ActivityModel') :
            this.grid.store.getAt(this.rowIndex);
        model.set('path', this.pathField.getValue());
        model.set('name', this.nameField.getValue());
        model.set('lookup', this.lookupField.getValue());
        model.set('description', this.descriptionField.getValue());
        model.set('actorId', actorId);
        model.set('actorName', actorName);
        model.set('statusId', statusId);
        model.set('statusName', statusName);
        model.set('activityType', this.activityTypeField.getValue());
        model.set('notify', this.notifyField.getValue());
        this.cancel();
        if (OPF.isEmpty(this.rowIndex)) {
            this.grid.store.add(model);
        }
        this.grid.refreshOrderPositions();
    },

    setGrid: function(grid) {
        this.grid = grid;
    },

    cancel: function() {
        this.hide();
    },

    reset: function() {
        this.fillEditor(this.baseLookup, this.rowIndex);
    },

    updateLookup: function() {
        var lookup = calculateLookup(this.pathField.getValue(), this.nameField.getValue());
        this.lookupField.setValue(lookup);
    }
});

Ext.define('OPF.console.domain.view.process.ActivitiesGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.activities-grid',
    height: 240,
    cls: 'border-radius-grid-body border-radius-grid-docked-top',
    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.ActivitiesGrid.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    plugins: [
        new OPF.core.component.GridDragDropRowOrder({
            listeners: {
                afterrowmove: function(gridDropTarget) {
                    gridDropTarget.grid.refreshOrderPositions();
                }
            }
        })
    ],

    store: 'Activities',

    columns: [
        {dataIndex: 'name', text: 'Name', width: 150},
        {dataIndex: 'description', text: 'Description', width: 150},
        {dataIndex: 'actorName', text: 'Actor', width: 75},
        {dataIndex: 'statusName', text: 'Status', width: 75},
        {xtype: 'booleancolumn', dataIndex: 'notify', text: 'Notify', width: 60}
    ],

    initComponent: function() {
        this.tbar = [
            OPF.Ui.createBtn('Add', 45, 'add-activity', {iconCls: 'silk-add'}),
            '-',
            OPF.Ui.createBtn('Delete', 60, 'delete-activity', {iconCls: 'silk-delete'})
        ];

        this.callParent(arguments);
    },

    getEditorDialog: function() {
        var winId = 'activitiesRowEditorDialog1987';
        var editorDialog = Ext.WindowMgr.get(winId);
        if (OPF.isEmpty(editorDialog)) {
            editorDialog = Ext.create('OPF.console.domain.view.process.ActivityEditorDialog', winId, this);
        } else {
            editorDialog.setGrid(this);
        }
        return editorDialog;
    },

    cleanFieldStore: function() {
        this.store.removeAll();
    },

    refreshOrderPositions: function() {
        var models = this.store.getRange();
        Ext.each(models, function(model, index) {
            model.set('sortPosition', index + 1);
        });
    },

    // add dropping possibility
    onRender: function(ct, position) {

        this.callParent(arguments);

        var instance = this;
        //var zone = this.getView().scroller;
        //var zone = this.getView().getEl();
        //var zone = this.view.el;
        var zone = this.getEl();
        var sourcePanelDropZone = new Ext.dd.DropZone(zone, {

            ddGroup: 'cloudNavigatorDDGroup',

            getTargetFromEvent: function(e) {
                return e.getTarget(instance.getView().rowSelector);
            },

            onNodeOver : function(target, dd, e, data){
                var sourceType = OPF.core.utils.RegistryNodeType.findRegistryNodeById(data.node);
                if (OPF.core.utils.RegistryNodeType.ACTOR == sourceType) {
                    return Ext.dd.DropZone.prototype.dropAllowed;
                }
            },

            onNodeDrop : function(target, dd, e, data){
                var sourceType = OPF.core.utils.RegistryNodeType.findRegistryNodeById(data.node);
                if (OPF.core.utils.RegistryNodeType.ACTOR == sourceType) {
                    var rowIndex = instance.getView().findRowIndex(target);
                    var model = instance.getStore().getAt(rowIndex);
                    model.set('actorName', data.node.text);
                    model.set('actorId', SqGetIdFromTreeEntityId(data.node.id));
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

});

Ext.define('OPF.console.domain.view.process.ActivitiesStrategyFieldSet', {
    extend: 'OPF.core.component.LabelContainer',
    fieldLabel: 'Strategy',
    subFieldLabel: '',
    supportMultiActivities: false,
    layout: 'anchor',

    initComponent: function() {
        var me = this;
        this.supportMultiBranchStrategyCheckBox = OPF.Ui.formCheckBox('supportMultiActivities', 'Support multi-branch strategy', {
            labelAlign: 'right',
            labelWidth: 200,
            subFieldLabel: '',
            listeners: {
                change:  function(checkbox, newValue) {
                    me.supportMultiActivities = newValue;
                }
            }
        });
        this.items = this.supportMultiBranchStrategyCheckBox;
        this.callParent(arguments);
    }
});

Ext.define('OPF.console.domain.view.process.ActivitiesFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'Activities',
    subFieldLabel: '',

    layout: 'anchor',

    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.ActivitiesFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {

        this.activitiesGrid = Ext.create('OPF.console.domain.view.process.ActivitiesGrid', this.editPanel);

        this.items = [
            this.activitiesGrid
        ];

        this.callParent(arguments);
    }
});