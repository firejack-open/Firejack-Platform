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

Ext.require([
    'OPF.core.utils.StoreHelper',
    'OPF.console.domain.model.BIReportUserFieldModel',
    'OPF.console.domain.model.BIReportModel',
    'OPF.console.domain.model.BIReportUserModel',
    'OPF.console.domain.model.BIReportFieldTreeModel',
    'OPF.prometheus.component.manager.AdvancedSearchComponent',
    'OPF.core.component.plugin.TreeViewDragDrop'
]);

Ext.define('OPF.prometheus.wizard.bireportconfigure.BIReportConfigureWizard', {
    extend: 'OPF.prometheus.wizard.AbstractWizard',
    alias: 'widget.prometheus.wizard.bireport-configure-wizard',

    statics: {
        id: 'bireportConfigureWizard'
    },

    title: 'Configure BI Report View',
    height: 850,

    biReportComponent: null,
    bireportLookup: '',
    userReportId: null,

    selectedUserModels: null,
    assignedRoleModels: null,
    currentModelLookup: null,
    currentRecord: null,
    rolesLoaded: false,

    initComponent: function() {
        var me = this;

        this.bireportModel = this.biReportComponent.modelInstance;
        this.bireportLookup = this.bireportModel.self.lookup;

        this.bireportNameField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'Title',
            name: 'title',
            anchor: '100%'
        });

        this.form = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            border: false,
            bodyPadding: 10,
            items: [
                this.bireportNameField
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        {
                            xtype: 'button',
                            ui: 'red',
                            width: 250,
                            height: 60,
                            hidden: true,
                            action: 'delete',
                            text: 'Delete This Report View',
                            handler: function() {
                                me.deleteBIReport();
                            }
                        },
                        '->',
                        {
                            xtype: 'button',
                            ui: 'blue',
                            width: 250,
                            height: 60,
                            text: 'Next',
                            formBind: true,
                            handler: function() {
                                me.goToSelectDimensionsPanel();
                            }
                        }
                    ]
                }
            ],
            listeners: {
                afterrender: function(form) {
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.BIReportUser', me.messagePanel, {
                        useBaseUrl: false
                    });

