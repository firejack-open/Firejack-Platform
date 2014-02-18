package net.firejack.platform.service.deployment.broker;
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


import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@TrackDetails
@Component
public class RestartBroker extends ServiceBroker<ServiceRequest, ServiceResponse> {

    @Override
    protected ServiceResponse perform(ServiceRequest request) throws Exception {
        logger.info("###########################################################################");
        logger.info("############################## RESTART ####################################");
        logger.info("###########################################################################");

        Process process = Runtime.getRuntime().exec(new String[]{"service", "firejack", "selfrestart"});
        print(process.getInputStream());
        print(process.getErrorStream());

        return new ServiceResponse("successfully", true);
    }

    private void print(final InputStream stream) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                try {
                    while (stream.read(buffer) != -1) {
                        logger.info(new String(buffer).trim());
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        thread.start();
    }
}
