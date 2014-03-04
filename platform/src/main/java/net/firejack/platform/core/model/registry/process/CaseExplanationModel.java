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

package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.UIDModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;


@Entity
@Table(name = "opf_case_explanation")
public class CaseExplanationModel extends UIDModel {
    private static final long serialVersionUID = 821989076672189863L;

    private ProcessModel process;

    private String shortDescription;

    private String longDescription;


    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_process")
    @ForeignKey(name = "FK_PROCESS_CASE_EXPLANATION")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ProcessModel getProcess() {
        return process;
    }

    /**
     * @param process
     */
    public void setProcess(ProcessModel process) {
        this.process = process;
    }

    /**
     * @return
     */
    @Column(name = "short_description", length = 255)
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * @param shortDescription
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * @return
     */
    @Column(name = "long_description", columnDefinition = "MEDIUMTEXT")
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * @param longDescription
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

}
