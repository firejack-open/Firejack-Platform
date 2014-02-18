/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.api;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;


public class BaseOpenFlameAPITest {

    private static final Logger logger = Logger.getLogger(BaseOpenFlameAPITest.class);

    protected Long time;

    protected TestBusinessContext context;

    @Before
    public void setUp() {
        time = System.currentTimeMillis();
        if (isAuthenticationRequired()) {
            context = new TestBusinessContext();
            context.prepareContext(OPFServiceTests.ADMIN_LOGIN, OPFServiceTests.ADMIN_PASSWORD);
        }
    }

    @After
    public void tearDown() {
        if (isAuthenticationRequired()) {
            if (context != null) {
                context.releaseContext();
            }
        }
        logger.info("Test execution time in milliseconds: " + (System.currentTimeMillis() - time));
    }

    protected boolean isAuthenticationRequired() {
        return true;
    }

}