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