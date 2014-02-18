/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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
