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

Ext.define('OPF.console.inbox.store.CaseTaskJsonReader', {
    extend: 'Ext.data.JsonReader',

    taskGridPanel: null,

    constructor: function(taskGridPanel, cfg) {
        cfg = cfg || {};
        OPF.console.inbox.store.CaseTaskJsonReader.superclass.constructor.call(this, Ext.apply({
            id: taskGridPanel.isTaskGrid? 'taskStore' : 'caseStore',
            taskGridPanel: taskGridPanel,
            successProperty: 'success',
            idProperty: 'id',
            root: 'data',
            messageProperty: 'message',
            totalProperty: 'total'
        }, cfg));
    },

    getCustomFields: function(firstRec) {
        return this.taskGridPanel.isTaskGrid ? firstRec['processCase'].customFields : firstRec.customFields;
    },

    getCustomFieldMapping: function(index) {
        return this.taskGridPanel.isTaskGrid ? 'processCase.customFields[' + index + '].value' : 'customFields[' + index + '].value';
    },

    // before reading the records we need to dynamically set dynamic column info (metadata), based on the custom fields received from the server
    readRecords : function(serviceResponse) {

        serviceResponse.metaData = {
            totalProperty: 'total',
            successProperty: 'success',
            idProperty: 'id',
            root: 'data',
            messageProperty: 'message'
        };

        serviceResponse.metaData.fields = [];
        serviceResponse.metaData.columnsData = [];
        var fixedFields = this.taskGridPanel.isTaskGrid ?
            OPF.console.inbox.model.TaskModel.configuredFields :
            OPF.console.inbox.model.CaseModel.configuredFields;
        var i;
        for (i = 0; i < fixedFields.length; i++) {
            serviceResponse.metaData.fields.push(fixedFields[i]);
        }
        for (i = 0; i < this.taskGridPanel.getDefaultColumnsData().length; i++) {
            serviceResponse.metaData.columnsData.push(this.taskGridPanel.getDefaultColumnsData()[i]);
        }
        // dynamic columns are formatted based on the first record (if any)
        if (OPF.isNotEmpty(serviceResponse.data) && OPF.isNotEmpty(serviceResponse.data[0])) {
            var customFields = this.getCustomFields(serviceResponse.data[0]); // ['case'].customFields
            if (Ext.isArray(customFields) && customFields.length > 0) {
                for (i = 0; i < customFields.length; i++) {
                    var fieldType = 'string';
                    var columnType = 'gridcolumn';
                    var columnAlign = 'left';
                    var columnFormat = null;
                    var trueText = null;
                    var falseText = null;

                    if (customFields[i].valueType == FIELD_TYPE_INTEGER) {
                        fieldType = 'number';
                        columnType = 'numbercolumn';
                        columnAlign = 'right';
                        columnFormat = customFields[i].format || '0,000';
                    } else if (customFields[i].valueType == FIELD_TYPE_LONG) {
                        fieldType = 'number';
                        columnType = 'numbercolumn';
                        columnAlign = 'right';
                        columnFormat = customFields[i].format || '0,000';
                    } else if (customFields[i].valueType == FIELD_TYPE_DATE) {
                        fieldType = 'date';
                        columnType = 'datecolumn';
                        columnFormat = customFields[i].format || null; // default date format used when null passed
                    } else if (customFields[i].valueType == FIELD_TYPE_DOUBLE) {
                        fieldType = 'number';
                        columnType = 'numbercolumn';
                        columnAlign = 'right';
                        columnFormat = customFields[i].format || '0,000.00';
                    } else if (customFields[i].valueType == FIELD_TYPE_BOOLEAN) {
                        fieldType = 'boolean';
                        columnType = 'booleancolumn';
                        if (customFields[i].format) {
                            var trueFalseTexts = customFields[i].format.split('/');
                            trueText = trueFalseTexts[0];
                            falseText = trueFalseTexts[1];
                        }
                        columnFormat = customFields[i].format || '0,000.00';
                    }

                    var newFieldName = 'cf_' + customFields[i].processFieldId;
                    serviceResponse.metaData.columnsData.push(OPF.console.inbox.view.task.Columns.columnInfo(
                        newFieldName, customFields[i].name, {
                            format: columnFormat,
                            align: columnAlign,
                            trueText: OPF.isEmpty(trueText) ? 'true' : trueText,
                            falseText: OPF.isEmpty(falseText) ? 'false' : falseText
                        }));
                    serviceResponse.metaData.fields.push({
                        name: 'cf_' + customFields[i].processFieldId, // this is sent as sortBy if this column is sorted by - need to be the same for all fields in one column
                        mapping: this.getCustomFieldMapping(i), // 'case.customFields[' + i + '].value'
                        type: fieldType
                    });
                }
            }
        }

        return this.callParent(arguments);
    },

    onMetaChange: function(metaData) {
        var fields = metaData.fields,
            newModel;

        Ext.apply(this, metaData);

        if (fields) {
            newModel = Ext.define("Ext.data.reader.Json-Model" + Ext.id(), {
                extend: 'Ext.data.Model',
                fields: fields
            });
            this.setModel(newModel, true);
            var sortingColumn = null;
            for (var i = 0; i < this.taskGridPanel.columns.length; i++) {
                if (OPF.isNotEmpty(this.taskGridPanel.columns[i].sortState)) {
                    sortingColumn = {};
                    sortingColumn.columnName = this.taskGridPanel.columns[i].dataIndex;
                    sortingColumn.sortOrder = this.taskGridPanel.columns[i].sortState;
                    break;
                }
            }
            /*if (sortingColumn != null) {
                var coll = new Ext.util.MixedCollection();
                coll.add({ property: sortingColumn.columnName, direction: sortingColumn.sortOrder });
                this.taskGridPanel.store.sorters = coll;
            }*/
            this.taskGridPanel.store.reconfigure(
                OPF.console.inbox.view.task.Columns.populateColumns(fields, metaData.columnsData, sortingColumn));
        } else {
            this.buildExtractors(true);
        }
    },

    createAccessor : function(expr) {
        // original createAccessor was creating accessors that threw ex if some property in the chain is undefined / null;
        // i.e. obj.assignee.username would throw an ex for null assignee - but we want to use convert function on the whole record here (skip accessor)
        return function(obj) {
            try {
                return OPF.console.inbox.store.CaseTaskJsonReader.superclass.createAccessor(expr)(obj);
            } catch(e) {
            }
        };
    }
});

