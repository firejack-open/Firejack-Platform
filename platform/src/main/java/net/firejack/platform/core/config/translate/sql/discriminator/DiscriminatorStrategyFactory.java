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

package net.firejack.platform.core.config.translate.sql.discriminator;

import java.util.Map;


public class DiscriminatorStrategyFactory {

    private static DiscriminatorStrategyFactory instance;

    private DiscriminatorStrategyFactory() {
    }

    /**
     * @param type
     * @param predefinedValues
     * @return
     */
    public IDiscriminatorColumnStrategy populateDefaultDiscriminatorStrategy(DiscriminatorType type, Map<String, String> predefinedValues) {
        if (type == DiscriminatorType.STRING) {
            return new DefaultStringDiscriminatorColumnStrategy(predefinedValues);
        } else {
            throw new UnsupportedOperationException("Discriminator columns of type [" + type + " does not supported.");
        }
    }

    /**
     * @return
     */
    public IDiscriminatorColumnStrategy populateFullNameDiscriminatorStrategy() {
        return new FullNameDiscriminatorColumnStrategy();
    }

    /**
     * @return
     */
    public static DiscriminatorStrategyFactory getInstance() {
        if (instance == null) {
            instance = new DiscriminatorStrategyFactory();
        }
        return instance;
    }
}