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

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.statistics.domain.LogEntry;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.List;


public class ClientAppAPITest {

    private static final Logger logger = Logger.getLogger(ClientAppAPITest.class);

    @Test
    public void testAPIExternalUsage() {
        try {
            //------ first "transaction" ------//
            OPFEngine.init(OPFServiceTests.ADMIN_LOGIN, OPFServiceTests.ADMIN_PASSWORD);
            ServiceResponse<LogEntry> statisticsResponse = OPFEngine.StatisticsService.readAllLogEntries();
            checkResponseForConsistence(statisticsResponse);
            logger.info("logEntries.size() = " + statisticsResponse.getTotal());
            ServiceResponse<UserPermission> response = OPFEngine.AuthorityService.loadGrantedPermissions();
            checkResponseForConsistence(response);
            List<UserPermission> grantedPermissions = response.getData();
            logger.info("grantedPermissions.size() = " + (grantedPermissions == null ? 0 : grantedPermissions.size()));
            String currentSessionToken = OPFContext.getContext().getSessionToken();
            ServiceResponse<SimpleIdentifier<Boolean>> isActiveStatusResp =
                    OPFEngine.AuthorityService.isSessionTokenActive(currentSessionToken);
            checkResponseForConsistence(isActiveStatusResp);
            logger.info("Session " + currentSessionToken +
                    (isActiveStatusResp.getItem().getIdentifier() ? "is active." : "is not active."));
            OPFEngine.release();

            //------ second "transaction" ------//
            OPFEngine.init(OPFServiceTests.ADMIN_LOGIN, OPFServiceTests.ADMIN_PASSWORD);
            statisticsResponse = OPFEngine.StatisticsService.readAllLogEntries();
            checkResponseForConsistence(statisticsResponse);
            logger.info("logEntries.size() = " + statisticsResponse.getTotal());
            OPFEngine.release();
        } catch (Throwable e) {
            logger.info(e.getMessage(), e);
        }
    }

    private static void checkResponseForConsistence(ServiceResponse response) {
        if (response == null) {
            throw new BusinessFunctionException("API Service call should not be null.");
        } else if (!response.isSuccess()) {
            throw new BusinessFunctionException("API Service call has failure status.");
        }
    }
}