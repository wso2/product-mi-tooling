/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mi.tooling.security;

import org.wso2.mi.tooling.security.exception.EncryptionToolException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

public class EncryptionUtils {

    /**
     * Generates a cipher based on keystore information.
     *
     * @param properties keystore information
     * @return cipher object
     */
    public static Cipher initializeCipher(Properties properties) {

        Cipher cipher;
        String keyStoreFile = properties.getProperty(Constants.PROP_KEYSTORE_LOCATION);
        String keyStoreType = properties.getProperty(Constants.PROP_KEYSTORE_TYPE);
        String keyAlias = properties.getProperty(Constants.PROP_KEYSTORE_ALIAS);
        String keyStorePassword = properties.getProperty(Constants.PROP_KEYSTORE_PASSWORD);
        // input password will be base64 encoded
        keyStorePassword = new String(Base64.getDecoder().decode(keyStorePassword));
        KeyStore keyStore = getKeyStore(keyStoreFile, keyStorePassword, keyStoreType);

        try {
            Certificate certs = keyStore.getCertificate(keyAlias);
            cipher = Cipher.getInstance(getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, certs);
        } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new EncryptionToolException("Error while initializing cipher ", e);
        }
        return cipher;
    }

    /**
     * Generate keystore object from a given keystore location
     *
     * @param location      the location of the keystore
     * @param storePassword keystore password
     * @param storeType     keystore type
     * @return keystore object
     */
    private static KeyStore getKeyStore(String location, String storePassword, String storeType)  {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(location))) {
            KeyStore keyStore = KeyStore.getInstance(storeType);
            keyStore.load(bufferedInputStream, storePassword.toCharArray());
            return keyStore;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new EncryptionToolException("Error initializing Cipher", e);
        }
    }

    /**
     * @return string algorithm
     */
    private static String getAlgorithm() {

        String algorithm = System.getenv(Constants.PROP_ENCRYPT_ALGORITHM);
        if (algorithm == null) {
            algorithm = "RSA/ECB/OAEPwithSHA1andMGF1Padding";
        }
        return algorithm;
    }

    /**
     * Encrypts plaintext values based on given cipher.
     *
     * @param cipher       Encryption cipher
     * @param plainTextPwd pain text password
     * @return string encrypted value
     */
    public static String doEncryption(Cipher cipher, String plainTextPwd) {

        String encodedValue;
        try {
            byte[] encryptedPassword = cipher.doFinal(plainTextPwd.getBytes(Charset.forName("UTF-8")));
            encodedValue = DatatypeConverter.printBase64Binary(encryptedPassword);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new EncryptionToolException("Error encrypting password ", e);
        }
        return encodedValue;
    }
}
