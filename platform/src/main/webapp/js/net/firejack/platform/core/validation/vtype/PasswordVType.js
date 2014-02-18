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

Ext.apply(Ext.form.field.VTypes, {
    password: function(value, field) {
        var valid = false;
        if (field.isDisabled() && otherField.isDisabled()) {
            valid = true;
        } else {
            if (field.compareWithId) {
                var otherField = Ext.getCmp(field.compareWithId);
                var otherValue = otherField.getValue();
                if (field.updateMode && otherField.updateMode && OPF.isBlank(value) && OPF.isBlank(otherValue)) {
                    valid = true;
                } else {
                    if (value == otherValue) {
                        if (value.match(/[0-9!@#\$%\^&\*\(\)\-_=\+]+/i)) {
                            otherField.clearInvalid();
                            if(value.length >= 6){
                                valid = true;
                            } else {
                                this.passwordText = 'Password should have length equal or longer than 6 characters.';
                                valid = false;
                            }
                        } else {
                            this.passwordText = 'Passwords must contain at least one number, or valid special character (!@#$%^&*{}-_=+)';
                            valid = false;
                        }
                    } else {
                        this.passwordText = 'The passwords entered do not match.';
                        valid = false;
                    }
                }
            }
        }
        return valid;
    },
    passwordText: 'Passwords must contain at least one number, or valid special character (!@#$%^&*{}-_=+)'
});