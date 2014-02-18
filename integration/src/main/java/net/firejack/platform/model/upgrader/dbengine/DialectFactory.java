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

package net.firejack.platform.model.upgrader.dbengine;

import net.firejack.platform.model.upgrader.dbengine.dialect.IDialect;
import net.firejack.platform.model.upgrader.dbengine.dialect.MySql5Dialect;

import javax.transaction.NotSupportedException;

public class DialectFactory {

    private static DialectFactory instance = new DialectFactory();

    private DialectFactory() {

    }

    /**
     * @return
     */
    public static DialectFactory getInstance() {
        if (instance == null) {
            instance = new DialectFactory();
        }
        return instance;
    }

    /**
     * @param dialectType
     * @return
     * @throws javax.transaction.NotSupportedException
     *
     */
    public IDialect getDialect(DialectType dialectType) throws NotSupportedException {
        IDialect dialect;
        switch (dialectType) {
            case MySQL5:
                dialect = new MySql5Dialect();
                break;
            default:
                throw new NotSupportedException("Dialect " + dialectType.name() + " doesn't support.");
        }
        return dialect;
    }

}
