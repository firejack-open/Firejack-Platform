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

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.config.export.IPackageExporter;
import net.firejack.platform.core.config.export.PackageExportResult;
import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.construct.ConfigElementFactory;
import net.firejack.platform.core.config.meta.diff.*;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.patch.DefaultPatchContext;
import net.firejack.platform.core.config.patch.IPatchProcessor;
import net.firejack.platform.core.config.patch.SupportPackageVersionPatchProcessor;
import net.firejack.platform.core.config.patch.UIDPatchProcessor;
import net.firejack.platform.core.config.translate.AddExtendedEntityAttributeSqlDecorator;
import net.firejack.platform.core.config.translate.IPackageDescriptorTranslator;
import net.firejack.platform.core.config.translate.ITranslationResult;
import net.firejack.platform.core.config.translate.StatusProviderTranslationResult;
import net.firejack.platform.core.config.translate.sql.ISqlNameResolver;
import net.firejack.platform.core.config.translate.sql.LeadIdPrefixNameResolver;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.registry.DatabaseName;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.store.registry.IDomainStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.OpenFlameDataSource;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.upgrader.dbengine.DialectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.util.*;

@Component
public class PackageInstallationService {

    @Autowired
    @Qualifier("xmlToRegistryTranslator")
    private IPackageDescriptorTranslator<Boolean> xmlToRegistryTranslator;

    @Autowired
    @Qualifier("basicPackageExporter")
    private IPackageExporter packageExporter;
	@Autowired
	private FileHelper helper;

    @Autowired
    @Qualifier("domainStore")
    private IDomainStore domainStore;

	private ISqlNameResolver resolver = new LeadIdPrefixNameResolver();

    /**
     *
     * @param stream package.xml file reference
     * @return package descriptor
     */
    public IPackageDescriptor getPackageDescriptor(InputStream stream) {
        IPatchProcessor patchProcessor = new UIDPatchProcessor();
        return patchProcessor.loadVersionPackage(stream);
    }

    /**
     * @param packageXml package.xml file reference
     * @param dataSource data source
     * @return package descriptor
     */
    public IPackageDescriptor install(File packageXml, DataSource dataSource) {
        IPatchProcessor patchProcessor = new UIDPatchProcessor();
        IPackageDescriptor packageDescriptor = patchProcessor.loadVersionPackage(packageXml);
        IElementDiffInfoContainer elementDiffContainer = patchProcessor.processDifferences(null, packageDescriptor);

        DefaultPatchContext patchContext = patchProcessor(dataSource, packageDescriptor);
        patchProcessor.applyPatch(elementDiffContainer, patchContext);
        return packageDescriptor;
    }

    /**
     *
     * @param packageXml package.xml file reference
     * @param targets data source map
     * @param source
     * @return package descriptor
     */
    public IPackageDescriptor install(File packageXml, Map<String, DataSource> targets, Map<String, DataSource> source) {
        if (packageXml == null) {
            throw new IllegalArgumentException("package.xml file should not be null.");
        } else if (!packageXml.exists()) {
            throw new IllegalArgumentException("package.xml file should be exist.");
        } else if (targets == null) {
            throw new IllegalArgumentException("Data Source map should not be null.");
        } else if (targets.isEmpty()) {
            throw new IllegalArgumentException("Data Source map should not be empty.");
        }
        IPatchProcessor patchProcessor = new UIDPatchProcessor();
        IPackageDescriptor packageDescriptor = patchProcessor.loadVersionPackage(packageXml);
        Map<String, DialectType> sqlDialectMapping = getSqlDialectMapping(packageDescriptor);
        Set<String> dialectMappingKeySet;
        if (sqlDialectMapping == null) {
            throw new BusinessFunctionException("Dialect Mapping is empty.");
        } else {
            dialectMappingKeySet = sqlDialectMapping.keySet();
            Set<String> dataSourceKeySet = targets.keySet();
            if (!dialectMappingKeySet.containsAll(dataSourceKeySet)) {
                throw new BusinessFunctionException("Data Source Mapping does not correspond to SQL Dialect Mapping.");
            }
            if (sqlDialectMapping.containsValue(null)) {
                throw new BusinessFunctionException("Not all domains marked with DataSource flag have actual database associated.");
            }
        }
        IElementDiffInfoContainer diffContainer = patchProcessor.processDifferences(null, packageDescriptor);
        for (Map.Entry<String, DataSource> entry : targets.entrySet()) {
            String lookupContext = entry.getKey();
            DataSource dataSource = entry.getValue();

            DefaultPatchContext patchContext = patchProcessor(dataSource, packageDescriptor);
	        if (source != null) {
                patchContext.setSourceDataSource(source.get(lookupContext));
            }

            patchContext.setSqlDialect(sqlDialectMapping.get(lookupContext));

            Collection<String> collection = calculateExclusionLookupList(lookupContext, dialectMappingKeySet);
            IElementDiffInfoContainer patch = narrowTo(diffContainer, lookupContext, collection);

            patchProcessor.applyPatch(patch, patchContext);
        }
        return packageDescriptor;
    }

