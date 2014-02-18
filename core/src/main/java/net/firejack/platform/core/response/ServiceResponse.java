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
