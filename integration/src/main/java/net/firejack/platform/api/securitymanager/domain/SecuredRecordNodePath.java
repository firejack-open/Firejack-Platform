/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.api.securitymanager.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.utils.CollectionUtils;
import net.firejack.platform.core.utils.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;



@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SecuredRecordNodePath extends AbstractDTO {

    public static final String EMPTY_PATH = "['']";
    private static final String VAL_COLON = ":";
    private static final String VAL_QUOTE = "'";

    private List<Long> pathEntries;

    public List<Long> getPathEntries() {
        return pathEntries;
    }

    public void setPathEntries(List<Long> pathEntries) {
        this.pathEntries = pathEntries;
    }

    /**
     * @return
     */
    public String composePath() {
        return pathEntries == null ?
                VAL_QUOTE + StringUtils.join(this.pathEntries, VAL_COLON) + VAL_QUOTE :
                EMPTY_PATH;
    }

    /**
     * @param id
     * @return
     */
    public boolean contains(Long id) {
        boolean result = false;
        if (this.pathEntries != null) {
            for (Long secRecId : this.pathEntries) {
                if (secRecId.equals(id)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecuredRecordNodePath)) return false;

        SecuredRecordNodePath srPath = (SecuredRecordNodePath) o;

        return CollectionUtils.isEqualCollection(pathEntries, srPath.pathEntries);
    }

    @Override
    public int hashCode() {
        return pathEntries != null ? pathEntries.hashCode() : 0;
    }

    public static SecuredRecordNodePath parse(String path) {
        if (StringUtils.isBlank(path) || path.length() < 2) {
            throw new IllegalArgumentException();
        }
        path = path.substring(1, path.length() - 1); //remove single quotes
        List<Long> pathEntries;
        if (StringUtils.isBlank(path)) {
            pathEntries = null;
        } else {
            String[] parentIds = path.split(VAL_COLON); //split by colon
            pathEntries = new ArrayList<Long>(parentIds.length);
            for (String parentId : parentIds) {
                pathEntries.add(Long.parseLong(parentId));
            }
        }
        SecuredRecordNodePath nodePath = new SecuredRecordNodePath();
        nodePath.setPathEntries(pathEntries);
        return nodePath;
    }
}