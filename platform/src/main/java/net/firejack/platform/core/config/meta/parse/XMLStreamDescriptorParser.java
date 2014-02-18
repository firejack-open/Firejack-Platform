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
package net.firejack.platform.core.config.meta.parse;

import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.construct.ConfigElementFactory;
import net.firejack.platform.core.config.meta.context.IUpgradeConfigContext;
import net.firejack.platform.core.config.meta.exception.ParseException;
import net.firejack.platform.core.config.meta.parse.xml.IPackageXmlProcessor;
import net.firejack.platform.core.exception.BusinessFunctionException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XMLStreamDescriptorParser  implements IPackageDescriptorParser<InputStream>{
	private static final Logger logger = Logger.getLogger(XMLStreamDescriptorParser.class);

	@Override
	public IPackageDescriptor parsePackageDescriptor(IUpgradeConfigContext<InputStream> context) throws ParseException {
		ConfigElementFactory elementFactory = ConfigElementFactory.getInstance();
		IPackageXmlProcessor xmlProcessor = elementFactory.getDefaultPackageXmlProcessor();

		try {
			String xml = IOUtils.toString(context.getConfigSource());
            uidValidation(xml);
			return DescriptorParserUtils.convertDescriptor(xml, xmlProcessor);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw new ParseException(e.getMessage(), e);
        }
    }

    private void uidValidation(String xml) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//*/@uid");
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        Map<String, Integer> uids = new HashMap<String, Integer>();
        for (int i = 0; i < nodes.getLength(); i++) {
            String uid = nodes.item(i).getNodeValue();
            Integer count = uids.get(uid);
            if (count == null) {
                count = 0;
            }
            count++;
            uids.put(uid, count);
        }
        StringBuilder errorMessage = new StringBuilder();
        for (Map.Entry<String, Integer> entry: uids.entrySet()) {
            if (entry.getValue() > 1) {
                errorMessage.append(" [").append(entry.getKey()).append("]");
            }
        }
        if (errorMessage.length() > 0) {
            throw new BusinessFunctionException("UIDs are not unique in package.xml. Check UIDs:" + errorMessage.toString());
        }
    }
}
