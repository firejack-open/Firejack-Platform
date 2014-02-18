/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.api.registry.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.validation.annotation.Length;
import net.firejack.platform.core.validation.annotation.Match;
import net.firejack.platform.core.validation.annotation.NotBlank;
import net.firejack.platform.core.validation.annotation.Validate;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static net.firejack.platform.core.validation.annotation.DomainType.*;


@Component
@XmlRootElement
@RuleSource("OPF.registry.BIReport")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
@Validate(type = BI_REPORT, parents = {ENTITY}, unique = {ENTITY, REPORT, BI_REPORT, WIZARD, ACTOR, PROCESS})
public class BIReport extends Lookup {
    private static final long serialVersionUID = 2448036722513932307L;

    @Property
	@XmlElementWrapper(name = "fields")
	private List<BIReportField> fields;

    @Override
    @NotBlank
    @Length(maxLength = 255)
    @Match(expression = "^([A-Z][a-z0-9]*)(\\s([A-Z][a-z0-9]*))*$", msgKey = "validation.parameter.bireport.name.should.match.exp")
    public String getName() {
        return super.getName();
    }

	public List<BIReportField> getFields() {
		return fields;
	}

	public void setFields(List<BIReportField> fields) {
		this.fields = fields;
	}
}