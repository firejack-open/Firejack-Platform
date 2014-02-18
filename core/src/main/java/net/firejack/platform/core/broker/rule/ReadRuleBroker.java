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

package net.firejack.platform.core.broker.rule;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ClassUtils;
import net.firejack.platform.core.validation.constraint.ConstraintsSourceClass;
import net.firejack.platform.core.validation.constraint.RuleMapper;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import net.firejack.platform.core.validation.constraint.vo.Field;
import net.firejack.platform.core.validation.processor.DefaultValueProcessor;
import net.firejack.platform.core.validation.processor.EditableValueProcessor;
import net.firejack.platform.core.validation.processor.RuleProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("readRuleBroker")
public class ReadRuleBroker
        extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<Field>> {

    @Autowired
    private RuleProcessor ruleProcessor;
    @Autowired
    private DefaultValueProcessor defaultValueProcessor;
    @Autowired
    private EditableValueProcessor editableValueProcessor;

    @Override
    protected ServiceResponse<Field> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        String constraintSourceId = request.getData().getIdentifier();
        ConstraintsSourceClass sourceClass = RuleMapper.getConstrainedType(constraintSourceId);
        List<Field> fields = new ArrayList<Field>();
        if (sourceClass != null && sourceClass.getClazz() != null) {
            Map<String, Method> readMethods = ClassUtils.getReadMethods(sourceClass.getClazz());
            for (String propertyName : readMethods.keySet()) {
                Field field = null;
                Method method = readMethods.get(propertyName);
                Map<String, String> params = sourceClass.getParams();

                List<Constraint> constraints = ruleProcessor.generateConstraints(method, propertyName, params);
                if (!constraints.isEmpty()) {
                    field = new Field(propertyName);
                    field.setConstraints(constraints);
                }

//                List<Object> entities = queryValueProcessor.execute(method);
//                if (entities != null) {
//                    if (field == null) {
//                        field = new Field(propertyName);
//                    }
//                    Constraint constraint = new Constraint("QueryValues");
//                    RuleParameter enumValuesParam = new RuleParameter("values", entities);
//                    List<RuleParameter> ruleParameters = new ArrayList<RuleParameter>();
//                    ruleParameters.add(enumValuesParam);
//                    constraint.setParams(ruleParameters);
//
//                    if (field.getConstraints() == null) {
//                        field.setConstraints(new ArrayList<Constraint>());
//                    }
//                    field.getConstraints().add(constraint);
//
//                    Object defaultValue = queryValueProcessor.defaultValue(method, entities);
//                    if (defaultValue != null) {
//                        field.setDefaultValue(defaultValue);
//                    }
//                }

                Object defaultValue = defaultValueProcessor.getDefaultValue(method, propertyName, params);
                if (defaultValue != null) {
                    if (field == null) {
                        field = new Field(propertyName);
                    }
                    field.setDefaultValue(defaultValue);
                }

                Boolean editable = editableValueProcessor.editable(method, params);
                if (editable != null) {
                    if (field == null) {
                        field = new Field(propertyName);
                    }
                    field.setEditable(editable);    
                }

                if (field != null) {
                    fields.add(field);
                }
            }
        }
        return new ServiceResponse<Field>(fields, "Returned validation constraints for entity: " + constraintSourceId, true);
    }

}
