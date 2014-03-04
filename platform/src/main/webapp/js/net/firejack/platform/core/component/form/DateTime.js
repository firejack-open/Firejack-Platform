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

Ext.define('OPF.core.component.form.DateTime', {
    extend: 'OPF.core.component.form.FieldContainer',
    alias: ['widget.opf-form-datetime', 'widget.opf-datetime'],

    /**
     * @cfg {String} dateFormat
     * The default is 'Y-m-d'
     */
    dateFormat: 'Y-m-d',
    /**
     * @cfg {String} timeFormat
     * The default is 'H:i:s'
     */
    timeFormat: 'H:i:s',
    /**
     * @cfg {String} dateTimeFormat
     * The format used when submitting the combined value.
     * Defaults to 'Y-m-d H:i:s'
     */
    dateTimeFormat: 'Y-m-d H:i:s',
    /**
     * @cfg {Object} dateConfig
     * Additional config options for the date field.
     */
    dateConfig:{},
    /**
     * @cfg {Object} timeConfig
     * Additional config options for the time field.
     */
    timeConfig:{},

    // properties
    dateValue: null, // Holds the actual date
    /**
     * @property dateField
     * @type Ext.form.field.Date
     */
    dateField: null,
    /**
     * @property timeField
     * @type Ext.form.field.Time
     */
    timeField: null,

    initComponent: function() {
        var me = this;
        me.items = me.items || [];

        me.dateField = Ext.create('Ext.form.field.Date', Ext.apply({
            format: me.dateFormat,
            flex: 1,
            submitValue: false,
            margin: '0 5 0 0'
        }, me.dateConfig));
        me.items.push(me.dateField);

        me.timeField = Ext.create('Ext.form.field.Time', Ext.apply({
            format: me.timeFormat,
            flex: 1,
            submitValue: false
        }, me.timeConfig));
        me.items.push(me.timeField);

        for (var i = 0; i < me.items.length; i++) {
            me.items[i].on('change', Ext.bind(me.validateOnChange, me));
            me.items[i].on('focus', Ext.bind(me.onItemFocus, me));
            me.items[i].on('blur', Ext.bind(me.onItemBlur, me));
            me.items[i].on('specialkey', function(field, event){
                var key = event.getKey(),
                    tab = key == event.TAB;

                if (tab && me.focussedItem == me.dateField) {
                    event.stopEvent();
                    me.timeField.focus();
                    return;
                }

                me.fireEvent('specialkey', field, event);
            });
        }

        me.callParent();

//        // this dummy is necessary because Ext.Editor will not check whether an inputEl is present or not
//        this.inputEl = {
//            dom:{},
//            swallowEvent:function(){}
//        };
//
//        me.initField();
    },

    focus: function(){
        this.callParent();
        this.dateField.focus();
    },

    onBlur: function() {
        this.validateOnChange();
    },

    onItemFocus: function(item){
        if (this.blurTask) this.blurTask.cancel();
        this.focussedItem = item;
    },

    onItemBlur: function(item){
        var me = this;
        if (item != me.focussedItem) return;
        // 100ms to focus a new item that belongs to us, otherwise we will assume the user left the field
        me.blurTask = new Ext.util.DelayedTask(function(){
            me.fireEvent('blur', me);
            me.onBlur();
        });
        me.blurTask.delay(100);
    },

    validateOnChange: function() {
        if (this.validateOnBlur) {
            this.validate();
        }
    },

    getValue: function() {
        var value = null, date = this.dateField.getSubmitValue(), time = this.timeField.getSubmitValue();

        if (date) {
            if (time) {
                var format = this.getFormat();
                value = Ext.Date.parse(date + ' ' + time, format);
            } else {
                value = this.dateField.getValue();
            }
        }
        return value;
    },

    getSubmitValue: function(){
        var value = this.getValue();
        return value ? Ext.Date.format(value, this.dateTimeFormat) : null;
    },

    setValue: function(value){
        if (Ext.isString(value)) {
            value = Ext.Date.parse(value, this.dateTimeFormat);
        }
        if (OPF.isNotEmpty(value) && !isNaN(value.getTime())) {
            this.dateField.setValue(value);
            this.timeField.setValue(value);
        }
    },

    getFormat: function(){
        return (this.dateField.submitFormat || this.dateField.format) + " " + (this.timeField.submitFormat || this.timeField.format);
    },

    // Bug? A field-mixin submits the data from getValue, not getSubmitValue
    getSubmitData: function(){
        var me = this,
        data = null;
        if (!me.disabled && me.submitValue && !me.isFileUpload()) {
            data = {};
            data[me.getName()] = '' + me.getSubmitValue();
        }
        return data;
    },

    getRawValue: function() {
        return this.getValue();
    },

    processRawValue: function(rawValue) {
        return rawValue;
    },

    getErrors: function(value) {
        var errors = [];

        value = value || this.getValue();

        if(Ext.isFunction(this.validator)){
            var msg = this.validator(value);
            if (OPF.isNotBlank(msg)) {
                errors.push(msg);
            }
        }

        return errors;
    },

    isValid : function() {
        var me = this;
        return me.disabled || me.validateValue(me.processRawValue(me.getRawValue()));
    },

    validateValue: function(value) {
        var me = this,
            errors = me.getErrors(value),
            isValid = Ext.isEmpty(errors);
        if (!me.preventMark) {
            if (isValid) {
                me.clearInvalid();
            } else {
                me.markInvalid(errors);
            }
        }

        return isValid;
    },

    markInvalid : function(errors) {
        // Save the message and fire the 'invalid' event
        var me = this,
            oldMsg = me.getActiveError();
        me.setActiveErrors(Ext.Array.from(errors));
        if (oldMsg !== me.getActiveError()) {
            me.doComponentLayout();
        }
    },

    clearInvalid : function() {
        // Clear the message and fire the 'valid' event
        var me = this,
            hadError = me.hasActiveError();
        me.unsetActiveError();
        if (hadError) {
            me.doComponentLayout();
        }
    }
});
