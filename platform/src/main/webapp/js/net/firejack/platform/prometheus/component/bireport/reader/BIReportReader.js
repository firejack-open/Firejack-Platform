Ext.require([
    'OPF.prometheus.component.bireport.column.BIReportTreeColumn'
]);

Ext.define('OPF.prometheus.component.bireport.reader.BIReportReader', {
    extend: 'Ext.data.reader.Json',
    alias: 'reader.bireport-reader',

    root: 'data',
    totalProperty: 'total',
    messageProperty: 'message',
    idProperty: 'id',

    readRecords: function (response, r,d) {

        var i, c, ch;
        var bireportData = response.data;
        delete response.data;

        if (bireportData.length > 0) {
            var columnsData = bireportData[0].columns;
            if (columnsData) {
                var fields = this.model.getFields();
                fields.push({
                    name: 'value',
                    type: 'string',
                    useNull: true
                });
                this.constructFields(fields, columnsData);

                var columns = [];
                this.constructColumns(columns, columnsData);

                var bgColors = this.generateBackgroundStyles(bireportData[0].countOfLevels);

                response.metaData = {
                    fields: fields,
                    columns: columns,
                    filter: bireportData[0].filter,
                    bgColors: bgColors
                };
            }

            var data = [];
            var rowsData = bireportData[0].rows;
            this.constructData(data, rowsData);

            response.children = data;
        }
        return this.callParent([response]);
    },

    constructFields: function(fields, columnsData) {
        for (var i = 0; i < columnsData.length; i++) {
            var columnData = columnsData[i];
            if (OPF.isNotEmpty(columnData.columnIndex)) {
                var field = {
                    name: 'column_' + columnData.columnIndex,
                    type: 'float',
                    useNull: true
                };
                if (columnData.type == 'MEASURE') {
                    field.type = 'float';
                } else {
                    field.type = 'string';
                }
                fields.push(field);
            } else if (OPF.isNotEmpty(columnData.children)) {
                this.constructFields(fields, columnData.children);
            }
        }
    },

    constructColumns: function(columns, columnsData) {
        for (var i = 0; i < columnsData.length; i++) {
            var columnData = columnsData[i];
            if (OPF.isEmpty(columnData.columnIndex)) {
                var nestedColumns = [];
                this.constructColumns(nestedColumns, columnData.children);

                var groupedColumn = {
                    text: columnData.name,
                    columns: nestedColumns,
                    cls: columnData.type.toLowerCase() + '-column-type'
                };
                columns.push(groupedColumn);
            } else {
                var column;
                if (columnData.type == 'VERTICAL') {
                    column = {
                        xtype: 'opf-bireport-treecolumn',
//                        align: 'center',
                        text: columnData.name,
                        width: 100,
                        tdCls: 'bireport-treecolumn',
                        sortable: true,
                        dataIndex: 'column_' + columnData.columnIndex,
                        unShift: columnData.unShift,
                        renderer: function(value, metaData) {
                            metaData.tdCls = OPF.isBlank(value) ? 'treecolumn-value-empty' : '';
                            return value;
                        }
                    };
                } else {
                    column = {
                        align: 'center',
                        text: columnData.name,
                        width: 100,
                        sortable: true,
                        dataIndex: 'column_' + columnData.columnIndex,
                        renderer: function(value, metaData, record) {
                            var depth = record.get('depth');
                            var bgColor = this.bgColors[depth - 1];
                            metaData.style = 'background-color: ' + bgColor + ';';
                            return value;
                        }
                    };
                }
                column.cls = columnData.type.toLowerCase() + '-column-type';
                columns.push(column);
            }
        }
    },

    constructData: function(data, rowsData) {
        for (var i = 0; i < rowsData.length; i++) {
            var rowData = rowsData[i];

            var item = {
                depth: rowData.depth,
                expanded: rowData.expanded,
                leaf: rowData.leaf,
                value: rowData.name
            };
            item['column_' + rowData.columnIndex] = rowData.name;

            if (OPF.isNotEmpty(rowData.columnIndexValues)) {
                for (var key in rowData.columnIndexValues) {
                    item['column_' + key] = rowData.columnIndexValues[key];
                }
            }

            if (!item.leaf) {
                var childrenItems = [];
                this.constructData(childrenItems, rowData.children);
                item.children = childrenItems.length > 0 ? childrenItems : null;
            }
            data.push(item);
        }
    },

    generateBackgroundStyles: function(depth) {
        var step = 1 / depth;
        var bgColors = [];
        for (var i = 0; i < depth; i++) {
            var coof = step * (i + 1);
            bgColors[i] = this.colorLuminance("#bbb", coof);
        }
        return bgColors;
    },

    colorLuminance: function(hex, lum) {
    	hex = String(hex).replace(/[^0-9a-f]/gi, '');
    	if (hex.length < 6) {
    		hex = hex[0]+hex[0]+hex[1]+hex[1]+hex[2]+hex[2];
    	}
    	lum = lum || 0;

    	// convert to decimal and change luminosity
    	var rgb = "#", c, i;
    	for (i = 0; i < 3; i++) {
    		c = parseInt(hex.substr(i*2,2), 16);
    		c = Math.round(Math.min(Math.max(0, c + ((255 - c) * lum)), 255)).toString(16);
    		rgb += ("00"+c).substr(c.length);
    	}
    	return rgb;
    }

});