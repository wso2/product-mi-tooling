/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *
 */

import axios from 'axios';
import { AuthClient } from "@asgardeo/auth-react";
import AuthManager from '../auth/AuthManager';
import {Constants} from './Constants';

export default class HTTPClient {

    static httpCall(method, path, params, body) {

        const url = AuthManager.getBasePath().concat(path)

        const requestConfig = {
            method: method,
            url: url,
            params: params,
            data: body
        };

        if (AuthManager.getUser().sso) {
            return AuthClient.httpRequest(requestConfig);
        } else {
            return axios.request(requestConfig)
        }
    }

    static get(path, params={}) {
        return this.httpCall("GET", path, params, null)
    }

    static post(path, body) {
        return this.httpCall("POST", path, null, body)
    }

    static patch(path, body) {
        return this.httpCall("PATCH", path, null, body)
    }

    static delete(path) {
        return this.httpCall("DELETE", path, null, null)
    }

    static getConfiguration(params) {
        return this.get("/configuration", params)
    }

    static getResource(resourcePath = "") {
        const path = `/${Constants.PREFIX_GROUPS}/${resourcePath}`
        return this.get(path)
    }

    static getSuperUser() {
        return this.get("/configs/super-admin")
    }

    static getGroups() {
        return this.getResource()
    }

    static getNodes(groupId) {
        const resourcePath = `${groupId}/${Constants.PREFIX_NODES}`
        return this.getResource(resourcePath)
    }

    static getUsers(groupId) {
        const resourcePath = `${groupId}/${Constants.PREFIX_USERS}`
        return this.getResource(resourcePath)
    }

    static deleteUser(groupId, userId) {
        var resourcePath = `/${Constants.PREFIX_GROUPS}/${groupId}/${Constants.PREFIX_USERS}/`
        const parts = userId.split("/")
        if(parts.length===1) {
            resourcePath = resourcePath.concat(parts[0]);
        } else {
            resourcePath = resourcePath.concat(parts[1]).concat("?domain=").concat(parts[0]);
        }
        return this.delete(resourcePath)
    }

    static addUser(groupId, payload) {
        const path = `/${Constants.PREFIX_GROUPS}/${groupId}/${Constants.PREFIX_USERS}`
        return this.post(path, payload)
    }

    static getRoles(groupId) {
        const resourcePath = `${groupId}/${Constants.PREFIX_ROLES}`
        return this.getResource(resourcePath)
    }

    static addRole(groupId, payload) {
        const path = `/${Constants.PREFIX_GROUPS}/${groupId}/${Constants.PREFIX_ROLES}`
        return this.post(path, payload)
    }

    static updateUserRoles(groupId, payload) {
        const path = `/${Constants.PREFIX_GROUPS}/${groupId}/${Constants.PREFIX_ROLES}`
        return this.patch(path, payload)
    }

    static deleteRole(groupId, roleName) {
        var resourcePath = `/${Constants.PREFIX_GROUPS}/${groupId}/${Constants.PREFIX_ROLES}/`;
        const parts = roleName.split("/");
        if(parts.length===1) {
            resourcePath = resourcePath.concat(parts[0])
        } else {
            resourcePath = resourcePath.concat(parts[1]).concat("?domain=").concat(parts[0]);
        }
        return this.delete(resourcePath)
    }

    static getLogConfigs(groupId, nodeId = "") {
        var resourcePath = `${groupId}/${Constants.PREFIX_LOG_CONFIGS}`
        if (nodeId !== "") {
            resourcePath = `${resourcePath}/${Constants.PREFIX_NODES}/${nodeId}`
        }
        return this.getResource(resourcePath)
    }

    static addLogConfig(groupId, payload) {
        const path = `/${Constants.PREFIX_GROUPS}/${groupId}/${Constants.PREFIX_LOG_CONFIGS}`
        return this.post(path, payload)
    }

    static updateAllLogConfig(groupId, payload) {
        const path = `/${Constants.PREFIX_GROUPS}/${groupId}/${Constants.PREFIX_LOG_CONFIGS}`
        return this.patch(path, payload)
    }

    static updateLogConfig(groupId, nodeId, payload) {
        const path = `/${Constants.PREFIX_GROUPS}/${groupId}/${Constants.PREFIX_LOG_CONFIGS}/${Constants.PREFIX_NODES}/${nodeId}`
        return this.patch(path, payload)
    }

    static getLogFiles(groupId, nodeList) {
        const resourcePath = `${groupId}/logs?nodes=${this.getNodeListAsQueryParams(nodeList)}`
        return this.getResource(resourcePath)
    }

    static getLocalEntryValue(groupId, nodeId, name) {
        const resourcePath = `${groupId}/${Constants.PREFIX_NODES}/${nodeId}/local-entries/${name}/value`
        return this.getResource(resourcePath)
    }

    static getArtifacts(artifactType, groupId, nodeList) {
        const resourcePath = `${groupId}/${artifactType}?nodes=${this.getNodeListAsQueryParams(nodeList)}`
        return new Promise((resolve, reject) => {
            this.getResource(resourcePath).then(response => {
                response.data.map(data => 
                    data.nodes.map(node => node.details = JSON.parse(node.details))
                )
                resolve(response)
            }).catch(error => {
                reject(error)
            })
        });
    }

    static getCappArtifacts(groupId, nodeId, artifactName) {
        const resourcePath = `${groupId}/${Constants.PREFIX_NODES}/${nodeId}/capps/${artifactName}/artifacts`
        return this.getResource(resourcePath)
    }

    static updateArtifact(groupId, pageId, payload) {
        const path = `/${Constants.PREFIX_GROUPS}/${groupId}/${pageId}`
        return this.patch(path, payload)
    }

    static getNodeListAsQueryParams(nodeList) {
        var nodeListQueryParams = ""
        nodeList.filter(node => {
            nodeListQueryParams = nodeListQueryParams.concat(node, '&nodes=')
        })
        return nodeListQueryParams.slice(0, -7);
    }
}
