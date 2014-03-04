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

package net.firejack.platform.core.request;

import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.validation.annotation.ValidateNested;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@XmlRootElement(name = "model")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceRequest<DTO extends AbstractDTO> {

	@XmlElementRef
	@XmlElementWrapper(name = "data")
	private List<DTO> data;

	public ServiceRequest() {
	}

	public ServiceRequest(DTO data) {
		this.data = Arrays.asList(data);
	}

    public ServiceRequest(List<DTO> data) {
        this.data = data;
    }

	public DTO getData() {
		if (data != null && !data.isEmpty()) {
			return data.get(0);
		}
		return null;
	}

    @JsonProperty("data")
	public void setData(DTO data) {
		this.data = Arrays.asList(data);
	}

    @ValidateNested(iterable = true, excludePath = true)
    public List<DTO> getDataList() {
        return data;
    }

    @JsonProperty("dataList")
    public void setDataList(List<DTO> data) {
        this.data = data;
    }

}
