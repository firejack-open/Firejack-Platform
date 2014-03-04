/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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