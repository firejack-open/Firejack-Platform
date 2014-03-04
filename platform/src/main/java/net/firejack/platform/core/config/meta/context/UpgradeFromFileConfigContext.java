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

import net.firejack.platform.core.config.meta.exception.ConfigSourceException;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import java.io.File;


public class UpgradeFromFileConfigContext implements IUpgradeConfigContext<File> {

    private static final Logger logger = Logger.getLogger(UpgradeFromFileConfigContext.class);
    private String configLocation;

    /**
     * @param configLocation
     */
    public UpgradeFromFileConfigContext(String configLocation) {
        this.configLocation = configLocation;
    }

    @Override
    public File getConfigSource() throws ConfigSourceException {
        if (StringUtils.isBlank(configLocation)) {
            logger.info("Failed to load config file from specified location. The reason is - specified config location parameter is empty");
            throw new ConfigSourceException("Couldn't load file from blank config location.");
        }
        File configFile = new File(configLocation);
        if (configFile.exists()) {
            return configFile;
        }
        logger.info("Failed to load config file from specified location. The reason is - specified file doesn't exist.");
        throw new ConfigSourceException("Couldn't load config file: file doesn't exist.");
    }

    @Override
    public Object[] getParameters() {
        return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
}