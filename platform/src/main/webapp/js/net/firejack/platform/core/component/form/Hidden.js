Ext.define('OPF.core.component.form.Hidden', {
    extend: 'OPF.core.component.form.Text',
    alias : ['widget.opf-form-hidden', 'widget.opf-hidden'],

    // private
    inputType : 'hidden',
    hideLabel: true,

    initComponent: function(){
        this.formItemCls += '-hidden';
        this.callParent();
    },

    /**
     * @private
     * Override. Treat undefined and null values as equal to an empty string value.
     */
    isEqual: function(value1, value2) {
        return this.isEqualAsString(value1, value2);
    },

    // These are all private overrides
    //    initEvents: Ext.emptyFn,
    setSize : Ext.emptyFn,
    setWidth : Ext.emptyFn,
    setHeight : Ext.emptyFn,
    setPosition : Ext.emptyFn,
    setPagePosition : Ext.emptyFn

});