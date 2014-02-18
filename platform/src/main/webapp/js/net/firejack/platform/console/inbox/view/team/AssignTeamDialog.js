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