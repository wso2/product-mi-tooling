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
import TableRow from '@material-ui/core/TableRow';
import TableCell from '@material-ui/core/TableCell';
import NodesCell from '../commons/NodesCell';
import StatusCell from '../commons/StatusCell';
import SwitchStatusCell from '../commons/SwitchStatusCell';
import Drawer from '@material-ui/core/Drawer';
import { makeStyles } from '@material-ui/core/styles';
import NodeInfoSideDrawer from '../commons/NodeInfoSideDrawer';

export default function TableRowCreator(props) {
    const { pageInfo, data, headers } = props;

    switch(pageInfo.pageId) {
        case 'nodeInfo':
            return <NodeRowCreator headers={headers} data={data} />
        case 'nodes':
            return <NodeRowCreator headers={headers} data={data} />
        case 'proxyPage':
            return <ProxyRowCreator headers={headers} data={data} />
        case 'endpoints':
            return <EndpointRowCreator headers={headers} data={data} />
        case 'inbound_endpoints':
            return <InboundEndpointRowCreator headers={headers} data={data} />
        case 'message_processors':
            return <EndpointRowCreator headers={headers} data={data} />
        case 'message_stores':
            return <MessageStoreRowCreator headers={headers} data={data} />
        case 'apis':
            return <APIRowCreator headers={headers} data={data} />
        case 'sequences':
            return <SequenceRowCreator headers={headers} data={data} />
        case 'dataservices':
            return <DataserviceRowCreator headers={headers} data={data} />
        case 'datasources':
            return <DatasourceRowCreator headers={headers} data={data} />
        case 'connectors':
            return <ConnectorRowCreator headers={headers} data={data} />
        case 'carbon_applications':
            return <CarbonApplicationRowCreator headers={headers} data={data} />
        case 'logfiles':
            return <LogFileRowCreator headers={headers} data={data} />

    }
}

function NodeRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'nodeId':
                return <TableCell>{data.nodeId}</TableCell>
            case 'status':
                return <TableCell><StatusCell data={data.status} /></TableCell>
            case 'role':
                return <TableCell>{data.role}</TableCell>
            case 'upTime':
                return <TableCell>{data.upTime}</TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}
function ProxyRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'service':
                return <TableCell>{data.service}</TableCell>
            case 'nodes':
                return <TableCell>{data.nodes.map(node=><NodesCell data={node} proxyName={data['service']}/>)}</TableCell>
            case 'wsdlUrl':
                return <TableCell>{data.nodes.map(node=><StringCell data={node.wsdlUrl}/>)}</TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}

function EndpointRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell data={node} proxyName={data['service']}/>)}</table></TableCell>
            case 'type':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.type} proxyName={data['service']}/>)}</table></TableCell>
            case 'state':
                return <TableCell>{data.nodes.map(node=><SwitchStatusCell data={node.state}/>)}</TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}

function InboundEndpointRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell data={node} proxyName={data['service']}/>)}</table></TableCell>
            case 'protocol':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.protocol} proxyName={data['service']}/>)}</table></TableCell>
            case 'port':
                return <TableCell>{data.nodes.map(node=><StringCell data={node.port}/>)}</TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}

function MessageStoreRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell data={node} proxyName={data['service']}/>)}</table></TableCell>
            case 'type':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.type} proxyName={data['service']}/>)}</table></TableCell>
            case 'message_count':
                return <TableCell>{data.nodes.map(node=><StringCell data={node.message_count}/>)}</TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}
function APIRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell data={node} proxyName={data['service']}/>)}</table></TableCell>
            case 'urls':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.urls} />)}</table></TableCell>
            case 'tracing':
                return <TableCell>{data.nodes.map(node=><SwitchStatusCell data={node}/>)}</TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}
function SequenceRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell data={node} proxyName={data['service']}/>)}</table></TableCell>
            case 'statistic':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.statistic} />)}</table></TableCell>
            case 'tracing':
                return <TableCell>{data.nodes.map(node=><SwitchStatusCell data={node}/>)}</TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}
function DataserviceRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell data={node} proxyName={data['service']}/>)}</table></TableCell>
            case 'wsdlUrl':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.wsdlURL} />)}</table></TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}

function DatasourceRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell data={node} proxyName={data['service']}/>)}</table></TableCell>
            case 'type':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.type} />)}</table></TableCell>
            case 'status':
                return <TableCell><table>{data.nodes.map(node=><StatusCell data={node.status} />)}</table></TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}

function ConnectorRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell data={node} proxyName={data['service']}/>)}</table></TableCell>
            case 'package':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.package} />)}</table></TableCell>
            case 'description':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.description} />)}</table></TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}

function CarbonApplicationRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell data={node} proxyName={data['service']}/>)}</table></TableCell>
            case 'version':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.version} />)}</table></TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}

function LogFileRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell data={node} proxyName={data['service']}/>)}</table></TableCell>
            case 'size':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.size} />)}</table></TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}

function NodeIDCell(props) {
    const {data} = props;
    const classes = useStyles();

    const [state, setState] = React.useState({
        openSideDrawer: false,
    });

    const toggleDrawer = (open) => (event) => {
        if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
            return;
        }

        setState({ ...state, openSideDrawer: open });
    };

    return <TableCell onClick={toggleDrawer(true)} className={classes.tableCell}>{data.id}
        <Drawer anchor='right' open={state['openSideDrawer']} onClose={toggleDrawer(false)} >
            <NodeInfoSideDrawer data={data} />
        </Drawer> </TableCell>
}
function StringCell(props) {
    var nodeData = props.data
    return <tr><td>{nodeData}</td></tr>
}
const useStyles = makeStyles((theme) => ({
    tableCell : {
        paddingLeft: '15px',
        color: '#3f51b5'
    }
}));
