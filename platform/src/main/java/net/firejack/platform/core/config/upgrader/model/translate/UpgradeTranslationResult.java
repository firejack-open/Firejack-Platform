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

package net.firejack.platform.core.config.upgrader.model.translate;

import net.firejack.platform.core.config.translate.AbstractUpdateTranslationResult;
import net.firejack.platform.model.upgrader.bean.IUpgradeModel;
import net.firejack.platform.model.upgrader.operator.bean.DataSourceType;

import java.util.List;

/**
 *
 */
@SuppressWarnings("unused")
public class UpgradeTranslationResult extends AbstractUpdateTranslationResult<IUpgradeModel, DataSourceType> {

    private String name;
    private String path;
    private String fromVersion;
    private String toVersion;
    private String prefix;

    @Override
    protected DataSourceType transform(List<IUpgradeModel> upgradeModels) {
        DataSourceType packageType = new DataSourceType();
        packageType.getCreateTableOrModifyColumnOrAddColumn().addAll(upgradeModels);
        packageType.setName(name);
        packageType.setPath(path);
        packageType.setPrefix(prefix);
        return packageType;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @param fromVersion
     */
    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }

    /**
     * @param toVersion
     */
    public void setToVersion(String toVersion) {
        this.toVersion = toVersion;
    }

    /**
     * @param prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}