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
import Drawer from '@material-ui/core/Drawer';
import { makeStyles } from '@material-ui/core/styles';
import NodeInfoSideDrawer from '../commons/NodeInfoSideDrawer';

export default function TableRowCreator(props) {
    const { pageInfo, data, headers } = props;

    switch(pageInfo.pageId) {
        case 'nodeInfo':
            return <NodeRowCreator headers={headers} data={data} />
        case 'proxyPage':
            return <ProxyRowCreator headers={headers} data={data} />
    }
}

function NodeRowCreator(props) {
    const { headers, data } = props;
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            case 'nodeId':
                return <NodeIDCell data={data}/>
            case 'status':
                return <TableCell>{data.isActive}</TableCell>
            case 'role':
                return <TableCell>{data.details.role}</TableCell>
            case 'upTime':
                return <TableCell>{data.details.upTime}</TableCell>
            default:
                return <TableCell>Table data not available</TableCell>
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
                return <TableCell>{data.nodes.map(node=><WsdlCell data={node}/>)}</TableCell>
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

function WsdlCell(props) {
    var nodeData = props.data;
    return <tr><td>{nodeData.wsdlUrl}</td></tr>
}

const useStyles = makeStyles((theme) => ({
    tableCell : {
        paddingLeft: '15px',
        color: '#3f51b5'
    }
}));
