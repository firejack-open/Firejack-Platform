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

package net.firejack.platform.model.wsdl.bean;


import javax.jws.WebParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private Class type;
    @XmlAttribute
    private boolean bean;
    @XmlAttribute
    private boolean list;
    @XmlAttribute
    private boolean holder;
    @XmlAttribute
    private WebParam.Mode mode;

    public Parameter() {
    }

    public Parameter(String name, Class type, boolean bean, boolean list, boolean holder, WebParam.Mode mode) {
        this.name = name;
        this.type = type;
        this.bean = bean;
        this.list = list;
        this.holder = holder;
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public boolean isBean() {
        return bean;
    }

    public void setBean(boolean bean) {
        this.bean = bean;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public boolean isHolder() {
        return holder;
    }

    public void setHolder(boolean holder) {
        this.holder = holder;
    }

    public WebParam.Mode getMode() {
        return mode;
    }

    public void setMode(WebParam.Mode mode) {
        this.mode = mode;
    }
}
