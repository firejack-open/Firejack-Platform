//@tag opf-console

Ext.override(Ext.grid.Scroller, {
    onAdded: function() {
        this.callParent(arguments);
        var me = this;
        if (me.scrollEl) {
            me.mun(me.scrollEl, 'scroll', me.onElScroll, me);
            me.mon(me.scrollEl, 'scroll', me.onElScroll, me);
        }
    }
});

// FIX: ComboBox bug with load mask
Ext.override(Ext.LoadMask, {
      onHide: function() { this.callParent(); }
});