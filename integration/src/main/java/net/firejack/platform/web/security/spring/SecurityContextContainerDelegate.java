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

package net.firejack.platform.web.security.spring;

import net.firejack.platform.web.security.exception.AuthenticationException;
import net.firejack.platform.web.security.model.context.IContextContainerDelegate;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.navigation.INavElementContainerFactory;
import net.firejack.platform.web.security.permission.DummyPermissionContainer;
import net.firejack.platform.web.security.permission.IPermissionContainer;
import net.firejack.platform.web.security.permission.IPermissionContainerFactory;
import net.firejack.platform.web.security.resource.IResourceLocationContainerFactory;
import net.firejack.platform.web.security.spring.token.AuthenticationToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;

public class SecurityContextContainerDelegate implements IContextContainerDelegate {

    private static final Logger logger = Logger.getLogger(SecurityContextContainerDelegate.class);
    private IPermissionContainerFactory permissionContainerFactory;
    private IResourceLocationContainerFactory resourceLocationContainerFactory;
    private INavElementContainerFactory navElementContainerFactory;

    /**
     * @param permissionContainerFactory
     */
    @Required
    public void setPermissionContainerFactory(IPermissionContainerFactory permissionContainerFactory) {
        this.permissionContainerFactory = permissionContainerFactory;
    }

    /**
     * @param resourceLocationContainerFactory
     */
    @Required
    public void setResourceLocationContainerFactory(IResourceLocationContainerFactory resourceLocationContainerFactory) {
        this.resourceLocationContainerFactory = resourceLocationContainerFactory;
    }

    /**
     * @param navElementContainerFactory
     */
    @Required
    public void setNavElementContainerFactory(INavElementContainerFactory navElementContainerFactory) {
        this.navElementContainerFactory = navElementContainerFactory;
    }

    /***/
    @PostConstruct
    public void initializeContext() {
        logger.info("Trying to initialize business context factory...");
        OPFContext.initContextContainerDelegate(this);
        logger.info("Initialization of context container delegate was successful");
        OPFContext.initPermissionContainerFactory(this.permissionContainerFactory);
        logger.info("Initialization of permission container factory was successful");
        OPFContext.initResourceLocationContainerFactory(this.resourceLocationContainerFactory);
        logger.info("Initialization of masked resource container factory was successful");
        OPFContext.initNavElementContainerFactory(this.navElementContainerFactory);
        logger.info("Initialization of navigation element container factory was successful");
    }

    @Override
    public void saveContext(OPFContext context) throws AuthenticationException {
        logger.info("Trying to save business context...");
        getAuthentication().setDetails(context);
        logger.info("Business context was saved successfully...");
    }

    @Override
    public OPFContext retrieveContext() throws AuthenticationException {
        logger.info("Trying to retrieve business context...");
        UsernamePasswordAuthenticationToken authentication = getAuthentication();
        OPFContext context;
        if (authentication.getDetails() != null && authentication.getDetails() instanceof OPFContext) {
            context = (OPFContext) authentication.getDetails();
            logger.info("Business context was retrieved successfully.");
        } else {
            context = null;
            logger.warn("Failed to retrieve business context from security context.");
        }
        return context;
    }

    @Override
    public boolean contextInitialized() {
        return true;
    }

    private UsernamePasswordAuthenticationToken getAuthentication() throws AuthenticationException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return (UsernamePasswordAuthenticationToken) authentication;
        } else if (authentication instanceof AnonymousAuthenticationToken) {
            IPermissionContainer permissionContainer = new DummyPermissionContainer();
            return new AuthenticationToken(
                    authentication.getPrincipal(),
                    authentication.getCredentials(),
                    authentication.getAuthorities(),
                    permissionContainer);
        } else {
            throw new AuthenticationException("Inappropriate authentication object");
        }
    }
}