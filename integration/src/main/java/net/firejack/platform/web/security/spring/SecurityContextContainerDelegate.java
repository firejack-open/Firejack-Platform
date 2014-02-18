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