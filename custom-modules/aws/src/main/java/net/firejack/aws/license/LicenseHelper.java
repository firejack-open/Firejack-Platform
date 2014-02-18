package net.firejack.aws.license;
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


import net.firejack.aws.web.model.License;
import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class LicenseHelper {

    public static File create(License license) throws IOException, NoSuchAlgorithmException, JAXBException {
        signature(license);

        File tmp = new File("/tmp/", license.getName() + ".xml");

        tmp.getParentFile().mkdirs();
        tmp.createNewFile();
        FileOutputStream stream = new FileOutputStream(tmp);

        JAXBContext jaxbContext = JAXBContext.newInstance(License.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(license, stream);
        stream.close();

        return tmp;
    }

    private static void signature(License license) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        StringBuilder builder = new StringBuilder();
        builder.append(license.getExpired()).append(license.getName()).append(license.getSession());
        byte[] bytes = builder.toString().getBytes("UTF-8");

        for (int i = 0; i < bytes.length; i += 2)
            bytes[i] += i;

        MessageDigest digest = MessageDigest.getInstance("MD5");
        license.setSignature(new String(Base64.encodeBase64(digest.digest(bytes))));
    }
}
