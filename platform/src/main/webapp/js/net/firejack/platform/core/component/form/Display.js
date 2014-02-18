Ext.define('OPF.core.component.form.Display', {
    extend: 'Ext.form.field.Display',
    alias : ['widget.opf-form-display', 'widget.opf-display'],

    cls: 'opf-display',

    mixins: {
        subLabelable: 'OPF.core.component.form.SubLabelable'
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    }

});