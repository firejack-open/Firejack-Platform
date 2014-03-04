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

package net.firejack.platform.web.security.license;


import com.sun.jersey.core.util.Base64;
import net.firejack.platform.api.registry.domain.License;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.InstallUtils;
import net.firejack.platform.web.security.x509.KeyUtils;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class LicenceHelper {

    public static final String LICENSE = "license.xml";

    public static License create(String name, Integer sessions, Long expired) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        License license = new License(name, sessions, expired);
        license.setSignature(new String(signature(name, sessions, expired)));

        return license;
    }

    public static void verify(License license) throws UnsupportedEncodingException, NoSuchAlgorithmException, IllegalAccessException {
        Long expired = license.getExpired();
        byte[] bytes = signature(license.getName(), license.getSession(), expired);

        if (!license.getSignature().equals(new String(bytes)))
            throw new IllegalAccessException("Invalid license key.");
        if (System.currentTimeMillis() > expired)
            throw new IllegalAccessException("License key period has been expired.");
    }

    public static void save(License license) throws IOException, JAXBException {
        ByteArrayOutputStream stream = (ByteArrayOutputStream) FileUtils.writeJAXB(license);
        KeyUtils.writeCrypt(Env.getEnvFile(LICENSE), InstallUtils.getKeyStore(), new ByteArrayInputStream(stream.toByteArray()));
    }

    public static License read() throws IOException, JAXBException {
        InputStream stream = KeyUtils.readCrypt(Env.getEnvFile(LICENSE), InstallUtils.getKeyStore());
        if (stream != null)
            return FileUtils.readJAXB(License.class, stream);
        return null;
    }

    private static byte[] signature(String name, Integer sessions, Long expired) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        StringBuilder builder = new StringBuilder();
        builder.append(expired).append(name).append(sessions);
        byte[] bytes = builder.toString().getBytes("UTF-8");

        for (int i = 0; i < bytes.length; i += 2)
            bytes[i] += i;

        MessageDigest digest = MessageDigest.getInstance("MD5");
        return Base64.encode(digest.digest(bytes));
    }
}
