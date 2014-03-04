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

package net.firejack.platform.core.adapter;

import org.w3c.dom.*;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapAdapter extends XmlAdapter<Object, Map<?, ?>> {

	@Override
	public Map<?, ?> unmarshal(Object domTree) {
		Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        if (domTree instanceof Element) {
            Element content = (Element) domTree;
            NodeList childNodes = content.getChildNodes();
            if (childNodes.getLength() > 0) {
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node child = childNodes.item(i);
                    String key = child.getNodeName();
                    // Skip text nodes
                    if (key.startsWith("#"))
                        continue;
                    String value = ((Text) child.getChildNodes().item(0)).getWholeText();
                    map.put(key, value);

                }
            }
        }
		return map;
	}

	@Override
	public Object marshal(Map<?, ?> map) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			Element customXml = doc.createElement("Map");
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				Element keyValuePair = doc.createElement(entry.getKey().toString());
				keyValuePair.appendChild(doc.createTextNode(entry.getValue().toString()));
				customXml.appendChild(keyValuePair);
			}
			return customXml;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

