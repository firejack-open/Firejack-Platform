//@tag opf-prototype
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

function getSQErrors(cmp, value) {
    var errors = [];

    value = value || cmp.processRawValue(cmp.getRawValue());

    if(Ext.isFunction(cmp.validator)){
        errors = cmp.validator(cmp, value);
    }

    if (!cmp.allowBlank && (value.length < 1 || value === cmp.emptyText)) { // if it's blank
        errors.push(cmp.blankText);
    }

    if (value.length < cmp.minLength) {
        errors.push(String.format(cmp.minLengthText, cmp.minLength));
    }

    if (value.length > cmp.maxLength) {
        errors.push(String.format(cmp.maxLengthText, cmp.maxLength));
    }

    if (cmp.vtype) {
        var vt = Ext.form.VTypes;
        if(!vt[cmp.vtype](value, cmp)){
            errors.push(cmp.vtypeText || vt[cmp.vtype +'Text']);
        }
    }

    if (cmp.regex && !cmp.regex.test(value)) {
        errors.push(cmp.regexText);
    }

    return errors;
}

Ext.define('OPF.core.component.Display', {
    extend: 'Ext.form.field.Display',
    alias : 'widget.opf-displayfield',

    labelWidth: 80,

    mixins: {
        sublabelable: 'OPF.core.component.SubLabelable'
    },

    initRenderTpl: function() {
        var me = this;
        if (!me.hasOwnProperty('renderTpl')) {
            me.renderTpl = me.getTpl('subLabelableRenderTpl');
        }
        return me.callParent();
    },

    initRenderData: function() {
        return Ext.applyIf(this.callParent(), this.getSubLabelableRenderData());
    }

});

Ext.define('OPF.core.component.TextField', {
    extend: 'Ext.form.field.Text',
    alias : 'widget.opf-textfield',

    cls: 'opf-text-field',

    mixins: {
        sublabelable: 'OPF.core.component.SubLabelable'
    },

    initRenderTpl: function() {
        var me = this;
        if (!me.hasOwnProperty('renderTpl')) {
            me.renderTpl = me.getTpl('subLabelableRenderTpl');
        }
        return me.callParent();
    },

    initRenderData: function() {
        return Ext.applyIf(this.callParent(), this.getSubLabelableRenderData());
    },

    getErrors: function(value) {
        return getSQErrors(this, value);
    }

});

Ext.define('OPF.core.component.Hidden', {
    extend:'OPF.core.component.TextField',
    alias: 'widget.opf-hidden',

    // private
    inputType : 'hidden',
    hideLabel: true,

    initComponent: function(){
        this.formItemCls += '-hidden';
        this.callParent();
    },

    /**
     * @private
     * Override. Treat undefined and null values as equal to an empty string value.
     */
    isEqual: function(value1, value2) {
        return this.isEqualAsString(value1, value2);
    },

    // These are all private overrides
//    initEvents: Ext.emptyFn,
    setSize : Ext.emptyFn,
    setWidth : Ext.emptyFn,
    setHeight : Ext.emptyFn,
    setPosition : Ext.emptyFn,
    setPagePosition : Ext.emptyFn
});

Ext.define('OPF.core.component.TextArea', {
    extend: 'Ext.form.field.TextArea',
    alias : 'widget.opf-textarea',

    cls: 'opf-text-area',

    mixins: {
        sublabelable: 'OPF.core.component.SubLabelable'
    },

    initRenderTpl: function() {
        var me = this;
        if (!me.hasOwnProperty('renderTpl')) {
            me.renderTpl = me.getTpl('subLabelableRenderTpl');
        }
        return me.callParent();
    },

    initRenderData: function() {
        return Ext.applyIf(this.callParent(), this.getSubLabelableRenderData());
    },

    getErrors: function(value) {
        return getSQErrors(this, value);
    }

});

