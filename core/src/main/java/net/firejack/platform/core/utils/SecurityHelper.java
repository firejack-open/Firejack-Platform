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

package net.firejack.platform.core.utils;

import com.sun.jersey.core.util.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;


public class SecurityHelper {
    private static Log LOGGER = LogFactory.getLog(SecurityHelper.class);

    /**
     * @param password
     * @param encryptedPass
     * @return
     */
    public static boolean isPasswordCorrect(String password, String encryptedPass) {
        String pass = SecurityHelper.hash(password);
        return pass.equals(encryptedPass);
    }

    /**
     * return hash
     *
     * @param value value to hash
     * @return hashed value
     */
    public static String hash(String value) {
        String result;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(value.getBytes());
            result = asHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * return hash
     *
     * @param plainText value to hash
     * @return hashed value
     */
    public static String hashMD5(String plainText) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            md.update(plainText.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
        byte raw[] = md.digest();
        return (new BASE64Encoder()).encode(raw);
    }

    /**
     * return hash
     *
     * @param plainText value to hash
     * @return hashed value
     */
    public static String hashSHA(String plainText) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            md.update(plainText.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
        byte raw[] = md.digest();
        return (new BASE64Encoder()).encode(raw);
    }

    public static void write64(byte[] data, String name) throws IOException {
		FileOutputStream stream = new FileOutputStream(name);
		stream.write(Base64.encode(data));
		stream.flush();
		stream.close();
	}

	public static byte[] read64(String name) throws IOException {
		File file = new File(name);
		FileInputStream stream = new FileInputStream(file);
		byte[] bytes = new byte[(int) file.length()];
		stream.read(bytes);
		stream.close();
		return Base64.decode(bytes);
	}

    /**
     * get expiration
     *
     * @param milliSecond expiration period of time in milliseconds
     * @return populated expiration date.
     */
    public static Date getExpiration(Long milliSecond) {
        long currentTime = System.currentTimeMillis();
        long expiration = currentTime + milliSecond;
        return new Date(expiration);
    }

    /**
     * generate unique and secure id
     *
     * @return generated UUID
     */
    public static String generateSecureId() {
        return UUID.randomUUID().toString();
    }

    /**
     * generate random sequence
     *
     * @param length lengh of sequence to generate
     * @return Generated random sequence
     */
    public static String generateRandomSequence(int length) {

        String password = "";

        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            for (int i = 0; i < length; i++) {
                char ch = 0;

                int j = random.nextInt(3);

                switch (j) {
                    case 0:
                        ch = (char) (97 + random.nextInt(26));
                        break;
                    case 1:
                        ch = (char) (65 + random.nextInt(26));
                        break;
                    case 2:
                        ch = (char) (48 + random.nextInt(10));
                        break;
                }
                password += ch;
            }

        } catch (NoSuchAlgorithmException e) {
            LOGGER.info("Couldn't create new instance of SecureRandom class", e);
            throw new RuntimeException(e);
        }

        return password;
    }

    /**
     * Private function to turn md5 result to 32 hex-digit string
     *
     * @param hash hash
     * @return returns appropriate hexadecimal representation of specified hash array
     */
    private static String asHex(byte hash[]) {
	    StringBuilder buf = new StringBuilder(hash.length * 2);
        int i;
        for (i = 0; i < hash.length; i++) {
            if (((int) hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    private static String encodeXOR(String source, String key) {
	    StringBuilder output = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char charCode = source.charAt(i);
            for (int j = 0; j < key.length(); j++) {
                charCode ^= (key.charAt(j) + i);
            }
            output.append(charCode);
        }
        return output.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(hash("admin"));
        System.out.println(generateRandomSequence(32));
    }
}