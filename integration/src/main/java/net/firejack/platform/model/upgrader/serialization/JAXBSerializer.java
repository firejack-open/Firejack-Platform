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