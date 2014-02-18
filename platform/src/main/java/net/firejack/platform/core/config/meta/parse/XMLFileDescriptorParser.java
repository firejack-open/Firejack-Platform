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

package net.firejack.platform.core.config.meta.parse;

import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.construct.ConfigElementFactory;
import net.firejack.platform.core.config.meta.context.IUpgradeConfigContext;
import net.firejack.platform.core.config.meta.exception.ConfigSourceException;
import net.firejack.platform.core.config.meta.exception.ParseException;
import net.firejack.platform.core.config.meta.parse.xml.IPackageXmlProcessor;
import net.firejack.platform.core.utils.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;


class XMLFileDescriptorParser implements IPackageDescriptorParser<File> {

	private static final Logger logger = Logger.getLogger(XMLFileDescriptorParser.class);

	@Override
	public IPackageDescriptor parsePackageDescriptor(IUpgradeConfigContext<File> context) throws ParseException {
		File configFile;
		try {
			configFile = context.getConfigSource();
		} catch (ConfigSourceException e) {
			logger.error(e.getMessage(), e);
			throw new ParseException(e);
		}
		ConfigElementFactory elementFactory = ConfigElementFactory.getInstance();
		IPackageXmlProcessor xmlProcessor = elementFactory.getDefaultPackageXmlProcessor();

		try {
			String xml = FileUtils.readFileToString(configFile);
			return DescriptorParserUtils.convertDescriptor(xml, xmlProcessor);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ParseException(e.getMessage(), e);
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			throw new ParseException(e.getMessage(), e);
		}
	}

}