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

export default class Datasources extends React.Component {
    constructor(props){
        super(props)
        this.state = { pageInfo: {
                pageId: "datasources",
                title: "Data Sources",
                headCells: [
                    {id: 'name', label: 'Inbound Endpoint Name'},
                    {id: 'nodes', label: 'Nodes'},
                    {id: 'type', label: 'Type'},
                    {id: 'data_source_status', label: 'Status'}],
                tableOrderBy: 'service'
            },
            datasourceList: [{
                name: "Calculator EP",
                nodes: [
                    { nodeId: "node_01",
                        type: "Type",
                        data_source_status: "Inactive"

                    },
                    { nodeId: "node_02",
                        protocol: "http",
                        data_source_status: "Active"

                    }
                ]
            }
            ]};
    }
    render() {
        return <EnhancedTable pageInfo={this.state.pageInfo} dataSet={this.state.datasourceList}/>;
    }
}
