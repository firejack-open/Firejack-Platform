Ext.define('OPF.core.component.form.TextArea', {
    extend: 'Ext.form.field.TextArea',
    alias : ['widget.opf-form-textarea', 'widget.opf-textarea'],

    cls: 'opf-text-area',

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