Ext.define('OPF.console.inbox.store.CaseTaskProxy', {
    extend: 'Ext.data.proxy.Ajax',

    taskGridPanel: null,

    constructor: function(taskGridPanel, cfg) {
        cfg = cfg || {};
        OPF.console.inbox.store.CaseTaskProxy.superclass.constructor.call(this, Ext.apply({
            taskGridPanel: taskGridPanel,
            simpleSortMode: true,
            reader: Ext.create('OPF.console.inbox.store.CaseTaskJsonReader', taskGridPanel)
        }, cfg));
    },

    doRequest: function(operation, callback, scope) {
        var data = null;
        var method;
        var type = this.taskGridPanel.isTaskGrid ? OPF.core.utils.RegistryNodeType.TASK : OPF.core.utils.RegistryNodeType.CASE;
        if (this.taskGridPanel.managerLayout.westPanel.filterBy == FILTER_SEARCH) {
            this.url = type.generateUrl('/search');
            data = { data: this.taskGridPanel.managerLayout.westPanel.getSearchParameters() };
            method = 'POST';
        } else if (this.taskGridPanel.managerLayout.westPanel.filterBy == FILTER_ALL_WORK) {
            this.url = type.generateUrl('/find-through-actors?');
            if (OPF.isNotEmpty(this.lookupPrefix)) {
                this.url += '&lookupPrefix=' + this.lookupPrefix;
            }
            this.url += '&active=' + !this.taskGridPanel.managerLayout.westPanel.filterDisplayInactive;
            method = 'GET';
        } else if (this.taskGridPanel.managerLayout.westPanel.filterBy == FILTER_MY_WORK) {
            this.url = type.generateUrl('/search');
            data = { data: { assigneeId: OPF.Cfg.USER_INFO.id, lookupPrefix: this.lookupPrefix } };
            data.data.active = !this.taskGridPanel.managerLayout.westPanel.filterDisplayInactive;
            method = 'POST';
        } else if (this.taskGridPanel.managerLayout.westPanel.filterBy == FILTER_MY_TEAMS_WORK) {
            this.url = type.generateUrl('/find-belonging-to-user-actor?');
            if (OPF.isNotEmpty(this.lookupPrefix)) {
                this.url += '&lookupPrefix=' + this.lookupPrefix;
            }
            this.url += '&active=' + !this.taskGridPanel.managerLayout.westPanel.filterDisplayInactive;
            method = 'GET';
        } else if (this.taskGridPanel.managerLayout.westPanel.filterBy == FILTER_MY_COMPLETED_WORK) {
            this.url = type.generateUrl(
                type == OPF.core.utils.RegistryNodeType.CASE ?
                    '/find-closed-cases-for-current-user?' : '/find-closed-tasks-for-current-user?'
            );
            if (OPF.isNotEmpty(this.lookupPrefix)) {
                this.url += '&lookupPrefix=' + this.lookupPrefix;
            }
            method = 'GET';
        } else if (this.taskGridPanel.managerLayout.westPanel.filterBy == FILTER_PROCESS) {
            this.url = type.generateUrl('/search');
            data = {
                data: {
                    processId: this.taskGridPanel.managerLayout.westPanel.selectedProcessId, lookupPrefix: this.lookupPrefix
                }
            };
            data.data.active = !this.taskGridPanel.managerLayout.westPanel.filterDisplayInactive;
            method = 'POST';
        }
        var writer  = this.getWriter(),
            request = this.buildRequest(operation, callback, scope);

        if (operation.allowWrite()) {
            request = writer.write(request);
        }

        Ext.apply(request, {
            headers       : this.headers,
            timeout       : this.timeout,
            scope         : this,
            callback      : this.createRequestCallback(request, operation, callback, scope),
            method        : method,
            jsonData      : data,
            disableCaching: false // explicitly set it to false, ServerProxy handles caching
        });

        Ext.Ajax.request(request);

        return request;
    }

});

