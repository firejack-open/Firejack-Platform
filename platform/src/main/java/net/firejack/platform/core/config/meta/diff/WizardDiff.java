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

package net.firejack.platform.core.config.meta.diff;

import net.firejack.platform.core.config.meta.IPackageDescriptorElement;
import net.firejack.platform.core.config.meta.construct.WizardElement;


@SuppressWarnings("unused")
public class WizardDiff extends PackageDescriptorElementDiff
        <IPackageDescriptorElement, WizardElement> {

    /**
     * @param added
     * @param wizardElement
     */
    public WizardDiff(Boolean added, WizardElement wizardElement) {
        super(added, wizardElement);
    }

    /**
     * @param oldWizard
     * @param newWizard
     */
    public WizardDiff(WizardElement oldWizard, WizardElement newWizard) {
        super(oldWizard, newWizard);
    }

}