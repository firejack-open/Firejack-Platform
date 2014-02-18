package net.firejack.platform.web.security.license;
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
