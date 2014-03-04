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

package net.firejack.aws.license;


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
