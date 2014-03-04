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


import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.validation.ValidationMessage;

import java.util.List;


public class ValidationException extends OpenFlameRuntimeException {
    private static final long serialVersionUID = 8439745178525754732L;

    private List<ValidationMessage> validationMessages;

    /**
     * @param validationMessages
     */
    public ValidationException(List<ValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * @return
     */
    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

}