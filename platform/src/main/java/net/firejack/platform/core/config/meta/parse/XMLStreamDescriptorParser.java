/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
