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