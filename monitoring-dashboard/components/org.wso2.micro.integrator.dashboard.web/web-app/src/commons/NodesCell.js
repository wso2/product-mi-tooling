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
import TableCell from "@material-ui/core/TableCell";
import Drawer from '@material-ui/core/Drawer';
import TableRow from '@material-ui/core/TableRow'
import { makeStyles } from '@material-ui/core/styles';
import ProxySideDrawer from './sideDrawers/ProxySideDrawer';
import EndpointSideDrawer from './sideDrawers/EndpointSideDrawer';
import HomePageSideDrawer from './sideDrawers/HomePageSideDrawer';
import ApiSideDrawer from './sideDrawers/ApiSideDrawer';
import SequenceSideDrawer from './sideDrawers/SequenceSideDrawer';
import InboundEpSideDrawer from './sideDrawers/InboundEpSideDrawer';

export default function NodesCell(props) {
    const classes = useStyles();
    const { pageId, nodeData } = props;
    const [state, setState] = React.useState({
        openSideDrawer: false,
    });

    const toggleDrawer = (open) => (event) => {
        if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
            return;
        }

        setState({ ...state, openSideDrawer: open });
    };

    return <TableRow hover role="presentation">
        <TableCell onClick={toggleDrawer(true)} className={classes.tableCell}>{nodeData.nodeId}</TableCell>
        <Drawer anchor='right' open={state['openSideDrawer']} onClose={toggleDrawer(false)} >
            <SideDrawer pageId={pageId} nodeData={nodeData} />
        </Drawer>
    </TableRow>;
}

function SideDrawer(props) {
    switch(props.pageId) {
        case 'proxy-services':
            return <ProxySideDrawer nodeData={props.nodeData} />
        case 'endpoints':
            return <EndpointSideDrawer nodeData={props.nodeData} />
        case 'apis':
            return <ApiSideDrawer nodeData={props.nodeData} />
        case 'sequences':
            return <SequenceSideDrawer nodeData={props.nodeData} />
        case 'inbound-endpoints':
            return <InboundEpSideDrawer nodeData={props.nodeData} />
        default :
            return <HomePageSideDrawer nodeData={props.nodeData} />
    }
}

const useStyles = makeStyles((theme) => ({
    tableCell : {
        padding: '1px',
        borderBottom: 'none',
        color: '#3f51b5'
    }
}));