//                    me.bireportNameField.customValidator = function(value) {
//                        var msg = null;
//                        if (me.lastNotUniqueName != value) {
//                            if (OPF.isNotBlank(value)) {
//                                me.checkUniqueNameTask.delay(250);
//                            }
//                        } else {
//                            msg = 'Name is not unique.';
//                        }
//                        return msg;
//                    };
                }
            }
        });

        // create the data store
        this.bireportStore = Ext.create('Ext.data.Store', {
            autoLoad: false,
            model: 'OPF.console.domain.model.BIReportModel',
            proxy: {
                type: 'ajax',
                url: OPF.Cfg.restUrl('/registry/bi/report/lookup/' + me.bireportLookup),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            listeners: {
                beforeload: function(store, operation) {
                    store.proxy.url = OPF.Cfg.restUrl('/registry/bi/report/lookup/' + me.bireportLookup);
                },
                load: function(store, records) {
                    if (me.userReportId) {
                        me.bireportUserStore.load();
                    } else {
                        me.populatePanels();
                    }
                    var factEntityId = records[0].get('parentId');
//                    me.initAddFilterMenu(factEntityId);
                }
            }
        });

        this.bireportUserStore = Ext.create('Ext.data.Store', {
            autoLoad: false,
            model: 'OPF.console.domain.model.BIReportUserModel',
            proxy: {
                type: 'ajax',
                url: OPF.Cfg.restUrl('/registry/bi/report-user/' + me.userReportId),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            listeners: {
                beforeload: function(store, operation) {
                    if (me.bireportLookup) {
                        store.proxy.url = OPF.Cfg.restUrl('/registry/bi/report-user/' + me.userReportId);
                        return true;
                    } else {
                        return false;
                    }
                },
                load: function(store, records) {
                    me.populatePanels();
                }
            }

        });

        this.availableDimStore = Ext.create('Ext.data.TreeStore', {
            autoLoad: false,
            storeId: 'availableDimStore',
            model: 'OPF.console.domain.model.BIReportFieldTreeModel',
            root:
                {
                    title: 'Available dimensions',
                    expanded: false,
                    children: []
                }
        });

        this.availableDimGrid = Ext.create('Ext.tree.Panel', {
            store: this.availableDimStore,
            rootVisible: true,
            border: false,
            width: 250,
            columns: [
                {
                    xtype: 'treecolumn',
                    text: 'Available Dimension Levels',
                    flex: 1,
                    sortable: true,
                    dataIndex: 'title'
                }
            ],
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    allowContainerDrops: true
                },
                listeners: {
                    drop: function (node, data, overModel, dropPosition, eOpts) {
                        var draggedNode = data.records[0];
                        if (draggedNode.childNodes.length > 0) {
                            me.updateAvailableDimTooltips();
                            draggedNode.collapse(true, function() {
                                this.set('leaf', true);
                            });
                        }
                    }
                }
            }
        });

        var verticalDimStore = Ext.create('Ext.data.TreeStore', {
            autoLoad: false,
            storeId: 'verticalDimStore',
            model: 'OPF.console.domain.model.BIReportFieldTreeModel',
            root: {
                title: 'Drag an available dimension here',
                alwaysExpanded: false,
                enabled: false,
                children: [],
                loaded: true,
                allowDrop: true
            },
            getAt: function (index) {
                var view = me.verticalDimGrid.getView();
                return view.getRecord(view.getNode(index));
            }
        });

        this.checkColumnRenderer = function(value, metadata, record) {
            var result = '';
            if (record.get('leaf') === true) {
                result = Ext.ux.CheckColumn.renderer(value);
            }
            return result;
        };

        this.verticalDimGrid = Ext.create('Ext.tree.Panel', {
            ui: 'white',
            store: verticalDimStore,
            rootVisible: true,
            flex: 1,
            border: false,
            columns: [
                {
                    xtype: 'treecolumn',
                    text: 'Vertical Dimension Levels (Row Titles)',
                    flex: 7,
                    sortable: false,
                    dataIndex: 'title'
                },
                {
                    xtype: 'checkcolumn',
                    align: 'center',
                    flex: 2,
                    text: 'Always Expanded',
                    sortable: false,
                    dataIndex: 'alwaysExpanded',
                    renderer: this.checkColumnRenderer
                },
                {
                    xtype: 'checkcolumn',
                    align: 'center',
                    flex: 1,
                    text: 'Enabled',
                    sortable: false,
                    dataIndex: 'enabled',
                    renderer: this.checkColumnRenderer
                }
            ],
            viewConfig: {
                plugins: {
                    ptype: 'opf-treeviewdragdrop',
                    isValidDropPoint: function(targetNode, sourceNodes) {
                        var allowDragAndDrop = false;
                        var sourceNode = sourceNodes[0];
                        if (targetNode.get('root') && sourceNode.get('type') == 'ENTITY') {
                            allowDragAndDrop = true;
                        } else if (targetNode.get('type') == 'ENTITY' && sourceNode.get('type') == 'FIELD' && targetNode.get('entityId') == sourceNode.get('entityId')) {
                            allowDragAndDrop = true;
                        } else if (targetNode.get('type') == 'FIELD' && sourceNode.get('type') == 'FIELD' && targetNode.get('entityId') == sourceNode.get('entityId')) {
                            allowDragAndDrop = true;
                        }
                        return allowDragAndDrop;
                    }
                },
                listeners: {
                    drop: function (node, data, overModel, dropPosition, eOpts) {
                        var draggedNode = data.records[0];
                        if (draggedNode.childNodes.length > 0) {
                            draggedNode.set('leaf', false);
                            draggedNode.set('qtip', null); // not working
                            draggedNode.expand();
                        }
                    }
                }
            }
        });

        var horizontalDimStore = Ext.create('Ext.data.TreeStore', {
            autoLoad: false,
            storeId: 'horizontalDimStore',
            model: 'OPF.console.domain.model.BIReportFieldTreeModel',
            root: {
                title: 'Drag an available dimension here',
                alwaysExpanded: false,
                enabled: false,
                leaf: false,
                children: []
            },
            getAt: function (index) {
                var view = me.horizontalDimGrid.getView();
                return view.getRecord(view.getNode(index));
            }
        });

        this.horizontalDimGrid = Ext.create('Ext.tree.Panel', {
            ui: 'white',
            store: horizontalDimStore,
            rootVisible: true,
            flex: 1,
            border: false,
            columns: [
                {
                    xtype: 'treecolumn',
                    text: 'Horizontal Dimension Levels (Column Titles)',
                    flex: 7,
                    sortable: false,
                    dataIndex: 'title'
                },
                {
                    xtype: 'checkcolumn',
                    align: 'center',
                    flex: 2,
                    text: 'Always Expanded',
                    sortable: false,
                    dataIndex: 'alwaysExpanded',
                    hidden: true,
                    renderer: this.checkColumnRenderer
                },
                {
                    xtype: 'checkcolumn',
                    align: 'center',
                    flex: 1,
                    text: 'Enabled',
                    sortable: false,
                    dataIndex: 'enabled',
                    renderer: this.checkColumnRenderer
                }
            ],
            viewConfig: {
                plugins: {
                    ptype: 'opf-treeviewdragdrop',
                    isValidDropPoint: function(targetNode, sourceNodes) {
                        var allowDragAndDrop = false;
                        var sourceNode = sourceNodes[0];
                        if (targetNode.get('root') && sourceNode.get('type') == 'ENTITY') {
                            allowDragAndDrop = true;
                        } else if (targetNode.get('type') == 'ENTITY' && sourceNode.get('type') == 'FIELD' && targetNode.get('entityId') == sourceNode.get('entityId')) {
                            allowDragAndDrop = true;
                        } else if (targetNode.get('type') == 'FIELD' && sourceNode.get('type') == 'FIELD' && targetNode.get('entityId') == sourceNode.get('entityId')) {
                            allowDragAndDrop = true;
                        }
                        return allowDragAndDrop;
                    }
                },
                listeners: {
                    drop: function (node, data, overModel, dropPosition, eOpts) {
                        var draggedNode = data.records[0];
                        if (draggedNode.childNodes.length > 0) {
                            draggedNode.set('leaf', false);
                            draggedNode.set('qtip', null);
                            draggedNode.expand();
                        }
                    }
                }
            }
        });

        this.selectDimensionPanel = Ext.create('Ext.panel.Panel', {
            border: false,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                this.availableDimGrid,
                {
                    xtype: 'container',
                    flex: 1,
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        this.verticalDimGrid,
                        this.horizontalDimGrid
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        {
                            xtype: 'container',
                            width: 250
                        },
                        '->',
                        {
                            xtype: 'container',
                            layout: {
                                type: 'hbox'
                            },
                            cls: 'error-container',
                            action: 'show-error',
                            hidden: true,
                            height: 60,
                            html: ''
                        },
                        '->',
                        {
                            xtype: 'button',
                            ui: 'blue',
                            width: 250,
                            height: 60,
                            text: 'Next',
                            handler: function() {
                                me.goToSelectMeasuresPanel();
                            }
                        }
                    ]
                }
            ]
        });

        this.measuresStore = Ext.create('Ext.data.Store', {
            autoLoad: false,
            storeId: 'measuresStore',
            model: 'OPF.console.domain.model.BIReportFieldTreeModel'
        });

        // create the measures grid
        this.measuresGrid = Ext.create('Ext.grid.Panel', {
            border: false,
            store: me.measuresStore,
            columns: [
                {
                    text: 'Measure Field',
                    flex: 3,
                    sortable: false,
                    dataIndex: 'title'
                },
                {
                    xtype: 'checkcolumn',
                    align: 'center',
                    text: 'Enabled',
                    dataIndex: 'enabled'
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        {
                            xtype: 'container',
                            width: 250
                        },
                        '->',
                        {
                            xtype: 'container',
                            layout: {
                                type: 'hbox'
                            },
                            cls: 'error-container',
                            action: 'show-error',
                            hidden: true,
                            height: 60,
                            html: ''
                        },
                        '->',
                        {
                            xtype: 'button',
                            ui: 'blue',
                            width: 250,
                            height: 60,
                            text: 'Next',
                            handler: function() {
                                me.goToPreFilterPanel();
                            }
                        }
                    ]
                }
            ]
        });

        this.advancedSearchComponent = Ext.create('OPF.prometheus.component.manager.AdvancedSearchComponent', {
            searchPanel: this,
            model: this.bireportModel.self.factModel,
            configs: {
                searchFieldNameCombo: {
                    listConfig: {
                        cls: 'x-wizards-boundlist',
                        getInnerTpl: function() {
                            return '<div data-qtip="{displayName}">{displayName}</div>';
                        }
                    }
                },
                searchOperationCombo: {
                    width: 160,
                    listConfig: {
                        cls: 'x-wizards-boundlist',
                        getInnerTpl: function() {
                            return '<div data-qtip="{description}">{displayName}</div>';
                        }
                    }
                },
                andSearchValueButton: {
                    height: 60
                },
                andSearchConditionButtonConfigs: {
                    height: 60
                },
                orSearchConditionButtonConfigs: {
                    height: 60
                }
            }
        });

        this.prefilterPanel = Ext.create('Ext.panel.Panel', {
            title: 'Pre-filter (Optional)',
            ui: 'blue',
            bodyPadding: 10,
            items: [
                this.advancedSearchComponent
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
                            width: 250,
                            height: 60,
                            text: 'Next',
                            handler: function() {
                                me.goToDeployPanel();
                            }
                        }
                    ]
                }
            ]
        });

        this.previewPanel = Ext.create('Ext.panel.Panel', {
            ui: 'blue',
            bodyPadding: 10,
            border: false,
            title: 'Preview',
            html: '<p>Preview BI Report is not implemented yet.</p>'
        });

        this.items = [
            {
                title: '1. Provide Details',
                layout: 'fit',
                items: [
                    this.form
                ],
                nextFrameFn: function() {
                    me.goToSelectDimensionsPanel();
                }
            },
            {
                title: '2. Select Dimensions',
                layout: 'fit',
                items: [
                    this.selectDimensionPanel
                ],
                prevFrameFn: function() {
                    me.goToProvideDetailsPanel();
                },
                nextFrameFn: function() {
                    me.goToSelectMeasuresPanel();
                }
            },
            {
                title: '3. Select Measures',
                layout: 'fit',
                items: [
                    this.measuresGrid
                ],
                prevFrameFn: function() {
                    me.goToSelectDimensionsPanel();
                },
                nextFrameFn: function() {
                    me.goToPreFilterPanel();
                }
            },
            {
                title: '4. Select Pre-filter',
                layout: 'fit',
                items: [
                    this.prefilterPanel
                ],
                prevFrameFn: function() {
                    me.goToSelectMeasuresPanel();
                },
                nextFrameFn: function() {
                    me.goToDeployPanel();
                }
            },
            {
                title: '5. Preview and Save',
                prevFrameFn: function() {
                    me.goToPreFilterPanel();
                }
            }
        ];

        this.messagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            border: true,
            form: this.form
        });
        this.form.insert(0, this.messagePanel);