    public void migrate(File packageXml, DataSource source, DataSource target) {
        if (packageXml == null) {
            throw new IllegalArgumentException("package.xml file should not be null.");
        } else if (!packageXml.exists()) {
            throw new IllegalArgumentException("package.xml file should be exist.");
        } else if (source == null || target == null) {
            throw new IllegalArgumentException("Data Source should not be null.");
        } else if (!(source instanceof OpenFlameDataSource && target instanceof OpenFlameDataSource)) {
            throw new IllegalArgumentException("Incorrect data-source type.");
        }

        IPatchProcessor patchProcessor = new UIDPatchProcessor();
        IPackageDescriptor packageDescriptor = patchProcessor.loadVersionPackage(packageXml);
        IElementDiffInfoContainer diffContainer = patchProcessor.processDifferences(null, packageDescriptor);

        DefaultPatchContext patchContext = patchProcessor(target, packageDescriptor);
        patchContext.setSourceDataSource(source);
        OpenFlameDataSource targetDataSource = (OpenFlameDataSource) target;
        DialectType dialectType;
        switch (targetDataSource.getName()) {
            case Oracle: dialectType = DialectType.ORACLE; break;
            case MSSQL: dialectType = DialectType.MSSQL; break;
            case MySQL: dialectType = DialectType.MySQL5; break;
            default: dialectType = null;
        }
        patchContext.setSqlDialect(dialectType);
        patchProcessor.applyPatch(diffContainer, patchContext);
    }

    /**
     * @param packageDescriptor package descriptor
     * @return generated sql
     */
    public Map<String, String> generateSQL(IPackageDescriptor packageDescriptor) {
        Map<String, DialectType> dialectMappings = getSqlDialectMapping(packageDescriptor);
        IPatchProcessor<List<String>> patchProcessor = new UIDPatchProcessor();
        IElementDiffInfoContainer elementDiffContainer = patchProcessor.processDifferences(null, packageDescriptor);

        Map<String, String> scriptMapping = new HashMap<String, String>();
        if (dialectMappings.isEmpty()) {
            String lookupContext = DiffUtils.lookup(packageDescriptor);
            for (DialectType dialect : DialectType.values()) {
                composePatch(packageDescriptor, elementDiffContainer, dialect,
                        scriptMapping, lookupContext, patchProcessor, null);
            }
        } else {
            Set<String> allContexts = dialectMappings.keySet();
            for (Map.Entry<String, DialectType> entry : dialectMappings.entrySet()) {
                String lookupContext = entry.getKey();
                DialectType sqlDialect = entry.getValue();
                if (sqlDialect == null) {
                    for (DialectType dialect : DialectType.values()) {
                        composePatch(packageDescriptor, elementDiffContainer, dialect,
                                scriptMapping, lookupContext, patchProcessor, allContexts);
                    }
                } else {
                    composePatch(packageDescriptor, elementDiffContainer, sqlDialect,
                            scriptMapping, lookupContext, patchProcessor, allContexts);
                }
            }
        }
        return scriptMapping;
    }

