Ext.define('OPF.core.component.form.ErrorHandler', {

    getValidatorErrors: function(value) {
        var me = this,
            errors = [],
            validator = me.validator,
            customValidator = me.customValidator,
            emptyText = me.emptyText,
            allowBlank = me.allowBlank,
            vtype = me.vtype,
            vtypes = Ext.form.field.VTypes,
            regex = me.regex,
            format = Ext.String.format,
            messages, trimmed;

        value = value || me.processRawValue(me.getRawValue());

        if (Ext.isFunction(customValidator)) {
            messages = customValidator.call(me, value);
            Ext.each(messages, function(message) {
                if (message) {
                    errors.push(message);
                }
            });
        }

        if (Ext.isFunction(validator)) {
            messages = validator.call(me, value);
            Ext.each(messages, function(message){
                errors.push(message);
            });
        }

        trimmed = me.allowOnlyWhitespace ? value : Ext.String.trim(value.toString());

        if (trimmed.length < 1 || (value === me.emptyText && me.valueContainsPlaceholder)) {
            if (!allowBlank) {
                errors.push(me.blankText);
            }

            return errors;
        }

        if (value.length < me.minLength) {
            errors.push(format(me.minLengthText, me.minLength));
        }

        if (value.length > me.maxLength) {
            errors.push(format(me.maxLengthText, me.maxLength));
        }

        if (vtype) {
            if(!vtypes[vtype](value, me)){
                errors.push(me.vtypeText || vtypes[vtype +'Text']);
            }
        }

        if (regex && !regex.test(value)) {
            errors.push(me.regexText || me.invalidText);
        }

        return errors;
    }

});