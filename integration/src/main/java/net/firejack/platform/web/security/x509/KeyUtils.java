/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.web.security.x509;

import com.sun.jersey.core.util.Base64;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.utils.ArrayUtils;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import sun.security.x509.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class KeyUtils {

    private static final Logger logger = Logger.getLogger(KeyUtils.class);
    private static final char[] SECRET = "OpenFlameTheBest".toCharArray();
    private static final String ALIAS = "alias";
    private static final int KEYSIZE = 1024;

    public static KeyPair generate(File keystore) {
        if (keystore == null) {
            throw new IllegalArgumentException("Key Store file should not be null.");
        }

        try {
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            if (keystore.exists()) {
                FileInputStream stream = new FileInputStream(keystore);
                ks.load(stream, SECRET);
                IOUtils.closeQuietly(stream);
            } else {
                ks.load(null, SECRET);
            }

            if (ks.containsAlias(ALIAS)) {
                PrivateKey privateKey = (PrivateKey) ks.getKey(ALIAS, SECRET);
                PublicKey publicKey = ks.getCertificate(ALIAS).getPublicKey();
                return new KeyPair(publicKey, privateKey);
            } else {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(KEYSIZE, new SecureRandom());
                return generator.generateKeyPair();
            }
        } catch (Throwable th) {
            logger.error("Failed to initialize key store");
            throw new OpenFlameRuntimeException(th.getMessage(), th);
        }
    }

    public static void add(File keystore, KeyPair pair, String domain) {
        if (keystore == null) {
            throw new IllegalArgumentException("Key Store file should not be null.");
        }

        try {
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            if (keystore.exists()) {
                FileInputStream stream = new FileInputStream(keystore);
                ks.load(stream, SECRET);
                IOUtils.closeQuietly(stream);
            } else {
                ks.load(null, SECRET);
            }

            if (!ks.containsAlias(ALIAS)) {
                X509Certificate certificate = generateCertificate(domain, 1, pair);

                ks.setKeyEntry(ALIAS, pair.getPrivate(), SECRET, new Certificate[]{certificate});

                FileOutputStream stream = new FileOutputStream(keystore);
                ks.store(stream, SECRET);
                IOUtils.closeQuietly(stream);
            }
        } catch (Throwable th) {
            logger.error("Failed to initialize key store");
            throw new OpenFlameRuntimeException(th.getMessage(), th);
        }
    }

    public static KeyPair load(File keyStoreFile) {
        if (keyStoreFile != null) {
            try {
                KeyStore ks = KeyStore.getInstance("JKS", "SUN");
                if (keyStoreFile.exists()) {
                    FileInputStream stream = new FileInputStream(keyStoreFile);
                    ks.load(stream, SECRET);
                    IOUtils.closeQuietly(stream);

                    PrivateKey privateKey = (PrivateKey) ks.getKey(ALIAS, SECRET);
                    if (privateKey == null) return null;
                    PublicKey publicKey = ks.getCertificate(ALIAS).getPublicKey();
                    return new KeyPair(publicKey, privateKey);
                }
            } catch (Throwable th) {
                logger.error("Failed to initialize key store");
                throw new OpenFlameRuntimeException(th.getMessage(), th);
            }
        } else {
            throw new IllegalArgumentException("Key Store file should not be null.");
        }
        return null;
    }

    public static X500Name getInfo(File keystore) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            FileInputStream stream = new FileInputStream(keystore);
            ks.load(stream, SECRET);
            IOUtils.closeQuietly(stream);
            X509CertImpl x509Cert = (X509CertImpl) ks.getCertificate(ALIAS);
            return (X500Name) x509Cert.getSubjectDN();
        } catch (Exception e) {
            return null;
        }
    }

    public static X509Certificate generateCertificate(String domain, int days, KeyPair keyPair) throws GeneralSecurityException, IOException {
        X509CertInfo info = new X509CertInfo();
        Date from = new Date();
        Date to = new Date(from.getTime() + days * 86400000l);
        CertificateValidity interval = new CertificateValidity(from, to);
        BigInteger sn = new BigInteger(64, new SecureRandom());

        X500Name owner = new X500Name("DC=" + domain);

        info.set(X509CertInfo.VALIDITY, interval);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
        info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
        info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
        info.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));

        AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));

        X509CertImpl cert = new X509CertImpl(info);
        cert.sign(keyPair.getPrivate(), "MD5WithRSA");

        return cert;
    }

    public static void verify(String cert64, String public64) throws CertificateException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if (StringUtils.isBlank(cert64)) {
            throw new CertificateException("Certificate is empty");
        } else if (StringUtils.isBlank(public64)) {
            throw new InvalidKeyException("Public key is empty");
        }
        byte[] cert = Base64.decode(cert64);
        byte[] key = Base64.decode(public64);
        X509CertImpl x509 = new X509CertImpl(cert);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        x509.verify(publicKey);
        x509.checkValidity(utc.getTime());
    }

    public static byte[] encrypt(PublicKey key, byte[] bytes) throws Exception {
        byte[] result = new byte[0];

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        int length = cipher.getOutputSize(bytes.length) - 11;
        for (int i = 0; i < bytes.length; i += length) {
            byte[] buffer = cipher.doFinal(bytes, i, Math.min(bytes.length - i, length));
            result = ArrayUtils.addAll(result, buffer);
        }

        return result;
    }

    public static byte[] encrypt(byte[] data, String password) throws Exception {
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key(password, 16), "AES"));
        return c.doFinal(data);
    }

    public static byte[] decrypt(PrivateKey key, byte[] bytes) throws Exception {
        byte[] result = new byte[0];

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        int length = cipher.getOutputSize(bytes.length);

        for (int i = 0; i < bytes.length; i += length) {
            byte[] bytes1 = cipher.doFinal(bytes, i, Math.min(bytes.length - i, length));
            result = ArrayUtils.addAll(result, bytes1);
        }

        return result;
    }

    public static byte[] decrypt(byte[] data, String password) throws Exception {
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key(password, 16), "AES"));
        return c.doFinal(data);
    }

    public static Properties getProperties(File properties, File keystore) {
        Properties props = new Properties();
        if (!properties.exists()) return props;

        InputStream stream = null;
        try {
            byte[] bytes = FileUtils.readFileToByteArray(properties);
            stream = new ByteArrayInputStream(bytes);
            if (keystore.exists()) {
                KeyPair keyPair = load(keystore);
                try {
                    byte[] decrypt = decrypt(keyPair.getPrivate(), bytes);
                    stream = new ByteArrayInputStream(decrypt);
                } catch (Exception e) {
                    logger.trace(e);
                }
            }
            props.load(stream);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return props;
    }

    private static byte[] key(String key, int len) {
        key = StringUtils.left(key, len);
        key = StringUtils.rightPad(key, len, '=');
        return key.getBytes();
    }

    public static Map<String, String> getMapProperties(File properties, File keystore) {
        Properties props = getProperties(properties, keystore);
        Map<String, String> propertiesMap = new HashMap<String, String>();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            propertiesMap.put((String) entry.getKey(), (String) entry.getValue());
        }
        return propertiesMap;
    }

    public static void setProperties(File properties, File keystore, Map<String, String> append) throws IOException {
        Properties props = getProperties(properties, keystore);

        if (properties.exists() || properties.createNewFile()) {
            props.putAll(append);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            props.store(baos, null);

            InputStream stream = new ByteArrayInputStream(baos.toByteArray());
            if (keystore.exists()) {
                KeyPair keyPair = load(keystore);
                if (keyPair != null) {
                    try {
                        byte[] decrypt = encrypt(keyPair.getPublic(), baos.toByteArray());
                        stream = new ByteArrayInputStream(decrypt);
                    } catch (Exception e) {
                        logger.trace(e);
                    }
                }
            }

            FileOutputStream writer = new FileOutputStream(properties);
            IOUtils.copy(stream, writer);
            IOUtils.closeQuietly(writer);
        }
    }

    public static Properties getProperties(byte[] key, byte[] data) {
        Properties properties = new Properties();
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(key);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            byte[] decrypt = decrypt(privateKey, data);

            InputStream stream = new ByteArrayInputStream(decrypt);
            properties.load(stream);
        } catch (Exception e) {
            logger.trace(e);
        }
        return properties;
    }

    public static void writeCrypt(File file, File keystore, InputStream stream) throws IOException {
        if (keystore.exists()) {
            KeyPair keyPair = load(keystore);
            if (keyPair != null) {
                try {
                    byte[] decrypt = encrypt(keyPair.getPublic(), IOUtils.toByteArray(stream));
                    FileUtils.writeByteArrayToFile(file, decrypt);
                } catch (Exception e) {
                    logger.trace(e);
                }
            }
        }
    }

    public static InputStream readCrypt(File file, File keystore) {
        if (!file.exists()) return null;

        InputStream stream = null;
        try {
            if (keystore.exists()) {
                KeyPair keyPair = load(keystore);
                byte[] decrypt = decrypt(keyPair.getPrivate(), FileUtils.readFileToByteArray(file));
                stream = new ByteArrayInputStream(decrypt);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return stream;
    }
}