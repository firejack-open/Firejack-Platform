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

package net.firejack.platform.core.validation.exception;


import net.firejack.platform.core.exception.OpenFlameException;
import net.firejack.platform.core.validation.ValidationMessage;

import java.util.List;


public class RuleValidationException extends OpenFlameException {
    private static final long serialVersionUID = -4347134040179677934L;

    private List<ValidationMessage> validationMessages;

    /***/
    public RuleValidationException() {
        super();
    }

    /**
     * @param message
     */
    public RuleValidationException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public RuleValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public RuleValidationException(Throwable cause) {
        super(cause);
    }

    /**
     * @param validationMessages
     */
    public RuleValidationException(List<ValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * @return
     */
    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

}
