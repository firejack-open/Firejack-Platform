/**
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


package net.firejack.platform.service.registry.broker.package_;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.PackageVersion;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.export.IPackageExporter;
import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.IRelationshipElement;
import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.config.meta.diff.*;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.patch.EntityElementManager;
import net.firejack.platform.core.config.patch.IPatchProcessor;
import net.firejack.platform.core.config.patch.UIDPatchProcessor;
import net.firejack.platform.core.config.translate.TranslationError;
import net.firejack.platform.core.config.upgrader.model.translate.UpgradeDiffTransformer;
import net.firejack.platform.core.config.upgrader.model.translate.UpgradeModelTranslator;
import net.firejack.platform.core.config.upgrader.model.translate.UpgradeTranslationResult;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.model.upgrader.dbengine.DialectType;
import net.firejack.platform.model.upgrader.operator.bean.DataSourceType;
import net.firejack.platform.model.upgrader.operator.bean.PackageType;
import net.firejack.platform.model.upgrader.serialization.IUpgradeModelSerializer;
import net.firejack.platform.model.upgrader.serialization.SerializerType;
import net.firejack.platform.model.upgrader.serialization.UpgradeModelSerializerFactory;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

@TrackDetails
@Component("generateUpgradeXmlBroker")
@ProgressComponent(weight = 20)
public class GenerateUpgradeXmlBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<PackageVersion>> {

	public static final MessageFormat UPGRADE_FILENAME = new MessageFormat("upgrade_{0}_{1}" + PackageFileType.PACKAGE_UPGRADE.getDotExtension());
	public static final String PARAM_VERSION = "version";
	public static final String PARAM_PACKAGE_ID = "packageId";

	private static final String END_OF_LINE = "\n\r";

	@Autowired
	private IPackageStore packageStore;
    @Autowired
    @Qualifier("packageInstallationService")
    private PackageInstallationService packageInstallationService;
	@Autowired
    @Qualifier("upgradePackageExporter")
	private IPackageExporter packageExporter;
	@Autowired
	private FileHelper helper;
	@Autowired
	private PackageVersionHelper packageVersionHelper;

	@Override
	protected ServiceResponse<PackageVersion> perform(ServiceRequest<NamedValues> request) throws Exception {
		Integer version = (Integer) request.getData().get(PARAM_VERSION);
		Long packageId = (Long) request.getData().get(PARAM_PACKAGE_ID);

		ServiceResponse<PackageVersion> response;
		try {
			PackageModel packageRN = packageStore.findById(packageId);

			IPatchProcessor patchProcessor = new UIDPatchProcessor();

			InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageRN.getName() + PackageFileType.PACKAGE_XML.getDotExtension(),helper.getVersion(), packageId.toString(), version.toString());
            if (stream == null)
                return new ServiceResponse<PackageVersion>("Previous version can't exist.", false);

			IPackageDescriptor oldPackageDescriptor = patchProcessor.loadVersionPackage(stream);
			IOUtils.closeQuietly(stream);

			IPackageDescriptor newPackageDescriptor = packageExporter.exportPackageDescriptor(packageId);
			IElementDiffInfoContainer elementDiffContainer = patchProcessor.processDifferences(oldPackageDescriptor, newPackageDescriptor);

            Map<String, DialectType> sqlDialectMapping = packageInstallationService.getSqlDialectMapping(newPackageDescriptor);
            if (sqlDialectMapping == null) {
                response = new ServiceResponse<PackageVersion>("Package data-source is not valid or not supported.", false);
            } else {
                UpgradeDiffTransformer upgradeDiffTransformer = populateUpgradeConditions(sqlDialectMapping);

                if (upgradeDiffTransformer == null)
                    return null;

                UpgradeModelTranslator translator = new UpgradeModelTranslator();
                translator.setElementsDiffTransformer(upgradeDiffTransformer);

                Map<String, IElementDiffInfoContainer> elementDiffContainers = splitElementDiffInfoContainer(upgradeDiffTransformer, elementDiffContainer);

                StringBuilder errorMessage = new StringBuilder();
                List<DataSourceType> dataSourceTypes = new ArrayList<DataSourceType>();
                for (Map.Entry<String, IElementDiffInfoContainer> entry : elementDiffContainers.entrySet()) {
                    UpgradeTranslationResult translationResult = (UpgradeTranslationResult) translator.translate(entry.getValue());
                    String name = DiffUtils.extractNameFromLookup(entry.getKey());
                    String path = DiffUtils.extractPathFromLookup(entry.getKey());
                    translationResult.setName(name);
                    translationResult.setPath(path);
                    DataSourceType packageType = translationResult.getResult();
                    if ((translationResult.getTranslationErrors() == null || translationResult.getTranslationErrors().length == 0) && packageType != null) {
                        dataSourceTypes.add(packageType);
                    } else {
                        for (TranslationError error : translationResult.getTranslationErrors()) {
                            errorMessage.append(error).append(END_OF_LINE);
                        }
                    }
                }

                if (errorMessage.length() == 0) {
                    IUpgradeModelSerializer upgradeModelSerializer = UpgradeModelSerializerFactory.getInstance().produceSerializer(SerializerType.JAXB);

                    PackageType packageType = new PackageType();
                    packageType.setName(newPackageDescriptor.getName());
                    packageType.setPath(newPackageDescriptor.getPath());
                    packageType.setFromVersion(oldPackageDescriptor.getVersion());
                    packageType.setToVersion(newPackageDescriptor.getVersion());
                    packageType.getDataSourceTypes().addAll(dataSourceTypes);

                    String upgradeXml = upgradeModelSerializer.serialize(PackageType.class, packageType);

                    String upgradeFileName = UPGRADE_FILENAME.format(new String[]{
                            oldPackageDescriptor.getVersion(),
                            newPackageDescriptor.getVersion()
                    });

                    OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, upgradeFileName, new ByteArrayInputStream(upgradeXml.getBytes()), helper.getVersion(), packageId.toString(), packageRN.getVersion().toString());

                    PackageVersion packageVersionVO = packageVersionHelper.populatePackageVersion(packageRN);
                    response = new ServiceResponse<PackageVersion>(packageVersionVO, "Generate upgrade xml successfully", true);
                } else {
                    String errorMessages = errorMessage.toString();
                    logger.error(errorMessages);
                    response = new ServiceResponse<PackageVersion>(errorMessages, false);
                }
            }
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response = new ServiceResponse<PackageVersion>(e.getMessage(), false);
		}

		return response;
	}

    private Map<String, IElementDiffInfoContainer> splitElementDiffInfoContainer(UpgradeDiffTransformer upgradeDiffTransformer, IElementDiffInfoContainer globalIElementDiffInfoContainer) {
        Map<String, DialectType> sqlDialectMapping = upgradeDiffTransformer.getSqlDialectMapping();
        List<String> lookupList = new ArrayList<String>(sqlDialectMapping.keySet());
        Collections.sort(lookupList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s2.compareTo(s1);
            }
        });
        String packageLookup = lookupList.get(lookupList.size() - 1);
        lookupList.remove(packageLookup);

        Map<String, IElementDiffInfoContainer> elementDiffInfoContainers = new LinkedHashMap<String, IElementDiffInfoContainer>();
        elementDiffInfoContainers.put(packageLookup, globalIElementDiffInfoContainer);
        EntityElementManager newEntitiesManager = globalIElementDiffInfoContainer.getNewEntitiesManager();
        EntityElementManager oldEntitiesManager = globalIElementDiffInfoContainer.getOldEntitiesManager();

        for (String lookup : lookupList) {
            ElementDiffInfoContainer elementDiffInfoContainer = new ElementDiffInfoContainer();
            elementDiffInfoContainer.setTypesRegistry(newEntitiesManager);
            elementDiffInfoContainer.setOldTypesRegistry(oldEntitiesManager);

            elementDiffInfoContainer.setEntityDiffList(new ArrayList<EntitiesDiff>());
            List<EntitiesDiff> entityDiffs = globalIElementDiffInfoContainer.getEntityDiffs();
            for (EntitiesDiff entityDiff : entityDiffs) {
                IEntityElement diffTarget = entityDiff.getDiffTarget();
                if (diffTarget.getPath().contains(lookup)) {
                    elementDiffInfoContainer.getEntityDiffs().add(entityDiff);
                }
            }
            globalIElementDiffInfoContainer.getEntityDiffs().removeAll(elementDiffInfoContainer.getEntityDiffs());

            elementDiffInfoContainer.setFieldDiffList(new ArrayList<FieldsDiff>());
            List<FieldsDiff> fieldsDiffs = globalIElementDiffInfoContainer.getFieldDiffs();
            for (FieldsDiff fieldDiff : fieldsDiffs) {
                IEntityElement targetParent = fieldDiff.getTargetParent();
                if (targetParent.getPath().contains(lookup)) {
                    elementDiffInfoContainer.getFieldDiffs().add(fieldDiff);
                }
            }
            globalIElementDiffInfoContainer.getFieldDiffs().removeAll(elementDiffInfoContainer.getFieldDiffs());

            elementDiffInfoContainer.setRelationshipDiffList(new ArrayList<RelationshipsDiff>());
            List<RelationshipsDiff> relationshipDiffs = globalIElementDiffInfoContainer.getRelationshipDiffs();
            for (RelationshipsDiff relationshipDiff : relationshipDiffs) {
                IRelationshipElement relationshipElement = relationshipDiff.getDiffTarget();
                Reference source = relationshipElement.getSource();
                if (source.getRefPath().contains(lookup)) {
                    elementDiffInfoContainer.getRelationshipDiffs().add(relationshipDiff);
                }
            }
            globalIElementDiffInfoContainer.getRelationshipDiffs().removeAll(elementDiffInfoContainer.getRelationshipDiffs());

            elementDiffInfoContainer.setIndexesDiffList(new ArrayList<IndexesDiff>());
            List<IndexesDiff> indexesDiffs = globalIElementDiffInfoContainer.getIndexesDiffs();
            for (IndexesDiff indexDiff : indexesDiffs) {
                IEntityElement targetParent = indexDiff.getTargetParent();
                if (targetParent.getPath().contains(lookup)) {
                    elementDiffInfoContainer.getIndexesDiffs().add(indexDiff);
                }
            }
            globalIElementDiffInfoContainer.getIndexesDiffs().removeAll(elementDiffInfoContainer.getIndexesDiffs());

            elementDiffInfoContainers.put(lookup, elementDiffInfoContainer);
        }
        return elementDiffInfoContainers;
    }

    private UpgradeDiffTransformer populateUpgradeConditions(Map<String, DialectType> sqlDialectMapping) {
        UpgradeDiffTransformer conditions;
        if (sqlDialectMapping == null || sqlDialectMapping.isEmpty()) {
            conditions = null;
        } else {
            conditions = new UpgradeDiffTransformer();
            conditions.setSqlDialectMapping(sqlDialectMapping);
        }
        return conditions;
    }

}