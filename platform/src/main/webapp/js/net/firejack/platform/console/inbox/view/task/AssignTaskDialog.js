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


Ext.define('OPF.console.inbox.task.AssignTaskDialog', {
    extend: 'Ext.window.Window',
    alias: 'widget.assign-task-dlg',

    title: 'Assign Task',
    modal: true,
    closable: true,
    closeAction: 'hide',

    grid: null,

    width: 400,

    constructor: function(id, grid, cfg) {
        cfg = cfg || {};
        OPF.console.inbox.task.AssignTaskDialog.superclass.constructor.call(this, Ext.apply({
            id: id,
            grid: grid
        }, cfg));
    },

    initComponent: function() {
        if (OPF.isEmpty(this.userField)) { // can be already set in subclass
            this.userField = OPF.Ui.comboFormField('assigneeId', 'Assignee', {
                store: 'Assignees',
                autoSelect: true,
                valueField: 'id',
                displayField:'username',
                editable: false,
                emptyText:'Select a user...',
                queryMode: 'local',
                hiddenName: 'assigneeId'
            });
        }

        this.explanationField = OPF.Ui.comboFormField('explanationId', 'Explanation', {
            hidden: true, // show only if there are records in the store
            store: Ext.create('OPF.console.inbox.store.Explanations', this),
            valueField: 'id',
            displayField:'shortDescription',
            editable: false,
            queryMode: 'local',
            emptyText:'Select an explanation...',
            hiddenName: 'explanationId'
        });

        this.noteTextField = OPF.Ui.textFormArea('noteText', 'Comment');

        this.assignTaskButton = OPF.Ui.createBtn('Assign Task', 70, 'assign-task', {formBind : true});
        this.cancelButton = OPF.Ui.createBtn('Cancel', 55, 'cancel');

        this.form = Ext.create('Ext.form.Panel', {
            flex: 1,
            padding: 10,
            border: false,
            headerAsText: false,
            monitorValid: true,
            frame: true,
            fbar: [
                this.assignTaskButton,
                this.cancelButton
            ],
            items: [
                this.userField,
                this.explanationField,
                this.noteTextField
            ]
        });

        this.items = [
            this.form
        ];

        this.callParent(arguments);
    },

    setRecord: function(model) {
        this.taskId = model.get('id');

        if (OPF.isNotEmpty(this.userField.store)) {
            this.userField.store.reloadStore(this);
        }
        this.userField.reset();
        this.noteTextField.reset();
        this.processId = model.get('processCase').process.id;
        this.explanationField.store.reloadStore();
    }

});