package net.firejack.platform.generate.service;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.api.registry.model.IndexType;
import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.construct.*;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.translate.sql.ISqlNameResolver;
import net.firejack.platform.core.config.translate.sql.LeadIdPrefixNameResolver;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.type.BaseUserType;
import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.ImportString;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.ModelType;
import net.firejack.platform.generate.beans.web.model.column.Field;
import net.firejack.platform.generate.beans.web.model.column.MappedType;
import net.firejack.platform.generate.beans.web.model.key.CompositeKey;
import net.firejack.platform.generate.beans.web.model.key.FieldKey;
import net.firejack.platform.generate.beans.web.report.Report;
import net.firejack.platform.generate.structure.Structure;
import net.firejack.platform.generate.tools.Utils;
import net.firejack.platform.utils.generate.FormattingUtils;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.stereotype.Component;

import javax.persistence.IdClass;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

@Component
public class ModelGeneratorService extends BaseGeneratorService implements IModelGeneratorService {
    private ISqlNameResolver resolver = new LeadIdPrefixNameResolver();

    @ProgressStatus(weight = 2, description = "Generate model objects")
    public List<Model> generateModels(IPackageDescriptor descriptor, Structure structure) throws IOException {
        String path = DiffUtils.lookup(descriptor.getPath(), descriptor.getName());

        List<Model> models = new ArrayList<Model>();
        generateBaseEntity(models);

        if (descriptor.getConfiguredEntities() != null) {
            linksEntity(descriptor.getConfiguredEntities(), descriptor);
            generateModel("", "", descriptor.getName(), path, "", StringUtils.defaultString(descriptor.getPrefix()), models, descriptor.getConfiguredEntities());
        }

        if (descriptor.getConfiguredDomains() != null) {
            linksDomain(descriptor.getConfiguredDomains(), descriptor);
            constructDomainTree("", path, StringUtils.defaultString(descriptor.getPrefix()), descriptor.getConfiguredDomains(), models);
        }

        if (models.size() <= 1) return models;

        for (Iterator<Model> iterator = models.iterator(); iterator.hasNext(); ) {
            Model<Model> model = iterator.next();
            if (model.isExtended()) {
                Model ext = getCacheItem(model.getParent().getLookup());
                model.setParent(ext);
                if (ext != null && !ext.getName().equalsIgnoreCase(model.getName())) {
                    model.addImport(ext);
                }

                List<Field> fields = model.getFields();
                if (fields != null) {
                    for (Field field : fields) {
                        List<Model> options = field.getOptions();
                        if (options != null) {
                            List<Model> list = new ArrayList<Model>(options.size());
                            for (Model option : options) {
                                Model item = getCacheItem(option.getLookup());
                                list.add(item);
                            }
                            field.setOptions(list);
                        }
                    }
                }
            }

            if (model.isNested()) {
                iterator.remove();
                Model owner = getCacheItem(model.getOwner().getLookup());
                model.setOwner(owner);
                owner.addSubclasses(model);
            }
        }

        if (descriptor.getRelationships() != null) {
            for (IRelationshipElement relationship : descriptor.getRelationships()) {
                generateRelationship(relationship);
            }
        }

        Model base = getCacheItem(ABSTRACT);
        definitionModelAnnotation(base);

        for (Model model : models) {
            if (!model.isSingle()) {
                List<Field> fields = model.getFields();
                if (fields != null) {
                    List<Field> primary = new ArrayList<Field>(fields.size());
                    for (Field field : fields) {
                        if (field.getIndex() == IndexType.PRIMARY) {
                            primary.add(field);
                        }
                    }

                    if (primary.size() > 1) {
                        CompositeKey key = new CompositeKey(model, primary);
                        model.setKey(key);
                        model.addImport(key);
                        model.addImport(new ImportString(IdClass.class));
                    } else if (primary.size() == 1) {
                        model.setKey(new FieldKey(primary.get(0).getType()));
                    } else {
                        throw new IllegalStateException("Can't find any primary key for model: " + model.getName());
                    }
                }
            }
        }

        generateReport(models, descriptor.getReportElements());
        generateBIReport(models, descriptor.getBiReportElements());

        for (Model<Model> model : models) {
            if (model instanceof Report) {
                if (!((Report) model).isBiReport())
                    generate(model, "templates/code/server/report/report.vsl", structure.getSrc());
            } else {
                if (!model.isSingle()) {
                    generate(model, "templates/code/server/store/model.vsl", structure.getSrc());
                    if (model.getKey().isComposite())
                        generate((Base) model.getKey(), "templates/code/server/store/compositeKey.vsl", structure.getSrc());
                }
            }
        }

        return models;
    }

