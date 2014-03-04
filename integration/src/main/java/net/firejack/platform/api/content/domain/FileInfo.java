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
