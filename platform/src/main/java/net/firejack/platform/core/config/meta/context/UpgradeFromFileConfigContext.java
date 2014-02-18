/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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