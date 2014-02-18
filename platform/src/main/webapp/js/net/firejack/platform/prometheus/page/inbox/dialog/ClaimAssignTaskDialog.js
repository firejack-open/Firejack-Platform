Ext.define('OPF.prometheus.page.inbox.dialog.ClaimAssignTaskDialog', {
    extend: 'Ext.window.Window',
    alias: 'widget.prometheus.inbox.claim-assign-task-dialog',
    ui: 'wizards',

    statics: {
        id: 'claimAssignTaskDialog'
    },

    id: 'claimAssignTaskDialog',
    title: 'PROCESS NAME',

    width: 600,
    modal: true,
    draggable:false,
    shadow:false,
    resizable: false,

    items: [
        {
            xtype: 'form',
            layout: 'anchor',
            border: false,
            bodyPadding: 10,
            autoScroll: true,
            items: [
                {
                    xtype: 'notice-container',
                    border: true,
                    delay: 200
                },
                {
                    xtype: 'container',
                    anchor: '100%',
                    layout: 'hbox',
                    items: [
                        {
                            xtype: 'opf-combo',
                            name: 'assigneeId',
                            labelAlign: 'top',
                            fieldLabel: 'Next Assignee',
                            flex: 1,
                            editable: false,
                            store: 'AssigneeStore',
                            queryMode: 'remote',
                            displayField: 'username',
                            valueField: 'id',
                            allowBlank: false
                        },
                        {
                            xtype: 'splitter'
                        },
                        {
                            xtype: 'button',
                            text: 'Claim',
                            ui: 'blue',
                            width: 100,
                            height: 28,
                            margin: '21 0 0 0',
                            action: 'claim'
                        }
                    ]
                },
//                {
//                    xtype: 'opf-combo',
//                    name: 'explanationId',
//                    labelAlign: 'top',
//                    fieldLabel: 'Explanation',
//                    anchor: '100%',
//                    editable: false,
//                    store: 'ExplanationStore',
//                    queryMode: 'remote',
//                    displayField: 'shortDescription',
//                    valueField: 'id',
//                    allowBlank: false,
//                    listConfig: {
//                        getInnerTpl: function() {
//                            var tpl = '<div class="x-combo-list-item">';
//                            tpl += '<div class="enum-item"><h3>{shortDescription}</h3>{longDescription}</div>';
//                            tpl += '</div>';
//                            return tpl;
//                        }
//                    }
//                },
                {
                    xtype: 'opf-textarea',
                    name: 'noteText',
                    labelAlign: 'top',
                    fieldLabel: 'Comment',
                    anchor: '100%'
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        {
                            xtype: 'button',
                            ui: 'blue',
                            width: 200,
                            height: 50,
                            text: 'Submit',
                            formBind: true,
                            action: 'submit'
                        },
                        {
                            xtype: 'button',
                            ui: 'grey',
                            width: 200,
                            height: 50,
                            text: 'Cancel',
                            action: 'cancel'
                        }
                    ]
                }
            ]
        }
    ],

    setTask: function(task) {
        this.task = task;
    }

});