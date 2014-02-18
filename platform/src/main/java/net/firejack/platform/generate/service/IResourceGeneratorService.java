package net.firejack.platform.generate.service;

import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.generate.beans.web.api.Api;
import net.firejack.platform.generate.structure.Structure;

import java.io.IOException;
import java.io.InputStream;

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

public interface IResourceGeneratorService {
    /**
     * @param descriptor
     * @param api
     * @param structure
     * @param version
     * @throws java.io.IOException
     */
    void generateResource(IPackageDescriptor descriptor, Api api, Structure structure, PackageModel version) throws Exception;

    /**
     * @param structure
     * @throws java.io.IOException
     */
    void unPackageResources(Structure structure) throws IOException;

    void copyJar(IPackageDescriptor descriptor, InputStream stream, Structure structure) throws Exception;

    /**
     * @param structure
     * @param commands
     * @param version
     * @throws Exception
     */
    void buildMavenProject(Structure structure, String commands, PackageModel version) throws Exception;

    /**
     * @param version
     * @throws java.io.IOException
     */
    void generateOFR(PackageModel version) throws IOException;

    /**
     * @param descriptor
     * @return
     * @throws java.io.IOException
     */
    Structure createTempProject(IPackageDescriptor descriptor) throws IOException;

    void generateUpgrade(PackageModel model) throws InterruptedException;
}
