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

package net.firejack.platform.core.config.meta.element.process;

import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.model.registry.process.CaseExplanationModel;


public class ExplanationElement extends PackageDescriptorElement<CaseExplanationModel> {

    private String shortDescription;
    private String longDescription;

    /**
     * @return
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * @param shortDescription
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * @return
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * @param longDescription
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @Override
    public Class<CaseExplanationModel> getEntityClass() {
        return CaseExplanationModel.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ExplanationElement that = (ExplanationElement) o;

        return !(longDescription != null ? !longDescription.equals(that.longDescription) : that.longDescription != null) &&
                !(shortDescription != null ? !shortDescription.equals(that.shortDescription) : that.shortDescription != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (shortDescription != null ? shortDescription.hashCode() : 0);
        result = 31 * result + (longDescription != null ? longDescription.hashCode() : 0);
        return result;
    }

}