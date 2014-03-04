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

package net.firejack.platform.core.config.meta.context;

import java.io.File;
import java.io.InputStream;


public final class ConfigContextFactory {

    private static ConfigContextFactory INSTANCE;

    private ConfigContextFactory() {
    }

    /**
     * @param configLocation
     * @return
     */
    public IUpgradeConfigContext<File> buildContext(String configLocation) {
        return new UpgradeFromFileConfigContext(configLocation);
    }

    public IUpgradeConfigContext<InputStream> buildContext(InputStream stream) {
        return new UpgradeStreamConfigContext(stream);
    }

    /**
     * @param objectToWrap
     * @param parameters
     * @return
     */
    public <T> IUpgradeConfigContext<T> buildWrapperContext(T objectToWrap, Object... parameters) {
        return new UpgradeFromObjectConfigContext<T>(objectToWrap, parameters);
    }

    /**
     * @param path
     * @param dbName
     * @return
     */
    public IUpgradeConfigContext<String> buildContextForInitialAccount(String path, String dbName) {
        return new UpgradeFromObjectConfigContext<String>(dbName, path);
    }

    /**
     * @return
     */
    public static ConfigContextFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigContextFactory();
        }
        return INSTANCE;
    }
}