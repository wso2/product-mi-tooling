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

package org.wso2.mi.tooling.security.output;

import org.wso2.mi.tooling.security.Constants;
import org.wso2.mi.tooling.security.exception.EncryptionToolException;
import org.wso2.mi.tooling.security.utils.IOUtils;

import java.util.Map;
import java.util.Properties;

/**
 * This class manages encrypted vale output.
 */
public class OutputManager {

    private String outputType;
    private Map<String, String> secrets;

    public OutputManager(String outputType, Map<String, String> encodedValues) {

        this.outputType = outputType;
        secrets = encodedValues;
    }

    /**
     * Sets the output based on the output type.
     */
    public void setOutput() {

        if (Constants.PROP_CONSOLE_OUTPUT.equalsIgnoreCase(outputType)) {
            doConsoleOutput();
        } else if (Constants.PROP_FILE_OUTPUT.equalsIgnoreCase(outputType)) {
            doFileOutput();
        } else if (Constants.PROP_K8_OUTPUT.equalsIgnoreCase(outputType)) {
            doK8Output();
        } else {
            throw new EncryptionToolException("Secret output type: "+outputType + " is not supported. Use console, file or k8.");
        }
    }

    /**
     * Sets the output to the console.
     */
    private void doConsoleOutput() {
        secrets.forEach((alias, encodedValue) -> System.out.println(alias + " : " + encodedValue));
    }

    /**
     * Sets the output as a properties file.
     */
    private void doFileOutput() {

        Properties properties = new Properties();
        properties.putAll(secrets);
        IOUtils.setProperties(IOUtils.getOutputFilePath("wso2mi-secrets.properties"), properties);
    }

    /**
     * Sets the output as a file compatible with k8 secret.
     */
    private void doK8Output() {

        K8SecretYaml k8SecretYaml = new K8SecretYaml();
        k8SecretYaml.setStringData(secrets);

        IOUtils.writeYamlFile(IOUtils.getOutputFilePath("wso2mikube-secrets.yaml"), k8SecretYaml);
    }
}
