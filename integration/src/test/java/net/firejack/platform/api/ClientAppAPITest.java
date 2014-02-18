/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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