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

/**
 *
 */
Ext.define('OPF.console.inbox.view.team.AssignTeamDialog', {
    extend: 'Ext.window.Window',

    alias: 'widget.assign-team-dlg',

    title: 'Assign Team',
    modal: true,
    closable: true,
    closeAction: 'hide',

    width: 400,

    caseId: null,

    constructor: function(id, caseId, processId, cfg) {
        cfg = cfg || {};
        OPF.console.inbox.view.team.AssignTeamDialog.superclass.constructor.call(this, Ext.apply({
            id: id,
            caseId: caseId,
            processId: processId
        }, cfg));
    },

    createFields: function(actorModels) {
        this.actorUserFields = new Array();

        for(var i = 0; i < actorModels.length; i++) {
            var actorModel = actorModels[i];

            var userStore = Ext.create('OPF.console.inbox.store.AssigneeTeam', actorModel.id);
            userStore.load();

            var actorUserField = OPF.Ui.comboFormField('userId', actorModel.name, {
                store: userStore,
                queryParam: 'term',
                valueField: 'id',
                displayField:'username',
                typeAhead: true,
                triggerAction: 'all',
                lazyRender:true,
                editable: false,
                //hideTrigger:true,
                mode: 'remote',
                minChars: 2,
                forceSelection: true,
                emptyText: 'Select a user...',
                selectOnFocus: true,
                actorId: actorModel.id,
                id: 'actorUserField' + this.id + 'actor' + actorModel.id
            });

            this.actorUserFields.push(actorUserField);
        }

        this.form.add(this.actorUserFields);
        this.form.doLayout();
    },

    initComponent: function(){
        var instance = this;

        this.assignTeamButton = OPF.Ui.createBtn('Assign', 55, 'assign-team', {formBind : true});
        this.cancelButton = OPF.Ui.createBtn('Cancel', 55, 'cancel');

        this.form = Ext.create('Ext.form.Panel', {
            flex: 1,
            padding: 10,
            border: false,
            headerAsText: false,
            monitorValid: true,
            frame: true,
            fbar: [
                this.assignTeamButton,
                this.cancelButton
            ],
            items: [
            ]
        });

        this.items = [
            this.form
        ];

        Ext.Ajax.request({
            url: OPF.core.utils.RegistryNodeType.ACTOR.generateUrl('/search/?processId=' + this.processId),
            method: 'GET',

            success: function(response) {
                var resp = Ext.decode(response.responseText);
                instance.createFields(resp.data);
            }
        });

        this.callParent(arguments);
    }
    
});