Ext.require([

]);

Ext.define('OPF.prometheus.page.inbox.controller.InboxController', {
    extend: 'Ext.app.Controller',

    views: [
        'OPF.prometheus.page.inbox.view.InboxView',
        'OPF.prometheus.page.inbox.dialog.ClaimAssignTaskDialog'
    ],

    models: [
        'OPF.console.domain.model.ProcessModel',
        'OPF.console.domain.model.ActivityModel',
        'OPF.console.domain.model.ActivityActionModel',
        'OPF.console.domain.model.CaseExplanationModel',
        'OPF.console.domain.model.ActorModel',
        'OPF.console.domain.model.StatusModel',
        'OPF.console.domain.model.CaseObjectModel',
        'OPF.console.inbox.model.TaskModel',
        'OPF.console.directory.model.UserModel'
    ],

    refs: [
        {
            ref: 'advancedSearchComponent',
            selector: 'component[xtype=prometheus.component.advanced-search-component]'
        },
        {
            ref: 'claimAssignTaskDialog',
            selector: 'component[xtype=prometheus.inbox.claim-assign-task-dialog]'
        },
        {
            ref: 'assigneeCombo',
            selector: 'component[xtype=prometheus.inbox.claim-assign-task-dialog] opf-combo[name=assigneeId]'
        },
        {
            ref: 'claimAssignTaskForm',
            selector: 'component[xtype=prometheus.inbox.claim-assign-task-dialog] form'
        },
        {
            ref: 'messagePanel',
            selector: 'component[xtype=prometheus.inbox.claim-assign-task-dialog] form notice-container'
        }
    ],

    init: function () {
        var me = this;

        this.control({
            'component[xtype=prometheus.component.inbox-menu-component]': {
                'menuitemclick': function (component, options) {
                    me.onClickTaskMenuItem(component, options);
                }
            },
            'component[xtype=prometheus.component.task-dataview-component] dataview': {
                'refresh': function (dataview) {
                    me.onRefreshDataView(dataview);
                }
            },
            'component[xtype=prometheus.inbox.claim-assign-task-dialog] button[action=claim]': {
                'click': function () {
                    me.onClickClaimOnClaimAssignTaskDialog();
                }
            },
            'component[xtype=prometheus.inbox.claim-assign-task-dialog] button[action=submit]': {
                'click': function () {
                    me.onClickSubmitOnClaimAssignTaskDialog();
                }
            },
            'component[xtype=prometheus.inbox.claim-assign-task-dialog] button[action=cancel]': {
                'click': function () {
                    me.onClickCancelOnClaimAssignTaskDialog();
                }
            }
        });

//        this.initModels();
        this.initStores();
    },

    initModels: function() {
        var me = this;

        this.models = new Ext.util.MixedCollection();
        for (var className in Ext.ClassManager.maps.nameToAliases) {
            var clazz = Ext.ClassManager.get(className);
            if (clazz.superclass && clazz.superclass.$className == 'Ext.data.Model') {
                me.models.add(clazz.lookup, clazz);
            }
        }
    },

    initStores: function () {
        var me = this;

        Ext.create('Ext.data.Store', {
            storeId: 'TaskStore',
            autoLoad: true,
            pageSize: 10,
            model: 'OPF.console.inbox.model.TaskModel',
            remoteSort: true,
            proxy: {
                type: 'rest',
                reader: {
                    root: 'data',
                    totalProperty: 'total',
                    messageProperty: 'message',
                    idProperty: 'id'
                },
                simpleSortMode: true,
                startParam: 'offset',
                limitParam: 'limit',
                sortParam: 'sortColumn',
                directionParam: 'sortDirection'
            },
            listeners: {
                beforeload: function (store, operation) {
                    var queryParameters = me.getAdvancedSearchComponent().getQueryParameters();
                    store.proxy.url = OPF.Cfg.restUrl(store.model.restSuffixUrl + '/advanced-search?queryParameters=' + Ext.encode(queryParameters) +
                        '&type=' + (me.advancedSearchType ? me.advancedSearchType : 'ALL'), false);
                }
            }
        });

        Ext.create('Ext.data.Store', {
            storeId: 'AssigneeStore',
            model: 'OPF.console.directory.model.UserModel',
            proxy: {
                type: 'ajax',
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            listeners: {
                beforeload: function (store, operation) {
                    var dialog = me.getClaimAssignTaskDialog();
                    var activityId = OPF.ModelHelper.getProperty(dialog.task, 'activity').id;
                    store.proxy.url = OPF.Cfg.restUrl('/process/activity/assignee-candidates/' + activityId, false);
                }
            }
        });

//        Ext.create('Ext.data.Store', {
//            storeId: 'ExplanationStore',
//            model: 'OPF.console.domain.model.CaseExplanationModel',
//            proxy: {
//                type: 'ajax',
//                reader: {
//                    type: 'json',
//                    root: 'data'
//                }
//            },
//            listeners: {
//                beforeload: function (store, operation) {
//                    var dialog = me.getClaimAssignTaskDialog();
//                    var processCase = OPF.ModelHelper.getProperty(dialog.task, 'processCase');
//                    store.proxy.url = OPF.Cfg.restUrl('/process/explanation/search?processId=' + processCase.process.id, false);
//                }
//            }
//        });
    },

    onClickTaskMenuItem: function (component, options) {
        this.advancedSearchType = options.type;
        this.getAdvancedSearchComponent().resetSearch();
        Ext.each(options.htmlEls, function (htmlEl) {
            var el = Ext.get(htmlEl);
            el.removeCls('active');
        });
        Ext.get(options.htmlEl).addCls('active');
    },

    onRefreshDataView: function (dataview) {
        var me = this;

//        var delayedTask = new Ext.util.DelayedTask(function () {
            var tasks = dataview.getStore().getRange();
            Ext.each(tasks, function (task) {
                var taskId = task.get('id');
                var activity = OPF.ModelHelper.getProperty(task, 'activity');
                var caseObject = OPF.ModelHelper.getProperty(task, 'caseObject');

                var activityActionMenuItems = [];
                if (caseObject) {
                    activityActionMenuItems.push({
                        text: 'View',
                        data: {
                            task: task
                        },
                        handler: function (btn) {
                            me.viewCaseObject(btn.data);
                        }
                    });
                    Ext.each(activity.activityActions, function (activityAction) {
                        activityActionMenuItems.push({
                            text: activityAction.name,
                            data: {
                                task: task,
                                activityAction: activityAction
                            },
                            handler: function (btn) {
                                me.viewCaseObject(btn.data);
                            }
                        });
                    });
                }
                activityActionMenuItems.push({
                    text: 'Claim / Assign',
                    data: {
                        task: task
                    },
                    handler: function (btn) {
                        me.showClaimAssignTaskDialog(btn.data);
                    }
                });

                Ext.create('Ext.button.Button', {
                    text: 'ACTIONS',
                    cls: 'activity-action-btn',
                    renderTo: 'task_' + taskId + '_activity_' + activity.id,
                    menu: {
                        xtype: 'menu',
                        baseCls: 'activity-action-menu',
                        items: activityActionMenuItems
                    }
                });
            });
//        });
//        delayedTask.delay(200);
    },

    showClaimAssignTaskDialog: function (data) {
        var task = data.task;
        var dialog = Ext.WindowMgr.get(OPF.prometheus.page.inbox.dialog.ClaimAssignTaskDialog.id);
        if (!dialog) {
            dialog = Ext.ComponentMgr.create({
                xtype: 'prometheus.inbox.claim-assign-task-dialog'
            });
            Ext.WindowMgr.register(dialog);
        }
        var processCase = OPF.ModelHelper.getProperty(task, 'processCase');
        dialog.setTitle('PROCESS NAME: ' + processCase.process.name);
        dialog.setTask(task);
        dialog.show();

        Ext.StoreManager.get('AssigneeStore').load();
//        Ext.StoreManager.get('ExplanationStore').load();
    },

    viewCaseObject: function(data) {
        var task = data.task;
        var activityAction = data.activityAction;
        var caseObject = OPF.ModelHelper.getProperty(task, 'caseObject');
        var processCase = OPF.ModelHelper.getProperty(task, 'processCase');
        var process = processCase.process;

        var path;
        if (process && /*process.processType == 'CREATABLE' &&*/ activityAction) {
            path = process.lookup.replace(OPF.Cfg.PACKAGE_LOOKUP, '');
        } else {
            var entityLookup = caseObject.get('entityType');
            path = entityLookup.replace(OPF.Cfg.PACKAGE_LOOKUP, '');
        }
        path = path.replace(/\./g, '/');
        var editorUrl = OPF.Cfg.fullUrl(path, true);

        var entityId = caseObject.get('entityId');
        editorUrl = OPF.Cfg.addParameterToURL(editorUrl, 'entityId', entityId);
        if (activityAction) {
            editorUrl = OPF.Cfg.addParameterToURL(editorUrl, 'activityActionId', activityAction.id);
        }
        document.location.href = editorUrl;
    },

    onClickClaimOnClaimAssignTaskDialog: function () {
        this.getAssigneeCombo().setValue(OPF.Cfg.USER_INFO.id);
    },

    onClickSubmitOnClaimAssignTaskDialog: function () {
        var me = this;

        var form = this.getClaimAssignTaskForm();
        var basicForm = form.getForm();
        if (basicForm.isValid()) {
            form.getEl().mask();

            var dialog = me.getClaimAssignTaskDialog();

            var formData = basicForm.getValues();
            formData.taskId = dialog.task.get('id');

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('/process/task/assign', false),
                method: 'POST',
                jsonData: {"data": formData},

                success: function (response, action) {
                    var resp = Ext.decode(response.responseText);
                    form.getEl().unmask();
                    if (resp.success) {
                        OPF.Msg.setAlert(true, resp.message);
                        me.getClaimAssignTaskDialog().close();
                    } else {
                        OPF.Msg.setAlert(true, resp.message);
                        me.getMessagePanel().cleanActiveErrors();
                        var error = [
                            {
                                level: OPF.core.validation.MessageLevel.ERROR,
                                msg: resp.message
                            }
                        ];
                        me.getMessagePanel().setNoticeContainer(error);
                    }
                },

                failure: function (response) {
                    form.getEl().unmask();
                    OPF.Msg.setAlert(false, response.message);
                    me.getMessagePanel().cleanActiveErrors();
                    var error = [
                        {
                            level: OPF.core.validation.MessageLevel.ERROR,
                            msg: response.message
                        }
                    ];
                    me.getMessagePanel().setNoticeContainer(error);
                }
            });
        }
    },

    onClickCancelOnClaimAssignTaskDialog: function () {
        this.getClaimAssignTaskDialog().close();
    }

});