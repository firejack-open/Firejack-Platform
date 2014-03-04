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
