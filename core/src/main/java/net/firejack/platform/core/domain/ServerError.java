/**
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
package net.firejack.platform.core.domain;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ServerError extends AbstractDTO {
	private static final long serialVersionUID = 2715376710532590969L;

	private String id;
	private String msg;
	private String name;

	/***/
	public ServerError() {
	}

	/**
	 * @param id
	 * @param msg
	 */
	public ServerError(String id, String msg) {
		this.id = id;
		this.msg = msg;
	}

	/**
	 * @param id
	 * @param msg
	 * @param name
	 */
	public ServerError(String id, String msg, String name) {
		this.id = id;
		this.msg = msg;
		this.name = name;
	}

	/** @return  */
	public String getId() {
		return id;
	}

	/** @param id  */
	public void setId(String id) {
		this.id = id;
	}

	/** @return  */
	public String getMsg() {
		return msg;
	}

	/** @param msg  */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/** @return  */
	public String getName() {
		return name;
	}

	/** @param name  */
	public void setName(String name) {
		this.name = name;
	}

}
