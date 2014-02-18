Ext.override(Ext.data.Model, {
    copy: function () {
        var me   = this,
            copy = this.callParent(arguments);

        Ext.each(this.associations.items, function(association) {
            if (association.type == 'belongsTo') {
                var associationModel = Ext.create(association.model);
                copy[association.instanceName] = me[association.instanceName];
            } else if (association.type == 'hasMany') {
                var associationModels = me[association.name + 'Store'].getRange();
                var store = Ext.create('Ext.data.Store', {
                    model: association.model
                });
                store.add(associationModels);
                copy[association.name + 'Store'] = store;
            }
        });
        return copy;
    }
});