Ext.define('OPF.core.component.HtmlEditor', {
    extend: 'Ext.form.field.HtmlEditor',
    alias : 'widget.opf-htmleditor',

    mixins: {
        sublabelable: 'OPF.core.component.SubLabelable'
    },

    initRenderTpl: function() {
        var me = this;
        if (!me.hasOwnProperty('renderTpl')) {
            me.renderTpl = me.getTpl('subLabelableRenderTpl');
        }
        return me.callParent();
    },

    initRenderData: function() {
        return Ext.applyIf(this.callParent(), this.getSubLabelableRenderData());
    },

    getValue: function() {
        var value = this.callParent();
        if (OPF.isNotBlank(value)) {
            value = value.replace(/\u200B/, '');
        }
        return value;
    },

    processRawValue: function(value) {
        return value || '';
    },

    getRawValue: function() {
        return this.getValue();
    },

    getErrors: function(value) {
        return getSQErrors(this, value);
    }

});

Ext.define('OPF.core.component.Checkbox', {
    extend: 'Ext.form.field.Checkbox',
    alias : 'widget.opf-checkbox',

    cls: 'opf-check-box',

    mixins: {
        sublabelable: 'OPF.core.component.SubLabelable'
    },

    initRenderTpl: function() {
        var me = this;
        if (!me.hasOwnProperty('renderTpl')) {
            me.renderTpl = me.getTpl('subLabelableRenderTpl');
        }
        return me.callParent();
    },

    initRenderData: function() {
        return Ext.applyIf(this.callParent(), this.getSubLabelableRenderData());
    },

    getErrors: function(value) {
        return getSQErrors(this, value);
    }

});

Ext.define('OPF.core.component.TriggerField', {
    extend: 'Ext.form.field.Trigger',
    alias : 'widget.opf-trigger',

    getErrors: function(value) {
        return getSQErrors(this, value);
    }

});

Ext.define('OPF.core.component.ComboBox', {
    extend: 'Ext.form.field.ComboBox',
    alias : 'widget.opf-combo',

    cls: 'opf-combo-field',

    mixins: {
        sublabelable: 'OPF.core.component.SubLabelable'
    },

    initRenderTpl: function() {
        var me = this;
        if (!me.hasOwnProperty('renderTpl')) {
            me.renderTpl = me.getTpl('subLabelableRenderTpl');
        }
        return me.callParent();
    },

    initRenderData: function() {
        return Ext.applyIf(this.callParent(), this.getSubLabelableRenderData());
    },

    getErrors: function(value) {
        return getSQErrors(this, value);
    },

    onListSelectionChange: function(list, selectedRecords) {
        var me = this,
            isMulti = me.multiSelect,
            hasRecords = selectedRecords.length > 0;


        if (!me.ignoreSelection && me.isExpanded) {
            if (!isMulti && hasRecords) {
                Ext.defer(me.collapse, 1, me);
            }

            if (isMulti || (hasRecords && me.picker)) {
                me.setValue(selectedRecords, false);
            }
            if (hasRecords) {
                me.fireEvent('select', me, selectedRecords);
            }
            me.inputEl.focus();
        }
    },

    onExpand: function() {
        this.callParent();
        var picker = this.getPicker();
        if (picker && picker.pagingToolbar) {
            picker.pagingToolbar.doLayout();
        }
    },

    setDisplayField: function(displayField) {
        this.displayField = displayField;
        this.displayTpl = new Ext.XTemplate(
            '<tpl for=".">',
                '{[typeof values === "string" ? values : values["' + this.displayField + '"]]}',
                '<tpl if="xindex < xcount">' + this.delimiter + '</tpl>',
            '</tpl>'
        );
    },

    // this is a patch for fixing bug with 'loading...' in 4.0.7
    createPicker: function() {
        var me = this,
            picker,
            menuCls = Ext.baseCSSPrefix + 'menu',
            opts = Ext.apply({
                pickerField: me,
                selModel: {
                    mode: me.multiSelect ? 'SIMPLE' : 'SINGLE'
                },
                floating: true,
                hidden: true,
                ownerCt: me.ownerCt,
                cls: me.el.up('.' + menuCls) ? menuCls : '',
                store: me.store,
                displayField: me.displayField,
                focusOnToFront: false,
                pageSize: me.pageSize,
                tpl: me.tpl,
                loadMask: me.queryMode !== 'local'
            }, me.listConfig, me.defaultListConfig);

        picker = me.picker = Ext.create('Ext.view.BoundList', opts);
        if (me.pageSize) {
            picker.pagingToolbar.on('beforechange', me.onPageChange, me);
        }

        me.mon(picker, {
            itemclick: me.onItemClick,
            refresh: me.onListRefresh,
            scope: me
        });

        me.mon(picker.getSelectionModel(), {
            'beforeselect': me.onBeforeSelect,
            'beforedeselect': me.onBeforeDeselect,
            'selectionchange': me.onListSelectionChange,
            scope: me
        });

        return picker;
    }
});

