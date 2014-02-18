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

package net.firejack.platform.api.registry.domain;

import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.validation.annotation.Length;
import net.firejack.platform.core.validation.annotation.Match;
import net.firejack.platform.core.validation.annotation.NotBlank;
import net.firejack.platform.core.validation.annotation.NotMatch;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlRootElement;

@Component
@XmlRootElement
@RuleSource("OPF.registry.RootDomain")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RootDomain extends Lookup {
    private static final long serialVersionUID = 210925870572130442L;

	@NotBlank
	@Length(maxLength = 255)
	@Match(expression = "^[a-z][a-z0-9]+\\.[a-z][a-z0-9]+$", example = "example.com")
    @NotMatch(expression = "\\b(?:package)\\b", example = "package")
	public String getName() {
		return super.getName();
	}

	@Override
	public Long getParentId() {
		return super.getParentId();
	}
}
