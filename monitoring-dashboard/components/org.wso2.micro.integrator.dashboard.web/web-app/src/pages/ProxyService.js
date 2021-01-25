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

export default class ProxyService extends React.Component {
    componentDidMount() {
        const url = "http://0.0.0.0:9743/api/rest/groups/mi_dev/proxy-services?nodes=node_1&nodes=node_2";
        axios.get(url).then(response => {
            response.data.map(data => 
                data.nodes.map(node => node.details = JSON.parse(node.details))
            )
            const proxyList = response.data
            this.setState({proxyList})
        })
    }

    constructor(props){
        super(props)
        this.state = { pageInfo: {
                pageId: "proxyPage",
                title: "Proxy Services",
                headCells: [
                    {id: 'service', label: 'Service'},
                    {id: 'nodes', label: 'Nodes'},
                    {id: 'wsdlUrl', label: 'WSDL 1.1'}],
                tableOrderBy: 'service'
            },
            proxyList:[]
        };
    }
    render() {
        return <EnhancedTable pageInfo={this.state.pageInfo} dataSet={this.state.proxyList}/>;
    }
}
