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