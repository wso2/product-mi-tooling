/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.micro.integrator.dashboard.utils;

import io.asgardeo.java.oidc.sdk.config.model.OIDCAgentConfig;

/**
 * This class is used to store SSO configs.
 */
public class SSOConfig {

    private OIDCAgentConfig oidcAgentConfig;
    private String adminGroupAttribute;
    private String allowedAdminGroups;
    private String wellKnownEndpoint;
    private String introspectionEndpoint;
    private String userInfoEndpoint;

    public SSOConfig(OIDCAgentConfig oidcAgentConfig, String adminGroupAttribute,
                     String allowedAdminGroups, String wellKnownEndpoint, String introspectionEndpoint,
                     String userInfoEndpoint) {

        this.oidcAgentConfig = oidcAgentConfig;
        this.adminGroupAttribute = adminGroupAttribute;
        this.allowedAdminGroups = allowedAdminGroups;
        this.wellKnownEndpoint = wellKnownEndpoint;
        this.introspectionEndpoint = introspectionEndpoint;
        this.userInfoEndpoint = userInfoEndpoint;
    }

    public String getWellKnownEndpoint() {

        return wellKnownEndpoint;
    }

    public void setWellKnownEndpoint(String wellKnownEndpoint) {

        this.wellKnownEndpoint = wellKnownEndpoint;
    }

    public OIDCAgentConfig getOidcAgentConfig() {

        return oidcAgentConfig;
    }

    public void setOidcAgentConfig(OIDCAgentConfig oidcAgentConfig) {

        this.oidcAgentConfig = oidcAgentConfig;
    }

    public String getAdminGroupAttribute() {

        return adminGroupAttribute;
    }

    public void setAdminGroupAttribute(String adminGroupAttribute) {

        this.adminGroupAttribute = adminGroupAttribute;
    }

    public String getAllowedAdminGroups() {

        return allowedAdminGroups;
    }

    public void setAllowedAdminGroups(String allowedAdminGroups) {

        this.allowedAdminGroups = allowedAdminGroups;
    }

    public String getIntrospectionEndpoint() {

        return introspectionEndpoint;
    }

    public void setIntrospectionEndpoint(String introspectionEndpoint) {

        this.introspectionEndpoint = introspectionEndpoint;
    }

    public String getUserInfoEndpoint() {

        return userInfoEndpoint;
    }

    public void setUserInfoEndpoint(String userInfoEndpoint) {

        this.userInfoEndpoint = userInfoEndpoint;
    }
}
