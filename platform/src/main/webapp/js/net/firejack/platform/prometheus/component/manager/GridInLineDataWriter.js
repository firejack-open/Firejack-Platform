Ext.define('OPF.prometheus.component.manager.GridInLineDataWriter', {
    extend: 'Ext.data.writer.Json',

    getRecordData: function(record, operation) {
        var recordData = this.callParent(arguments);
        if (OPF.isNotEmpty(record.associations)) {
            Ext.each(record.associations.items, function(association, ind) {
                if (association.instanceName && record[association.instanceName]) {
                    var fk = record['get' + association.name]();
                    if (OPF.isNotEmpty(fk)) {
                        recordData[association.name] = {id: fk.data.id};//temp
                    }
                }
            });
        }
        return recordData;
    }

});