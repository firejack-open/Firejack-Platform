Ext.require([
    'OPF.prometheus.layout.AbstractLayout',
    'OPF.prometheus.component.toolbar.ToolbarComponent',
    'OPF.prometheus.component.header.HeaderComponent',
    'OPF.prometheus.component.topmenu.TopMenuComponent',
    'OPF.prometheus.component.breadcrumbs.BreadcrumbsComponent',
    'OPF.prometheus.component.footer.FooterComponent',
    'OPF.prometheus.page.inbox.component.InboxMenuComponent',
    'OPF.prometheus.page.inbox.component.TaskDataViewComponent'
]);

Ext.define('OPF.prometheus.page.inbox.view.InboxView', {
    extend: 'OPF.prometheus.layout.AbstractLayout',
    alias: 'widget.prometheus.page.inbox',


    initComponent: function() {
        var me = this;

        this.items = [
            {
                xtype: 'container',
                cls: 'top-container',
                items: [
                    {
                        xtype: 'prometheus.component.toolbar-component',
                        area: 'top'
                    },
                    {
                        xtype: 'prometheus.component.header-component',
                        area: 'top',
                        siteLogoLookup: OPF.Cfg.PACKAGE_LOOKUP + '.site-logo'
                    },
                    {
                        xtype: 'prometheus.component.top-menu-component',
                        area: 'top'
                    },
                    {
                        xtype: 'prometheus.component.breadcrumbs-component',
                        area: 'top'
                    }
                ]
            },
            {
                xtype: 'container',
                cls: 'body-container',
                items: [
                    {
                        xtype: 'container',
                        id: 'bodyPanel',
                        layout: {
                            type: 'column',
                            autoSize: true
                        },
                        cls: 'home-container',
                        items: [
                            {
                                xtype: 'container',
                                id: 'leftPanel',
                                cls: 'left-panel',
                                width: 200,
                                autoHeight: true,
                                items: [
                                    {
                                        xtype: 'prometheus.component.inbox-menu-component'
                                    }
                                ]
                            },
                            {
                                xtype: 'container',
                                cls: 'body-panel',
                                columnWidth: 1,
                                autoHeight: true,
                                layout: 'column',
                                items: [
                                    {
                                        xtype: 'container',
                                        columnWidth: 1,
//                                        height: 400,
                                        items: [
                                            {
                                                xtype: 'prometheus.component.task-dataview-component'
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    resize: function(panel, adjWidth, adjHeight) {
                                        var height = panel.getHeight();
                                        var leftPanel = panel.up('#bodyPanel').down('#leftPanel');
                                        leftPanel.setHeight(height);
                                    }
                                }
                            }
                        ]
                    }
                ]
            },
            {
                xtype: 'container',
                cls: 'footer',
                items: [
                    {
                        xtype: 'prometheus.component.footer-component',
                        area: 'footer',
                        footerLogoLookup: OPF.Cfg.PACKAGE_LOOKUP + '.footer-site-logo',
                        copyRightLookup: OPF.Cfg.PACKAGE_LOOKUP + '.copyright'
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }

});