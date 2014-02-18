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

Ext.define('OPF.prototype.component.Footer', {
    extend: 'Ext.container.Container',
    alias: 'widget.opf.prototype.component.footer',

    copyRightLookup: null,

    initComponent: function() {

        this.html =
            '<div id="copyright"></div>' +
            '<div class="footer">' +
                '<div class="footer-border">' +
                    '<div class="footer1">Firejack Platform Gateway &reg; 1.0.0 [1233]</div>' +
                    '<div class="footer2"><img src="' + OPF.Cfg.fullUrl('images/fireball.png') + '" alt="Firejack Platform" title="Firejack Platform"> Powered by Firejack Platform</div>' +
                '</div>' +
            '</div>';

        this.callParent(arguments);
    },

    listeners: {
        afterrender: function(container) {

            var copyRight = new OPF.core.component.resource.TextResourceControl({
                textResourceLookup: this.copyRightLookup,
                textInnerCls: 'copyright',
                renderTo: 'copyright',
                autoInit: true
            });

        }
    }

});