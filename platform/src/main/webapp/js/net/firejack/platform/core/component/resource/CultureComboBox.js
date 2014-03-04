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


Ext.define('OPF.core.component.CultureComboBox', {
    extend: 'Ext.form.field.ComboBox',

    triggerAction: 'all',
    editable: false,

    valueField: 'culture',
    displayField: 'country',
    width: 100,

    initComponent: function() {
        this.store = new Ext.data.Store({
            model: 'OPF.core.model.Culture',
            data: [
                {
                    country: "US",
                    culture: "AMERICAN"
                }
            ],
            proxy: {
                type: 'ajax',
                url : OPF.Cfg.restUrl('content/resource/culture'),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            }
        });

        this.listConfig = {
            getInnerTpl: function() {
                return '<div data-qtip="{culture}"><img src="' + OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/16/flag_{countryLC}_16.png" />{country}</div>';
            }
        };

        this.callParent(arguments);
    },

    listeners: {
        afterrender: function(combobox) {
            combobox.setValue('AMERICAN');
            combobox.store.proxy.url = OPF.Cfg.restUrl('content/resource/culture');
        }
    }

});
