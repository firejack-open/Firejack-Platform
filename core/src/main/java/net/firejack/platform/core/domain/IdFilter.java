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

import net.firejack.platform.core.utils.ArrayUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class IdFilter extends AbstractDTO {
	private static final long serialVersionUID = 7869372991577825321L;

	private Long[] grantedIdList;
    private Long[] deniedIdList;
    private boolean globalPermissionGranted = false;

    /**
     * @return
     */
    public Long[] getGrantedIdList() {
        return grantedIdList;
    }

    /**
     * @param grantedIdList
     */
    public void setGrantedIdList(Long[] grantedIdList) {
        this.grantedIdList = grantedIdList;
    }

    /**
     * @return
     */
    public Long[] getDeniedIdList() {
        return deniedIdList;
    }

    /**
     * @param deniedIdList
     */
    public void setDeniedIdList(Long[] deniedIdList) {
        this.deniedIdList = deniedIdList;
    }

    /**
     * @return
     */
    public boolean isGlobalPermissionGranted() {
        return globalPermissionGranted;
    }

    /**
     * @param globalPermissionGranted
     */
    public void setGlobalPermissionGranted(boolean globalPermissionGranted) {
        this.globalPermissionGranted = globalPermissionGranted;
    }

    /**
     * @param id
     */
    public void grantId(Long id) {
        if (id != null && (this.getGrantedIdList() == null ||
                !ArrayUtils.contains(this.getGrantedIdList(), id))) {
            List<Long> idList = this.getGrantedIdList() == null ? new ArrayList<Long>() :
                    new ArrayList<Long>(Arrays.asList(this.getGrantedIdList()));
            idList.add(id);
            this.setGrantedIdList(idList.toArray(new Long[idList.size()]));
        }
    }

	/**
	 *
	 * @param id
	 */
    public void denyId(Long id) {
        if (id != null && (this.getDeniedIdList() == null ||
                !ArrayUtils.contains(this.getDeniedIdList(), id))) {
            List<Long> idList = this.getDeniedIdList() == null ? new ArrayList<Long>() :
                    new ArrayList<Long>(Arrays.asList(this.getDeniedIdList()));
            idList.add(id);
            this.setDeniedIdList(idList.toArray(new Long[idList.size()]));
        }
    }

    public void denyIdList(List<Long> idListToDeny) {
        this.setDeniedIdList(ArrayUtils.append(this.getDeniedIdList(), idListToDeny, Long.class));
    }

}