    /**
     * @param packageDescriptor package descriptor
     * @return list of generated sql statements
     */
    public Map<String, List<String>> generateSQLTables(IPackageDescriptor packageDescriptor) {
        Map<String, List<String>> tablesMap = new HashMap<String, List<String>>();

	    generateSQL(packageDescriptor);
	    linksDomain(packageDescriptor.getConfiguredDomains(), packageDescriptor);
	    linksEntity(packageDescriptor.getConfiguredEntities(), packageDescriptor);

	    List<IRelationshipElement> elements = new ArrayList<IRelationshipElement>();
	    IRelationshipElement[] relationships = packageDescriptor.getRelationships();
	    if(relationships!=null){
		    for (IRelationshipElement relationship : relationships) {
			    if(relationship.getType().equals(RelationshipType.ASSOCIATION)){
				    elements.add(relationship);
			    }
		    }
	    }

        Map<String, String> tableMapping = new HashMap<String, String>();
	    resolveDomain(packageDescriptor.getConfiguredDomains(), elements, tableMapping);
	    resolveEntity(packageDescriptor.getConfiguredEntities(), elements, tableMapping);

        Map<String, DialectType> dialectMappings = getSqlDialectMapping(packageDescriptor);
        String packageLookup = DiffUtils.lookup(packageDescriptor.getPath(), packageDescriptor.getName());
        dialectMappings.remove(packageLookup);
        for (Map.Entry<String, String> tEntry : tableMapping.entrySet()) {
            String entityLookup = tEntry.getKey();
            String entityName = tEntry.getValue();
            boolean isFound = false;
            for (Map.Entry<String, DialectType> entry : dialectMappings.entrySet()) {
                String lookupContext = entry.getKey();
                isFound = entityLookup.startsWith(lookupContext);
                if (isFound) {
                    List<String> tables = tablesMap.get(lookupContext);
                    if (tables == null) {
                        tables = new ArrayList<String>();
                        tablesMap.put(lookupContext, tables);
                    }
                    tables.add(entityName);
                    break;
                }
            }
            if (!isFound) {
                List<String> tables = tablesMap.get(packageLookup);
                if (tables == null) {
                    tables = new ArrayList<String>();
                    tablesMap.put(packageLookup, tables);
                }
                tables.add(entityName);
            }
        }
	    return tablesMap;
    }

    public Map<String, DialectType> getSqlDialectMapping(IPackageDescriptor packageDescriptor) {
        String packageUID = packageDescriptor.getUid();
        Map<String, DatabaseModel> databaseAssociations = domainStore.findAllWithDataSourcesByPackageUID(packageUID);
        Map<String, DialectType> dialectMapping;
        if (databaseAssociations == null) {
            dialectMapping = null;
        } else {
            dialectMapping = new HashMap<String, DialectType>();
            for (Map.Entry<String, DatabaseModel> entry : databaseAssociations.entrySet()) {
                DialectType dialectType = dialectFromDB(entry.getValue());
                dialectMapping.put(entry.getKey(), dialectType);
            }
        }
        return dialectMapping;
    }

    /**
     *
     *
     * @param packageXml package xml input stream
     * @param resourceZip resources archive input stream
     * @return operation result
     */
    public StatusProviderTranslationResult activatePackage(InputStream packageXml, InputStream resourceZip) {
	    if (resourceZip != null) {
		    OPFEngine.FileStoreService.unzip(OpenFlame.FILESTORE_BASE, resourceZip, helper.getTemp());
	    }

	    IPatchProcessor patchProcessor = new UIDPatchProcessor();
	    IPackageDescriptor packageDescriptor = patchProcessor.loadVersionPackage(packageXml);
	    if (packageDescriptor == null || packageDescriptor.getUid() == null) {
		    throw new OpenFlameRuntimeException("Wrong parse result.");
	    }
	    IPackageDescriptor oldPackageDescriptor = packageExporter.exportPackageDescriptor(packageDescriptor.getUid());
	    String oldPackageXml = null;
	    if (oldPackageDescriptor != null) {
		    PackageExportResult convertedToString = packageExporter.exportPackageDescriptor(oldPackageDescriptor);
		    oldPackageXml = convertedToString.getPackageXml();
	    }
	    IElementDiffInfoContainer elementDiffContainer = patchProcessor.processDifferences(oldPackageDescriptor, packageDescriptor);
	    StatusProviderTranslationResult translationResult = (StatusProviderTranslationResult) xmlToRegistryTranslator.translate(elementDiffContainer);
        //todo put image resources copying here
	    if (oldPackageXml != null) {
		    translationResult.setOldPackageXml(oldPackageXml);
	    }
	    return translationResult;
    }