Ext.define('OPF.core.component.Date', {
    extend: 'Ext.form.field.Date',
    alias : 'widget.opf-datefield',

    cls: 'opf-date-field',

    mixins: {
        sublabelable: 'OPF.core.component.SubLabelable'
    },

    initRenderTpl: function() {
        var me = this;
        if (!me.hasOwnProperty('renderTpl')) {
            me.renderTpl = me.getTpl('subLabelableRenderTpl');
        }
        return me.callParent();
    },

    initRenderData: function() {
        return Ext.applyIf(this.callParent(), this.getSubLabelableRenderData());
    },

    getErrors: function(value) {
        var me = this,
            format = Ext.String.format,
            clearTime = Ext.Date.clearTime,
            errors = getSQErrors(this, value),
            disabledDays = me.disabledDays,
            disabledDatesRE = me.disabledDatesRE,
            minValue = me.minValue,
            maxValue = me.maxValue,
            len = disabledDays ? disabledDays.length : 0,
            i = 0,
            svalue,
            fvalue,
            day,
            time;

        value = me.formatDate(value || me.processRawValue(me.getRawValue()));

        if (value === null || value.length < 1) { // if it's blank and textfield didn't flag it then it's valid
             return errors;
        }

        svalue = value;
        value = me.parseDate(value);
        if (!value) {
            errors.push(format(me.invalidText, svalue, me.format));
            return errors;
        }

        time = value.getTime();
        if (minValue && time < clearTime(minValue).getTime()) {
            errors.push(format(me.minText, me.formatDate(minValue)));
        }

        if (maxValue && time > clearTime(maxValue).getTime()) {
            errors.push(format(me.maxText, me.formatDate(maxValue)));
        }

        if (disabledDays) {
            day = value.getDay();

            for(; i < len; i++) {
                if (day === disabledDays[i]) {
                    errors.push(me.disabledDaysText);
                    break;
                }
            }
        }

        fvalue = me.formatDate(value);
        if (disabledDatesRE && disabledDatesRE.test(fvalue)) {
            errors.push(format(me.disabledDatesText, fvalue));
        }

        return errors;
    },

    notNullValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotBlank(value)) {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be null';
            validateMessages.push(msg);
        }
    },

    getDisplayValue: function() {
        return this.formatDate(this.getValue());
    }
});

