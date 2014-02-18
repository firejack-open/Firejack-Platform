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

package net.firejack.platform.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "opf_lookup", uniqueConstraints = @UniqueConstraint(name = "uk_hash", columnNames = "hash"))
public class Lookup extends BaseEntityModel {
    private static final long serialVersionUID = -8439075688214700674L;
    private String hash;

    /***/
    public Lookup() {
    }

    /**
     * @param hash
     */
    public Lookup(String hash) {
        this.hash = hash;
    }

    /**
     * @return
     */
    @Column(columnDefinition = "TINY_TEXT", nullable = false)
    public String getHash() {
        return hash;
    }

    /**
     * @param hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }
}
