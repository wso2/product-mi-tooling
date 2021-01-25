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
export default class Nodes extends React.Component {
    componentDidMount() {
        const url = "http://0.0.0.0:9743/api/rest/groups/mi_dev/nodes";
        axios.get(url).then(response => {
            response.data.map(data => data.details = JSON.parse(data.details))
            const nodeList = response.data
            this.setState({nodeList})
        })
        
    }
    constructor(props) {
        super(props)
        this.state = { pageInfo: {
                pageId: "nodesPage",
                title: "Nodes",
                headCells: [
                    {id: 'nodeId', label: 'Node ID'},
                    {id: 'node_status', label: 'Status'},
                    {id: 'role', label: 'Role'}],
                tableOrderBy: 'service'
            },
            nodeList: []
        };
    }
    render() {
        return <EnhancedTable pageInfo={this.state.pageInfo} dataSet={this.state.nodeList}/>;
    }
}