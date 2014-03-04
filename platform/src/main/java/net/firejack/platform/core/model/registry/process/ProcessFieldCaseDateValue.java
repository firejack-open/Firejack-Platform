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
import java.util.Date;

/**
 * Class represents process field case value entity of Date type
 */
@Entity
@DiscriminatorValue(ProcessFieldType.DATE_CONST)
public class ProcessFieldCaseDateValue extends ProcessFieldCaseValue {

    private Date dateValue;

     /**
     * Gets Date value
     * @return process field value for a case
     */
    public Date getDateValue() {
        return dateValue;
    }

    /**
     * Sets Date value
     * @param dateValue process field value for a case
     */
    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    /**
     * Gets the value
     * @return Date value of the process field
     */
    @Override
    @Transient
    public Object getValue() {
        return getDateValue();
    }

    /**
     * Sets the value
     * @param value Date value of the process field
     */
    @Override
    public void setValue(Object value) {
        this.dateValue = (Date) value;
    }

}
