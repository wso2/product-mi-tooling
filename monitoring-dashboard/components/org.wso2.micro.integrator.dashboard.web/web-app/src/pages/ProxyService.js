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

export default class ProxyService extends React.Component {
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
            proxyList: [{
                service: "HospitalProxy",
                nodes: [
                    { nodeId: "node_01",
                        isActive: false,
                        wsdlUrl : "http://dulanjali:8290/services/Hospital?wsdl",
                        endpoints: ["http://localhost:8290/services/HospitalProxy","https://localhost:8253/services/HospitalProxy"],
                        source: '<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"Calculator\" transports=\"http https\" startOnLoad=\"true\"><target><inSequence/><outSequence/><faultSequence/></target></proxy>',
                        details: { "statistics": "disabled",
                            "tracing": true  }
                    },
                    { nodeId: "node_02",
                        isActive: true,
                        wsdlUrl : "http://dulanjali:8291/services/Hospital?wsdl",
                        endpoints: ["http://localhost:8291/services/HospitalProxy","https://localhost:8254/services/HospitalProxy"],
                        details: { "statistics": "disabled",
                            "tracing": false  }
                    }
                ]
            },

                {
                    service: "SchoolProxy",
                    nodes: [
                        { nodeId: "node_03",
                            isActive: true,
                            wsdlUrl : "http://dulanjali:8290/services/School?wsdl",
                            endpoints: ["http://localhost:8290/services/SchoolProxy","https://localhost:8253/services/SchoolProxy"],
                            details: { "statistics": "disabled",
                                "tracing": true  }
                        },
                        { nodeId: "node_04",
                            isActive: true,
                            wsdlUrl : "http://dulanjali:8291/services/School?wsdl",
                            endpoints: ["http://localhost:8291/services/SchoolProxy","https://localhost:8254/services/SchoolProxy"],
                            details: { "statistics": "disabled",
                                "tracing": true  }
                        }
                    ]
                }
            ]};
    }
    render() {
        return <EnhancedTable pageInfo={this.state.pageInfo} dataSet={this.state.proxyList}/>;
    }
}
