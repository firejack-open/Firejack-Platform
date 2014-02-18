Ext.define('OPF.core.component.form.HtmlEditor', {
    extend: 'Ext.form.field.HtmlEditor',
    alias : ['widget.opf-form-htmleditor', 'widget.opf-htmleditor'],

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

    processRawValue: function(value) {
        return value || '';
    },

    getRawValue: function() {
        return this.getValue();
    }

});