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

package net.firejack.platform.core.config.patch;

import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.diff.IElementDiffInfoContainer;
import net.firejack.platform.core.config.meta.exception.PatchExecutionException;
import net.firejack.platform.core.config.patch.listener.IPackagePatchListener;
import net.firejack.platform.core.config.translate.IPackageDescriptorTranslator;

import java.io.File;
import java.io.InputStream;

public interface IPatchProcessor<R> {

    /**
     * @param configLocation
     * @return
     */
    IPackageDescriptor loadVersionPackage(String configLocation);

	IPackageDescriptor loadVersionPackage(InputStream stream);

    /**
     * @param configLocation
     * @return
     */
    IPackageDescriptor loadVersionPackage(File configLocation);

    /**
     * @param oldPackageDescriptor
     * @param newPackageDescriptor
     * @return
     */
    IElementDiffInfoContainer processDifferences(IPackageDescriptor oldPackageDescriptor, IPackageDescriptor newPackageDescriptor);

    /**
     * @param patch
     * @param patchContext
     */
    void applyPatch(IElementDiffInfoContainer patch, IPatchContext patchContext);

    /**
     * @param patch
     * @param patchContext
     * @return
     * @throws net.firejack.platform.core.config.meta.exception.PatchExecutionException
     *
     */
    R generatePatch(IElementDiffInfoContainer patch, IPatchContext patchContext) throws PatchExecutionException;

    /**
     * @param translator
     */
    void addAdditionalUpdateTranslator(IPackageDescriptorTranslator<R> translator);

    /**
     * @param listener
     */
    void addPatchListener(IPackagePatchListener listener);

    /**
     * @param listener
     */
    void removePatchListener(IPackagePatchListener listener);

}