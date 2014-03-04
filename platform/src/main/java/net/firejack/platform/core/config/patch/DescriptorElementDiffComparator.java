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