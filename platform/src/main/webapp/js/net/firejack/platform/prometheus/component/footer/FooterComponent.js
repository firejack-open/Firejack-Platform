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

Ext.define('OPF.prometheus.component.footer.FooterComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.footer-component',

    copyRightLookup: null,
    footerLogoLookup: null,

    initComponent: function() {

        var tweeterAccount = OPF.Cfg.EXTRA_PARAMS.companyTweeterAccount;

        this.html =
            '<div class="footer-inner">' +
                '<div class="footer-left">' +
                   '<div class="logo" id="footerLogo"></div>' +
                   '<div class="copyright" id="footerCopyright"></div>' +
                '</div>' +
                '<div class="footer-right">' +
                    '<div class="soc-block">' +
                        '<ul>' +
                           '<li>' + (tweeterAccount == null ? '' : this.addTwitterFollowPlugin(tweeterAccount, 60, 20)) + '</li>' +
                           '<li>' + this.addFacebookLikePlugin() + '</li>' +
                        '</ul>' +
                    '</div>' +
                '</div>' +
            '</div>';

        this.callParent(arguments);
    },

    addFacebookLikePlugin: function() {
        var currentUrlEncoded = encodeURIComponent(document.location.href);
        return '<iframe src="//www.facebook.com/plugins/like.php?href=' + currentUrlEncoded +
            '&amp;send=false&amp;layout=standard&amp;width=350&amp;show_faces=false&amp;font&amp;colorscheme=light&amp;action=like&amp;height=35" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:350px; height:35px;" allowTransparency="true"></iframe>'
    },

    addTwitterFollowPlugin: function(tweeterAccountName, width, height) {
        return '<iframe allowtransparency="true" frameborder="0" scrolling="no" ' +
            'src="//platform.twitter.com/widgets/follow_button.html?screen_name=' +  tweeterAccountName +
            '&show_screen_name=false&show_count=false&lang=en" style="width:' + width + 'px; height:' + height + 'px;"></iframe>';
    },

    listeners: {
        afterrender: function(container) {
            this.footerCopyright = Ext.ComponentMgr.create({
                xtype: 'text-resource-control',
                textResourceLookup: this.copyRightLookup,
                textInnerCls: 'copyright',
                renderTo: 'footerCopyright'
            });

            this.footerLogo = Ext.ComponentMgr.create({
                xtype: 'image-resource-control',
                imgResourceLookup: this.footerLogoLookup,
                cls: 'image-resource-control',
                renderTo: 'footerLogo'
            });

        }
    }

});