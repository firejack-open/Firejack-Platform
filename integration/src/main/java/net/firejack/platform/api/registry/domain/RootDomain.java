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
