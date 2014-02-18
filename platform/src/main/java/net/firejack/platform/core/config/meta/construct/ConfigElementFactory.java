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

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.config.exception.NameConventionViolationException;
import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.element.FolderElement;
import net.firejack.platform.core.config.meta.element.NavigationConfigElement;
import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.config.meta.element.directory.DirectoryElement;
import net.firejack.platform.core.config.meta.parse.xml.IConfigClassConsumer;
import net.firejack.platform.core.config.meta.parse.xml.IPackageXmlProcessor;
import net.firejack.platform.core.config.meta.parse.xml.PackageXmlProcessorFactory;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.INameProvider;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public class ConfigElementFactory {//todo: implement <entity><value>VAL_1<value/><value>VAL_2<value/></entity>

    private static ConfigElementFactory instance;
    private static final String WHITE_SPACES = " \t\n\r";

    private ConfigElementFactory() {
    }

    /**
     *
     * @param name
     * @param domains
     * @param entities
     * @param prefix
     * @param wsdlLocation
     * @return
     */
    public IDomainElement produceDomain(String name, List<IDomainElement> domains, List<IEntityElement> entities, String prefix, Boolean dataSource, String wsdlLocation) {
        if (!StringUtils.containsNone(name, WHITE_SPACES)) {
            throw new IllegalArgumentException("Domain should not contain white spaces.");
        }
        //DomainConfigElement domainConfigElement = new DomainConfigElement(name.toLowerCase());
        DomainConfigElement domainConfigElement = new DomainConfigElement(name);
        //checkElementName(domain);
        normalizeName(domainConfigElement);
        domainConfigElement.setConfiguredDomains(domains);
        domainConfigElement.setConfiguredEntities(entities);
        domainConfigElement.setPrefix(prefix);
        domainConfigElement.setDataSource(dataSource);
        domainConfigElement.setWsdlLocation(wsdlLocation);
        return domainConfigElement;
    }

    /**
     * @param name
     * @return
     */
    public IEntityElement producePackageEntity(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name property should not be blank.");
        }
//        name = WordUtils.capitalize(name.toLowerCase());
        EntityConfigElement entityConfigElement = new EntityConfigElement(name);
        normalizeName(entityConfigElement);
        return entityConfigElement;
        //checkElementName(entityConfigElement);
        //return entityConfigElement;
    }

    /**
     * @param name
     * @param compoundKeyColumnsInfo
     * @return
     */
    public IEntityElement producePackageEntity(String name, List<CompoundKeyColumnsRule> compoundKeyColumnsInfo) {
        EntityConfigElement entityConfigElement = (EntityConfigElement) producePackageEntity(name);
        //checkElementName(entityConfigElement);
        if (compoundKeyColumnsInfo != null && compoundKeyColumnsInfo.size() != 0) {
            entityConfigElement.setCompoundKeyColumnsRules(compoundKeyColumnsInfo);
        }
        return entityConfigElement;
    }

    /**
     * @param name
     * @param extendedEntityPath
     * @param compoundKeyColumnsInfo
     * @return
     */
    public IEntityElement producePackageEntity(String name, String extendedEntityPath, List<CompoundKeyColumnsRule> compoundKeyColumnsInfo) {
        EntityConfigElement entityConfigElement = (EntityConfigElement) producePackageEntity(name, compoundKeyColumnsInfo);
        if (StringUtils.isNotBlank(extendedEntityPath)) {
            entityConfigElement.setExtendedEntityPath(extendedEntityPath);
        }
        return entityConfigElement;
    }

    /**
     * @param name
     * @param extendedEntityPath
     * @param compoundKeyColumnsInfo
     * @return
     */
    public IEntityElement produceAbstractPackageEntity(String name, String extendedEntityPath, List<CompoundKeyColumnsRule> compoundKeyColumnsInfo) {
        EntityConfigElement entityConfigElement = (EntityConfigElement) producePackageEntity(name, extendedEntityPath, compoundKeyColumnsInfo);
        entityConfigElement.setAbstractEntity(true);
        return entityConfigElement;
    }

    /**
     * @param name
     * @param compoundKeyColumnsRule
     * @return
     */
    public IEntityElement producePackageEntity(String name, CompoundKeyColumnsRule compoundKeyColumnsRule) {
        List<CompoundKeyColumnsRule> ukRuleList = null;
        if (compoundKeyColumnsRule != null) {
            ukRuleList = new ArrayList<CompoundKeyColumnsRule>();
            ukRuleList.add(compoundKeyColumnsRule);
        }
        return producePackageEntity(name, ukRuleList);
    }

    /**
     * @param entitiesProvider
     * @param entityList
     * @return
     */
    public <EP extends IEntityProvider> EP assignEntities(
            EP entitiesProvider, List<IEntityElement> entityList) {
        if (entitiesProvider instanceof IEntityElementContainer) {
            IEntityElementContainer entityElementContainer = (IEntityElementContainer) entitiesProvider;
            entityElementContainer.setConfiguredEntities(entityList);
        }
        return entitiesProvider;
    }

