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

package net.firejack.platform.api.server;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.core.response.ServiceResponse;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
public class RemoteDeploymentServiceTest {

    private static final Logger logger = Logger.getLogger(RemoteDeploymentServiceTest.class);

    @Test
    public void testRemoteDeploy() {
        OPFEngine.init("admin", "123123");
        IRemoteDeploymentService deploymentService = OPFEngine.getRemoteProxy(
                //"http://192.168.0.102:8080/platform", IDeploymentService.class);
                "http://localhost:8081/opf-server", IRemoteDeploymentService.class);
        String logUUID = UUID.randomUUID().toString();
        InputStream resourceStream = getClass().getResourceAsStream("/sample.ofr");
        if (resourceStream == null) {
            logger.error("Failed to load sample.ofr file");
        } else {
            logger.info("Trying to install sample.ofr to remote tomcat.");
            ServiceResponse response = deploymentService.installPackageArchive(
                    resourceStream, logUUID, "json", true);
            Assert.assertTrue("Remote Installation failed.", response.isSuccess());
            logger.info("Sample application installed successfully. Trying to read logs...");
        }
        OPFEngine.release();
    }

    @Test
    public void testRemoteUnDeploy() {
        OPFEngine.init("admin", "123123");

        IRemoteDeploymentService deploymentService = OPFEngine.getRemoteProxy(
                "http://localhost:8081/opf-server", IRemoteDeploymentService.class);
        ServiceResponse response = deploymentService.uninstallPackageArchive("sample");
        logger.info((response.isSuccess() ?
                "Uninstall operation completed successfully. Response message: " :
                "Uninstall operation failed. Response message: ") +
                response.getMessage());
        logger.info("Uninstall operation failed. Response message: " + response.getMessage());

        OPFEngine.release();
    }
}