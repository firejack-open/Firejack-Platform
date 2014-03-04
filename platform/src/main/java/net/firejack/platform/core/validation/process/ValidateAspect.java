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

package net.firejack.platform.core.validation.process;

import net.firejack.platform.api.registry.domain.Domain;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.validation.annotation.Revalidation;
import net.firejack.platform.core.validation.annotation.Validate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class ValidateAspect {

	@Autowired
	private ValidateParent validateParent;
	@Autowired
	private  ValidateUnique validateUnique;
	@Autowired
	private  ValidateUniqueAll validateUniqueAll;

	@Order(3)
	@Around(value = "execution(* execute(..)) && target(broker) && args(request)", argNames = "pjp, broker, request")
	public Object save(ProceedingJoinPoint pjp, SaveBroker broker, ServiceRequest<? extends BaseEntity> request) throws Throwable {
		BaseEntity entity = request.getData();
		Class<? extends BaseEntity> entityClass = entity.getClass();
		Validate validate = entityClass.getAnnotation(Validate.class);
		if (validate == null)
			return pjp.proceed();

		validateParent.validate(entity, validate);
		validateUnique.validate(entity, validate);

		return pjp.proceed();
	}

	@Order(4)
	@Around(value = "execution(* execute(..)) && @target(revalidation) && args(request)", argNames = "pjp, revalidation, request")
	public Object revalidation(ProceedingJoinPoint pjp, Revalidation revalidation, ServiceRequest request) throws Throwable {
		AbstractDTO data = request.getData();
		if (data instanceof Package) {
			validateUniqueAll.validatePackage((Package) data);
		} else if (data instanceof Domain) {
			validateUniqueAll.validateDomain((Domain) data);
		}

		return pjp.proceed();
	}
}