//    public IEntityElement produceEnhancedEntity(IEntityElement packageEntity, IFieldElement... fields) {
//        List<IFieldElement> entityFields = new ArrayList<IFieldElement>();
//        //entityFields
//        Collections.addAll(entityFields, packageEntity.getFields());
//        Collections.addAll(entityFields, fields);
//
//        List<CompoundKeyColumnsRule> compoundKeyColumnRules = new ArrayList<CompoundKeyColumnsRule>();
//        Collections.addAll(compoundKeyColumnRules, packageEntity.getCompoundKeyColumnsRules());
//        EntityConfigElement cloneEntityConfigElement = (EntityConfigElement) producePackageEntity(
//                packageEntity.getName(), packageEntity.getExtendedEntityPath(), compoundKeyColumnRules);
//        cloneEntityConfigElement.setRequiredData(packageEntity.getRequiredData());
//
//        return cloneEntityConfigElement;
//    }

    /**
     * @param packageEntity
     * @param field
     * @return
     */
    public boolean enhanceEntity(IEntityElement packageEntity, IFieldElement field) {
        if (packageEntity == null || field == null) {
            throw new IllegalArgumentException("Parameters should not be null.");
        }
        EntityConfigElement entityConfigElement = (EntityConfigElement) packageEntity;
        IFieldElement[] fields = entityConfigElement.getFields();
        if (DiffUtils.findNamedElement(fields, field.getName()) != null) {
            return false;
        }
        List<IFieldElement> fieldList = new ArrayList<IFieldElement>();
        Collections.addAll(fieldList, fields);
        fieldList.add(field);
        entityConfigElement.setFields(fieldList);
        return true;
    }

    //TODO: populate UID for generated fields

    /**
     * @param generatedFieldType
     * @param context
     * @param required
     * @return
     */
    public IFieldElement produceGeneratedField(
            GeneratedFieldType generatedFieldType, GeneratedFieldContext context, boolean required) {
	    FieldConfigElement field;
        switch (generatedFieldType) {
            case ID:
                field = (FieldConfigElement) produceField(context.getEntity(),
                        context.getNameResolver().resolveIdColumn(), FieldType.NUMERIC_ID, true, null);
                break;
            case CREATED:
                field = (FieldConfigElement) produceField(context.getEntity(),
                        context.getNameResolver().resolveCreatedColumn(), FieldType.CREATION_TIME, true, null);
                break;
            case DISCRIMINATOR:
                field = (FieldConfigElement) produceField(context.getEntity(),
                        context.getNameResolver().resolveDiscriminatorColumn(), FieldType.TINY_TEXT, true, null);
                break;
            case REF:
                String referenceFieldName = context.getNameResolver().resolveReference(context.getReference());
                field = produceGenField(context.getEntity(), referenceFieldName, FieldType.NUMERIC_ID, required);
                break;
            case PARENT_REF:
                field = produceGenField(context.getEntity(), context.getNameResolver()
                        .resolveParentColumn(), FieldType.NUMERIC_ID, required);
                break;
            case EXTENDED_ENTITY:
                field = (FieldConfigElement) produceField(context.getEntity(),
                        context.getNameResolver().resolveReference(context.getReference()), FieldType.NUMERIC_ID, null, null);
                break;
            default:
                field = null;
        }
        if (field != null) {
            field.setAutoGenerated(true);
        }
        return field;
    }

    /**
     * @param parentEntity
     * @param name
     * @param type
     * @param required
     * @param defaultValue
     * @return
     */
    public IFieldElement produceField(IEntityElement parentEntity, String name, FieldType type,
                                      Boolean required, Object defaultValue) {
        if (parentEntity == null) {
            throw new IllegalArgumentException("Field-holder domain object should not be null.");
        } else if (!(parentEntity instanceof EntityConfigElement)) {
            throw new IllegalArgumentException("Inconsistent parent domain object argument.");
        } else if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("The name of field should not be blank.");
        } else if (type == null) {
            throw new IllegalArgumentException("The type of field should not be null.");
        }

        IFieldElement[] fields = parentEntity.getFields();
        if (fields != null) {
            IFieldElement field = DiffUtils.findNamedElement(fields, name);
            if (field != null) {
                return field;//field already exists
            }
        }

        FieldConfigElement fieldConfigElement = produceField(name, type, required, defaultValue);

        List<IFieldElement> fieldsList = new ArrayList<IFieldElement>();
        if (fields != null) {
            Collections.addAll(fieldsList, parentEntity.getFields());
        }
        fieldsList.add(fieldConfigElement);
        EntityConfigElement domainObject = (EntityConfigElement) parentEntity;
        domainObject.setFields(fieldsList);

        return fieldConfigElement;
    }

    public FieldConfigElement produceField(String name, FieldType type, Boolean required, Object defaultValue) {
        FieldConfigElement fieldConfigElement = new FieldConfigElement(name);
        //checkElementName(field);
        normalizeName(fieldConfigElement);
        fieldConfigElement.setType(type);
        if (required != null && required) {
            fieldConfigElement.setRequired(required);
        }

        fieldConfigElement.setDefaultValue(defaultValue);
        return fieldConfigElement;
    }

    /**
     *
     *
     * @param fieldElementContainer
     * @param model
     * @return
     */
    public IFieldElement produceField(IFieldElementContainer fieldElementContainer, FieldModel model) {
	    String name = model.getName();
	    FieldType type = model.getFieldType();

	    if (fieldElementContainer == null) {
		    throw new IllegalArgumentException("Field-holder domain object should not be null.");
	    } else if (!(fieldElementContainer instanceof EntityConfigElement || fieldElementContainer instanceof RelationshipConfigElement || fieldElementContainer instanceof DirectoryElement)) {
		    throw new IllegalArgumentException("Inconsistent parent domain object argument.");
	    } else {
		    if (StringUtils.isBlank(name)) {
		        throw new IllegalArgumentException("The name of field should not be blank.");
	        } else {
		        if (type == null) {
		            throw new IllegalArgumentException("The type of field should not be null.");
		        }
	        }
        }

        IFieldElement[] fields = fieldElementContainer.getFields();
        if (fields != null) {
            IFieldElement field = DiffUtils.findNamedElement(fields, name);
            if (field != null) {
                return null;
            }
        }

        FieldConfigElement fieldConfigElement = new FieldConfigElement(name);
        //checkElementName(field);
        normalizeName(fieldConfigElement);
        fieldConfigElement.setType(type);
	    fieldConfigElement.setRequired(model.getRequired());
	    fieldConfigElement.setSearchable(model.getSearchable());
	    fieldConfigElement.setAutoGenerated(model.getAutoGenerated());

	    Object defaultValue = getDefaultValue(type, model.getDefaultValue());
        fieldConfigElement.setDefaultValue(defaultValue);
        fieldConfigElement.setCustomType(model.getCustomFieldType());
        fieldConfigElement.setDescription(model.getDescription());
        fieldConfigElement.setDisplayName(model.getDisplayName());
        fieldConfigElement.setDisplayDescription(model.getDisplayDescription());
        fieldConfigElement.setAllowValues(StringUtils.join(model.getAllowedFieldValueList().getEntries(), "|"));

        List<EntityModel> options = model.getOptions();
        if (options != null){
            List<Reference> references = new ArrayList<Reference>(options.size());
            for (EntityModel option : options) {
                references.add(new Reference(option.getName(),option.getPath()));
            }
            fieldConfigElement.setOptions(references);
        }

        List<IFieldElement> fieldsList = new ArrayList<IFieldElement>();
        if (fields != null) {
            Collections.addAll(fieldsList, fieldElementContainer.getFields());
        }
        fieldsList.add(fieldConfigElement);
        fieldElementContainer.setFields(fieldsList);

	    if (model.getUid() != null) {
		    fieldConfigElement.setUid(model.getUid().getUid());
	    }

	    return fieldConfigElement;
    }

    public IIndexElement produceIndex(IndexModel indexModel) {
        IndexConfigElement indexElement = new IndexConfigElement(indexModel.getName());
        indexElement.setType(indexModel.getIndexType());
        RelationshipModel relationshipModel = indexModel.getRelationship();
        if (relationshipModel != null) {
            Reference relationship = new Reference();
            relationship.setRefName(relationshipModel.getName());
            relationship.setRefPath(relationshipModel.getPath());
            indexElement.setRelationship(relationship);
        }
        return indexElement;
    }

	private Object getDefaultValue(FieldType fieldType, String strValue) {
	    if (StringUtils.isBlank(strValue) || fieldType == null) {
	        return null;
	    }
	    if (fieldType.isString()) {
	        return strValue;
	    } else if (fieldType.isInteger()) {
            if (StringUtils.isNumeric(strValue)) {
                return Integer.valueOf(strValue);
            } else {
                return strValue;
            }
	    } else if (fieldType.isLong()) {
            if (StringUtils.isNumeric(strValue)) {
                return Long.valueOf(strValue);
            } else {
                return strValue;
            }
	    } else if (fieldType.isReal()) {
	        return fieldType == FieldType.CURRENCY || fieldType == FieldType.DECIMAL_NUMBER ?
	                Double.valueOf(strValue) : Float.valueOf(strValue);
	    } else if (fieldType.isTimeRelated()) {
	        return strValue; // todo need to discuss
	    } else if (fieldType == FieldType.FLAG) {
	        return Boolean.valueOf(strValue);
	    } else {
	        throw new UnsupportedOperationException("Can't de-serialize default value of type [" + fieldType.name() + "]");
	    }
	}

    /**
     * @param name
     * @param source
     * @param target
     * @param fields
     * @return
     */
    public IRelationshipElement produceTypeRelationship(String name, String hint, Reference source,
                                                        Reference target, IFieldElement... fields) {
        return produceTypeRelationship(name, hint, source, target, null, null, fields);
    }

    /**
     * @param name
     * @param source
     * @param target
     * @param fields
     * @return
     */
    public IRelationshipElement produceLinkRelationship(String name, String hint, Reference source,
                                                        Reference target, IFieldElement... fields) {
        return populateRelationship(name, hint, RelationshipType.LINK, source, target, fields);
    }

    /**
     * @param name
     * @param source
     * @param target
     * @param onDeleteOptions
     * @param onUpdateOptions
     * @param fields
     * @return
     */
    public IRelationshipElement produceTypeRelationship(String name, String hint, Reference source,
                                                        Reference target,
                                                        RelationshipOption onDeleteOptions, RelationshipOption onUpdateOptions, IFieldElement... fields) {
        RelationshipConfigElement rel = populateRelationship(name, hint, RelationshipType.TYPE, source, target, fields);
        rel.setOnDeleteOptions(onDeleteOptions);
        rel.setOnUpdateOptions(onUpdateOptions);
        return rel;
    }

    /**
     * @param name
     * @param source
     * @param target
     * @param weighted
     * @param fields
     * @return
     */
    public IRelationshipElement produceAssociationRelationship(String name, String hint, Reference source,
                                                               Reference target, boolean weighted, IFieldElement... fields) {
        RelationshipType type = weighted ? RelationshipType.WEIGHTED_ASSOCIATION : RelationshipType.ASSOCIATION;
        return populateRelationship(name, hint, type, source, target, fields);
    }

    /**
     * @param name
     * @param source
     * @param fields
     * @return
     */
    public IRelationshipElement produceTreeRelationship(String name, String hint, Reference source, IFieldElement... fields) {
        return populateRelationship(name, hint, RelationshipType.TREE, source, null, fields);
    }

    /**
     * @param name
     * @param source
     * @param target
     * @return
     */
    public IRelationshipElement produceParentChildRelationship(String name, String hint, Reference source, Reference target) {
        return populateRelationship(name, hint, RelationshipType.PARENT_CHILD, source, target);
    }

    /**
     * @return
     */
    public IPackageXmlProcessor getDefaultPackageXmlProcessor() {
        PackageXmlProcessorFactory factory = PackageXmlProcessorFactory.getInstance();

        IConfigClassConsumer streamProcessor = factory.getXStreamProcessor();
        streamProcessor.setRelationshipClass(RelationshipConfigElement.class);
        streamProcessor.setPackageEntityClass(EntityConfigElement.class);
        streamProcessor.setFieldClass(FieldConfigElement.class);
        streamProcessor.setIndexClass(IndexConfigElement.class);
        streamProcessor.setDomainClass(DomainConfigElement.class);

        return streamProcessor;
    }

    /**
     * @param rel
     * @param required
     */
    public void setRelationshipRequiredStatus(IRelationshipElement rel, Boolean required) {
        if (rel != null && rel instanceof RelationshipConfigElement) {
            RelationshipConfigElement relationship = (RelationshipConfigElement) rel;
            relationship.setRequired(required);
        }
    }

    /**
     * @param rel
     * @param onUpdateOptions
     * @param onDeleteOptions
     */
    public void setRelationshipOptions(IRelationshipElement rel, RelationshipOption onUpdateOptions, RelationshipOption onDeleteOptions) {
        if (rel != null && rel instanceof RelationshipConfigElement) {
            RelationshipConfigElement relationship = (RelationshipConfigElement) rel;
            relationship.setOnDeleteOptions(onDeleteOptions);
            relationship.setOnUpdateOptions(onUpdateOptions);
        }
    }

    /**
     * @param target
     * @param path
     */
    public void attachPath(INamedPackageDescriptorElement target, String path) {
        if (target == null) {
            return;
        }
        if (target instanceof BaseConfigElement) {
            BaseConfigElement configElement = (BaseConfigElement) target;
            configElement.setPath(path);
        } else {
            throw new IllegalArgumentException("Invalid target class for path owner");
        }
    }

    /**
     * @param parentReferenceOwner
     * @param parent
     */
    public void assignParent(IParentReferenceOwner parentReferenceOwner, INamedPackageDescriptorElement parent) {
        if (parentReferenceOwner == null) {
            return;
        }
        if (parentReferenceOwner instanceof DomainConfigElement) {
            DomainConfigElement domainConfigElement = (DomainConfigElement) parentReferenceOwner;
            domainConfigElement.setParent(parent);
        } else if (parentReferenceOwner instanceof EntityConfigElement) {
            EntityConfigElement entityConfigElement = (EntityConfigElement) parentReferenceOwner;
            entityConfigElement.setParent(parent);
        }
    }

    /**
     * @return
     */
    public static ConfigElementFactory getInstance() {
        if (instance == null) {
            instance = new ConfigElementFactory();
        }
        return instance;
    }

    /**
     * @param element
     */
    public void checkElementName(IPackageDescriptorElement element) {
	    if (true) {
		    return;
	    }
	    if (element instanceof INamedPackageDescriptorElement) {
            String elementName = ((INamedPackageDescriptorElement) element).getName();
            if (element instanceof IPackageDescriptor) {
                IPackageDescriptor packageDescriptor = (IPackageDescriptor) element;
                if (!noWhiteSpacesAndNoUpperCase(packageDescriptor.getPath())) {
                    throw new NameConventionViolationException(
                            "Root domains should not contain whitespaces and upper case characters [" +
                                    packageDescriptor.getPath() + "].");
                } else if (!noWhiteSpacesAndNoUpperCase(elementName)) {
                    throw new NameConventionViolationException(
                            "Package name should not contain whitespaces and upper case characters [" +
                                    elementName + "].");
                }
            } else if (element instanceof IDomainElement && !noWhiteSpacesAndNoUpperCase(elementName)) {
                throw new NameConventionViolationException(
                        "Domain name should not contain whitespaces and upper case characters [" +
                                elementName + "].");
            } else if (element instanceof IEntityElement && !allWordsCapitalized(elementName)) {
                throw new NameConventionViolationException(
                        "Entity name should contain only capitalized words [" +
                                elementName + "].");
            } else if (element instanceof IRelationshipElement && !allWordsCapitalized(elementName)) {
                throw new NameConventionViolationException(
                        "Relationship name should contain only capitalized words [" +
                                elementName + "].");
            } else if (element instanceof IFieldElement && !StringUtils.containsNone(elementName, WHITE_SPACES)) {
                throw new NameConventionViolationException(
                        "Field name should not contain whitespaces [" +
                                elementName + "].");
            } else if (element instanceof ActionElement && !noWhiteSpacesAndNoUpperCase(elementName)) {
                throw new NameConventionViolationException(
                        "Action name should not contain whitespaces and upper case characters [" +
                                elementName + "].");
            } else if (element instanceof DirectoryElement && !elementName.equals(elementName.toLowerCase())) {
                throw new NameConventionViolationException(
                        "Directory should contain only lower case characters [" +
                                elementName + "].");
            } else if (element instanceof FolderElement && !noWhiteSpacesAndNoUpperCase(elementName)) {
                throw new NameConventionViolationException(
                        "Resource folder should not contain whitespaces and upper case characters [" +
                                elementName + "].");
            } else if (element instanceof NavigationConfigElement && !noWhiteSpacesAndNoUpperCase(elementName)) {
                throw new NameConventionViolationException(
                        "Navigation element should not contain whitespaces and upper case characters [" +
                                elementName + "].");
            }
        }
    }

    /**
     * @param element
     */
    public void normalizeName(IPackageDescriptorElement element) {
	    if(true){
		    return;
	    }
        if (element instanceof INameProvider) {
            INameProvider namedElement = (INameProvider) element;
            String name = namedElement.getName();
            if (element instanceof IPackageDescriptor || element instanceof IDomainElement ||
                    element instanceof ActionElement || element instanceof FolderElement ||
                    element instanceof NavigationConfigElement) {
                name = StringUtils.changeWhiteSpacesWithSymbol(name, "-");
                namedElement.setName(name.toLowerCase());
            } else if (element instanceof IEntityElement ||
                    element instanceof IRelationshipElement) {
                name = WordUtils.capitalize(name);
                namedElement.setName(name);
            } else if (element instanceof DirectoryElement) {
                namedElement.setName(name.toLowerCase());
            } else if (element instanceof IFieldElement) {
                name = StringUtils.changeWhiteSpacesWithSymbol(name, "_");
                namedElement.setName(name);
            }
        }
    }

    private static boolean allWordsCapitalized(String value) {
        Scanner sc = new Scanner(value);
        while (sc.hasNext()) {
            String word = sc.next();
            if (!StringUtils.capitalize(word).equals(word)) {
                return false;
            }
        }
        return true;
    }

    private static boolean noWhiteSpacesAndNoUpperCase(String value) {
        return StringUtils.containsNone(value, WHITE_SPACES) &&
                value.equals(value.toLowerCase());
    }

    private FieldConfigElement produceGenField(IEntityElement parentEntity, String name, FieldType type, Boolean required) {
        if (parentEntity == null) {
            throw new IllegalArgumentException("Field-holder domain object should not be null.");
        } else if (!(parentEntity instanceof EntityConfigElement)) {
            throw new IllegalArgumentException("Inconsistent parent domain object argument.");
        } else if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("The name of field should not be blank.");
        } else if (type == null) {
            throw new IllegalArgumentException("The type of field should not be null.");
        }

        IFieldElement[] fields = parentEntity.getFields();
        if (fields != null) {
            IFieldElement field = DiffUtils.findNamedElement(fields, name);
            if (field != null) {
                return (FieldConfigElement) field;//field already exists
            }
        }

        return produceField(name, type, required, null);
    }

    private RelationshipConfigElement populateRelationship(String name, String hint, RelationshipType relType, Reference source, Reference target, IFieldElement... fields) {
        name = WordUtils.capitalize(name);
        RelationshipConfigElement rel = new RelationshipConfigElement(name);
        //checkElementName(rel);
        normalizeName(rel);
        rel.setHint(hint);
        rel.setType(relType);
        rel.setSource(source);
        rel.setTarget(target);
        if (fields.length != 0) {
            List<IFieldElement> fieldList = new ArrayList<IFieldElement>();
            Collections.addAll(fieldList, fields);
            rel.setFields(fieldList);
        }
        return rel;
    }

}