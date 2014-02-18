Ext.define('OPF.core.component.form.Checkbox', {
    extend: 'Ext.form.field.Checkbox',
    alias : ['widget.opf-form-checkbox', 'widget.opf-checkbox'],

    cls: 'opf-check-box',

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