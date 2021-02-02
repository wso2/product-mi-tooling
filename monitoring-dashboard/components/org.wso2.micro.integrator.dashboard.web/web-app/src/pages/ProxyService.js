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
import axios from 'axios';
import EnhancedTable from '../commons/EnhancedTable';
import { useSelector } from 'react-redux';

export default function ProxyService() {
    const [pageInfo, setPageInfo] = React.useState({
        pageId: "proxyPage",
        title: "Proxy Services",
        headCells: [
            {id: 'service', label: 'Service'},
            {id: 'nodes', label: 'Nodes'},
            {id: 'wsdlUrl', label: 'WSDL 1.1'}],
        tableOrderBy: 'service'
    });
    const [proxyList, setProxyList] = React.useState([]);

    const globalGroupId = useSelector(state => state.groupId);
    const selectedNodeList = useSelector(state => state.nodeList);
    
    React.useEffect(() => {
        var nodeListQueryParams="";
        selectedNodeList.filter(node => {
            nodeListQueryParams = nodeListQueryParams.concat(node, '&nodes=')
        })
        const url = "http://0.0.0.0:9743/api/rest/groups/".concat(globalGroupId).concat("/proxy-services?nodes=").concat(nodeListQueryParams.slice(0,-7));
        axios.get(url).then(response => {
            response.data.map(data => 
                data.nodes.map(node => node.details = JSON.parse(node.details))
            )
            setProxyList(response.data)
        })
    },[globalGroupId, selectedNodeList])

    return <EnhancedTable pageInfo={pageInfo} dataSet={proxyList}/>
}