Ext.define('OPF.core.component.Time', {
    extend: 'Ext.form.field.Time',
    alias : 'widget.opf-timefield',

    cls: 'opf-time-field',

    mixins: {
        sublabelable: 'OPF.core.component.SubLabelable'
    },

    initRenderTpl: function() {
        var me = this;
        if (!me.hasOwnProperty('renderTpl')) {
            me.renderTpl = me.getTpl('subLabelableRenderTpl');
        }
        return me.callParent();
    },

    initRenderData: function() {
        return Ext.applyIf(this.callParent(), this.getSubLabelableRenderData());
    },

    getErrors: function(value) {
        var me = this,
            format = Ext.String.format,
            errors = getSQErrors(this, value),
            minValue = me.minValue,
            maxValue = me.maxValue,
            date;

        value = me.formatDate(value || me.processRawValue(me.getRawValue()));

        if (value === null || value.length < 1) { // if it's blank and textfield didn't flag it then it's valid
             return errors;
        }

        date = me.parseDate(value);
        if (!date) {
            errors.push(format(me.invalidText, value, me.format));
            return errors;
        }

        if (minValue && date < minValue) {
            errors.push(format(me.minText, me.formatDate(minValue)));
        }

        if (maxValue && date > maxValue) {
            errors.push(format(me.maxText, me.formatDate(maxValue)));
        }

        return errors;
    },

    notNullValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotBlank(value)) {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be null';
            validateMessages.push(msg);
        }
    },

    getDisplayValue: function() {
        return this.formatDate(this.getValue());
    }
});

Ext.define('OPF.core.component.File', {
    extend: 'Ext.form.field.File',
    alias : ['widget.opf-filefield', 'widget.opf-file'],

    cls: 'opf-text-field',

    allowBlank: false,
    msgTarget: 'side',
    emptyText: 'Select a file to upload',
    buttonConfig: {
        text: '',
        iconCls: 'upload-icon',
        cls: 'upload-btn',
        height: 28,
        width: 28
    },

    mixins: {
        sublabelable: 'OPF.core.component.SubLabelable'
    },

    initComponent: function() {
        this.regex = new RegExp('.*?\\.(' + this.fileTypes.join('|') + ')', 'i');
        this.regexText = 'Uploaded file has wrong extension.';

        this.callParent(arguments);
    },

    initRenderTpl: function() {
        var me = this;
        if (!me.hasOwnProperty('renderTpl')) {
            me.renderTpl = me.getTpl('subLabelableRenderTpl');
        }
        return me.callParent();
    },

    initRenderData: function() {
        return Ext.applyIf(this.callParent(), this.getSubLabelableRenderData());
    },

    getErrors: function(value) {
        return getSQErrors(this, value);
    }

});

Ext.define('OPF.core.component.ActionColumn', {
    extend: 'Ext.grid.column.Action',
    alias : 'widget.opf-actioncolumn',

    constructor: function(cfg) {
        var me = this,
            items = cfg.items || (me.items = [me]),
            i,
            item;

        OPF.core.component.ActionColumn.superclass.constructor.call(me, cfg);

//      Renderer closure iterates through items creating an <img> element for each and tagging with an identifying
//      class name x-action-col-{n}
        me.renderer = function(v, meta) {
//          Allow a configured renderer to create initial value (And set the other values in the "metadata" argument!)
            v = Ext.isFunction(cfg.renderer) ? cfg.renderer.apply(me, arguments)||'' : '';

            meta.tdCls += ' ' + Ext.baseCSSPrefix + 'action-col-cell';
            for (i = 0; i < items.length; i++) {
                item = items[i];
                item.disable = Ext.Function.bind(me.disableAction, me, [i]);
                item.enable = Ext.Function.bind(me.enableAction, me, [i]);
                if (Ext.isBoolean(item.visible) && item.visible) {
                    v += '<img alt="' + (item.altText || me.altText) + '" src="' + (item.icon || Ext.BLANK_IMAGE_URL) +
                        '" class="' + Ext.baseCSSPrefix + 'action-col-icon ' + Ext.baseCSSPrefix + 'action-col-' + String(i) + ' ' + (item.disabled ? Ext.baseCSSPrefix + 'item-disabled' : ' ') + (item.iconCls || '') +
                        ' ' + (Ext.isFunction(item.getClass) ? item.getClass.apply(item.scope || me.scope || me, arguments) : (me.iconCls || '')) + '"' +
                        ((item.tooltip) ? ' data-qtip="' + item.tooltip + '"' : '') + ' />';
                }
            }
            return v;
        };
    }

});