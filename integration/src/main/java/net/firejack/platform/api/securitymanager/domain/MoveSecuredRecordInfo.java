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
import net.firejack.platform.core.domain.IdLookup;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlRootElement;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MoveSecuredRecordInfo extends AbstractDTO {
	private static final long serialVersionUID = 9220402144042442332L;

	private Long id;
    private String lookup;
    private IdLookup parent;
    private IdLookup[] oldParents;

    public MoveSecuredRecordInfo() {
    }

    public MoveSecuredRecordInfo(Long id, String lookup,
                                 IdLookup parent, IdLookup[] oldParents) {
        this.id = id;
        this.lookup = lookup;
        this.parent = parent;
        this.oldParents = oldParents;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public IdLookup getParent() {
        return parent;
    }

    public void setParent(IdLookup parent) {
        this.parent = parent;
    }

    public IdLookup[] getOldParents() {
        return oldParents;
    }

    public void setOldParents(IdLookup[] oldParents) {
        this.oldParents = oldParents;
    }

}