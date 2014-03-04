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

package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.registry.ProcessFieldType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Class represents process field case value entity of Long type
 */
@Entity
@DiscriminatorValue(ProcessFieldType.LONG_CONST)
public class ProcessFieldCaseLongValue extends ProcessFieldCaseValue {

    private Long longValue;

    /**
     * Gets Long value
     * @return process field value for a case
     */
    public Long getLongValue() {
        return longValue;
    }

    /**
     * Sets Long value
     * @param longValue process field value for a case
     */
    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    /**
     * Gets the value
     * @return Long value of the process field
     */
    @Override
    @Transient
    public Object getValue() {
        return getLongValue();
    }

    /**
     * Sets the value
     * @param value Long value of the process field
     */
    @Override
    public void setValue(Object value) {
        this.longValue = (Long) value;
    }

}