    private void linksEntity(IEntityElement[] elements, INamedPackageDescriptorElement parent) {
        if (elements != null) {
            for (IEntityElement element : elements) {
                element.setParent(parent);
                element.setPath(DiffUtils.lookup(parent.getPath(), parent.getName()));
                linksEntity(element.getConfiguredEntities(), element);
            }
        }
    }

    private void linksDomain(IDomainElement[] domains, INamedPackageDescriptorElement parent) {
        if (domains != null) {
            for (IDomainElement domain : domains) {
                domain.setParent(parent);
                domain.setPath(DiffUtils.lookup(parent.getPath(), parent.getName()));
                linksEntity(domain.getConfiguredEntities(), domain);
                linksDomain(domain.getConfiguredDomains(), domain);
            }
        }
    }

    private void constructDomainTree(String classPath, String path, String prefix, IDomainElement[] domains, List<Model> models) {
        if (domains != null) {
            for (IDomainElement domain : domains) {
                String name = StringUtils.normalize(domain.getName());
                String classPath0 = DiffUtils.lookup(classPath, name);

                generateModel(classPath0, classPath0, name, path, "", prefix + StringUtils.defaultString(domain.getPrefix()), models, domain.getConfiguredEntities());
                constructDomainTree(classPath0, path, prefix + StringUtils.defaultString(domain.getPrefix()), domain.getConfiguredDomains(), models);
            }
        }
    }

