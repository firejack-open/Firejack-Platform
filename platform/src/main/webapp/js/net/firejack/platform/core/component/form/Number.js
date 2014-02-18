/*
 * GNU General Public License Usage
 * This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 *
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @description: This class provide aditional format to numbers by extending Ext.form.field.Number
 *
 * @author: Greivin Britton
 * @email: brittongr@gmail.com
 * @version: 2 compatible with ExtJS 4
 */
Ext.define('OPF.core.component.form.Number', {
    extend: 'Ext.form.field.Number',
    alias: ['widget.opf-form-number', 'widget.opf-number'],

    currencySymbol: null,
    useThousandSeparator: true,
    thousandSeparator: ',',
    alwaysDisplayDecimals: false,
    cls: 'opf-number-field',

    mixins: {
        subLabelable: 'OPF.core.component.form.SubLabelable',
        errorHandler: 'OPF.core.component.form.ErrorHandler'
    },

    initComponent: function() {
        if (this.useThousandSeparator && this.decimalSeparator == ',' && this.thousandSeparator == ',')
            this.thousandSeparator = '.';
        else
        if (this.allowDecimals && this.thousandSeparator == '.' && this.decimalSeparator == '.')
            this.decimalSeparator = ',';

        this.initSubLabelable();
        this.callParent(arguments);
    },

    getErrors: function(value) {
        var number = this.removeFormat(value);
        return this.getValidatorErrors(number);
    },

    setValue: function(value) {
        OPF.core.component.form.Number.superclass.setValue.call(this, value != null ? value.toString().replace('.', this.decimalSeparator) : value);

        this.setRawValue(this.getFormattedValue(this.getValue()));
    },

    getFormattedValue: function(value) {
        return OPF.core.component.form.Number.formattedValue(value, this);
    },

    /**
     * overrides parseValue to remove the format applied by this class
     */
    parseValue: function(value) {
        //Replace the currency symbol and thousand separator
        return OPF.core.component.form.Number.superclass.parseValue.call(this, this.removeFormat(value));
    },

    /**
     * Remove only the format added by this class to let the superclass validate with it's rules.
     * @param {Object} value
     */
    removeFormat: function(value) {
        if (Ext.isEmpty(value)) {
            return null;
        } else if (!this.hasFormat())
            return value;
        else {
            value = value.toString().replace(this.currencySymbol + ' ', '');

            value = this.useThousandSeparator ? value.replace(new RegExp('[' + this.thousandSeparator + ']', 'g'), '') : value;

            return value;
        }
    },

    hasFormat: function() {
        return OPF.core.component.form.Number.hasFormat(this.getRawValue(), this);
    },

    /**
     * Display the numeric value with the fixed decimal precision and without the format using the setRawValue, don't need to do a setValue because we don't want a double
     * formatting and process of the value because beforeBlur perform a getRawValue and then a setValue.
     */
    onFocus: function() {
        this.setRawValue(this.removeFormat(this.getRawValue()));

        this.callParent(arguments);
    },

    notNullValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotBlank(value)) {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be null';
            validateMessages.push(msg);
        }
    }
});

OPF.core.component.form.Number.formattedValue = function(value, cfg) {
    if (Ext.isEmpty(value) || !OPF.core.component.form.Number.hasFormat(value, cfg))
        return value;
    else {
        var neg = null;

        value = (neg = value < 0) ? value * -1 : value;
        value = cfg.allowDecimals && cfg.alwaysDisplayDecimals ? value.toFixed(cfg.decimalPrecision) : value;

        if (cfg.useThousandSeparator) {
            if (cfg.useThousandSeparator && Ext.isEmpty(cfg.thousandSeparator))
                throw ('NumberFormatException: invalid thousandSeparator, property must has a valid character.');

            if (cfg.thousandSeparator == cfg.decimalSeparator)
                throw ('NumberFormatException: invalid thousandSeparator, thousand separator must be different from decimalSeparator.');

            value = value.toString();

            var ps = value.split('.');
            ps[1] = ps[1] ? ps[1] : null;

            var whole = ps[0];

            var r = /(\d+)(\d{3})/;

            var ts = cfg.thousandSeparator;

            while (r.test(whole))
                whole = whole.replace(r, '$1' + ts + '$2');

            value = whole + (ps[1] ? cfg.decimalSeparator + ps[1] : '');
        }

        return Ext.String.format('{0}{1}{2}', (neg ? '-' : ''), (Ext.isEmpty(cfg.currencySymbol) ? '' : cfg.currencySymbol + ' '), value);
    }
};

OPF.core.component.form.Number.hasFormat = function(rawValue, cfg) {
    return cfg.decimalSeparator != '.' || (cfg.useThousandSeparator && rawValue != null) || !Ext.isEmpty(cfg.currencySymbol) || cfg.alwaysDisplayDecimals;
};
