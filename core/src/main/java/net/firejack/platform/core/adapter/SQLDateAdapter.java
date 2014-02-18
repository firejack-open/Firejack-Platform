package net.firejack.platform.core.adapter;
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


import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.sql.Date;

public class SQLDateAdapter extends XmlAdapter<Long, Date> {

	@Override
	public Date unmarshal(Long v) throws Exception {
		return new Date(v);
	}

	@Override
	public Long marshal(Date v) throws Exception {
		return v.getTime();
	}
}