	/**
     * @param stream packageXmlFile
     */
    public ITranslationResult<Boolean> supportVersion(InputStream stream) {
        IPatchProcessor patchProcessor = new SupportPackageVersionPatchProcessor();
        IPackageDescriptor packageDescriptor = patchProcessor.loadVersionPackage(stream);
        IPackageDescriptor currentPackageDescriptor =
                packageExporter.exportPackageDescriptor(packageDescriptor.getUid());
        IElementDiffInfoContainer diffContainer = patchProcessor.processDifferences(
                packageDescriptor, currentPackageDescriptor);
        return xmlToRegistryTranslator.translate(diffContainer);
    }

    private DialectType dialectFromDB(DatabaseModel databaseModel) {
        DialectType dialectType;
        if (databaseModel == null) {
            dialectType = DialectType.MySQL5;
        } else {
            DatabaseName rdbms = databaseModel.getRdbms();
            dialectType = DialectType.getDialect(rdbms);
        }
        return dialectType;
    }

    private void linksEntity(IEntityElement[] elements, INamedPackageDescriptorElement parent) {
        if (elements != null) {
            for (IEntityElement element : elements) {
                element.setParent(parent);
                linksEntity(element.getConfiguredEntities(), element);
            }
        }
    }

    private void linksDomain(IDomainElement[] domains, INamedPackageDescriptorElement parent) {
        if (domains != null) {
            for (IDomainElement domain : domains) {
                domain.setParent(parent);
                linksEntity(domain.getConfiguredEntities(), domain);
                linksDomain(domain.getConfiguredDomains(), domain);
            }
        }
    }

    private void resolveDomain(IDomainElement[] elements, List<IRelationshipElement> relationships, Map<String, String> tableMapping) {
        if (elements != null) {
            for (IDomainElement element : elements) {
                resolveEntity(element.getConfiguredEntities(), relationships, tableMapping);
                resolveDomain(element.getConfiguredDomains(), relationships, tableMapping);
            }
        }
    }

    private void resolveEntity(IEntityElement[] elements, List<IRelationshipElement> relationships, Map<String, String> tableMapping) {
        if (elements != null) {
            for (IEntityElement element : elements) {
                resolveEntity(element.getConfiguredEntities(), relationships, tableMapping);

                String entityLookup = DiffUtils.lookup(element);
                String name = resolver.resolveTableName(element);
                String extendedEntityPath = element.getExtendedEntityPath();
                if (extendedEntityPath == null && !element.isTypeEntity()) {
                    tableMapping.put(entityLookup, name);
                }

                for (IRelationshipElement relationship : relationships) {
                    String refEntityLookup = DiffUtils.lookupByRefPath(relationship.getSource().getRefPath());
                    entityLookup = DiffUtils.lookup(element);
                    if(entityLookup.equals(refEntityLookup)) {
                        IEntityElement entityElement = ConfigElementFactory.getInstance().producePackageEntity(relationship.getName());
                        entityElement.setParent(element);
                        entityLookup = DiffUtils.lookup(entityElement);
                        name = resolver.resolveTableName(entityElement);
                        tableMapping.put(entityLookup, name);
                    }
                }
            }
        }
    }

    private DefaultPatchContext patchProcessor(DataSource dataSource, IPackageDescriptor packageDescriptor) {
        DefaultPatchContext patchContext = new DefaultPatchContext(dataSource, null);
        LeadIdPrefixNameResolver sqlNamesResolver = new LeadIdPrefixNameResolver();
        patchContext.setSqlNamesResolver(sqlNamesResolver);
        patchContext.setNewPackage(packageDescriptor);
        AddExtendedEntityAttributeSqlDecorator addExtendedEntityAttributeSqlDecorator =
                new AddExtendedEntityAttributeSqlDecorator();
        addExtendedEntityAttributeSqlDecorator.setNameOfEntityToEnhance("Registry Node");
        addExtendedEntityAttributeSqlDecorator.setSqlNamesResolver(sqlNamesResolver);
        patchContext.addEntityDecorator(addExtendedEntityAttributeSqlDecorator);
        return patchContext;
    }

