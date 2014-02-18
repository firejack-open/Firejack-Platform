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

package net.firejack.platform.core.config.translate;


import net.firejack.platform.core.model.registry.domain.PackageModel;

public class StatusProviderTranslationResult extends AbstractTranslationResult<Boolean> {

    private Boolean successStatus;
    private PackageModel _package;
    private Integer versionNumber;
    private String oldPackageXml;

    @Override
    public Boolean getResult() {
        return getErrorList().isEmpty() && (successStatus == null || successStatus);
    }

    /**
     * @param successStatus
     */
    public void setSuccessStatus(Boolean successStatus) {
        this.successStatus = successStatus;
    }

    /**
     * @return
     */
    public PackageModel getPackage() {
        return _package;
    }

    /**
     * @param _package
     */
    public void setPackage(PackageModel _package) {
        this._package = _package;
    }

    /**
     * @return
     */
    public Integer getVersionNumber() {
        return versionNumber;
    }

    /**
     * @param versionNumber
     */
    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * @return
     */
    public String getOldPackageXml() {
        return oldPackageXml;
    }

    /**
     * @param oldPackageXml
     */
    public void setOldPackageXml(String oldPackageXml) {
        this.oldPackageXml = oldPackageXml;
    }
}