    public void generateModel(String classPath, String lookup, String serviceName, String pkg, String entityPath, String prefix, List<Model> domains, IEntityElement[] entities) {
        if (entities == null || entities.length == 0) {
            return;
        }

        for (IEntityElement element : entities) {
            Model model = new Model();

            String name = Utils.classFormatting(element.getName());
            String normalize = StringUtils.normalize(element.getName());

            model.setName(name);
            model.setNormalize(normalize);
            model.setOriginalName(element.getName());
            model.setLookup(DiffUtils.lookup(element.getPath(), element.getName()));
            model.setClassPath(classPath);
            model.setEntityPath(entityPath);
            model.setProjectPath(pkg);
            model.setServiceName(StringUtils.capitalize(serviceName));
            model.setSingle(element.isTypeEntity());
            model.setAbstracts(element.isAbstractEntity());
            model.setWsdl(isWsdl(element));
            model.setPrefix(prefix);
            model.setDescription(element.getDescription());

            String table = element.getDatabaseRefName();
            if (StringUtils.isBlank(table)) {
                table = resolver.resolveTableName(element);
            }
            model.setTable(table);

            String unique = generateUnique(element);
            model.setUnique(unique);

            if (element.getReferenceObject() != null) {
                ReferenceObjectData referenceObject = element.getReferenceObject();

                model.setReferenceHeading(Utils.conversionReferenceString(referenceObject.getHeading()));
                model.setReferenceSubHeading(Utils.conversionReferenceString(referenceObject.getSubHeading()));
                model.setReferenceDescription(Utils.conversionReferenceString(referenceObject.getDescription()));
            }

            String extendedEntityPath = element.getExtendedEntityPath();
            if (StringUtils.isNotBlank(extendedEntityPath)) {
                extendedEntityPath = DiffUtils.lookupByRefPath(extendedEntityPath);
                model.setParent(new Model(extendedEntityPath));
            } else {
                model.setParent(new Model(ABSTRACT));
            }

            if (element.isSubEntity()) {
                model.setOwner(new Model(element.getPath()));
            }

            String url = getCacheItem(model.getLookup());
            model.setUrl(url);

            IFieldElement[] fieldElements = element.getFields();
            if (fieldElements != null && fieldElements.length != 0) {
                String entityLookup = model.getLookup();

                for (IFieldElement fieldElement : fieldElements) {
                    if (fieldElement.isAutoGenerated() && fieldElement.getName().equals("type")) continue;

                    Field field = new Field();
                    if (model.isWsdl()) {
                        field.setName(Utils.wsdlFieldModelFormatting(fieldElement.getName()));
                        field.setMethod(StringUtils.capitalize(fieldElement.getName()));
                    } else {
                        field.setName(Utils.fieldModelFormatting(fieldElement.getName()));
                        field.setMethod(Utils.methodFormatting(fieldElement.getName()));
                    }
                    field.setColumn(fieldElement.getName().toLowerCase());
                    field.setType(fieldElement.getType());
                    field.setNullable(!fieldElement.isRequired());
                    field.setSearchable(fieldElement.isSearchable());
                    field.setDescription(fieldElement.getDescription());
                    field.setDisplayName(Utils.escape(fieldElement.getDisplayName()));
                    field.setDisplayDescription(Utils.escape(fieldElement.getDisplayDescription()));
                    field.setAutoGenerated(fieldElement.isAutoGenerated());

                    if (fieldElement.getDefaultValue() != null) {
                        field.setValue(fieldElement.getDefaultValue().toString());
                    }

                    if (StringUtils.isNotBlank(fieldElement.getAllowValues())) {
                        String[] values = fieldElement.getAllowValues().split(BaseUserType.ENTRY_DELIMITER_REGEXP);
                        List<String> list = new ArrayList<String>();
                        for (String value : values) {
                            list.add(FormattingUtils.escape(value));
                        }
                        field.setAllowValues(list);
                    }

                    if (fieldElement.getType().equals(FieldType.CREATION_TIME)) {
                        field.setInsertable(false);
                        field.setUpdatable(false);
                        field.setDefinition(true);
                        field.setAutoGenerated(true);
                    }

                    List<Reference> options = fieldElement.getOptions();
                    if (options != null) {
                        List<Model> models = new ArrayList<Model>(options.size());
                        field.setOptions(models);
                        for (Reference option : options) {
                            String optionLookup = DiffUtils.lookup(option.getRefPath(), option.getRefName());
                            models.add(new Model(optionLookup));
                        }
                    }
                    addCacheItem(DiffUtils.lookup(entityLookup, fieldElement.getName()), field);
                    model.addField(field);
                }
            }

            IIndexElement[] indexes = element.getIndexes();
            if (indexes != null) {
                for (IIndexElement index : indexes) {
                    if (index.getType() == IndexType.PRIMARY) {
                        List<IFieldElement> indexFields = index.getFields();
                        if (indexFields != null) {
                            for (IFieldElement indexField : indexFields) {
                                String indexFieldName = Utils.fieldModelFormatting(indexField.getName());
                                Field field = model.findField(indexFieldName);
                                field.setIndex(index.getType());
                                field.setNullable(false);
                            }
                        }
                    }
                }
            }

            domains.add(model);
            addCacheItem(model.getLookup(), model);
            addCacheItem(DiffUtils.lookup(model.getLookup(), "source"), element);
            generateModel(classPath, DiffUtils.lookup(lookup, normalize), serviceName, pkg, DiffUtils.lookup(entityPath, normalize), prefix, domains, element.getConfiguredEntities());
        }
    }

    private boolean isWsdl(IParentReferenceOwner entity) {
        if (entity instanceof IEntityElement)
            return isWsdl((IParentReferenceOwner) entity.getParent());
        return entity != null && entity instanceof IDomainElement && StringUtils.isNotBlank(((IDomainElement) entity).getWsdlLocation());
    }

