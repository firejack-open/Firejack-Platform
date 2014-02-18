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

Ext.define('OPF.prometheus.component.header.HeaderComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.header-component',

    autoEl: 'header',
    cls: 'header',

    siteTitleLookup: null,
    siteSubTitleLookup: null,
    siteLogoLookup: null,

    initComponent: function() {

        this.siteLogo = Ext.ComponentMgr.create({
            xtype: 'image-resource-control',
            imgResourceLookup: this.siteLogoLookup,
            componentCls: 'logo'
        });

        /*this.searchSiteField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            name: 'search-site',
            emptyText: 'Search this site'
        });

        this.searchSiteButton = Ext.ComponentMgr.create({
            xtype: 'button',
            iconCls: 'icon-search',
            height: 30,
            scale: 'small',
            scope: this,
            handler: this.onClickSearchButton
        });*/

        this.items = [
            this.siteLogo,
            {
                xtype: 'container',
                cls: 'm-header-search',
                items: [
                    //this.searchSiteField,
                    //this.searchSiteButton
                ]
            }
        ];

        this.callParent(arguments);
    }

});

