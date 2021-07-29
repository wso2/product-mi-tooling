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
import { useSelector } from 'react-redux';
import HTTPClient from '../utils/HTTPClient';

export default function ProxyService() {
    const [pageInfo] = React.useState({
        pageId: "proxy-services",
        title: "Proxy Services",
        headCells: [
            {id: 'name', label: 'Service Name'},
            {id: 'nodes', label: 'Nodes'},
            {id: 'wsdlUrl', label: 'WSDL 1.1'},
            {id: 'isRunning', label: 'State'}
        ],
        tableOrderBy: 'name'
    });
    const [proxyList, setProxyList] = React.useState([]);

    const globalGroupId = useSelector(state => state.groupId);
    const selectedNodeList = useSelector(state => state.nodeList);

    const retrieveProxyList = () => {
        HTTPClient.getArtifacts("proxy-services", globalGroupId, selectedNodeList).then(response => {
            setProxyList(response.data)
        })
    }

    React.useEffect(() => {
        retrieveProxyList();
    },[globalGroupId, selectedNodeList])

    const retrieveData = () => {
        retrieveProxyList();
    }

    return <EnhancedTable pageInfo={pageInfo} dataSet={proxyList} retrieveData={retrieveData}/>
}
