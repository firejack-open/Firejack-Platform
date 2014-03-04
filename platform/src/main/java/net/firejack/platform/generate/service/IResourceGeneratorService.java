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

package net.firejack.platform.generate.service;

import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.generate.beans.web.api.Api;
import net.firejack.platform.generate.structure.Structure;

import java.io.IOException;
import java.io.InputStream;

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
