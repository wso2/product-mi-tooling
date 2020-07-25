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

package org.wso2.mi.tooling.security.utils;

import org.wso2.mi.tooling.security.exception.EncryptionToolException;
import org.wso2.mi.tooling.security.output.K8SecretYaml;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class IOUtils {

    private static final String CLI_HOME_PROP = "wso2.mi.cli.home";

    /**
     * Get properties defined in file.
     *
     * @param fileName properties file name
     * @return Properties object
     */
    public static Properties getProperties(String fileName) {
        Properties properties =  new Properties();
        File propertiesFile = new File(fileName);
        if (!propertiesFile.exists()) {
            throw new EncryptionToolException(fileName + " file cannot be found");
        }

        try (InputStream in = new FileInputStream(propertiesFile)) {
            properties.load(in);
        } catch (IOException error) {
            throw new EncryptionToolException("Error loading properties from file");
        }
        return properties;
    }

    /**
     * Writes properties to defined file.
     *
     * @param filePath   destination
     * @param properties content of file
     */
    public static void setProperties(String filePath, Properties properties) {

        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            System.out.println("writing secrets to " + filePath);
            properties.store(outputStream, null);
        } catch (IOException error) {
            throw new EncryptionToolException("Error occurred while output file " + filePath);
        }
    }

    /**
     * Provides the absolute output path for a file.
     *
     * @param fileName file name
     * @return absolute output path
     */
    public static String getOutputFilePath(String fileName) {

        String cliHome = System.getenv(CLI_HOME_PROP);
        File directory = new File(cliHome);
        File securityDir = new File(directory.getParent() + File.separator + "security");
        if (!securityDir.exists() && !securityDir.mkdir()) {
            throw new EncryptionToolException("Failed to create directory in " + securityDir);
        }
        return securityDir.getPath() + File.separator + fileName;
    }

    /**
     * Writes k8 secret .yml file
     *
     * @param filePath file destination
     * @param yaml     K8SecretYaml object
     */
    public static void writeYamlFile(String filePath, K8SecretYaml yaml) {

        DumperOptions options = new DumperOptions();
        // Configuration to hide the bean type (org.wso2.mi.tooling.security.output.K8SecretYaml)
        Representer representer = new Representer();
        representer.addClassTag(K8SecretYaml.class, Tag.MAP);

        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml output = new Yaml(representer, options);
        try (FileWriter writer = new FileWriter(filePath)) {
            output.dump(yaml, writer);
            System.out.println("Kubernetes secret file created in " + filePath + " with default name and namespace");
            System.out.println("You can change the default values as required before applying.");
        } catch (IOException e) {
            throw new EncryptionToolException("Error while creating " + filePath);
        }
    }

    /**
     * Default location for any file is working directory
     * @param fileName file name
     * @return default location for file
     */
    public static String getDefaultLocation(String fileName) {
        String workingDirectory = System.getProperty("user.dir");
        return Paths.get(workingDirectory, fileName).toString();
    }
}
