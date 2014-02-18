Ext.define('OPF.prometheus.layout.AbstractLayout', {
    extend: 'Ext.container.Container',

    listeners: {
        afterrender: function(container) {
            Ext.ComponentMgr.each(function(key, component) {
                if ('prometheus.component.top-menu-component' == component.xtype ||
                    'prometheus.component.left-menu-component' == component.xtype) {
                    component.initMenu();
                }
            });

            var batchResourceLookupData = [];
            var resourceComponents = new Ext.util.MixedCollection();
            Ext.ComponentMgr.each(function(key, component, lenght) {
                if ('text-resource-control' == component.xtype ||
                    'image-resource-control' == component.xtype) {
                    batchResourceLookupData.push(component.getResourceLookup());
                    resourceComponents.add(component.getResourceLookup(), component);
                }
            });

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/by-lookup-list'),
                method: 'POST',
                jsonData: {"data": {
                    lookup: batchResourceLookupData
                }},

                success: function(response){
                    var jsonData = Ext.decode(response.responseText);
                    if (jsonData.data) {
                        Ext.each(jsonData.data, function(resourceData) {
                            var resourceLookup = resourceData.lookup;
                            var resourceComponent = resourceComponents.getByKey(resourceLookup);
                            if (OPF.isNotEmpty(resourceComponent)) {
                                resourceComponent.initResource(resourceData);
                            }
                        });
                    }
                },
                failure: function(response) {
                    var errorJsonData = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(false, errorJsonData.message);
                }
            });
        }
    }

});