    private IElementDiffInfoContainer narrowTo(
            IElementDiffInfoContainer patch, String contextLookup, Collection<String> excludeBaseLookupSet) {
        IElementDiffInfoContainer resultContainer;
        if ((excludeBaseLookupSet == null || excludeBaseLookupSet.isEmpty()) &&
                contextLookup.equals(StringUtils.getPackageLookup(contextLookup))) {
            resultContainer = patch;
        } else {
            ElementDiffInfoContainer container = new ElementDiffInfoContainer();
            List<FieldsDiff> newFieldDiffList = filter(patch.getFieldDiffs(), contextLookup, excludeBaseLookupSet);
            List<EntitiesDiff> newEntityDiffList = filter(patch.getEntityDiffs(), contextLookup, excludeBaseLookupSet);
            List<RelationshipsDiff> newRelationshipDiffList = filter(
                    patch.getRelationshipDiffs(), contextLookup, excludeBaseLookupSet);
            List<DomainsDiff> newDomainDiffList = filter(patch.getDomainDiffs(), contextLookup, excludeBaseLookupSet);
            container.setFieldDiffList(newFieldDiffList);
            container.setEntityDiffList(newEntityDiffList);
            container.setRelationshipDiffList(newRelationshipDiffList);
            container.setDomainDiffList(newDomainDiffList);
            container.setTypesRegistry(patch.getNewEntitiesManager());
            container.setOldTypesRegistry(patch.getOldEntitiesManager());
            resultContainer = container;
        }
        return resultContainer;
    }

    private <T extends PackageDescriptorElementDiff
            <?, ? extends INamedPackageDescriptorElement>> List<T> filter(
            List<T> sourceElements, String contextLookup, Collection<String> excludeBaseLookupSet) {
        List<T> newItemDiffList;
        if (sourceElements == null) {
            newItemDiffList = null;
        } else {
            newItemDiffList = new ArrayList<T>();
            for (T diff : sourceElements) {
                boolean isUpdateOperation = diff.getType() == DifferenceType.UPDATED;
                //todo: For sql generation we should have more universal validation that checks base lookup set from previous package version etc...
                //For now, we'll check only if new item meets contextLookup & excludeBaseLookupSet conditions
                INamedPackageDescriptorElement entity = isUpdateOperation ? diff.getNewElement() : diff.getDiffTarget();
                String itemLookup;
                if (entity instanceof IRelationshipElement) {
                    IRelationshipElement rel = (IRelationshipElement) entity;
                    String parentLookup = DiffUtils.lookupByRefPath(rel.getSource().getRefPath());
                    itemLookup = DiffUtils.lookup(parentLookup, rel.getName());
                } else {
                    itemLookup = DiffUtils.lookup(entity);
                }
                if (itemLookup.startsWith(contextLookup)) {
                    boolean include = true;
                    if (excludeBaseLookupSet != null && !excludeBaseLookupSet.isEmpty()) {
                        for (String baseLookupToExclude : excludeBaseLookupSet) {
                            if (itemLookup.startsWith(baseLookupToExclude)) {
                                include = false;
                                break;
                            }
                        }
                    }
                    if (include) {
                        newItemDiffList.add(diff);
                    }
                }

            }
        }
        return newItemDiffList;
    }

    private void composePatch(IPackageDescriptor packageDescriptor, IElementDiffInfoContainer elementDiffContainer,
                              DialectType sqlDialect, Map<String, String> scriptMapping, String lookupContext,
                              IPatchProcessor<List<String>> patchProcessor, Set<String> allContexts) {
        DefaultPatchContext patchContext = patchProcessor(null, packageDescriptor);
        patchContext.setSqlDialect(sqlDialect);

        Collection<String> collection = calculateExclusionLookupList(lookupContext, allContexts);
        IElementDiffInfoContainer patch = narrowTo(elementDiffContainer, lookupContext, collection);

        List<String> sqlQueries = patchProcessor.generatePatch(patch, patchContext);
        String dbScript = StringUtils.join(sqlQueries.toArray(),
                sqlDialect.getSeparator()) + sqlDialect.getSeparator();
        dbScript = dbScript.replaceAll(";{2,}", ";");
        scriptMapping.put(lookupContext + '-' + sqlDialect.name(), dbScript);
    }

    private Collection<String> calculateExclusionLookupList(String lookupContext, Set<String> allContexts) {
        Collection<String> collection = new ArrayList<String>();
        if (allContexts != null) {
            for (String context : allContexts) {
                if (!lookupContext.startsWith(context)) {
                    collection.add(context);
                }
            }
        }
        return collection;
    }

}