    private String generateUnique(IEntityElement element) {
        String unique = "";
        String separator = "";
        IIndexElement[] indexes = element.getIndexes();
        if (indexes != null) {
            for (IIndexElement index : indexes) {
                if (index.getType() == IndexType.UNIQUE) {
                    String u = "@UniqueConstraint(columnNames = {";
                    String s = "";
                    List<IFieldElement> fields = index.getFields();
                    if (fields != null) {
                        for (IFieldElement field : fields) {
                            u += s + "\"" + field.getName() + "\"";
                            s = ",";
                        }
                    }
                    List<Reference> references = index.getEntities();
                    if (references != null) {
                        for (Reference reference : references) {
                            u += s + "\"" + reference.getConstraintName() + "\"";
                            s = ",";
                        }
                    }

                    u += "})";
                    unique += separator + u;
                    separator = ",";
                }
            }
        }
        return unique;
    }

    private void generateRelationship(IRelationshipElement relationship) {
        RelationshipType type = relationship.getType();


        Model source = getCacheItem(DiffUtils.lookupByRefPath(relationship.getSource().getRefPath()));
        Model target = source;
        if (relationship.getTarget() != null) {
            target = getCacheItem(DiffUtils.lookupByRefPath(relationship.getTarget().getRefPath()));
        }

        Field field = new Field();
        if (type.equals(RelationshipType.PARENT_CHILD) || type.equals(RelationshipType.TREE)) {
            field.setName("parent");
            field.setColumn("id_parent");
            field.setMethod("Parent");
            field.setRelationshipType(type);
            field.setType(FieldType.OBJECT);
            field.setMapped(MappedType.ManyToOne);
            field.setSource(source);
            field.setTarget(target);
            field.setHint(relationship.getHint());
        } else if (type.equals(RelationshipType.TYPE) || type.equals(RelationshipType.LINK)) {
            String name = relationship.getName();
            String sourceColumnName;
            if (relationship.getTarget().getRefName() != null) {
                sourceColumnName = relationship.getTarget().getRefName();
            } else {
                sourceColumnName = Utils.createTableName("id", target.getSimpleName());
            }

            field.setName(Utils.fieldFormatting(name));
            field.setColumn(sourceColumnName);
            field.setMethod(Utils.classFormatting(name));
            field.setType(FieldType.OBJECT);
            field.setRelationshipType(type);
            field.setMapped(MappedType.ManyToOne);
            field.setTarget(target);
            field.setHint(relationship.getHint());
        } else if (type.equals(RelationshipType.ASSOCIATION)) {
            String name = relationship.getName();

            if (source.isWsdl()) {
                field.setName(Utils.fieldFormatting(name));
                field.setMethod(Utils.classFormatting(name));
            } else {
                field.setName(Utils.fieldFormattingWithPluralEnding(name));
                field.setMethod(Utils.classFormattingWithPluralEnding(name));
            }
            field.setType(FieldType.LIST);
            field.setMapped(MappedType.ManyToMany);
            field.setRelationshipType(type);
            field.setSource(source);
            field.setTarget(target);
            field.setHint(relationship.getHint());
            field.setTable(resolver.resolveRelationshipTableName(relationship));

            if (relationship.getSource().getRefName() != null) {
                field.setJoin(relationship.getSource().getRefName());
            } else {
                field.setJoin(Utils.createTableName("id", source.getSimpleName()));
            }

            if (relationship.getTarget().getRefName() != null) {
                field.setInverseJoin(relationship.getTarget().getRefName());
            } else {
                field.setInverseJoin(Utils.createTableName("id", target.getSimpleName()));
            }
        }

        if (RelationshipOption.CASCADE.equals(relationship.getOnDeleteOptions())) {
            field.setDeleteCascade(RelationshipOption.CASCADE.equals(relationship.getOnDeleteOptions()));

            source.addImport(new ImportString(OnDelete.class));
            source.addImport(new ImportString(OnDeleteAction.class));
        }

        IEntityElement sourceElement = getCacheItem(DiffUtils.lookup(source.getLookup(), "source"));
        IIndexElement[] indexes = sourceElement.getIndexes();
        if (indexes != null) {
            for (IIndexElement index : indexes) {
                if (index.getType() == IndexType.PRIMARY) {
                    List<Reference> entities = index.getEntities();
                    if (entities != null) {
                        for (Reference entity : entities) {
                            if (field.getColumn().equals(entity.getConstraintName())) {
                                field.setIndex(IndexType.PRIMARY);
                                field.setNullable(false);
                            }
                        }
                    }
                    break;
                }
            }
        }

        source.addField(field);
        source.addImport(target);
        addCacheItem(DiffUtils.lookup(source.getLookup(), relationship.getName()), field);
    }

