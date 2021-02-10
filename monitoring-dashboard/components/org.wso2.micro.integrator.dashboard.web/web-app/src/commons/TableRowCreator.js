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
import TableRow from '@material-ui/core/TableRow';
import TableCell from '@material-ui/core/TableCell';
import NodesCell from './NodesCell';
import StatusCell from '../commons/StatusCell';
import Switch from "react-switch";
import { makeStyles } from '@material-ui/core/styles';
import { useSelector } from 'react-redux';

export default function TableRowCreator(props) {
    const { pageId, data, headers } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            // common
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell pageId={pageId} nodeData={node} />)}</table></TableCell>

            // Proxy Services
            case 'wsdlUrl':
                return <TableCell><table>{data.nodes.map(node=><LinkCell data={node.details.wsdl1_1} />)}</table></TableCell>
            case 'isRunning':
                return <TableCell>{data.nodes.map(node=><SwitchStatusCell pageId={pageId} artifactName={node.details.name} 
                        nodeId={node.nodeId} status={node.details.isRunning}/>)}</TableCell>
            // Endpoints
            case 'type':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.type} />)}</table></TableCell>
            case 'state':
                return <TableCell>{data.nodes.map(node=><SwitchStatusCell pageId={pageId} artifactName={node.details.name} 
                        nodeId={node.nodeId} status={node.details.isActive}/>)}</TableCell>

            // Inbound Endpoints
            case 'protocol':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.protocol} />)}</table></TableCell>

            // Apis
            case 'url':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.url} />)}</table></TableCell>

            // Templates
            case 'template_nodes':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.nodeId} />)}</table></TableCell>

            // Sequences
            case 'statistic':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.stats} />)}</table></TableCell>

            case 'version':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.version} />)}</table></TableCell>
            case 'size':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.size} />)}</table></TableCell>
            case 'package':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.package} />)}</table></TableCell>
            case 'description':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.description} />)}</table></TableCell>
            case 'data_source_status':
                return <TableCell><table>{data.nodes.map(node=><StatusCell data={node.data_source_status} />)}</table></TableCell>
            case 'message_count':
                return <TableCell>{data.nodes.map(node=><StringCell data={node.message_count}/>)}</TableCell>
            case 'port':
                return <TableCell>{data.nodes.map(node=><StringCell data={node.port}/>)}</TableCell>
            
            // Node page
            case 'nodeId':
                return <TableCell><table><NodesCell pageId={pageId} nodeData={data}/></table></TableCell>
            case 'node_status':
                return <TableCell>Active</TableCell>
            case 'role':
                return <TableCell>Member</TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}

function StringCell(props) {
    var data = props.data
    return <tr><td>{data}</td></tr>
}

function LinkCell(props) {
    const classes = useStyles();
    var data = props.data
    return <tr><td><a className={classes.tableCell} href={data}>{data}</a></td></tr>
}

function SwitchStatusCell(props) {
    const { pageId, artifactName, nodeId, status } = props;
    var isActive = status;
    const globalGroupId = useSelector(state => state.groupId);
    const basePath = useSelector(state => state.basePath);

    const changeState = () => {
        isActive = !isActive
        updateArtifact()
    };

    const updateArtifact = () => {
        const url = basePath.concat('/groups/').concat(globalGroupId).concat("/").concat(pageId);
        axios.patch(url, {
            "artifactName": artifactName,
            "nodeId": nodeId,
            "type": "status",
            "value": isActive
        });
    }

    return <tr><td><Switch checked={isActive} onChange={changeState}/></td></tr>
}

const useStyles = makeStyles((theme) => ({
    tableCell : {
        paddingLeft: '15px',
        color: '#3f51b5'
    }
}));
