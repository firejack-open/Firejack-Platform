Ext.define('OPF.core.component.form.ComboBox', {
    extend: 'Ext.form.field.ComboBox',
    alias : ['widget.opf-form-combo', 'widget.opf-combo'],

    cls: 'opf-combo-field',

    mixins: {
        subLabelable: 'OPF.core.component.form.SubLabelable',
        errorHandler: 'OPF.core.component.form.ErrorHandler'
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    },

    onListSelectionChange: function(list, selectedRecords) {
        var me = this,
            isMulti = me.multiSelect,
            hasRecords = selectedRecords.length > 0;


        if (!me.ignoreSelection && me.isExpanded) {
            if (!isMulti && hasRecords) {
                Ext.defer(me.collapse, 1, me);
            }

            if (isMulti || (hasRecords && me.picker)) {
                me.setValue(selectedRecords, false);
            }
            if (hasRecords) {
                me.fireEvent('select', me, selectedRecords);
            }
            me.inputEl.focus();
        }
    },

    onExpand: function() {
        this.callParent();
        var picker = this.getPicker();
        if (picker && picker.pagingToolbar) {
            picker.pagingToolbar.doLayout();
        }
    },

    setDisplayField: function(displayField) {
        this.displayField = displayField;
        this.displayTpl = new Ext.XTemplate(
            '<tpl for=".">',
                '{[typeof values === "string" ? values : values["' + this.displayField + '"]]}',
                '<tpl if="xindex < xcount">' + this.delimiter + '</tpl>',
            '</tpl>'
        );
    }

});