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