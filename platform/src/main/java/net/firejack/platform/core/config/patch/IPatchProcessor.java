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