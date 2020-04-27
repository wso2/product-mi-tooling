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

public class Constants {

    // keystore location property
    public static final String PROP_KEYSTORE_LOCATION = "secret.keystore.location";
    // keystore type property i.e: JKS
    public static final String PROP_KEYSTORE_TYPE = "secret.keystore.type";
    // keystore alias property
    public static final String PROP_KEYSTORE_ALIAS = "secret.keystore.alias";
    // keystore password property
    public static final String PROP_KEYSTORE_PASSWORD = "secret.keystore.password";
    // encryption algorithm environment variable property
    public static final String PROP_ENCRYPT_ALGORITHM = "secret.encryption.algorithm";
    // secret input type property i.e: console/file
    public static final String PROP_SECRET_INPUT_TYPE = "secret.input.type";
    // secret output type property
    public static final String PROP_SECRET_OUTPUT_TYPE = "secret.output.type";
    // property for plaintext of secret
    public static final String PROP_SECRET_PLAINTEXT = "secret.plaintext.secret";
    // property for alias of secret
    public static final String PROP_SECRET_ALIAS = "secret.plaintext.alias";
    // bulk secret file location property
    public static final String PROP_SECRET_FILE = "secret.input.file";
    // location property for the keystore-info.properties file
    public static final String PROP_KEYSTORE_SOURCE = "keystore.source";
    // location property for the secret-info.properties file
    public static final String PROP_SECRET_SOURCE = "secret.source";
    // default location keystore-info.properties file
    public static final String PROP_KEYSTORE_DEFAULT_SOURCE = "keystore-info.properties";
    // default location for the secret-info.properties file
    public static final String PROP_SECRET_DEFAULT_SOURCE = "secret-info.properties";
    // console input type
    public static final String PROP_CONSOLE_INPUT = "console";
    // file input type
    public static final String PROP_FILE_INPUT = "file";
    // console output type
    public static final String PROP_CONSOLE_OUTPUT = "console";
    // k8 output type
    public static final String PROP_K8_OUTPUT = "k8";
    // docker output type
    public static final String PROP_DOCKER_OUTPUT = "docker";
    // properties file output type
    public static final String PROP_FILE_OUTPUT = "file";

}
