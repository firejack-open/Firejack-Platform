/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.model.service;

import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.model.registry.ParameterTransmissionType;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.ActionParameterModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.store.registry.IActionParameterStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.tools.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentationLinkService {

    @Autowired
    @Qualifier("entityStore")
    private IEntityStore entityStore;
	@Autowired
	private IActionParameterStore actionParameterStore;

    /**
     * @param action
     * @return
     */
    public String generateRestUrl(ActionModel action) {
        StringBuilder restUrlPathParameters = new StringBuilder();
        StringBuilder restUrlQueries = new StringBuilder();
        if (action.getActionParameters() != null) {
            for (ActionParameterModel actionParameter : action.getActionParameters()) {
                if (ParameterTransmissionType.PATH.equals(actionParameter.getLocation())) {
                    restUrlPathParameters.append("/{");
                    restUrlPathParameters.append(actionParameter.getName());
                    restUrlPathParameters.append("}");
                }
            }
            int index = 0;
            for (ActionParameterModel actionParameter : action.getActionParameters()) {
                if (ParameterTransmissionType.QUERY.equals(actionParameter.getLocation())) {
                    if (index == 0) {
                        restUrlQueries.append("?");
                    } else {
                        restUrlQueries.append("&");
                    }
                    index++;
                    restUrlQueries.append(actionParameter.getName());
                    restUrlQueries.append("=value");
                }
            }
        }
        return action.getUrlPath() + restUrlPathParameters.toString() + restUrlQueries.toString();
    }

	/**
	 * @param action
	 *
	 * @return
	 */
	public String generateRestRequestExample(ActionModel action) {
		StringBuilder restJson = new StringBuilder();
		EntityModel entity = entityStore.findWithInheritedFieldsById(action.getInputVOEntity().getId());
		if (entity != null) {
			generateEntityJson(entity, restJson, 1, action.getMethod(), true);
		}
		return "{\n" +
				tabs(1) + "\"data\": " + restJson.toString() + "\n" +
				"}";
	}

	/**
     * @param action
     * @return
     */
    public String generateRestResponseExample(ActionModel action) {
        StringBuilder restJson = new StringBuilder();
        EntityModel entity = action.getOutputVOEntity() == null || action.getOutputVOEntity().getId() == null ?
                null : entityStore.findWithInheritedFieldsById(action.getOutputVOEntity().getId());
        if (entity != null) {
	        generateEntityJson(entity, restJson, 1, action.getMethod(), false);
        }
        return "{\n" +
                tabs(1) + "\"data\": " + restJson.toString() + ",\n" +
                tabs(1) + "\"message\": Action completed successfully,\n" +
                tabs(1) + "\"success\": true,\n" +
                tabs(1) + "\"total\": null\n" +
                "}";
    }

	/**
	 * @param action
	 *
	 * @return
	 */
	public String generateSoapRequestExample(ActionModel action) {
		StringBuilder builder = new StringBuilder();
		List<ActionParameterModel> parameters = actionParameterStore.findAllByParentIdWithFilter(action.getId(), null);
		generateParameterSoap(parameters, builder, 4);

		if (HTTPMethod.POST.equals(action.getMethod()) || HTTPMethod.PUT.equals(action.getMethod())) {
			EntityModel entity = entityStore.findWithInheritedFieldsById(action.getInputVOEntity().getId());
			if (entity != null) {
				if (parameters != null && !parameters.isEmpty()) {
					builder.append("\n");
				}
                String name = DiffUtils.classFormatting(entity.getName().toLowerCase());
                name = StringUtils.uncapitalize(name);

				builder.append(tabs(5)).append("<request>");
				builder.append("\n").append(tabs(6)).append("<data>");
				builder.append("\n").append(tabs(7)).append("<dom:").append(name).append(">");
				generateEntitySoap(entity.getFields(), builder, 8, action.getMethod(), true);
				builder.append("\n").append(tabs(7)).append("</dom:").append(name).append(">");
				builder.append("\n").append(tabs(6)).append("</data>");
				builder.append("\n").append(tabs(5)).append("</request>");
			}
		}

		return builder.toString();
	}

	/**
	 * @param action
	 *
	 * @return
	 */
	public String generateSoapResponseExample(ActionModel action) {
		StringBuilder builder = new StringBuilder();
		StringBuilder fields = new StringBuilder();
		String name = DiffUtils.classFormatting(action.getParent().getName().toLowerCase());
		name = new StringBuffer(name.length()).append(Character.toLowerCase(name.charAt(0))).append(name.substring(1)).toString();

		EntityModel entity = entityStore.findWithInheritedFieldsById(action.getOutputVOEntity().getId());
		if (entity != null) {
			generateEntitySoap(entity.getFields(), fields, 10, action.getMethod(), false);
		}

		builder.append(tabs(9)).append("<ns1:").append(name).append(">");
		builder.append(fields);
		builder.append("\n").append(tabs(9)).append("</ns1:").append(name).append(">");
		return builder.toString();
	}

	private void generateEntityJson(EntityModel entity, StringBuilder restJson, int depth,HTTPMethod method, boolean request) {
		StringBuilder objectFields = new StringBuilder();
		objectFields.append("{\n");
		int index = 0;
		List<FieldModel> fields = entity.getFields();
		if (fields != null) {
			for (FieldModel field : fields) {
				if (request && method == HTTPMethod.PUT && field.getName().matches("created")) continue;
				else if (request && method != HTTPMethod.PUT && field.getName().matches("id|created")) continue;
				if (index > 0) {
					objectFields.append(",\n");
				}
				index++;
				String fieldName = Utils.fieldModelFormatting(field.getName());
				objectFields.append(tabs(depth + 1)).append("\"");
				objectFields.append(fieldName);
				objectFields.append("\" : ");
				if (field.getFieldType().isString()) {
					objectFields.append("\'value\'");
				} else if (field.getFieldType().isInteger()) {
					objectFields.append("123");
				} else if (field.getFieldType().isLong()) {
					objectFields.append("123");
				} else if (field.getFieldType().isReal()) {
					objectFields.append("4,56");
				} else if (field.getFieldType().isTimeRelated()) {
					objectFields.append("\'date_format\'");
				} else if (field.getFieldType().isBoolean()) {
					objectFields.append("false");
				} else if (field.getFieldType().isList()) {
					objectFields.append("[]");
				} else if (field.getFieldType().isObject()) {
					if (!entity.getLookup().equals(field.getCustomFieldType())) {
						EntityModel fieldEntity = entityStore.findWithInheritedFieldsByLookup(field.getCustomFieldType());
						if (fieldEntity != null) {
							generateEntityJson(fieldEntity, objectFields, depth + 2,method, request);
						}
					}
				}
			}
		}
		objectFields.append("\n").append(tabs(depth)).append("}");
		restJson.append(objectFields.toString());
	}

	private void generateEntitySoap(List<FieldModel> fields, StringBuilder rest, int depth,HTTPMethod method,  boolean request) {
		StringBuilder objectFields = new StringBuilder();
		if (fields != null) {
			for (FieldModel field : fields) {
				if (request && method == HTTPMethod.PUT && field.getName().matches("created")) continue;
				else if (request && method != HTTPMethod.PUT && field.getName().matches("id|created")) continue;
				String fieldName = Utils.fieldModelFormatting(field.getName());
				objectFields.append("\n").append(tabs(depth + 1));
				objectFields.append("<").append(fieldName).append(">");
				if (field.getFieldType().isString()) {
					objectFields.append("value");
				} else if (field.getFieldType().isInteger()) {
					objectFields.append("123");
				} else if (field.getFieldType().isLong()) {
					objectFields.append("123");
				} else if (field.getFieldType().isReal()) {
					objectFields.append("4,56");
				} else if (field.getFieldType().isTimeRelated()) {
					objectFields.append("date_format");
				} else if (field.getFieldType().isBoolean()) {
					objectFields.append("false");
				} else if (field.getFieldType().isList()) {
					objectFields.append("");
				} else if (field.getFieldType().isObject()) {
					EntityModel entity = entityStore.findWithInheritedFieldsByLookup(field.getCustomFieldType());
					if (entity != null) {
						generateEntitySoap(entity.getFields(), objectFields, depth + 2, method, request);
					}
					objectFields.append("\n").append(tabs(depth + 1));
				}
				objectFields.append("</").append(field.getName()).append(">");
			}
		}
		objectFields.append(tabs(depth));
		rest.append(objectFields.toString());
	}

	private void generateParameterSoap(List<ActionParameterModel> parameters, StringBuilder rest, int depth) {
		StringBuilder objectFields = new StringBuilder();
		if (parameters != null && !parameters.isEmpty()) {
			String  newline="";
			for (ActionParameterModel field : parameters) {

				objectFields.append(newline).append(tabs(depth + 1));
				objectFields.append("<").append(field.getName()).append(">");
				newline="\n";
				if (field.getFieldType().isString()) {
					objectFields.append("value");
				} else if (field.getFieldType().isInteger()) {
					objectFields.append("123");
				} else if (field.getFieldType().isLong()) {
					objectFields.append("123");
				} else if (field.getFieldType().isReal()) {
					objectFields.append("4,56");
				} else if (field.getFieldType().isTimeRelated()) {
					objectFields.append("date_format");
				}
				objectFields.append("</").append(field.getName()).append(">");
			}
			objectFields.append(tabs(depth));
		}
		rest.append(objectFields.toString());
	}

	private String tabs(int depth) {
		return StringUtils.repeat("  ", depth);
	}
}
