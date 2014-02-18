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
