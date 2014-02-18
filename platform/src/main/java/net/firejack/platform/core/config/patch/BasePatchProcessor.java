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

package net.firejack.platform.core.config.patch;

import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.construct.ConfigElementFactory;
import net.firejack.platform.core.config.meta.context.ConfigContextFactory;
import net.firejack.platform.core.config.meta.context.IUpgradeConfigContext;
import net.firejack.platform.core.config.meta.diff.IElementDiffInfoContainer;
import net.firejack.platform.core.config.meta.exception.ParseException;
import net.firejack.platform.core.config.meta.exception.PatchExecutionException;
import net.firejack.platform.core.config.meta.parse.DescriptorParserFactory;
import net.firejack.platform.core.config.meta.parse.DescriptorParserType;
import net.firejack.platform.core.config.meta.parse.IPackageDescriptorParser;
import net.firejack.platform.core.config.patch.listener.IPackagePatchListener;
import net.firejack.platform.core.config.patch.listener.PackagePatchEvent;
import net.firejack.platform.core.config.patch.listener.PatchFlowException;
import net.firejack.platform.core.config.patch.listener.PatchFlowState;
import net.firejack.platform.core.config.translate.*;
import net.firejack.platform.core.utils.OpenFlameDataSource;
import net.firejack.platform.core.utils.db.DBUtils;
import net.firejack.platform.model.upgrader.dbengine.DialectType;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class BasePatchProcessor implements IPatchProcessor<List<String>> {

    private List<IPackageDescriptorTranslator<List<String>>> translators;
    private List<IPackagePatchListener> listeners;
    private IPackageDescriptorParser<File> xmlMetaDataParser;
    private ConfigElementFactory factory = ConfigElementFactory.getInstance();
    private static final Logger logger = Logger.getLogger(BasePatchProcessor.class);

    public IPackageDescriptor loadVersionPackage(File configLocation) {
        ConfigContextFactory configContextFactory = ConfigContextFactory.getInstance();
        IUpgradeConfigContext<File> context = configContextFactory.buildWrapperContext(configLocation);
        try {
            IPackageDescriptor loadedPackageDescriptor = getXmlMetaDataParser().parsePackageDescriptor(context);
            checkNameConventionForPackageElements(loadedPackageDescriptor);
            return loadedPackageDescriptor;
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

	/**
	 *
	 * @return xml meta data parser
	 */
    public IPackageDescriptorParser<File> getXmlMetaDataParser() {
        if (xmlMetaDataParser == null) {
            DescriptorParserFactory parserFactory = DescriptorParserFactory.getInstance();
            xmlMetaDataParser = parserFactory.buildMetaDataParser(DescriptorParserType.XML_FILE);
        }
        return xmlMetaDataParser;
    }

	public IPackageDescriptorParser<InputStream> getXmlStreamMetaDataParser() {
		DescriptorParserFactory parserFactory = DescriptorParserFactory.getInstance();
		return parserFactory.buildMetaDataParser(DescriptorParserType.XML_STREAM);
	}

    public IPackageDescriptor loadVersionPackage(String configLocation) {
        ConfigContextFactory configContextFactory = ConfigContextFactory.getInstance();
        IUpgradeConfigContext<File> context = configContextFactory.buildContext(configLocation);
        try {
            return getXmlMetaDataParser().parsePackageDescriptor(context);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public IPackageDescriptor loadVersionPackage(InputStream stream) {
        ConfigContextFactory configContextFactory = ConfigContextFactory.getInstance();
        IUpgradeConfigContext<InputStream> context = configContextFactory.buildContext(stream);
        try {
            return getXmlStreamMetaDataParser().parsePackageDescriptor(context);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void addAdditionalUpdateTranslator(IPackageDescriptorTranslator<List<String>> translator) {
        if (!getTranslators().contains(translator)) {
            getTranslators().add(translator);
        }
    }

    @Override
    public void addPatchListener(IPackagePatchListener listener) {
        if (!getListeners().contains(listener)) {
            getListeners().add(listener);
        }
    }

    @Override
    public void removePatchListener(IPackagePatchListener listener) {
        if (getListeners().contains(listener)) {
            getListeners().remove(listener);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void applyPatch(IElementDiffInfoContainer patch, IPatchContext patchContext) throws PatchExecutionException {
        if (!getListeners().isEmpty()) {
            PackagePatchEvent event = generateEvent(patchContext);
            try {
                for (IPackagePatchListener listener : getListeners()) {
                    listener.onBeforePatch(event);
                }
            } catch (PatchFlowException e) {
                if (e.getFlowState() == PatchFlowState.STOP) {
                    throw new PatchExecutionException(e);
                }
            }
        }
        ITranslationResult<List<String>> translationResult = getTranslatorResult(patch, patchContext);
        applyTranslation(patchContext, translationResult);
        for (IPackageDescriptorTranslator<List<String>> translator : getTranslators()) {
            translationResult = translator.translate(patch);
            applyTranslation(patchContext, translationResult);
        }

        PackagePatchEvent event = generateEvent(patchContext);
        for (IPackagePatchListener listener : getListeners()) {
            listener.onAfterPatch(event);
        }
    }

    protected List<IPackageDescriptorTranslator<List<String>>> getTranslators() {
        if (this.translators == null) {
            this.translators = new ArrayList<IPackageDescriptorTranslator<List<String>>>();
        }
        return this.translators;
    }

    protected List<IPackagePatchListener> getListeners() {
        if (listeners == null) {
            listeners = new ArrayList<IPackagePatchListener>();
        }
        return listeners;
    }

    protected void checkNameConventionForPackageElements(IPackageDescriptor loadedPackageDescriptor) {
        checkNameConvention(loadedPackageDescriptor.getActionElements());
        checkNameConvention(loadedPackageDescriptor.getDirectoryElements());
        checkNameConvention(loadedPackageDescriptor.getNavigationElements());
        checkNameConvention(loadedPackageDescriptor.getFolderElements());
        checkNameConvention(loadedPackageDescriptor.getRoles());
        checkNameConvention(loadedPackageDescriptor.getPermissions());
        checkNameConvention(loadedPackageDescriptor.getFolderElements());
        checkNameConvention(loadedPackageDescriptor.getResources());
        checkNameConvention(loadedPackageDescriptor.getUsers());
        checkNameConvention(loadedPackageDescriptor);
    }

    protected void checkNameConvention(IPackageDescriptorElement[] elements) {
        if (elements != null) {
            for (IPackageDescriptorElement element : elements) {
                factory.checkElementName(element);
            }
        }
    }

    protected void checkNameConvention(IEntityProvider rootElement) {
        if (rootElement != null && rootElement instanceof IPackageDescriptorElement) {
            factory.checkElementName((IPackageDescriptorElement) rootElement);
            IEntityElement[] entities = rootElement.getConfiguredEntities();
            if (entities != null) {
                checkNameConvention(entities);
                for (IEntityElement entity : entities) {
                    checkNameConvention(entity);
                }
            }
            if (rootElement instanceof IRootElementContainer) {
                IRootElementContainer elementsContainer = (IRootElementContainer) rootElement;
                IDomainElement[] domains = elementsContainer.getConfiguredDomains();
                if (domains != null) {
                    checkNameConvention(domains);
                    for (IDomainElement domain : domains) {
                        checkNameConvention(domain);
                    }
                }
            }
        }
    }

    protected PackagePatchEvent generateEvent(IPatchContext patchContext) {
        return new PackagePatchEvent(
                patchContext.getOldPackage(), patchContext.getNewPackage(),
                patchContext.getDataSource(), patchContext.getUser());
    }

    protected ITranslationResult<List<String>> getTranslatorResult(IElementDiffInfoContainer patch, IPatchContext patchContext) {
        DialectType sqlDialect = patchContext.getSqlDialect();
        DescriptorTranslatorFactory translatorFactory = DescriptorTranslatorFactory.getInstance();
        AbstractUpdateTranslator<List<String>, SqlTranslationResult> packageMetaTranslator =
                translatorFactory.getSqlTranslator(sqlDialect, patchContext);
        for (IPackageDescriptorElementSqlDecorator<IEntityElement> decorator : patchContext.getEntityDecorators()) {
            packageMetaTranslator.addEntityDecorator(decorator);
        }
        for (IPackageDescriptorElementSqlDecorator<IRelationshipElement> decorator : patchContext.getRelationshipDecorators()) {
            packageMetaTranslator.addRelationshipDecorator(decorator);
        }
        return packageMetaTranslator.translate(patch);
    }

    @Override
    public List<String> generatePatch(IElementDiffInfoContainer patch, IPatchContext patchContext) throws PatchExecutionException {
        List<String> sqlQueries = new ArrayList<String>();
        ITranslationResult<List<String>> translationResult = getTranslatorResult(patch, patchContext);
        if (translationResult.getTranslationErrors() != null && translationResult.getTranslationErrors().length > 0) {
            throw new PatchExecutionException(translationResult.getTranslationErrors());
        }
        sqlQueries.addAll(translationResult.getResult());
        for (IPackageDescriptorTranslator<List<String>> translator : getTranslators()) {
            translationResult = translator.translate(patch);
            if (translationResult.getTranslationErrors() != null && translationResult.getTranslationErrors().length > 0) {
                throw new PatchExecutionException(translationResult.getTranslationErrors());
            }
            sqlQueries.addAll(translationResult.getResult());
        }
        return sqlQueries;
    }

    protected void applyTranslation(IPatchContext patchContext, ITranslationResult<List<String>> translationResult) {
        if (translationResult.getTranslationErrors() != null && translationResult.getTranslationErrors().length > 0) {
            throw new PatchExecutionException(translationResult.getTranslationErrors());
        }
        for (String statement : translationResult.getResult()) {
	        if (!patchContext.isDataInserted() && statement.matches("(?is)ALTER TABLE.*FOREIGN KEY.*")) {
		        patchContext.setDataInserted(true);
		        try {
			        insertData(patchContext);
		        } catch (Throwable throwable) {
			        logger.warn("Insert data error");
		        }
	        }

	        logger.info("Trying to execute sql statement: [\n" + statement + "\n]");
            if (!DBUtils.executeStatement(patchContext.getDataSource(), statement)) {
                logger.error("There were problems encountered during sql statement execution.");
                throw new PatchExecutionException();
            }
        }
    }

	private void insertData (IPatchContext patchContext) {
        DataSource source = patchContext.getSourceDataSource();
        DataSource target = patchContext.getDataSource();
        if (source != null && target != null && source instanceof OpenFlameDataSource &&
                target instanceof OpenFlameDataSource) {
            OpenFlameDataSource sourceInfo = (OpenFlameDataSource) source;
            OpenFlameDataSource targetInfo = (OpenFlameDataSource) target;

            DBUtils.migrateData(sourceInfo, targetInfo);
        }
	}

    protected <T> boolean isEmpty(List<T> elements) {
        return elements == null || elements.isEmpty();
    }

    protected <T> boolean isEmpty(T[] elements) {
        return elements == null || elements.length == 0;
    }

    protected <T> boolean isNotEmpty(List<T> elements) {
        return elements != null && !elements.isEmpty();
    }

    protected <T> boolean isNotEmpty(T[] elements) {
        return elements != null && elements.length > 0;
    }

}