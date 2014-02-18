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

import net.firejack.platform.core.annotation.Property;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlRootElement;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AudioResourceVersion extends AbstractResourceVersion {
    private static final long serialVersionUID = 382857741440959127L;

    @Property(name = "temporaryFilename")
    private String resourceFileTemporaryName;
    @Property(name = "originalFilename")
    private String resourceFileOriginalName;


    public String getStoredFilename() {
        String storedFileName = null;
        if (getId() != null) {
            storedFileName = getId() + "_" + getVersion() + "_" + getCulture().name();
        }
        return storedFileName;
    }

    public String getResourceFileTemporaryName() {
        return resourceFileTemporaryName;
    }

    public void setResourceFileTemporaryName(String resourceFileTemporaryName) {
        this.resourceFileTemporaryName = resourceFileTemporaryName;
    }

    public String getResourceFileOriginalName() {
        return resourceFileOriginalName;
    }

    public void setResourceFileOriginalName(String resourceFileOriginalName) {
        this.resourceFileOriginalName = resourceFileOriginalName;
    }

}
