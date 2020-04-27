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

import org.wso2.mi.tooling.security.output.OutputManager;
import org.wso2.mi.tooling.security.utils.IOUtils;

import java.util.Map;
import java.util.Properties;
import javax.crypto.Cipher;

public class EncryptionTool {

    public static void main(String[] args) {

        String keyStoreSource = System.getenv(Constants.PROP_KEYSTORE_SOURCE);

        if (keyStoreSource == null) {
            keyStoreSource = IOUtils.getDefaultLocation(Constants.PROP_KEYSTORE_DEFAULT_SOURCE);
            System.out.println("Using default keystore-info.properties location: " + keyStoreSource);
        }
        Cipher cipher = EncryptionUtils.initializeCipher(IOUtils.getProperties(keyStoreSource));

        String secretSource = System.getenv(Constants.PROP_SECRET_SOURCE);
        if (secretSource == null) {
            secretSource = IOUtils.getDefaultLocation(Constants.PROP_SECRET_DEFAULT_SOURCE);
            System.out.println("Using default secret-info.properties location: " + secretSource);
        }

        Properties encryptionInformation = IOUtils.getProperties(secretSource);
        EncryptionManager encryptionManager = new EncryptionManager(cipher, encryptionInformation);
        Map<String, String> encodedValues = encryptionManager.encrypt();
        OutputManager outputManager = new OutputManager(encryptionInformation
                .getProperty(Constants.PROP_SECRET_OUTPUT_TYPE), encodedValues);
        outputManager.setOutput();
        System.out.println("Encryption completed successfully... ");
    }
}
