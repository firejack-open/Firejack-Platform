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


Ext.define('OPF.console.installation.view.InstallationView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.installation-page',

    title: 'Installation Page',
    layout: 'fit',
    height: 500,

    initComponent: function() {
        var instance = this;

        this.info = Ext.ComponentMgr.create({
            xtype: 'panel',
            flex: 1,
            padding: 10,
            border: false,
            html: 'Firejack Platform is not properly installed and configured on the target server. ' +
                  'Please edit the environment.xml file and set the FIREJACK_CONFIG environment ' +
                  'variable and re-run the installation script to initialize the server. ' +
                  'This page will be replaced with the working version of the Firejack Platform Console ' +
                  'when installation is successful.'
        });

        this.items = [
            this.info
        ];

        this.callParent(arguments);
    }

});