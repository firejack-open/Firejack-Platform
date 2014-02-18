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


Ext.define('OPF.core.component.NoticeContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.notice-container',

    cls: 'notice-container',
    hidden: true,
    form: null,
    delay: null,

    activeMessageContainerTpl: [
        '<div class="{containerCls}">',
            '<tpl if="messages && messages.length">',
                '<h4><img src="{imageUrl}" border="0"/>{title}</h4>',
                '<ul><tpl for="messages"><li class="{level.css} <tpl if="xindex == xcount"> last</tpl>">{msg}</li></tpl></ul>',
            '</tpl>',
        '</div>'
    ],

    initComponent: function() {
        var me = this;

        if (OPF.isNotEmpty(this.delay)) {
            var task = new Ext.util.DelayedTask(function() {
                me.initErrorContainer();
            }, this);
            task.delay(this.delay);
        } else {
            this.initErrorContainer();
        }

        this.items = this.items || [];

        this.callParent(arguments);
    },

    initErrorContainer: function() {
        var me = this;
        if (!this.form) {
            this.form = this.up('form');
        }
        if (this.form) {
            var fields = this.form.getForm().getFields();
            Ext.each(fields.items, function(field) {
                field.on('errorchange', me.showErrors, me);
            });
        }
    },

    showInfo: function(level, msg) {
        this.showError(level, msg);
    },

    showError: function(level, msg) {
        var errors = [
            {
                level: level,
                msg: msg
            }
        ];
        this.setNoticeContainer(errors);
    },

    showErrors: function() {
        var me = this;
        if (OPF.isNotEmpty(this.task)) {
            this.task.cancel();
//            console.log("Canceled task");
        }
        this.task = new Ext.util.DelayedTask(function() {
            me.showLastErrors();
        }, this);
        this.task.delay(10);
    },

    showLastErrors: function() {
        var errors = [];
        var fields = this.form.getForm().getFields();
        Ext.each(fields.items, function(field) {
            var activeErrors = field.getActiveErrors();
            Ext.each(activeErrors, function(activeError) {
                if (OPF.isNotBlank(activeError)) {
                    errors.push({
                        level: OPF.core.validation.MessageLevel.ERROR,
                        msg: activeError
                    });
                }
            });
        });
        this.setNoticeContainer(errors);
    },

    setNoticeContainer: function(notices) {
        var errors = [];
        Ext.each(notices, function(notice) {
            if (notice.level == OPF.core.validation.MessageLevel.ERROR) {
                errors.push(notice);
            }
        });
        var allActiveError = '';
        if (errors.length > 0) {
            allActiveError = this.getTpl('activeMessageContainerTpl').apply({
                title: 'Error Message!',
                containerCls: 'error-container',
                messages: errors,
                imageUrl: OPF.Cfg.fullUrl('images/error.png')
            });
        }

        var warns = [];
        Ext.each(notices, function(notice) {
            if (notice.level == OPF.core.validation.MessageLevel.WARN) {
                warns.push(notice);
            }
        });
        var allActiveWarns = '';
        if (warns.length > 0) {
            allActiveWarns = this.getTpl('activeMessageContainerTpl').apply({
                title: 'Warning Message!',
                containerCls: 'warn-container',
                messages: warns,
                imageUrl: OPF.Cfg.fullUrl('images/info.png')
            });
        }

        var infos = [];
        Ext.each(notices, function(notice) {
            if (notice.level == OPF.core.validation.MessageLevel.INFO) {
                infos.push(notice);
            }
        });
        var allActiveInfo = '';
        if (infos.length > 0) {
            allActiveInfo = this.getTpl('activeMessageContainerTpl').apply({
                title: 'Info Message!',
                containerCls: 'info-container',
                messages: infos,
                imageUrl: OPF.Cfg.fullUrl('images/info.png')
            });
        }
        this.update(allActiveError + allActiveWarns + allActiveInfo);
        this.setVisible(OPF.isNotBlank(allActiveError + allActiveWarns + allActiveInfo));
    },

    cleanActiveErrors: function() {  //TODO need to rename
        this.update('');
        this.hide();
    }

});