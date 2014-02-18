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


Ext.define('OPF.core.component.ErrorContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.error-container',

    cls: 'error-container',
    hidden: true,
    form: null,
    delay: null,

    activeErrorContainerTpl: [
        '<tpl if="errors && errors.length">',
            '<h4><img src="{errorImageUrl}" border="0"/>Error Message!</h4>',
            '<ul><tpl for="errors"><li class="{level.css} <tpl if="xindex == xcount"> last</tpl>">{msg}</li></tpl></ul>',
        '</tpl>'
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

        var fields = this.form.getForm().getFields();
        Ext.each(fields.items, function(field) {
//            field.on('validitychange', me.showErrors, me);
            field.on('errorchange', me.showErrors, me);
        });
    },

    showError: function(level, msg) {
        var errors = [
            {
                level: level,
                msg: msg
            }
        ];
        this.setActiveErrorContainer(errors);
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
//        console.log("Executed task");
        this.setActiveErrorContainer(errors);
    },

    setActiveErrorContainer: function(errors) {
        var allActiveError = this.getTpl('activeErrorContainerTpl').apply({
            errors: errors,
            errorImageUrl: OPF.Cfg.fullUrl('images/error.png')
        });
        this.update(allActiveError);
        this.setVisible(OPF.isNotBlank(allActiveError));
    },

    cleanActiveErrors: function() {
        this.update('');
        this.hide();
    }

});
