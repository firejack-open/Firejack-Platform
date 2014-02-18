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

import net.firejack.platform.core.config.meta.INamedPackageDescriptorElement;
import net.firejack.platform.core.config.meta.diff.IPackageDescriptorElementDiff;
import net.firejack.platform.core.config.meta.element.authority.PermissionElement;
import net.firejack.platform.core.utils.StringUtils;

import java.util.Comparator;


public class DescriptorElementDiffComparator
        implements Comparator<IPackageDescriptorElementDiff<?, ? extends INamedPackageDescriptorElement>> {

    @Override
    public int compare(IPackageDescriptorElementDiff<?, ? extends INamedPackageDescriptorElement> diff1,
                       IPackageDescriptorElementDiff<?, ? extends INamedPackageDescriptorElement> diff2) {
        int result = 0;
        if (diff1.getType() == diff2.getType()) {
            String path1 = diff1.getDiffTarget().getPath();
            String path2 = diff2.getDiffTarget().getPath();
            INamedPackageDescriptorElement element1 = diff1.getDiffTarget();
            INamedPackageDescriptorElement element2 = diff2.getDiffTarget();
            if (element1 instanceof PermissionElement ^ element2 instanceof PermissionElement) {
                return element1 instanceof PermissionElement ? 1 : -1;
            }
            if (StringUtils.isNotBlank(path1) && StringUtils.isNotBlank(path2)) {
                path1 = path1.toLowerCase();
                path2 = path2.toLowerCase();
                if (path1.contains(path2) && !path2.contains(path1)) {
                    result = 1;
                } else if (path2.contains(path1) && !path1.contains(path2)) {
                    result = -1;
                }
            } else if (StringUtils.isBlank(path1) && StringUtils.isNotBlank(path2)) {
                result = 1;
            } else if (StringUtils.isNotBlank(path1) && StringUtils.isBlank(path2)) {
                result = -1;
            }
        } else {
            result = diff2.getType().compareTo(diff1.getType());
        }
        return result;
    }
}