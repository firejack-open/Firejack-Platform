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

package net.firejack.platform.core.config.translate;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractTranslationResult<R> implements ITranslationResult<R> {

    private List<TranslationError> errorList;

    @Override
    public TranslationError[] getTranslationErrors() {
        return errorList == null ? new TranslationError[0] :
                getErrorList().toArray(new TranslationError[getErrorList().size()]);
    }

    /**
     * @param errorCode
     * @param errorMessage
     */
    public void addError(int errorCode, String errorMessage) {
        getErrorList().add(new TranslationError(errorCode, errorMessage));
    }

    protected List<TranslationError> getErrorList() {
        if (errorList == null) {
            errorList = new ArrayList<TranslationError>();
        }
        return errorList;
    }
}