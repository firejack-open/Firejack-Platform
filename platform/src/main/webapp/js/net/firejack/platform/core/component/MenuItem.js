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

Ext.define('OPF.core.component.MenuItem', {
    extend: 'Ext.menu.Item',
    alias: 'widget.opf-menuitem',

    renderTpl: [
        '<tpl if="plain">',
            '{text}',
        '<tpl else>',
            '<a id="{id}-itemEl" class="' + Ext.baseCSSPrefix + 'menu-item-link" href="{href}" <tpl if="hrefTarget">target="{hrefTarget}"</tpl> hidefocus="true" unselectable="on">',
//                '<img id="{id}-iconEl" src="{icon}" class="' + Ext.baseCSSPrefix + 'menu-item-icon {iconCls}" />',
                '<span id="{id}-textEl" class="' + Ext.baseCSSPrefix + 'menu-item-text header" <tpl if="arrowCls"></tpl> ><b>{text}</b></span><br/>',
                '<span id="{id}-descriptionEl" class="' + Ext.baseCSSPrefix + 'menu-item-text" <tpl if="arrowCls"></tpl> >{description}</span>',
//                '<img id="{id}-arrowEl" src="{blank}" class="{arrowCls}" />',
            '</a>',
        '</tpl>'
    ],

    beforeRender: function() {
        var me = this;

        me.callParent();

        Ext.applyIf(me.renderData, {
            description: me.description
        });
    }

});


