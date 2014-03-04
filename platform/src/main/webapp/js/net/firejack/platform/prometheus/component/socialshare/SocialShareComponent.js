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

Ext.define('OPF.prometheus.component.socialshare.SocialShareComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.social-share-component',

    socialSiteData: {
        facebook: {
            name: 'Facebook',
            iconCls: 'facebook-login-icon',
            urlTpl: new Ext.XTemplate('http://www.facebook.com/sharer.php?u={url}&t={heading} {subHeading} {description}'),
            window: true
        },
        twitter: {
            name: 'Twitter',
            iconCls: 'twitter-login-icon',
            urlTpl: new Ext.XTemplate('http://twitter.com/share?url={url}&text={heading} {subHeading} {description}'),
            window: true
        },
        linkedin: {
            name: 'LinkedIn',
            iconCls: 'linkedin-login-icon',
            urlTpl: new Ext.XTemplate('http://www.linkedin.com/shareArticle?mini=true&url={url}&title={heading} {subHeading}&summary={description}&source={source}'),
            window: true
        },
        googlebookmarks: {
            name: 'Add to Google Bookmarks',
            iconCls: 'bookmarks-login-icon',
            urlTpl: new Ext.XTemplate('http://www.google.com/bookmarks/mark?op=edit&output=popup&bkmk={url}&title={heading} {subHeading}&annotation={description}'),
            window: true
        },
        googleplus: {
            name: 'Google+',
            iconCls: 'google-login-icon',
            urlTpl: new Ext.XTemplate('https://plus.google.com/share?url={url}&title={heading}'),
            window: true
        },
        email: {
            name: 'Email',
            iconCls: 'mail-login-icon',
            urlTpl: new Ext.XTemplate('mailto:?Subject={heading}&Body={subHeading} {description} : {url}'),
            window: false
        }
    },

    socialSiteNames: ['facebook', 'twitter', 'linkedin', 'googlebookmarks', 'googleplus', 'email'],

    initComponent: function() {
        var me = this;

        me.addEvents(
            'beforeshare'
        );

        var shareButtons = [];
        Ext.each(this.socialSiteNames, function(socialSiteName) {
            var socialDataItem = me.socialSiteData[socialSiteName];
            var shareButton = {
                xtype: 'button',
                ui: 'social',
                width: 32,
                height: 32,
                tooltip: socialDataItem.name,
                iconCls: socialDataItem.iconCls,
                handler: function() {
                    me.shareRecord(socialDataItem);
                }
            };
            shareButtons.push(shareButton);
        });
        this.items = shareButtons;

        this.callParent();
    },

    shareRecord: function(socialDataItem) {
        if (this.fireEvent('beforeshare', this, socialDataItem) === true) {
            var recordUrl = document.location.href;
            if (recordUrl.indexOf('entityId') == -1) {
                recordUrl = OPF.Cfg.addParameterToURL(recordUrl, 'entityId', socialDataItem.sharedRecord.id);
            }
            recordUrl = Ext.String.escape(recordUrl);

            var heading = Ext.String.htmlDecode(OPF.htmlToText(socialDataItem.sharedHeading));
            var subHeading = Ext.String.htmlDecode(OPF.htmlToText(socialDataItem.sharedSubHeading));
            var description = Ext.String.htmlDecode(OPF.htmlToText(socialDataItem.sharedDescription));

            var recordData = {
                url: recordUrl,
                heading: heading,
                subHeading: subHeading,
                description: description
            };

            var url = socialDataItem.urlTpl.apply(recordData);
            if (socialDataItem.window) {
                window.open(url, socialDataItem.name, "scrollbars=1,width=800,height=500");
            } else {
                window.location.href = url;
            }
        } else {
            Ext.Msg.alert('Status', 'Sorry! Can\'t share this record.');
        }
    }

});
