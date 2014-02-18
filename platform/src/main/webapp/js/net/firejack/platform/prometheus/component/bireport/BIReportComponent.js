Ext.require([
    'OPF.prometheus.component.manager.AbstractGridComponent',
    'OPF.prometheus.component.bireport.reader.BIReportReader',
    'OPF.console.domain.model.BIReportUserModel',
    'OPF.prometheus.wizard.bireportconfigure.BIReportConfigureWizard'
]);

Ext.define('OPF.prometheus.component.bireport.BIReportComponent', {
    extend: 'OPF.prometheus.component.manager.AbstractGridComponent',
    alias: 'widget.prometheus.component.bireport-component',

    initComponent:function() {
        var me = this;

        this.modelInstance = Ext.create(this.model);

        this.store = this.getStore();


        this.bireportUserStore = Ext.create('Ext.data.Store', {
            autoLoad: false,
            model: 'OPF.console.domain.model.BIReportUserModel',
            proxy: {
                type: 'ajax',
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            listeners: {
                beforeload: function(store) {
                    store.proxy.url = OPF.Cfg.restUrl('/registry/bi/report-user/allow/' + me.modelInstance.self.lookup);
                },
                load: function(store, records) {
                    var bireportUserId = me.bireportUserCombo.getValue();
                    if (Ext.isNumeric(bireportUserId)) {
                        me.reloadBIReportUserView(bireportUserId);
                    } else if (records.length > 0) {
                        var record = records[0];
                        me.bireportUserCombo.select(record);
                        bireportUserId = record.get('id');
                        me.reloadBIReportUserView(bireportUserId);
                    }
                }
            }
        });

        this.bireportUserCombo = Ext.create('OPF.core.component.form.ComboBox', {
            name: 'bireportUser',
            fieldLabel: 'Select BIReport View',
            labelWidth: 150,
            store: this.bireportUserStore,
            queryMode: 'local',
            displayField: 'title',
            valueField: 'id',
            editable: false,
            typeAhead: true,
            listeners: {
                select: function(combo, records) {
                    if (records.length > 0) {
                        var record = records[0];
                        var bireportUserId = record.get('id');
                        me.reloadBIReportUserView(bireportUserId);
                    }
                }
            }
        });

        this.controlToolbar = Ext.create('Ext.toolbar.Toolbar', {
            dock: 'top',
            ui: 'footer',
            margin: '0 0 10 0',
            padding: 0,
            items: [
                this.bireportUserCombo,
                '->',
                {
                    xtype: 'button',
                    text: 'New View',
                    listeners: {
                        click: function(button) {
                            var wizard = Ext.WindowMgr.get(OPF.prometheus.wizard.bireportconfigure.BIReportConfigureWizard.id);
                            if (!wizard) {
                                wizard = Ext.ComponentMgr.create({
                                    xtype: 'prometheus.wizard.bireport-configure-wizard',
                                    biReportComponent: me
                                });
                                Ext.WindowMgr.register(wizard);
                            }
                            wizard.showWizard();
                        }
                    }
                },
                {
                    xtype: 'button',
                    text: 'Configure View',
                    listeners: {
                        click: function(button) {
                            var reportUserId = me.bireportUserCombo.getValue();
                            if (reportUserId) {
                                var wizard = Ext.WindowMgr.get(OPF.prometheus.wizard.bireportconfigure.BIReportConfigureWizard.id);
                                if (!wizard) {
                                    wizard = Ext.ComponentMgr.create({
                                        xtype: 'prometheus.wizard.bireport-configure-wizard',
                                        biReportComponent: me
                                    });
                                    Ext.WindowMgr.register(wizard);
                                }
                                wizard.userReportId = reportUserId;
                                wizard.showWizard();
                            } else {
                                Ext.MessageBox.alert('Warning', 'Please select user report to configure');
                            }
                        }
                    }
                }
            ]
        });

        this.filteredPanel = Ext.create('OPF.prometheus.component.manager.AdvancedSearchComponent', {
            title: 'Data is Filtered',
            margin: '0 0 10 0',
            cls: 'workflow-info data-is-filtered',
            bodyPadding: '10 10 0 10',
            searchPanel: this,
            model: this.modelInstance.self.factModel,
            visibleOnly: true
        });

        this.grid = Ext.create('Ext.tree.Panel', {
            cls: 'bireport-grid',
            rootVisible: false,
            useArrows: true,
            height: 510,
            store: this.store,
            columns: [],
            root: {
                children : []
            },
            viewConfig: {
                loadMask: true
            }
        });

        this.items = [
            this.controlToolbar,
            this.filteredPanel,
            this.grid
        ];

        this.bireportUserStore.load();

        this.callParent(arguments);
    },

    getStore: function(options) {
        var me = this;

        return Ext.create('Ext.data.TreeStore', Ext.apply({
            model: me.model,
            proxy: {
                type: 'ajax',
                reader: 'bireport-reader'
            },
            indexOf: Ext.emptyFn,
            listeners: {
                beforeload: function(store, operation) {
                    var biReportUserId = store.biReportUserId;
                    var url = store.model.restSuffixUrl + '/' + biReportUserId;

                    var nodeValues = [];
                    me.findParentNodeValues(nodeValues, operation.node);
                    nodeValues.reverse();
                    var parentNodeValues = Ext.encode(nodeValues);

                    url = OPF.Cfg.addParameterToURL(url, 'parentNodeValues', parentNodeValues);
                    store.proxy.url = url;
                },
                'metachange': function(store, meta) {
                    me.grid.reconfigure(me.store, meta.columns);
                    me.grid.bgColors = meta.bgColors;
                    me.refreshPreFilterPanel(meta.filter);
                }
            }
        }, options));
    },

    reloadBIReportUserCombo: function(isCurrentDeleted) {
        if (isCurrentDeleted) {
            this.bireportUserCombo.clearValue();
        }
        this.bireportUserStore.load();
    },

    reloadBIReportUserView: function(biReportUserId) {
        this.store.getRootNode().removeAll();
        this.store.biReportUserId = biReportUserId;
        this.store.load();
    },

    refreshPreFilterPanel: function(filter) {
        var queryParameters = Ext.decode(filter);
        this.filteredPanel.setQueryParameters(queryParameters);
    },

    findParentNodeValues: function(nodeValues, node) {
        if (!node.isRoot()) {
            nodeValues.push(node.get('value'));
            this.findParentNodeValues(nodeValues, node.parentNode);
        }
    }

});
