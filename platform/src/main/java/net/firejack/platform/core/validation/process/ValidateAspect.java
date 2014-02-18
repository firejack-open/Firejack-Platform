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
