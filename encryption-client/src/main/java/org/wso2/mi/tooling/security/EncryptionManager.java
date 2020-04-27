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
import org.wso2.mi.tooling.security.utils.IOUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.crypto.Cipher;

/**
 * This class handles encryption related functions.
 */
public class EncryptionManager {

private Cipher cipher;
private Properties properties;
private Map<String, String> encryptedSecrets = new HashMap<>();


    public EncryptionManager(Cipher cipher, Properties info) {
        this.cipher = cipher;
        this.properties = info;
    }

    /**
     * Encrypts values provided in object properties.
     *
     * @return Map with encrypted values
     */
    public Map encrypt() {

        String inputType = properties.getProperty(Constants.PROP_SECRET_INPUT_TYPE).toLowerCase();
        // Check if single entry or bulk encryption from file
        if (Constants.PROP_CONSOLE_INPUT.equals(inputType)) {
            String secret = properties.getProperty(Constants.PROP_SECRET_PLAINTEXT);
            String alias = properties.getProperty(Constants.PROP_SECRET_ALIAS);

            encryptedSecrets.put(alias, EncryptionUtils.doEncryption(cipher, secret));
        } else if (Constants.PROP_FILE_INPUT.equals(inputType)){
            doBulkEncryptionFromFile();
        } else {
            throw new EncryptionToolException("secret input type has not been set properly");
        }

        return encryptedSecrets;
    }

    /**
     * Does bulk encryption for file inputs.
     */
    private void doBulkEncryptionFromFile() {
        String file = properties.getProperty(Constants.PROP_SECRET_FILE);
        Properties secrets = IOUtils.getProperties(file);
        Set<String> aliases = secrets.stringPropertyNames();
        for (String alias: aliases) {
            encryptedSecrets.put(alias, EncryptionUtils.doEncryption(cipher, secrets.getProperty(alias)));
        }
    }
}