Ext.define('OPF.console.inbox.store.CaseTaskReconfigurableStore', {
    extend: 'Ext.data.Store',

    autoLoad: {
        params:{ start: 0, limit: 20 }
    },
    remoteSort: true,
    pageSize: 20,

    taskGridPanel: null,

    constructor: function(taskGridPanel, cfg) {
        cfg = cfg || {};
        var model = taskGridPanel.isTaskGrid ? 'OPF.console.inbox.model.TaskModel' : 'OPF.console.inbox.model.CaseModel';
        OPF.console.inbox.store.CaseTaskReconfigurableStore.superclass.constructor.call(this, Ext.apply({
            taskGridPanel: taskGridPanel,
            model: model,
            proxy: Ext.create('OPF.console.inbox.store.CaseTaskProxy', taskGridPanel)
        }, cfg));
    },

    reconfigure: function(columns) {
        this.taskGridPanel.reconfigure(this.taskGridPanel.store, columns);
    },

    listeners: {
        datachanged: function() {
            this.taskGridPanel.managerLayout.taskDetailsPanel.refreshPanels(null, null); // clear details panels
            this.taskGridPanel.disableButtons();
        },

        metachange: function(store, meta) {
            var columns = [];
            for (var i = 0; i < meta.fields.length; i++) {
                if (meta.fields[i].header) {
                    columns.push({
                        header: meta.fields[i].header,
                        dataIndex: meta.fields[i].name,
                        sortable: true,
                        xtype: meta.fields[i].columnType,
                        align: meta.fields[i].columnAlign,
                        format: meta.fields[i].columnFormat,
                        trueText: meta.fields[i].booleanColumnTrueText,
                        falseText: meta.fields[i].booleanColumnFalseText
                    });
                }
            }

            store.reconfigure(columns);
        }
    }

});