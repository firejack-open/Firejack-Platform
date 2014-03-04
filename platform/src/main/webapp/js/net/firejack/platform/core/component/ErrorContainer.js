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
