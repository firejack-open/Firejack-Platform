Ext.define('OPF.prometheus.wizard.workflow.ManageActivityComponent', {
    extend: 'OPF.core.component.form.FieldContainer',
    alias: 'widget.prometheus.component.manage-activity-component',

    layout: 'anchor',
    cls: 'reference-search-component',

    name: null,          //association.name + '_' + associationModel.idProperty,
    fieldLabel: null,    //association.displayName || OPF.getSimpleClassName(association.model),
    subFieldLabel: null, //association.displayDescription,
    validationName: null,

    readOnly: false,
    data: null,

    allowDelete: true,
    allowBlank: true,
    errorMessage: '{0} is required',
    emptyMessage: '<div class="x-form-empty-field">Press config button to search for statuses.</div>',

    model: 'OPF.prometheus.wizard.workflow.model.ActivityActionModel',

    value: null,

    initComponent: function() {
        var me = this;

        this.actionActivityDropContainer = Ext.create('Ext.container.Container', {
            cls: 'reference-search-inner',
            tpl: [
                '<div><b>{name}</b></div>',
                '<div>{description}</div>'
            ]
        });

        this.items = [
            this.actionActivityDropContainer
        ];

        this.callParent(arguments);
    },

    getValue: function() {
        return this.value;
    },

    setValue: function(value) {
        this.value = value;

        if (value) {
            if (Ext.isObject(value)) {
                this.actionActivityDropContainer.update({
                    name: value.get('name'),
                    description: value.get('description')
                });
            }
        } else {
            this.actionActivityDropContainer.update('');
        }
    },

    afterRender: function(ct, position) {
        var me = this;
        this.callParent(arguments);

        var sourcePanelDropTarget = new Ext.dd.DropTarget(this.el.dom, {
            ddGroup: 'actionActivityGroup',
            notifyEnter: function(ddSource, e, data) {
                var actionActivityModel = data.records[0];
                if (OPF.isEmpty(actionActivityModel.get('isActivity')) || !actionActivityModel.get('isActivity')) {
                    this.lock();
                    setTimeout(function() {
                        sourcePanelDropTarget.unlock();
                    }, 200);
                } else {
                    me.el.highlight();
                }
            },
            notifyDrop: function(ddSource, e, data){
                var activityModel = data.records[0];
                var dropAllowed = OPF.isNotEmpty(activityModel) && activityModel.get('isActivity');
                if (dropAllowed) {
                    me.setValue(activityModel);
                }
                return  dropAllowed;
            }
        });
    }

});