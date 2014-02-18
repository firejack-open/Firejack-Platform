/*
 * Firejack Firejack Platform - Copyright (c) 2012 Firejack Technologies
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

Ext.define('OPF.console.forgotpassword.controller.ForgotPasswordController', {
    extend: 'Ext.app.Controller',

    views: ['ForgotPasswordView' ],

    init: function() {
        this.initNavigation();

        this.control(
            {

            }
        )
    },

    initNavigation: function() {
        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('site/navigation/tree-by-lookup/net.firejack.platform.top'),
            method: 'GET',
            jsonData: '[]',

            success: function(response, action) {
                var activeButton = null;
                var navigationElements = Ext.decode(response.responseText).data;
                Ext.each(navigationElements, function(navigationElement, index) {
                    OPF.NavigationMenuRegister.add(navigationElement);
                });
            },

            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, errorJsonData.message);
            }
        });
    }

});