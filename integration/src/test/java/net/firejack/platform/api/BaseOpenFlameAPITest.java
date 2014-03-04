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