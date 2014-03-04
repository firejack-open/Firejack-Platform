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