package net.firejack.platform.installation.processor.event;

import org.springframework.context.ApplicationEvent;

/**
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

public class TemplateEvent extends ApplicationEvent {
	private static final long serialVersionUID = 5621614614699721619L;
	protected String name;
	protected String path;
	protected String lookup;

	public TemplateEvent(Object source) {
		super(source);
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public boolean isModify() {
		return path != null && name != null;
	}
}
