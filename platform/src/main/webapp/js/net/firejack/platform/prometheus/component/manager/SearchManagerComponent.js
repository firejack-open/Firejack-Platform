Ext.require([
    'OPF.prometheus.component.manager.AdvancedSearchComponent',
    'OPF.prometheus.component.manager.SimpleSearchComponent'
]);

Ext.define('OPF.prometheus.component.manager.SearchManagerComponent', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.prometheus.component.search-manager-component',

    border: false,
    margin: '0 0 25 0',

    model: null,
    managerPanel: null,
    store: null,

    isSimpleSearch: true,
    reportMode: false,

    configs: {
        simpleSearch: null,
        advancedSearch: null
    },

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.Object.merge(this.configs, cfg.configs);

        this.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        this.searchLabel = Ext.create('Ext.container.Container', {
            html: '<h2>Search</h2>'
        });

        this.simpleSearch = Ext.create('OPF.prometheus.component.manager.SimpleSearchComponent', {
            managerPanel: this.managerPanel,
            searchPanel: this,
            model: this.model,
            configs: this.configs.simpleSearch
        });

        this.advancedSearch = Ext.create('OPF.prometheus.component.manager.AdvancedSearchComponent', {
            managerPanel: this.managerPanel,
            searchPanel: this,
            model: this.model,
            hidden: true,
            configs: this.configs.advancedSearch
        });

        this.items = [
            this.searchLabel,
            this.simpleSearch,
            this.advancedSearch
        ];

        this.switchSearchModeButton = Ext.create('Ext.button.Button', {
            text: 'Advanced Search',
            cls: 'advanced-search',
            iconCls: 'arrow-up',
            iconAlign: 'right',
            width: 126,
            handler: me.onClickSwitchSearchModeButton,
            scope: this,
            hidden: this.reportMode
        });

        this.advancedSearchButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'Search',
            ui: 'search-button',
            height: 28,
            scope: this,
            handler: this.onClickAdvancedSearchButton,
            hidden: true
        });

        this.dockedItems = [
            {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                items: [
                    this.switchSearchModeButton,
                    '->',
                    this.advancedSearchButton
                ]
            }
        ];

        this.callParent(arguments);

        if (this.reportMode) {
            this.onClickSwitchSearchModeButton();
            this.store.searchMode = 'ADVANCED_SEARCH';
        }
    },

    onClickSwitchSearchModeButton: function() {
        if (this.isSimpleSearch) {
            this.simpleSearch.hide();
            this.advancedSearch.show();
            this.advancedSearchButton.show();
            this.switchSearchModeButton.setText('Hide');
            this.switchSearchModeButton.setIconCls('arrow-down');
        } else {
            this.simpleSearch.show();
            this.advancedSearch.hide();
            this.advancedSearchButton.hide();
            this.switchSearchModeButton.setText('Advanced Search');
            this.switchSearchModeButton.setIconCls('arrow-up');
        }
        this.isSimpleSearch = !this.isSimpleSearch;
    },

    onClickAdvancedSearchButton: function() {
        this.advancedSearch.onClickSearchButton();
    },

    executeSearch: function(searchMode) {
        this.store.searchMode = searchMode;
        this.store.load();
    }

});