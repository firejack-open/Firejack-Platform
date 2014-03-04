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


import net.firejack.platform.core.model.registry.domain.PackageModel;

public class StatusProviderTranslationResult extends AbstractTranslationResult<Boolean> {

    private Boolean successStatus;
    private PackageModel _package;
    private Integer versionNumber;
    private String oldPackageXml;

    @Override
    public Boolean getResult() {
        return getErrorList().isEmpty() && (successStatus == null || successStatus);
    }

    /**
     * @param successStatus
     */
    public void setSuccessStatus(Boolean successStatus) {
        this.successStatus = successStatus;
    }

    /**
     * @return
     */
    public PackageModel getPackage() {
        return _package;
    }

    /**
     * @param _package
     */
    public void setPackage(PackageModel _package) {
        this._package = _package;
    }

    /**
     * @return
     */
    public Integer getVersionNumber() {
        return versionNumber;
    }

    /**
     * @param versionNumber
     */
    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * @return
     */
    public String getOldPackageXml() {
        return oldPackageXml;
    }

    /**
     * @param oldPackageXml
     */
    public void setOldPackageXml(String oldPackageXml) {
        this.oldPackageXml = oldPackageXml;
    }
}