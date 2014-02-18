Ext.define('OPF.prometheus.wizard.entity.component.ReferenceFieldView', {
    extend: 'Ext.view.View',
    alias : 'widget.prometheus.wizard.entity.reference-field-view',

    mixins: {
        dragSelector: 'Ext.ux.DataView.DragSelector',
        draggable   : 'Ext.ux.DataView.Draggable'
    },

    tpl: [
        '<tpl for=".">',
            '<li class="x-boxselect-item">{name}</li>',
        '</tpl>'
    ],

    itemSelector: 'li.x-boxselect-item',
    singleSelect: true,
    autoScroll: true,

    initComponent: function() {
        this.mixins.dragSelector.init(this);
        this.mixins.draggable.init(this, {
            ddConfig: {
                ddGroup: 'fieldGridDDGroup'
            },
            ghostTpl: [
                '<li class="x-boxselect-item">{name}</li>'
            ]
        });

        this.callParent();
    }
});