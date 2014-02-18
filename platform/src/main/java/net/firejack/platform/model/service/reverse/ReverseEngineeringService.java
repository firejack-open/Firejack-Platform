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

package net.firejack.platform.model.service.reverse;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.model.*;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.model.registry.domain.*;
import net.firejack.platform.core.model.registry.field.AllowedFieldValueList;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.field.IndexEntityReferenceModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.model.registry.resource.FileResourceModel;
import net.firejack.platform.core.model.registry.resource.FileResourceVersionModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.store.registry.*;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.core.utils.db.DBUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.reverse.analyzer.AbstractTableAnalyzer;
import net.firejack.platform.model.service.reverse.analyzer.MSSQLTableAnalyzer;
import net.firejack.platform.model.service.reverse.analyzer.MySQLTableAnalyzer;
import net.firejack.platform.model.service.reverse.analyzer.OracleTableAnalyzer;
import net.firejack.platform.model.service.reverse.bean.*;
import net.firejack.platform.model.wsdl.bean.Parameter;
import net.firejack.platform.model.wsdl.bean.Service;
import net.firejack.platform.model.wsdl.bean.Wsdl;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;

import static javax.jws.WebParam.Mode.*;
import static net.firejack.platform.api.registry.model.FieldType.*;
import static net.firejack.platform.api.registry.model.RelationshipType.*;

@Component
public class ReverseEngineeringService {

    private static final Logger logger = Logger.getLogger(ReverseEngineeringService.class);
    public static final String RETURN = "returnValue";
    public static final String WSDL_SCHEME = "wsdl-scheme";

    @Autowired
    @Qualifier("progressAspect")
    private ManuallyProgress progress;
    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IActionStore actionStore;
    @Autowired
    private IIndexStore indexStore;
    @Autowired
    private IFieldStore fieldStore;
    @Autowired
    private IRelationshipStore relationshipStore;
    @Autowired
    private IPackageStore packageStore;
    @Autowired
    private IPackageChangesStore changesStore;
    @Autowired
    private FileHelper helper;
    @Autowired
    @Qualifier("fileResourceStore")
    private IResourceStore<FileResourceModel> resourceStore;
    @Autowired
    @Qualifier("fileResourceVersionStore")
    private IResourceVersionStore<FileResourceVersionModel> resourceVersionStore;

    private ThreadLocal<PackageModel> packageModel = new InheritableThreadLocal<PackageModel>();

