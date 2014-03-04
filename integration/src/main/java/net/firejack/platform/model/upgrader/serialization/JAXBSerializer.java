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

package net.firejack.platform.model.upgrader.serialization;

import net.firejack.platform.model.upgrader.bean.IUpgradeModel;
import net.firejack.platform.model.upgrader.serialization.exception.UpgradeModelDeSerializationException;
import net.firejack.platform.model.upgrader.serialization.exception.UpgradeModelSerializationException;

import javax.xml.bind.*;
import java.io.*;

/**
 *
 */
@SuppressWarnings({"unused", "unchecked"})
public class JAXBSerializer implements IUpgradeModelSerializer {

    @Override
    public <T extends IUpgradeModel> String serialize(Class<T> clazz, T model) {
        try {
            Marshaller marshaller = createMarshaller(clazz);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter writer = new StringWriter();
            marshaller.marshal(model, writer);
            StringBuffer buffer = writer.getBuffer();
            return buffer.toString();
        } catch (JAXBException e) {
            throw new UpgradeModelSerializationException(e);
        }
    }

    @Override
    public <T extends IUpgradeModel> T deSerialize(Class<T> clazz, String xml) {
        try {
            Unmarshaller u = createUnmarshaller(clazz);
            JAXBElement<T> doc = (JAXBElement<T>) u.unmarshal(new StringReader(xml));
            return doc.getValue();
        } catch (JAXBException e) {
            throw new UpgradeModelDeSerializationException(e);
        }
    }

    @Override
    public <T extends IUpgradeModel> Writer serializeToWriter(Class<T> clazz, T model) {
        try {
            Marshaller u = createMarshaller(clazz);
            Writer writer = new PrintWriter(new StringWriter());
            u.marshal(model, writer);
            return writer;
        } catch (JAXBException e) {
            throw new UpgradeModelDeSerializationException(e);
        }
    }

    @Override
    public <T extends IUpgradeModel> T deSerializeFromReader(Class<T> clazz, Reader xmlReader) {
        try {
            Unmarshaller u = createUnmarshaller(clazz);
            JAXBElement<T> doc = (JAXBElement<T>) u.unmarshal(new BufferedReader(xmlReader));
            return doc.getValue();
        } catch (JAXBException e) {
            throw new UpgradeModelDeSerializationException(e);
        }
    }

    private Marshaller createMarshaller(Class<?> clazz) throws JAXBException {
        String modelPackageName = clazz.getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(modelPackageName);
        return jc.createMarshaller();
    }

    private Unmarshaller createUnmarshaller(Class<?> clazz) throws JAXBException {
        String modelPackageName = clazz.getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(modelPackageName);
        return jc.createUnmarshaller();
    }

}