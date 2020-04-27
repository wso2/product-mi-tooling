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

import java.util.HashMap;
import java.util.Map;

/**
 * This class is the templates the k8 secret .yaml file
 */
public class K8SecretYaml {

    private String apiVersion;
    private String kind;
    private String type;
    private Map<String, String>  metadata;
    private Map<String, String> stringData;

    public K8SecretYaml() {

        apiVersion = "v1";
        kind = "Secret";
        type = "Opaque";
        metadata = new HashMap<>();
        metadata.put("name", "wso2misecret");
        metadata.put("namespace", "default");
    }

    public String getApiVersion() {

        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {

        this.apiVersion = apiVersion;
    }

    public String getKind() {

        return kind;
    }

    public void setKind(String kind) {

        this.kind = kind;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public Map<String, String> getMetadata() {

        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {

        this.metadata = metadata;
    }

    public Map<String, String> getStringData() {

        return stringData;
    }

    public void setStringData(Map<String, String> stringData) {

        this.stringData = stringData;
    }
}
