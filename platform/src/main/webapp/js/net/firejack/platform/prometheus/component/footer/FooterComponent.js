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