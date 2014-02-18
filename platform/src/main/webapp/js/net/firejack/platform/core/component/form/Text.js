Ext.define('OPF.core.component.form.Text', {
    extend: 'Ext.form.field.Text',
    alias : ['widget.opf-form-text', 'widget.opf-text', 'widget.opf-textfield'],

    cls: 'opf-text-field',

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
    }

});