    private void definitionModelAnnotation(Model<Model> model) {
        for (Model child : model.getChildren()) {
            if (model.isType(ModelType.MappedSuperclass) && child.isEmptyChild()) {
                child.setType(ModelType.Table);
            } else if (model.isType(ModelType.MappedSuperclass) && !child.isEmptyChild()) {
                child.setType(ModelType.DiscriminatorColumn);
            } else if (model.isType(ModelType.DiscriminatorColumn) || model.isType(ModelType.DiscriminatorValue)) {
                child.setType(ModelType.DiscriminatorValue);
            } else if (model.isType(ModelType.Entity) && !child.isEmptyChild()) {
                child.setType(ModelType.Entity);
            }

            if (!child.isEmptyChild() && !child.isSingle()) {
                definitionModelAnnotation(child);
            }
        }
    }

    private void generateReport(List<Model> models, ReportElement[] reportElements) throws IOException {
        if (reportElements == null)
            return;

        for (ReportElement element : reportElements) {
            Model model = getCacheItem(element.getPath());
            if (model != null) {
                Report report = new Report(model);
                report.setName(Utils.classFormatting(element.getName()));
                report.setNormalize(StringUtils.normalize(element.getName()));
                report.setLookup(DiffUtils.lookup(element.getPath(), element.getName()));
                List<ReportField> elementFields = element.getFields();
                if (elementFields != null) {
                    for (ReportField elementField : elementFields) {
                        Field field = new Field();
                        StringBuilder projection = new StringBuilder();
                        StringBuilder name = new StringBuilder();

                        List<Reference> relationships = elementField.getRelationships();
                        if (relationships != null) {
                            for (Reference relationship : relationships) {
                                String lookup = DiffUtils.lookup(relationship.getRefPath(), relationship.getRefName());
                                Field relationshipField = getCacheItem(lookup);
                                projection.append(relationshipField.getName()).append(".");
                                name.append(StringUtils.capitalize(relationshipField.getName()));
                            }
                        }

                        String lookupField = elementField.getField();
                        Field reportField = getCacheItem(lookupField);

                        projection.append(reportField.getName());
                        name.append(StringUtils.capitalize(reportField.getName()));

                        field.setProjection(projection.toString());
                        field.setName(StringUtils.uncapitalize(name.toString()));
                        field.setMethod(Utils.methodFormatting(name.toString()));
                        field.setType(reportField.getType());
                        field.setTarget(reportField.getTarget());
                        field.setDisplayName(Utils.escape(elementField.getDisplayName()));
                        field.setHidden(!elementField.isVisible());

                        report.addField(field);
                    }
                }

                model.addReport(report);
                models.add(report);
                addCacheItem(report.getLookup(), report);
            }
        }
    }

    private void generateBIReport(List<Model> models, BIReportElement[] reportElements) throws IOException {
        if (reportElements == null)
            return;

        for (BIReportElement element : reportElements) {
            Model model = getCacheItem(element.getPath());
            if (model != null) {
                Report report = new Report(model);
                report.setName(Utils.classFormatting(element.getName()));
                report.setNormalize(StringUtils.normalize(element.getName()));
                report.setLookup(DiffUtils.lookup(element.getPath(), element.getName()));
                report.setBiReport(true);

                model.addReport(report);
                models.add(report);
                addCacheItem(report.getLookup(), report);
            }
        }
    }
}
