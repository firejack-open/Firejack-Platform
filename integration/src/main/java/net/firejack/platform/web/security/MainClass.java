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