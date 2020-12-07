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
 *
 *
 */

import React from 'react';
import EnhancedTable from '../commons/EnhancedTable';

export default class NodeInfo extends React.Component {
    constructor(props){
        super(props)
        this.state = {  pageInfo: {
                pageId: "nodeInfo",
                headCells: [
                    {id: 'nodeId', label: 'Node ID'},
                    {id: 'status', label: 'Status'},
                    {id: 'role', label: 'Role'},
                    {id: 'upTime', label: 'Up Time'}],
                title: "CLUSTER INFORMATION",
                tableOrderBy: "nodeId",
            },
            nodeList: [
                {id: "node_01",
                    isActive: "true",
                    details: {
                        role: "cordinator",
                        upTime: "12mins",
                        serverName: "WSO2 micro integrator",
                        version: "1.2.0",
                        miHome: "/Users/wso2/mi",
                        javaHome: "/Users/wso2/java",
                        javaVersion: "1.8.0_191",
                        javaVendor: "Oracle",
                        os: "Linux"
                    }},
                {id: "node_02",
                    isActive: "true",
                    details: {
                        role: "member",
                        upTime: "7mins",
                        serverName: "WSO2 micro integrator",
                        version: "1.2.0",
                        miHome: "/Users/wso2/mi",
                        javaHome: "/Users/wso2/java",
                        javaVersion: "1.8.0_191",
                        javaVendor: "Oracle",
                        os: "Linux"
                    }},
                {id: "node_03",
                    isActive: "true",
                    details: {
                        role: "member",
                        upTime: "2mins",
                        serverName: "WSO2 micro integrator",
                        version: "1.2.0",
                        miHome: "/Users/wso2/mi",
                        javaHome: "/Users/wso2/java",
                        javaVersion: "1.8.0_191",
                        javaVendor: "Oracle",
                        os: "Linux"
                    }},
                {id: "node_04",
                    isActive: "true",
                    details: {
                        role: "member",
                        upTime: "1mins",
                        serverName: "WSO2 micro integrator",
                        version: "1.2.0",
                        miHome: "/Users/wso2/mi",
                        javaHome: "/Users/wso2/java",
                        javaVersion: "1.8.0_191",
                        javaVendor: "Oracle",
                        os: "Linux"
                    }}
            ]
        }
    }
    render() {
        return <EnhancedTable pageInfo={this.state.pageInfo} dataSet={this.state.nodeList}/>;
    }
}
