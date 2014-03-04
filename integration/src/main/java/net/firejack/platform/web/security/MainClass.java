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

package net.firejack.platform.web.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class MainClass {

//        public static void main(String[] args) throws Exception {
//            KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
//            keyGenerator.init(128);
//            Key blowfishKey = keyGenerator.generateKey();
//
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//            keyPairGenerator.initialize(1024);
//            KeyPair keyPair = keyPairGenerator.genKeyPair();
//
//            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
//
//            byte[] blowfishKeyBytes = blowfishKey.getEncoded();
//            System.out.println(new String(blowfishKeyBytes));
//            byte[] cipherText = cipher.doFinal(blowfishKeyBytes);
//            System.out.println(new String(cipherText));
//            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
//
//            byte[] decryptedKeyBytes = cipher.doFinal(cipherText);
//            System.out.println(new String(decryptedKeyBytes));
//            SecretKey newBlowfishKey = new SecretKeySpec(decryptedKeyBytes, "Blowfish");
//        }

    /**
     * @param args
     * @throws javax.crypto.BadPaddingException
     *
     * @throws javax.crypto.NoSuchPaddingException
     *
     * @throws java.security.NoSuchAlgorithmException
     *
     * @throws java.security.InvalidKeyException
     *
     * @throws java.security.NoSuchProviderException
     *
     * @throws javax.crypto.IllegalBlockSizeException
     *
     */
    public static void main(String[] args) throws NoSuchAlgorithmException,

            InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException,

            BadPaddingException, NoSuchPaddingException {

/* Generate a RSA key pair */

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

        keyGen.initialize(1024, random);

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();
//System.out.println("Public is" + pub);
//System.out.println("Private is" + priv);

/* Create the cipher */
        Cipher rsaCipher = Cipher.getInstance("RSA");

// Initialize the cipher for encryption
        rsaCipher.init(Cipher.ENCRYPT_MODE, pub);

// Cleartext
        byte[] cleartext = null;
        cleartext = "This is Bilal".getBytes();
//byte[] cleartext = "This is Bilal".getBytes();
//String cleartext = "This is just an example";
        System.out.println("the original cleartext is:" + new String(cleartext));
//System.out.println("the original cleartext is: " + cleartext);

// Encrypt the cleartext
        byte[] ciphertext = null;
        ciphertext = rsaCipher.doFinal(cleartext);
//byte[] ciphertext = rsaCipher.doFinal(cleartext);
//String ciphertext = rsaCipher.doFinal(cleartext);
        System.out.println("the encrypted text is:" + new String(ciphertext));

// Initialize the same cipher for decryption
        rsaCipher.init(Cipher.DECRYPT_MODE, priv);

// Decrypt the ciphertext
        byte[] cleartext1 = rsaCipher.doFinal(ciphertext);
//String cleartext1 = rsaCipher.doFinal(ciphertext);
        System.out.println("the final cleartext is:" + new String(cleartext1));
//System.out.println("the final cleartext is: " + cleartext1);
    }

}