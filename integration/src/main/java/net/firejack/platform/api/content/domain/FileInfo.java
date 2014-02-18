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

package net.firejack.platform.api.content.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FileInfo extends AbstractDTO {
    private static final long serialVersionUID = -3953003610004177089L;

    private String filename;
    private Long updated;
    private InputStream stream;
	private byte[] data;
    private String orgFilename;

    /***/
    public FileInfo() {
    }

    public FileInfo(String filename) {
        this.filename = filename;
    }

    /**
     * @param filename
     * @param updated
     */
    public FileInfo(String filename, Long updated) {
        this.filename = filename;
        this.updated = updated;
    }

	public FileInfo(String filename, InputStream stream) {
		this.filename = filename;
		this.stream = stream;
	}

	/**
     * @return
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return
     */
    public Long getUpdated() {
        return updated;
    }

    /**
     * @param updated
     */
    public void setUpdated(Long updated) {
        this.updated = updated;
    }

	@XmlTransient
    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

	@JsonIgnore
	public byte[] getData() throws IOException {
		if (data == null && stream != null) {
			data = IOUtils.toByteArray(stream);
		}
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
		this.stream = new ByteArrayInputStream(data);
	}

	/**
     * @return
     */
    public String getOrgFilename() {
        return orgFilename;
    }

    /**
     * @param orgFilename
     */
    public void setOrgFilename(String orgFilename) {
        this.orgFilename = orgFilename;
    }
}
