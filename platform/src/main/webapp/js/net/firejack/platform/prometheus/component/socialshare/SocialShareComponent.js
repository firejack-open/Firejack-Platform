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
