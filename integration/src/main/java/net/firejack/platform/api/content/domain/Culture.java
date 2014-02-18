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
import net.firejack.platform.core.model.registry.resource.Cultures;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlRootElement;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class Culture extends AbstractDTO {
    private static final long serialVersionUID = 8899269254477738646L;

    private Cultures culture;
    private String country;

    /***/
    public Culture() {
    }

    /**
     * @param culture
     * @param country
     */
    public Culture(Cultures culture, String country) {
        this.culture = culture;
        this.country = country;
    }

    /**
     * @return
     */
    public Cultures getCulture() {
        return culture;
    }

    /**
     * @param culture
     */
    public void setCulture(Cultures culture) {
        this.culture = culture;
    }

    /**
     * @return
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

}
