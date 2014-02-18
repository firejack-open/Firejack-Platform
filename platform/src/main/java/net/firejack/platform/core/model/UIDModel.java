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
package net.firejack.platform.core.model;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;


@MappedSuperclass
public class UIDModel extends BaseEntityModel implements IUIDModel {
	private static final long serialVersionUID = 3658348401095399673L;

	private UID uid;

	public UIDModel() {
	}

	public UIDModel(Long id) {
		super(id);
	}

	@XmlTransient
	@OneToOne(fetch = FetchType.LAZY,cascade = {CascadeType.REMOVE})
	@ForeignKey(name = "fk_uid")
	@JoinColumn(name = "id_uid", updatable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	public UID getUid() {
		return uid;
	}

	public void setUid(UID uid) {
		this.uid = uid;
	}

}
