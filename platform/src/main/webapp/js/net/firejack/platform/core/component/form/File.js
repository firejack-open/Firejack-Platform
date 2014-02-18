Ext.define('OPF.core.component.form.File', {
    extend: 'Ext.form.field.File',
    alias: ['widget.opf-form-file', 'widget.opf-file'],

    cls: 'opf-text-field',

    allowBlank: false,
    msgTarget: 'side',
    emptyText: 'Select a file to upload',
    buttonConfig: {
        text: '',
        iconCls: 'upload-icon',
        cls: 'upload-btn',
        height: 28,
        width: 28
    },

    mixins: {
        subLabelable: 'OPF.core.component.form.SubLabelable',
        errorHandler: 'OPF.core.component.form.ErrorHandler'
    },

    initComponent: function() {
        this.regex = new RegExp('.*?\\.(' + this.fileTypes.join('|') + ')', 'i');
        this.regexText = 'Uploaded file has wrong extension.';

        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    }

});