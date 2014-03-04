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

package net.firejack.platform.core.response;

import net.firejack.platform.core.domain.AbstractDTO;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@Component
@XmlRootElement(name = "model")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ServiceResponse<DTO extends AbstractDTO> extends AbstractDTO {

    private List<DTO> data;
    private String message;
    private boolean success;
	private Integer total;

    public ServiceResponse() {
    }

    public ServiceResponse(List<DTO> data) {
        this(data, "OK", true);
    }

    public ServiceResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public ServiceResponse(DTO data, String message, boolean success) {
        this.data = new ArrayList<DTO>();
        this.data.add(data);
        this.message = message;
        this.success = success;
    }

    public ServiceResponse(List<DTO> data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public ServiceResponse(List<DTO> data, String message, boolean success, Integer total) {
        this.data = data;
        this.message = message;
        this.success = success;
        this.total = total;
    }

    @XmlElementRef
	@XmlElementWrapper(name = "data")
    public List<DTO> getData() {
        return data;
    }

    public void setData(List<DTO> data) {
        this.data = data;
    }

    @XmlTransient
    @JsonIgnore
    public DTO getItem() {
        DTO item = null;
        List<DTO> list = this.getData();
        if (list != null && !list.isEmpty()) {
            item = list.get(0);
        }
        return item;
    }

    public void addItem(DTO item) {
        if (this.data == null) {
            this.data = new ArrayList<DTO>();
        }
        this.data.add(item);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}
}