    @ProgressStatus(weight = 100, description = "Preparing for reverse engineering...")
    public void reverseEngineeringProcess(RegistryNodeModel registryNodeModel, DatabaseModel databaseModel) {
        String host = databaseModel.getServerName();
        String port = databaseModel.getPort().toString();
        String schema = databaseModel.getUrlPath();
        String sid = databaseModel.getParentPath();
        String user = databaseModel.getUsername();
        String password = databaseModel.getPassword();
        if (StringUtils.isBlank(host) || StringUtils.isBlank(port) || StringUtils.isBlank(schema) ||
                StringUtils.isBlank(user)) {
            throw new BusinessFunctionException("Required parameter is null.");
        }
        DatabaseName rdbms = databaseModel.getRdbms();
        String dbSchemaUrl = rdbms.getDbSchemaUrlConnection(DatabaseProtocol.JDBC, host, port, sid, schema);

        if (!OpenFlameDataSource.ping(rdbms, host, port, user, password, sid, schema)) {
            throw new BusinessFunctionException("Database by url: " + dbSchemaUrl + " is not exist.");
        }

        DataSource dataSource = DBUtils.populateDataSource(rdbms.getDriver(), dbSchemaUrl, user, password);

        List<Table> tables;
        AbstractTableAnalyzer tableAnalyzer = null;
        try {
            if (DatabaseName.MySQL.equals(rdbms)) {
                tableAnalyzer = new MySQLTableAnalyzer(dataSource, schema);
            } else if (DatabaseName.Oracle.equals(rdbms)) {
                tableAnalyzer = new OracleTableAnalyzer(dataSource, sid, schema);
            } else if (DatabaseName.MSSQL.equals(rdbms)) {
                tableAnalyzer = new MSSQLTableAnalyzer(dataSource, schema, sid);
            }
            if (tableAnalyzer != null) {
                tableAnalyzer.setProgress(progress);
                tables = tableAnalyzer.tableAnalyzing();
            } else {
                throw new BusinessFunctionException("Doesn't support " + rdbms.name() + " database.");
            }
        } catch (SQLException e) {
            throw new BusinessFunctionException(e);
        }

        this.packageModel.set(packageStore.findPackage(registryNodeModel.getLookup()));

        int tableSize = tables.size();
        if (tableSize > 0) {
            int tableWeight = 15000 / tableSize;
            int currentTable = 0;
            List<Table> indexTables = new ArrayList<Table>();
            Map<String, EntityModel> entityModels = new HashMap<String, EntityModel>();
            for (Table table : tables) {
                currentTable++;
                String message = "Processing table '" + table.getName() + "' [" + currentTable + " of " + tableSize + "]...";
                progress.status(message, tableWeight, LogLevel.INFO);
                logger.info(message);
                List<Column> columns = table.getColumns();
                List<Constraint> constraints = table.getConstraints();
                if (!(constraints.size() == 2 && columns.size() == 2)) {
                    EntityModel entityModel = convertToEntity(table, registryNodeModel);
                    List<FieldModel> fieldModels = convertToFields(table, entityModel);
                    entityModel.setFields(fieldModels);
                    entityModel.setReverseEngineer(true);
                    entityStore.save(entityModel);
                    savePackageChanges(entityModel);
                    entityModels.put(table.getName(), entityModel);
                    indexTables.add(table);
                }
            }

            progress.status("Processing relationships...", 200, LogLevel.INFO);
            List<RelationshipModel> relationshipModels = new ArrayList<RelationshipModel>();
            Map<String, RelationshipModel> constraintRelationships = new HashMap<String, RelationshipModel>();
            for (Table table : tables) {
                List<Constraint> constraints = table.getConstraints();
                if (!constraints.isEmpty()) {
                    List<Column> columns = table.getColumns();
                    List<Index> indexes = table.getIndexes();
                    Index primaryKey = findIndexByType(indexes, IndexType.PRIMARY);
                    List<Column> primaryKeyColumns = new ArrayList<Column>();
                    if (primaryKey != null) {
                        primaryKeyColumns = primaryKey.getColumns();
                    }

                    if (primaryKeyColumns.size() == 1 && constraints.size() < columns.size()) {
                        for (Constraint constraint : constraints) {
                            progress.status("Processing relationship '" + constraint.getName() + "' for table '" + table.getName() + "'...", 2, LogLevel.INFO);
                            RelationshipModel relationshipModel = convertToOneToManyRelationship(constraint, entityModels);
                            if (!checkDuplicateRelationship(relationshipModels, relationshipModel)) {
                                relationshipStore.save(relationshipModel);
                                savePackageChanges(relationshipModel);
                                relationshipModels.add(relationshipModel);
                                constraintRelationships.put(constraint.getName(), relationshipModel);
                            }
                        }
                    } else if (primaryKeyColumns.size() == 2 && columns.size() == 2 && constraints.size() == 2) {
                        progress.status("Processing relationship '" + table.getName() + "'...", 2, LogLevel.INFO);
                        RelationshipModel relationshipModel = convertToManyToManyRelationship(table, entityModels);
                        relationshipStore.save(relationshipModel);
                        savePackageChanges(relationshipModel);
                    } else if (primaryKeyColumns.size() > 1 && constraints.size() < columns.size()) {   //TODO it can be refactored with the first condition but later
                        for (Constraint constraint : constraints) {
                            progress.status("Processing relationship '" + constraint.getName() + "' for table '" + table.getName() + "'...", 2, LogLevel.INFO);
                            RelationshipModel relationshipModel = convertToOneToManyRelationship(constraint, entityModels);
                            if (!checkDuplicateRelationship(relationshipModels, relationshipModel)) {
                                relationshipStore.save(relationshipModel);
                                savePackageChanges(relationshipModel);
                                relationshipModels.add(relationshipModel);
                                constraintRelationships.put(constraint.getName(), relationshipModel);
                            }
                        }
                    } else {
                        progress.status("Not supported this table '" + table.getName() + "'...", 2, LogLevel.WARN);
                    }
                }
            }

            int fixedWeight = 15000 - tableWeight * tableSize;
            progress.status("Processing indexes for tables...", fixedWeight, LogLevel.INFO);
            for (Table table : indexTables) {
                logger.info("Create indexes for table: " + table.getName());
                List<IndexModel> indexModels = convertToIndexes(table, entityModels, constraintRelationships);
                for (IndexModel indexModel : indexModels) {
                    indexStore.save(indexModel);
                }
            }
        } else {
            progress.status("Not found any tables by url: " + dbSchemaUrl, 2, LogLevel.ERROR);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private EntityModel convertToEntity(Table table, RegistryNodeModel registryNodeModel) {
        EntityModel entityModel = new EntityModel();
        String entityName = table.getName().replaceAll("\\p{Punct}", " ").replaceAll("\\s+", " ").trim();
        if (entityName.matches("^\\d.*?")) {
            entityName = "Fix" + entityName;
        }
        entityName = WordUtils.capitalize(entityName.toLowerCase());
        entityModel.setName(entityName);
        entityModel.setLookup(registryNodeModel.getLookup() + "." + StringUtils.normalize(entityName));
        entityModel.setPath(registryNodeModel.getLookup());
        entityModel.setAbstractEntity(false);
        entityModel.setTypeEntity(false);
        entityModel.setProtocol(EntityProtocol.HTTP);
        entityModel.setDescription(table.getComment());
        entityModel.setParent(registryNodeModel);
        entityModel.setDatabaseRefName(table.getName());
        return entityModel;
    }

    private List<FieldModel> convertToFields(Table table, EntityModel entityModel) {
        List<FieldModel> fieldModels = new ArrayList<FieldModel>();
        List<Column> columns = table.getColumns();
        for (Column column : columns) {
            if (!checkConstraintColumn(table, column)) {
                FieldModel fieldModel = convertToField(column, entityModel);
                fieldModels.add(fieldModel);
            }
        }
        return fieldModels;
    }

    private FieldModel convertToField(Column column, EntityModel entityModel) {
        FieldModel fieldModel = new FieldModel();
        fieldModel.setName(column.getName());
        fieldModel.setPath(entityModel.getLookup());
        fieldModel.setLookup(entityModel.getLookup() + "." + StringUtils.normalize(column.getName()));
        fieldModel.setDescription(column.getComment());
        fieldModel.setAutoGenerated(Boolean.TRUE.equals(column.getAutoIncrement()));
        fieldModel.setDefaultValue(column.getDefaultValue());
        String displayFieldName = column.getName().replaceAll("\\p{Punct}", " ").replaceAll("\\s+", " ").trim();
        displayFieldName = WordUtils.capitalize(displayFieldName.toLowerCase());
        fieldModel.setDisplayName(displayFieldName);
        fieldModel.setDisplayDescription(column.getComment());
        fieldModel.setRequired(!column.getNullable());
        String customFieldType = column.getDataType();
        if (column.getLength() != null) {
            customFieldType += "(" + column.getLength();
            if (column.getDecimalDigits() != null) {
                customFieldType += ", " + column.getDecimalDigits();
            }
            customFieldType += ")";
        }
        fieldModel.setCustomFieldType(customFieldType);
        fieldModel.setFieldType(column.getFieldType());
        fieldModel.setParent(entityModel);
        return fieldModel;
    }

    private List<IndexModel> convertToIndexes(Table table, Map<String, EntityModel> entityModelMap, Map<String, RelationshipModel> constraintRelationships) {
        EntityModel entityModel = entityModelMap.get(table.getName());
        List<IndexModel> indexModels = new ArrayList<IndexModel>();
        List<Index> indexes = table.getIndexes();
        for (Index index : indexes) {
            IndexModel indexModel = new IndexModel();
            indexModel.setName(index.getName());
            indexModel.setPath(entityModel.getLookup());
            indexModel.setLookup(entityModel.getLookup() + "." + StringUtils.normalize(index.getName()));
            indexModel.setIndexType(index.getIndexType());
            List<IndexEntityReferenceModel> referenceEntityModels = new ArrayList<IndexEntityReferenceModel>();
            List<FieldModel> fieldModels = new ArrayList<FieldModel>();
            List<Constraint> constraints = new ArrayList<Constraint>();
            for (Column column : index.getColumns()) {
                Constraint constraint = getConstraintByColumn(table, column);
                if (constraint != null) {
                    Column sourceColumn = constraint.getSourceColumn();
                    Table destinationTable = constraint.getDestinationColumn().getTable();
                    EntityModel referenceEntityModel = entityModelMap.get(destinationTable.getName());
                    IndexEntityReferenceModel referenceModel = new IndexEntityReferenceModel();
                    referenceModel.setColumnName(sourceColumn.getName());
                    referenceModel.setEntityModel(referenceEntityModel);
                    referenceModel.setIndex(indexModel);
                    referenceEntityModels.add(referenceModel);
                    constraints.add(constraint);
                } else {
                    for (FieldModel fieldModel : entityModel.getFields()) {
                        if (fieldModel.getName().equals(column.getName())) {
                            if (IndexType.PRIMARY.equals(index.getIndexType()) &&
                                    (fieldModel.getFieldType().isLong() || fieldModel.getFieldType().isInteger())) {
                                fieldModel.setFieldType(FieldType.NUMERIC_ID);
                                fieldModel.setAutoGenerated(true);
                                fieldStore.saveOrUpdate(fieldModel);
                            }
                            fieldModels.add(fieldModel);
                            break;
                        }
                    }
                }
            }
            if (constraints.size() == 1) {
                Constraint constraint = constraints.get(0); // TODO it is temporary solution to bind relationship with index.
                RelationshipModel relationshipModel = constraintRelationships.get(constraint.getName());
                if (relationshipModel != null) {
                    continue;
                }
            }
            indexModel.setFields(fieldModels);
            indexModel.setReferences(referenceEntityModels);
            indexModel.setParent(entityModel);
            indexModels.add(indexModel);
        }
        return indexModels;
    }

    private boolean checkDuplicateRelationship(List<RelationshipModel> relationshipModels, RelationshipModel checkedRelationshipModel) {
        boolean isDuplicate = false;
        for (RelationshipModel relationshipModel : relationshipModels) {
            if (RelationshipType.TREE.equals(checkedRelationshipModel.getRelationshipType())) {
                if (relationshipModel.getSourceEntity().getLookup().equals(checkedRelationshipModel.getSourceEntity().getLookup())) {
                    isDuplicate = true;
                    break;
                }
            }
        }
        return isDuplicate;
    }

    private RelationshipModel convertToOneToManyRelationship(Constraint constraint, Map<String, EntityModel> entityModels) {
        RelationshipModel relationshipModel = new RelationshipModel();
        String relationshipName = constraint.getName().replaceAll("\\p{Punct}", " ").replaceAll("\\s+", " ").trim();
        relationshipName = WordUtils.capitalize(relationshipName.toLowerCase());
        relationshipModel.setName(relationshipName);

        Column sourceColumn = constraint.getSourceColumn();
        Table sourceTable = sourceColumn.getTable();
        EntityModel sourceEntityModel = entityModels.get(sourceTable.getName());
        relationshipModel.setSourceEntity(sourceEntityModel);

        Column destinationColumn = constraint.getDestinationColumn();
        Table destinationTable = destinationColumn.getTable();
        EntityModel destinationEntityModel = entityModels.get(destinationTable.getName());
        relationshipModel.setTargetEntity(destinationEntityModel);

        relationshipModel.setPath(sourceEntityModel.getLookup());
        relationshipModel.setLookup(sourceEntityModel.getLookup() + "." + StringUtils.normalize(relationshipName));
        if (sourceTable.equals(destinationTable) && "id_parent".equals(sourceColumn.getName())) {      //TODO hard code for OPF schemas
            relationshipModel.setRelationshipType(TREE);
            relationshipModel.setDescription("Relationship with type: TREE created by constraint: '" + constraint.getName() + "'");
        } else {
            relationshipModel.setRelationshipType(TYPE);
            relationshipModel.setDescription("Relationship with type: TYPE created by constraint: '" + constraint.getName() + "'");
            relationshipModel.setTargetConstraintName(constraint.getName());
            relationshipModel.setOnDeleteOption(convertToRelationshipOption(constraint.getOnDelete()));
            relationshipModel.setOnUpdateOption(convertToRelationshipOption(constraint.getOnUpdate()));
        }
        relationshipModel.setParent(sourceEntityModel);

        String sourceColumnName = sourceColumn.getName();
        if (relationshipModel.getRelationshipType() == PARENT_CHILD) {
            relationshipModel.setSourceConstraintName(sourceColumnName);
        } else {
            relationshipModel.setTargetEntityRefName(sourceColumnName);
        }
        relationshipModel.setReverseEngineer(true);

        return relationshipModel;
    }

    private RelationshipModel convertToManyToManyRelationship(Table table, Map<String, EntityModel> entityModels) {
        RelationshipModel relationshipModel = new RelationshipModel();
        String relationshipName = table.getName().replaceAll("\\p{Punct}", " ").replaceAll("\\s+", " ").trim();
        relationshipName = WordUtils.capitalize(relationshipName.toLowerCase());
        relationshipModel.setName(relationshipName);

        List<Constraint> constraints = table.getConstraints();

        Constraint constraint1 = constraints.get(1);
        Column source1Column = constraint1.getSourceColumn();
        Column destination1Column = constraint1.getDestinationColumn();
        Table destination1Table = destination1Column.getTable();
        EntityModel destination1EntityModel = entityModels.get(destination1Table.getName());
        relationshipModel.setSourceEntity(destination1EntityModel);
        relationshipModel.setSourceConstraintName(constraint1.getName());
        relationshipModel.setSourceEntityRefName(source1Column.getName());

        Constraint constraint2 = constraints.get(0);
        Column source2Column = constraint2.getSourceColumn();
        Column destination2Column = constraint2.getDestinationColumn();
        Table destination2Table = destination2Column.getTable();
        EntityModel destination2EntityModel = entityModels.get(destination2Table.getName());
        relationshipModel.setTargetEntity(destination2EntityModel);
        relationshipModel.setTargetConstraintName(constraint2.getName());
        relationshipModel.setTargetEntityRefName(source2Column.getName());
        relationshipModel.setPath(destination1EntityModel.getLookup());
        relationshipModel.setLookup(destination1EntityModel.getLookup() + "." + StringUtils.normalize(relationshipName));
        relationshipModel.setRelationshipType(RelationshipType.ASSOCIATION);
        relationshipModel.setParent(destination1EntityModel);

        relationshipModel.setOnDeleteOption(convertToRelationshipOption(constraint2.getOnDelete()));
        relationshipModel.setOnUpdateOption(convertToRelationshipOption(constraint2.getOnUpdate()));

        relationshipModel.setDescription("Relationship between tables: '" +
                destination1Table.getName() + " (" + destination1Column.getName() + ")' and " +
                destination2Table.getName() + " (" + destination2Column.getName() + ")'");
        relationshipModel.setReverseEngineer(true);

        return relationshipModel;
    }

    private RelationshipOption convertToRelationshipOption(Behavior behavior) {
        RelationshipOption relationshipOption;
        switch (behavior) {
            case CASCADE:
                relationshipOption = RelationshipOption.CASCADE;
                break;
            case NO_ACTION:
                relationshipOption = RelationshipOption.CASCADE;
                break;
            case RESTRICT:
                relationshipOption = RelationshipOption.RESTRICT;
                break;
            case SET_NULL:
                relationshipOption = RelationshipOption.SET_NULL;
                break;
            default:
                relationshipOption = RelationshipOption.RESTRICT;
        }
        return relationshipOption;
    }

    private boolean checkConstraintColumn(Table table, Column column) {
        boolean isFKColumn = false;
        List<Constraint> constraints = table.getConstraints();
        for (Constraint constraint : constraints) {
            isFKColumn |= constraint.getSourceColumn().equals(column);
        }
        return isFKColumn;
    }

    private Constraint getConstraintByColumn(Table table, Column column) {
        Constraint foundConstraint = null;
        List<Constraint> constraints = table.getConstraints();
        for (Constraint constraint : constraints) {
            if (constraint.getSourceColumn().equals(column)) {
                foundConstraint = constraint;
            }
        }
        return foundConstraint;
    }

    private Index findIndexByType(List<Index> indexes, IndexType type) {
        Index foundIndex = null;
        for (Index index : indexes) {
            if (type.equals(index.getIndexType())) {
                foundIndex = index;
                break;
            }
        }
        return foundIndex;
    }

    private void savePackageChanges(RegistryNodeModel entity) {
        PackageChangesModel changesModel = new PackageChangesModel();
        changesModel.setPackageModel(this.packageModel.get());
        changesModel.setEntity(entity);

        StringBuilder sb = new StringBuilder();
        sb.append("New ")
                .append(entity.getClass().getSimpleName().replace("Model", ""))
                .append(" ")
                .append(entity.getName())
                .append(" has been created");
        changesModel.setDescription(sb.toString());
        changesStore.saveOrUpdate(changesModel);
    }

    @ProgressStatus(weight = 100, description = "Preparing for reverse engineering...")
    public void reverseEngineeringProcess(RegistryNodeModel registryNodeModel, String wsdl) throws IOException, InterruptedException, NotFoundException, CannotCompileException, JAXBException {
        List<EntityModel> models = entityStore.findChildrenByParentId(registryNodeModel.getId(), null);
        for (EntityModel model : models) {
            entityStore.deleteRecursiveById(model.getId());
        }
        File dir = generate(wsdl, registryNodeModel.getName());
        ClassLoader classLoader = new ClassLoader(dir);
        Collection<Class> classes = classLoader.allClasses();

        analyze(classes, registryNodeModel);

        FileUtils.forceDelete(dir);
    }

    private File generate(String wsdl, String domain) throws IOException, InterruptedException {
        String name = SecurityHelper.generateRandomSequence(16);
        File temp = new File(FileUtils.getTempDirectory(), name);
        FileUtils.forceMkdir(temp);

        Process exec = Runtime.getRuntime().exec(new String[]{"wsimport", "-d", temp.getPath(), "-p", "wsdl." + domain, "-target", "2.1", "-extension", wsdl});
        exec.getInputStream().close();
        exec.getErrorStream().close();
        exec.getOutputStream().close();
        exec.waitFor();

        return temp;
    }

    public void saveResource(String resource, String fileName, RegistryNodeModel model, File file) throws IOException {
        if (!file.exists())
            return;

        FileResourceModel resourceModel = resourceStore.findByLookup(DiffUtils.lookup(model.getLookup(), resource));
        FileResourceVersionModel resourceVersionModel = null;
        if (resourceModel != null) {
            resourceVersionModel = resourceVersionStore.findLastVersionByResourceId(resourceModel.getId());
        }

        String fileResourceVersionFilename;

        if (resourceVersionModel == null) {
            resourceVersionModel = new FileResourceVersionModel();
            resourceVersionModel.setOriginalFilename(fileName);
            resourceVersionModel.setVersion(1);
            resourceVersionModel.setCulture(Cultures.AMERICAN);

            resourceModel = new FileResourceModel();
            resourceModel.setName(resource);
            resourceModel.setParent(model);
            resourceModel.setSelectedVersion(1);
            resourceModel.setResourceVersion(resourceVersionModel);

            resourceStore.save(resourceModel);
        }

        fileResourceVersionFilename = FileHelper.getResourceName(resourceVersionModel);

        InputStream stream = FileUtils.openInputStream(file);
        OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT, fileResourceVersionFilename, stream, helper.getFile(), String.valueOf(resourceModel.getId()));
        IOUtils.closeQuietly(stream);
    }

    private void analyze(Collection<Class> classes, RegistryNodeModel model) throws IOException, JAXBException {
        Class service = null;
        Class endpoint = null;
        for (Class clazz : classes) {
            if (clazz.isInterface() && clazz.isAnnotationPresent(WebService.class)) {
                service = clazz;
                if (!clazz.isAnnotationPresent(SOAPBinding.class))
                    break;
            }
        }

        for (Class clazz : classes) {
            if (javax.xml.ws.Service.class.isAssignableFrom(clazz)) {
                endpoint = clazz;
                break;
            }
        }

        if (service == null)
            throw new BusinessFunctionException("Cannot find Service class");

        Wsdl wsdl = new Wsdl(endpoint, service);

        analyzeService(service.getDeclaredMethods(), model, wsdl);

        String name = SecurityHelper.generateRandomSequence(16);
        File temp = new File(FileUtils.getTempDirectory(), name);
        FileOutputStream stream = FileUtils.openOutputStream(temp);
        FileUtils.writeJAXB(wsdl, stream);
        IOUtils.closeQuietly(stream);

        saveResource(WSDL_SCHEME, model.getName() + ".sch", model, temp);
        FileUtils.forceDelete(temp);
    }

    private void analyzeService(Method[] methods, RegistryNodeModel model, Wsdl wsdl) {
        Map<String, EntityModel> models = new HashMap<String, EntityModel>();
        List<RelationshipModel> relationships = new ArrayList<RelationshipModel>();
        List<ActionModel> actions = new ArrayList<ActionModel>(methods.length);

        for (Method method : methods) {
            if (method.isAnnotationPresent(WebMethod.class)) {
                Service service = new Service(method.getName());

                RequestWrapper requestWrapper = method.getAnnotation(RequestWrapper.class);
                ResponseWrapper responseWrapper = method.getAnnotation(ResponseWrapper.class);

                String messageName = StringUtils.capitalize(method.getName());
                String request = requestWrapper != null ? requestWrapper.localName() : messageName;
                String response = responseWrapper != null ? responseWrapper.localName() : messageName + "Response";

                EntityModel input = createWrapperEntity(request, method, model, true, relationships, models, service);
                EntityModel output = createWrapperEntity(response, method, model, false, relationships, models, service);
                ActionModel action = createAction(method.getName(), output, input);
                actions.add(action);
                wsdl.addService(service);
            }
        }

        this.packageModel.set(packageStore.findPackage(model.getLookup()));

        for (EntityModel entity : models.values()) {
            save(entity);
            savePackageChanges(entity);
        }

        for (RelationshipModel relationship : relationships) {
            relationshipStore.save(relationship);
            progress.status("Processing relationship '" + relationship.getName() + "'...", 2, LogLevel.INFO);
            savePackageChanges(relationship);
        }

        for (ActionModel action : actions) {
            actionStore.save(action);
            progress.status("Processing Action '" + action.getName() + "'...", 2, LogLevel.INFO);
            savePackageChanges(action);
        }
    }


    private EntityModel createWrapperEntity(String name, Method method, RegistryNodeModel registryNodeModel, boolean request, List<RelationshipModel> relationships, Map<String, EntityModel> models, Service service) {
        EntityModel model = models.get(name);
        if (model != null)
            return model;

        String modelName = name.replaceAll("\\B([A-Z]+)\\B", " $1");

        EntityModel entityModel = new EntityModel();
        entityModel.setName(modelName);
        entityModel.setLookup(DiffUtils.lookup(registryNodeModel.getLookup(), modelName));
        entityModel.setPath(registryNodeModel.getLookup());
        entityModel.setAbstractEntity(false);
        entityModel.setTypeEntity(true);
        entityModel.setReverseEngineer(true);
        entityModel.setProtocol(EntityProtocol.HTTP);
        entityModel.setParent(registryNodeModel);

        models.put(name, entityModel);

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Type[] parameterTypes = method.getGenericParameterTypes();
        List<FieldModel> fields = new ArrayList<FieldModel>(parameterTypes.length);

        for (int i = 0; i < parameterTypes.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for (Annotation annotation : annotations) {
                if (WebParam.class.isInstance(annotation)) {
                    WebParam param = (WebParam) annotation;
                    if (param.mode() == INOUT || request && param.mode() == IN || !request && param.mode() == OUT) {
                        analyzeField(param.name(), parameterTypes[i], TYPE, entityModel, null, models, relationships, fields);
                        analyzeField(param.name(), parameterTypes[i], param.mode(), false, false, false, service);
                    }
                    break;
                }
            }
        }

        Type returnType = method.getGenericReturnType();
        if (!request && void.class != returnType) {
            analyzeField(RETURN, returnType, TYPE, entityModel, null, models, relationships, fields);
            analyzeField(RETURN, returnType, null, false, false, true, service);
        }

        entityModel.setFields(fields);

        return entityModel;
    }

    private EntityModel createBeanEntity(Class clazz, RegistryNodeModel registryNodeModel, List<RelationshipModel> relationships, Map<String, EntityModel> models) {
        if (clazz == Object.class)
            return null;

        EntityModel model = models.get(clazz.getName());
        if (model != null)
            return model;

        String name = clazz.getSimpleName().replaceAll("\\B([A-Z]+)\\B", " $1");

        EntityModel entityModel;
        if (clazz.isMemberClass()) {
            entityModel = new SubEntityModel();
            name = "Nested " + name;
            registryNodeModel = models.get(clazz.getEnclosingClass().getName());
        } else {
            entityModel = new EntityModel();
        }

        models.put(clazz.getName(), entityModel);

        entityModel.setName(name);
        entityModel.setLookup(DiffUtils.lookup(registryNodeModel.getLookup(), name));
        entityModel.setPath(registryNodeModel.getLookup());
        entityModel.setAbstractEntity(Modifier.isAbstract(clazz.getModifiers()));
        entityModel.setTypeEntity(true);
        entityModel.setReverseEngineer(true);
        entityModel.setExtendedEntity(createBeanEntity(clazz.getSuperclass(), registryNodeModel, relationships, models));
        entityModel.setProtocol(EntityProtocol.HTTP);
        entityModel.setParent(registryNodeModel);

        Field[] declaredFields = clazz.getDeclaredFields();
        List<FieldModel> fields = new ArrayList<FieldModel>(declaredFields.length);

        for (Field field : declaredFields) {
            analyzeField(field.getName(), field.getGenericType(), TYPE, entityModel, field.getAnnotation(XmlElement.class), models, relationships, fields);

            XmlElements xmlElements = field.getAnnotation(XmlElements.class);
            if (xmlElements != null) {
                XmlElement[] elements = xmlElements.value();
                List<EntityModel> options = new ArrayList<EntityModel>(elements.length);
                RegistryNodeModel parent = clazz.isMemberClass() ? registryNodeModel.getParent() : registryNodeModel;
                for (XmlElement element : elements) {
                    if (element.type().isAnnotationPresent(XmlAccessorType.class)) {
                        EntityModel entity = createBeanEntity(element.type(), parent, relationships, models);
                        options.add(entity);
                    }
                }
                FieldModel fieldModel = fields.get(fields.size() - 1);
                fieldModel.setOptions(options);
            }
        }

        entityModel.setFields(fields);

        return entityModel;
    }

    private void analyzeField(String name, Type type, RelationshipType relationshipType, EntityModel entityModel, XmlElement element, Map<String, EntityModel> models, List<RelationshipModel> relationships, List<FieldModel> fields) {
        if (type instanceof Class) {
            Class clazz = (Class) type;
            boolean required = false;
            if (element != null) {
                required = element.required();
                if (!name.equals(StringUtils.uncapitalize(element.name())) && !"##default".equals(element.name()))
                    name = element.name();
            }

            if (clazz.isAnnotationPresent(XmlAccessorType.class)) {
                EntityModel target = createBeanEntity(clazz, entityModel.getParent(), relationships, models);
                RelationshipModel relationshipModel = createRelationship(name, required, entityModel, target, relationshipType);
                relationships.add(relationshipModel);
            } else {
                FieldModel field = createField(name, clazz, required, entityModel);
                fields.add(field);
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            Type generic = parameterizedType.getActualTypeArguments()[0];
            if (rawType == Holder.class) {
                analyzeField(name, generic, relationshipType, entityModel, element, models, relationships, fields);
            } else if (rawType == List.class) {
                if (generic instanceof Class && ((Class) generic).isAnnotationPresent(XmlAccessorType.class)) {
                    analyzeField(name, generic, ASSOCIATION, entityModel, element, models, relationships, fields);
                } else {
                    analyzeField(name, rawType, relationshipType, entityModel, element, models, relationships, fields);
                }
            } else {
                throw new IllegalStateException("Unknown type: " + type + " entity: " + entityModel.getName());
            }
        }
    }

    private void analyzeField(String name, Type type, WebParam.Mode mode, boolean list, boolean holder, boolean _return, Service service) {
        if (type instanceof Class) {
            Class clazz = (Class) type;
            Parameter parameter = new Parameter(StringUtils.uncapitalize(name), clazz, clazz.isAnnotationPresent(XmlAccessorType.class), list, holder, mode);
            if (_return) {
                service.setReturn(parameter);
            } else {
                service.addParameter(parameter);
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            Type generic = parameterizedType.getActualTypeArguments()[0];
            if (rawType == Holder.class) {
                analyzeField(name, generic, mode, false, true, _return, service);
            } else if (rawType == List.class) {
                if (generic instanceof Class && ((Class) generic).isAnnotationPresent(XmlAccessorType.class)) {
                    analyzeField(name, generic, mode, true, holder, _return, service);
                } else {
                    analyzeField(name, rawType, mode, true, holder, _return, service);
                }
            } else {
                throw new IllegalStateException("Unknown type: " + type);
            }
        }
    }

    private FieldModel createField(String name, Class type, boolean required, EntityModel entityModel) {
        FieldModel fieldModel = new FieldModel();
        fieldModel.setName(StringUtils.uncapitalize(name));
        fieldModel.setPath(entityModel.getLookup());
        fieldModel.setLookup(DiffUtils.lookup(entityModel.getLookup(), name));
        fieldModel.setDisplayName(StringUtils.capitalize(name));
        fieldModel.setRequired(required);
        fieldModel.setParent(entityModel);

        if (type.isEnum()) {
            Enum[] enumConstants = (Enum[]) type.getEnumConstants();
            fieldModel.setFieldType(LONG_TEXT);
            AllowedFieldValueList valueList = new AllowedFieldValueList();
            for (Enum enumConstant : enumConstants)
                valueList.add(enumConstant.name());
            fieldModel.setAllowedFieldValueList(valueList);
        } else {
            fieldModel.setFieldType(convert(type));
        }

        return fieldModel;
    }

    private RelationshipModel createRelationship(String name, boolean required, EntityModel source, EntityModel target, RelationshipType type) {
        name = StringUtils.capitalize(name.replaceAll("\\B([A-Z]+)\\B", " $1"));

        RelationshipModel relationshipModel = new RelationshipModel();
        relationshipModel.setName(name);
        relationshipModel.setRequired(required);
        relationshipModel.setSourceEntity(source);
        relationshipModel.setTargetEntity(target);
        relationshipModel.setTargetEntityRefName(name);
        relationshipModel.setParent(source);
        relationshipModel.setPath(source.getLookup());
        relationshipModel.setLookup(DiffUtils.lookup(source.getLookup(), name));
        relationshipModel.setRelationshipType(type);
        relationshipModel.setChildCount(0);

        return relationshipModel;
    }


    private FieldType convert(Class clazz) {
        if (clazz == String.class)
            return LONG_TEXT;
        else if (clazz == Long.class || clazz == long.class)
            return LARGE_NUMBER;
        else if (clazz == java.sql.Date.class)
            return DATE;
        else if (clazz == java.sql.Time.class)
            return TIME;
        else if (clazz == Date.class || clazz == XMLGregorianCalendar.class)
            return FieldType.EVENT_TIME;
        else if (clazz == Integer.class || clazz == BigInteger.class || clazz == int.class)
            return INTEGER_NUMBER;
        else if (clazz == Double.class || clazz == Float.class || clazz == BigDecimal.class || clazz == float.class || clazz == double.class)
            return DECIMAL_NUMBER;
        else if (clazz == Boolean.class || clazz == boolean.class)
            return FLAG;
        else if (clazz == Byte.class)
            return BLOB;
        else if (List.class.isAssignableFrom(clazz))
            return LIST;

        throw new IllegalArgumentException("Can't find field type for: " + clazz);
    }

    private void save(EntityModel model) {
        EntityModel extendedEntity = model.getExtendedEntity();
        RegistryNodeModel parent = model.getParent();
        if (extendedEntity != null && extendedEntity.getId() == null)
            save(extendedEntity);
        if (parent != null && parent.getId() == null)
            save((EntityModel) parent);
        progress.status("Processing Entity '" + model.getName() + "'...", 2, LogLevel.INFO);
        entityStore.save(model);
    }

    private ActionModel createAction(String name, EntityModel parent, EntityModel input) {
        String normalize = name.replaceAll("\\B([A-Z]+)\\B", "-$1").toLowerCase();

        ActionModel action = new ActionModel();
        action.setParent(parent);
        action.setChildCount(0);
        action.setName(normalize);
        action.setPath(parent.getLookup());
        action.setLookup(DiffUtils.lookup(parent.getLookup(), normalize));
        action.setMethod(HTTPMethod.POST);
        action.setServerName(parent.getServerName());
        action.setParentPath(parent.getParentPath());
        action.setPort(parent.getPort());
        action.setSoapMethod(name);
        action.setProtocol(EntityProtocol.HTTP);

        action.setStatus(parent.getStatus());
        action.setInputVOEntity(input);
        action.setOutputVOEntity(parent);

        return action;
    }

    public final class ClassLoader extends java.lang.ClassLoader implements FileFilter {
        private final Map<String, Class> classes = new HashMap<String, Class>();
        private final Map<String, byte[]> bytes = new HashMap<String, byte[]>();

        public ClassLoader(File file) throws IOException {
            readClass(file.getPath(), file.listFiles(this));

            for (Map.Entry<String, byte[]> stringEntry : bytes.entrySet()) {
                String name = stringEntry.getKey();
                if (!classes.containsKey(name)) {
                    byte[] data = stringEntry.getValue();
                    Class<?> clazz = defineClass(name, data, 0, data.length);
                    classes.put(name, clazz);
                }
            }

            bytes.clear();
        }

        private void readClass(String path, File[] files) throws IOException {
            if (files == null)
                return;
            for (File file : files) {
                if (file.isFile()) {
                    String className = file.getPath().replace(path + File.separator, "").replaceAll("\\\\|/", ".").replace(".class", "");
                    byte[] bytes = FileUtils.readFileToByteArray(file);
                    this.bytes.put(className, bytes);
                } else {
                    readClass(path, file.listFiles(this));
                }
            }
        }

        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory() || pathname.getName().endsWith(".class");
        }

        private Collection<Class> allClasses() {
            return classes.values();
        }

        @Override
        protected Class findClass(String name) throws ClassNotFoundException {
            Class clazz = classes.get(name);
            if (clazz == null) {
                byte[] data = bytes.get(name);
                if (data != null) {
                    clazz = defineClass(name, data, 0, data.length);
                    classes.put(name, clazz);
                }
            }

            if (clazz == null) {
                throw new ClassNotFoundException(name);
            }
            return clazz;
        }
    }
}
