package net.firejack.platform.core.model;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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

@javax.persistence.Entity
@Table(name = "opf_uid", uniqueConstraints = @UniqueConstraint(name = "uk_uid", columnNames = "uid"))
public class UID extends BaseEntityModel {
    private static final long serialVersionUID = -6934996570096701460L;

    private String uid;

    /***/
    public UID() {
    }

    /**
     * @param uid
     */
    public UID(String uid) {
        this.uid = uid;
    }

    /**
     * @param id
     */
    public UID(Long id) {
        super(id);
    }

    /**
     * @return
     */
    @Column(columnDefinition = "TINY_TEXT", nullable = false, updatable = false, unique = true)
    public String getUid() {
        return uid;
    }

    /**
     * @param uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
}