//        this.lastNotUniqueName = null;
//        this.checkUniqueNameTask = new Ext.util.DelayedTask(function(){
//            var name = me.bireportNameField.getValue();
//            var rootRecord = me.rootEntityCombo.findRecordByValue(me.rootEntityCombo.getValue());
//            if (OPF.isNotBlank(name) && rootRecord != null) {
//                var path = rootRecord.get('lookup');
//                var url = OPF.Cfg.restUrl('/registry/check/' + path + '/REPORT', false);
//                url = OPF.Cfg.addParameterToURL(url, 'name', name);
//                Ext.Ajax.request({
//                    url: url,
//                    method: 'GET',
//                    success: function (response) {
//                        if (me.reportNameField) {
//                            var resp = Ext.decode(response.responseText);
//                            if (resp.success) {
//                                var activeErrors = me.reportNameField.activeErrors;
//                                if (activeErrors && activeErrors.length == 0) {
//                                    me.reportNameField.clearInvalid();
//                                }
//                            } else {
//                                me.reportNameField.markInvalid(resp.message);
//                                me.lastNotUniqueName = name;
//                            }
//                        }
//                    },
//                    failure: function () {
//                        Ext.Msg.alert('Error', 'Connection error!');
//                    }
//                });
//            }
//        });

        this.callParent(arguments);
    },

    initDeployPanel: function(panel) {
        var me = this;

        panel.layout = 'fit';

        panel.border = false;


        panel.items = [
            this.previewPanel
        ];

        panel.dockedItems = [
            {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                defaults: {
                    width: 250,
                    height: 60
                },
                items: [
                    '->',
                    {
                        ui: 'light-green',
                        text: 'Save',
                        disabled: false,
                        handler: function() {
                            me.saveBIReport();
                        }
                    },
//                    {
//                        ui: 'light-green',
//                        text: 'Save As',
//                        disabled: false,
//                        handler: function() {
//                            me.saveBIReport();
//                        }
//                    },
                    {
                        ui: 'grey',
                        text: 'Cancel',
                        handler: function() {
                            me.close();
                        }
                    }
                ]
            }
        ]
    },

    goToProvideDetailsPanel: function() {
        this.clearErrorContainer();
        var layout = this.getCardPanelLayout();
        layout.setActiveItem(0);
    },

    goToSelectDimensionsPanel: function() {
        this.clearErrorContainer();
        this.validateForm(function(scope) {
            var layout = scope.getCardPanelLayout();
            layout.setActiveItem(1);
        }, this);
    },

    goToSelectMeasuresPanel: function() {
        this.clearErrorContainer();
        this.validateForm(function(scope) {
            scope.validateDimensions(function(scope) {
                var layout = scope.getCardPanelLayout();
                layout.setActiveItem(2);
            }, scope);
        }, this);
    },

    goToPreFilterPanel: function() {
        this.clearErrorContainer();
        this.validateForm(function(scope) {
            scope.validateDimensions(function(scope) {
                scope.validateMeasures(function(scope) {
                    var layout = scope.getCardPanelLayout();
                    layout.setActiveItem(3);
                }, scope);
            }, scope);
        }, this);
    },

    goToDeployPanel: function() {
        this.clearErrorContainer();
        this.validateForm(function(scope) {
            scope.validateDimensions(function(scope) {
                scope.validateMeasures(function(scope) {
                    var layout = scope.getCardPanelLayout();
                    layout.setActiveItem(4);
                }, scope);
            }, scope);
        }, this);
    },

    validateForm: function(executeFn, scope) {
        if (this.form.getForm().isValid()) {
            executeFn(scope);
        }
    },

    validateDimensions: function(executeFn, scope) {
        var me = this;

        var isSelectedHorizontalDims = false;
        var horizontalRoot = this.horizontalDimGrid.getStore().getRootNode();
        horizontalRoot.eachChild(function(child) {
            var dimFields = me.getDimensionFields(child, 'HORIZONTAL');
            isSelectedHorizontalDims |= dimFields.length > 0;
        });

        var isSelectedVerticalDims = false;
        var verticalRoot = this.verticalDimGrid.getStore().getRootNode();
        verticalRoot.eachChild(function(child) {
            var dimFields = me.getDimensionFields(child, 'VERTICAL');
            isSelectedVerticalDims |= dimFields.length > 0;
        });

        if (isSelectedHorizontalDims && isSelectedVerticalDims) {
            executeFn(scope);
        } else {
            var activeItem = this.getCardPanelLayout().getActiveItem();
            var errorContainer = activeItem.query('container[action=show-error]')[0];
            if (errorContainer) {
                errorContainer.update('Please select and enable at least 1 vertical AND 1 horizontal field');
                errorContainer.setVisible(true);
            }
        }
    },

    validateMeasures: function(executeFn, scope) {
        var isSelectedMeasures = false;
        this.measuresGrid.getStore().each(function(record) {
            isSelectedMeasures |= record.get('enabled');
        });
        if (isSelectedMeasures) {
            executeFn(scope);
        } else {
            var activeItem = this.getCardPanelLayout().getActiveItem();
            var errorContainer = activeItem.query('container[action=show-error]')[0];
            if (errorContainer) {
                errorContainer.update('Please enable at least 1 measure field');
                errorContainer.setVisible(true);
            }
        }
    },

    showWizard: function() {
        this.show();
        var pos = this.getPosition();
        this.setPosition(pos[0], 0);

        this.bireportStore.load();

        if (OPF.isNotEmpty(this.userReportId)) {
            this.down('button[action=delete]').show();
        }
    },

    saveBIReport: function() {
        var me = this;
        var reportId = this.bireportStore.getRange()[0].get('id');
        var isNew = OPF.isEmpty(this.userReportId);
        var title = this.bireportNameField.getValue();

        var horizontalRoot = this.horizontalDimGrid.getStore().getRootNode();
        var horizontalFields = [];
        horizontalRoot.eachChild(function(child) {
            var dimFields = me.getDimensionFields(child, 'HORIZONTAL');
            horizontalFields = horizontalFields.concat(dimFields);
        });
        for (var i = 0; i < horizontalFields.length; i++) {
            horizontalFields[i].order = i + 1;
        }

        var verticalRoot = this.verticalDimGrid.getStore().getRootNode();
        var verticalFields = [];
        verticalRoot.eachChild(function(child) {
            var dimFields = me.getDimensionFields(child, 'VERTICAL');
            verticalFields = verticalFields.concat(dimFields);
        });
        for (var i = 0; i < verticalFields.length; i++) {
            verticalFields[i].order = i + 1;
        }

        var fields = horizontalFields.concat(verticalFields);
        var measureRecords = this.measuresGrid.getStore().getRange();
        for (var i = 0; i < measureRecords.length; i++) {
            var measureOrder = 1;
            if (measureRecords[i].get('enabled')) {
                var biReportField = {
                    id: measureRecords[i].get('id')
                };
                fields.push({
                    field: biReportField,
                    location: 'MEASURE',
                    order: measureOrder
                });
                measureOrder++;
            }
        }

        var filter = Ext.encode(this.advancedSearchComponent.getQueryParameters());

        var jsonData = {
            report: {
                id: reportId
            },
            title: title,
            fields: fields,
            filter: filter
        };
        var errorMsg = this.validateFormData(jsonData);
        if (errorMsg) {
            Ext.Msg.alert('Validation error', errorMsg);
            return;
        }
        if (isNew) {
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('/registry/bi/report-user', false),
                method: 'POST',
                jsonData: {'data': jsonData},

                success: function (response, opts) {
                    var responseData = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(true, responseData.message);

                    me.biReportComponent.reloadBIReportUserCombo(false);
                    me.close();
                },
                failure: function (response, opts) {
                    OPF.Msg.setAlert('Failed', response);
                }
            });
        } else {
            jsonData.id = me.userReportId;
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('/registry/bi/report-user/' + me.userReportId, false),
                method: 'PUT',
                jsonData: {'data': jsonData},

                success: function (response, opts) {
                    var responseData = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(true, responseData.message);

                    me.biReportComponent.reloadBIReportUserCombo(false);
                    me.close();
                },
                failure: function (response, opts) {
                    OPF.Msg.setAlert('Failed', response);
                }
            });
        }
    },

    deleteBIReport: function() {
        var me = this;
        if (me.userReportId) {
            Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the report?',
                function(btn) {
                    if (btn == 'yes') {
                        Ext.Ajax.request({
                            url: OPF.Cfg.restUrl('/registry/bi/report-user/' + me.userReportId),
                            method: "DELETE",

                            success: function (response, opts) {
                                var resp = Ext.decode(response.responseText);
                                OPF.Msg.setAlert(true, resp.message);
                                if (resp.success) {
                                    me.biReportComponent.reloadBIReportUserCombo(true);
                                    me.close();
                                } else {
                                    OPF.Msg.setAlert('Failed', resp.message);
                                }
                            },
                            failure: function (response, opts) {
                                OPF.Msg.setAlert('Failed', response);
                            }
                        });
                    }
                }
            );
        }
    },

    getDimensionFields: function(dimension, location) {
        var fields = [];
        dimension.eachChild(function(child) {
            if (child.get('enabled')) {

                var biReportField = {
                    id: child.get('id')
                };
                var biReportUserField = {
                    field: biReportField,
                    location: location,
                    expanded: child.get('alwaysExpanded'),
                    order: 0
                };
                fields.push(biReportUserField);
            }
        });
        return fields;
    },

    populatePanels: function() {
        var report = this.bireportStore.getRange()[0];
        var measures = {};
        if (this.userReportId) {
            var userReport = this.bireportUserStore.getRange()[0];

            this.bireportNameField.setValue(userReport.get('title'));

            var userFields = userReport.get('fields');
            // populate panels with fields from user report
            for (var i = 0; i < userFields.length; i++) {
                var field = userFields[i].field;
                if (userFields[i].location == 'HORIZONTAL' || userFields[i].location == 'VERTICAL') {
                    var grid = userFields[i].location == 'HORIZONTAL' ? this.horizontalDimGrid : this.verticalDimGrid;
                    var dim = grid.getStore().getRootNode().findChild('entityId', field.entity.id, true);
                    if (!dim) {
                        dim = Ext.create('OPF.console.domain.model.BIReportFieldTreeModel', {
                            title: field.entity.name,
                            entityId: field.entity.id,
                            type: 'ENTITY',
                            allowDrag: true,
                            allowDrop: true
                        });
                        grid.getStore().getRootNode().appendChild(dim);
                    }
                    dim.appendChild(Ext.create('OPF.console.domain.model.BIReportFieldTreeModel', {
                        id: field.id,
                        title: field.displayName,
                        entityId: field.entity.id,
                        type: 'FIELD',
                        enabled: true,
                        alwaysExpanded: userFields[i].expanded,
                        leaf: true,
                        allowDrag: true,
                        allowDrop: false,
                        isEntity: false
                    }));
                    dim.expand();
                } else if (userFields[i].location == 'MEASURE') {
                    measures[field.id] = Ext.create('OPF.console.domain.model.BIReportFieldTreeModel', {
                        id: field.id,
                        title: field.displayName,
                        enabled: true
                    });
                }
            }

            var queryParameters = Ext.decode(OPF.ifBlank(userReport.get('filter'), '[]'));
            this.advancedSearchComponent.setQueryParameters(queryParameters);
        }
        // populate panels with fields from report
        var reportParentId = report.get('parentId');
        var fields = report.get('fields');
        for (var i = 0; i < fields.length; i++) {
            if (fields[i].entity.id == reportParentId) { // measure
                if (!this.isAutoGeneratedField(fields[i].field) && !measures[fields[i].id]) {
                    measures[fields[i].id] = Ext.create('OPF.console.domain.model.BIReportFieldTreeModel', {
                        id: fields[i].id,
                        title: fields[i].displayName,
                        enabled: false
                    });
                }
            } else {
                var dim = null;
                if (this.horizontalDimGrid.getStore().getRootNode().findChild('entityId', fields[i].entity.id, true)) { // check whether dimension is added to horizontal or vertical grid
                    dim = this.horizontalDimGrid.getStore().getRootNode().findChild('entityId', fields[i].entity.id, true);
                } else if (this.verticalDimGrid.getStore().getRootNode().findChild('entityId', fields[i].entity.id, true)) {
                    dim = this.verticalDimGrid.getStore().getRootNode().findChild('entityId', fields[i].entity.id, true);
                }

                var child = null;
                if (dim) { // if dim is found in horizontal or vertical panel - check if field is enabled
                    child = dim.findChild('id', fields[i].id, true);
                } else {   // if not in horizontal and vertical panels - then check in available panel
                    dim = this.availableDimStore.getRootNode().findChild('entityId', fields[i].entity.id, true);
                    // if dimension doesn't exist in available panel - then create it
                    if (!dim) {
                        dim = Ext.create('OPF.console.domain.model.BIReportFieldTreeModel', {
                            id: fields[i].id,
                            entityId: fields[i].entity.id,
                            type: 'ENTITY',
                            title: fields[i].entity.name,
                            leaf: true,
                            allowDrag: true,
                            allowDrop: true
                        });
                        this.availableDimStore.getRootNode().appendChild(dim);
                    }
                }
                if (fields[i].field && !child && !this.isAutoGeneratedField(fields[i].field)) {
                    child = Ext.create('OPF.console.domain.model.BIReportFieldTreeModel', {
                        id: fields[i].id,
                        entityId: fields[i].entity.id,
                        type: 'FIELD',
                        title: fields[i].displayName,
                        leaf: true,
                        allowDrag: true,
                        allowDrop: false
                    });
                    dim.appendChild(child);
                } else if (!fields[i].field) {
                    dim.set('title', fields[i].displayName);
                }
            }
        }
        this.updateAvailableDimTooltips();
        this.availableDimGrid.getStore().getRootNode().loaded = true;
        this.availableDimGrid.getStore().getRootNode().expand(false,
            function() {
                this.availableDimGrid.doLayout();
            },
            this
        );

        this.horizontalDimGrid.getStore().getRootNode().loaded = true;
        this.horizontalDimGrid.getStore().getRootNode().expand(true,
            function() {
                this.horizontalDimGrid.doLayout();
            },
            this
        );

        this.verticalDimGrid.getStore().getRootNode().loaded = true;
        this.verticalDimGrid.getStore().getRootNode().expand(false,
            function() {
                this.verticalDimGrid.doLayout();
            },
            this
        );

        this.measuresStore.add(Ext.Object.getValues(measures));
    },

    validateFormData: function (jsonData) {
        if (OPF.isBlank(jsonData.title)) {
            return 'The title is not specified';
        }
        var horizontalSelected = false;
        var verticalSelected = false;
        var measureSelected = false;
        for (var i=0; i<jsonData.fields.length; i++) {
            if (jsonData.fields[i].location == 'HORIZONTAL') {
                horizontalSelected = true;
            } else if (jsonData.fields[i].location == 'VERTICAL') {
                verticalSelected = true;
            }  else if (jsonData.fields[i].location == 'MEASURE') {
                measureSelected = true;
            }
        }
        if (!horizontalSelected) {
            return 'Enable please at least one horizontal dimension';
        }
        if (!verticalSelected) {
            return 'Enable please at least one vertical dimension';
        }
        if (!measureSelected) {
            return 'Enable please at least one measure';
        }
        return '';
    },

    isAutoGeneratedField: function (field) {
        return field.autoGenerated || field.displayName.toUpperCase() === 'ID';
    },

    updateAvailableDimTooltips: function () {
        var me = this;
        var root = this.availableDimGrid.getStore().getRootNode();
        root.eachChild(function(dim) {
            var tooltip = me.getTooltipForDimension(dim);
            dim.set('qtip', tooltip);
        });
    },

    getTooltipForDimension: function (dimNode) {
        var tooltip = '';
        dimNode.eachChild(function(child) {
            tooltip += child.get('title') + '<br>';
        });
        return tooltip;
    },

    executeSearch: function(searchMode) {
    },

    clearErrorContainer: function() {
        var activeItem = this.getCardPanelLayout().getActiveItem();
        var errorContainer = activeItem.query('container[action=show-error]')[0];
        if (errorContainer) {
            errorContainer.update('');
            errorContainer.setVisible(false);
        }
    }
});