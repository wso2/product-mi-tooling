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
import TemplatesSideDrawer from './sideDrawers/TemplatesSideDrawer';
import SequenceSideDrawer from './sideDrawers/SequenceSideDrawer';
import InboundEpSideDrawer from './sideDrawers/InboundEpSideDrawer';
import MessageStoreSideDrawer from './sideDrawers/MessageStoreSideDrawer';
import MessageProcessorSideDrawer from './sideDrawers/MessageProcessorSideDrawer';
import TasksSideDrawer from './sideDrawers/TasksSideDrawer';
import LocalEntriesSideDrawer from './sideDrawers/LocalEntriesSideDrawer';
import CarbonApplicationsSideDrawer from './sideDrawers/CarbonApplicationsSideDrawer';
import DataServicesSideDrawer from './sideDrawers/DataServicesSideDrawer';
import DataSourcesSideDrawer from './sideDrawers/DataSourcesSideDrawer';

export default function NodesCell(props) {
    const classes = useStyles();
    const { pageId, nodeData, retrieveUpdatedArtifact } = props;
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
        <Drawer anchor='right' open={state['openSideDrawer']} onClose={toggleDrawer(false)} classes={{paper: classes.drawerPaper}}>
            <SideDrawer pageId={pageId} nodeData={nodeData} retrieveUpdatedArtifact = {retrieveUpdatedArtifact} />
        </Drawer>
    </TableRow>;
}

function SideDrawer(props) {
    const { nodeData, retrieveUpdatedArtifact } = props
    switch(props.pageId) {
        case 'proxy-services':
            return <ProxySideDrawer nodeData={nodeData} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
        case 'endpoints':
            return <EndpointSideDrawer nodeData={nodeData} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
        case 'apis':
            return <ApiSideDrawer nodeData={nodeData} retrieveUpdatedArtifact = {retrieveUpdatedArtifact}/>
        case 'templates':
            return <TemplatesSideDrawer nodeData={nodeData} />
        case 'sequences':
            return <SequenceSideDrawer nodeData={nodeData} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
        case 'inbound-endpoints':
            return <InboundEpSideDrawer nodeData={nodeData} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
        case 'message-stores':
            return <MessageStoreSideDrawer nodeData={nodeData} />
        case 'message-processors':
            return <MessageProcessorSideDrawer nodeData={nodeData} />
        case 'tasks':
            return <TasksSideDrawer nodeData={nodeData} />
        case 'local-entries':
            return <LocalEntriesSideDrawer nodeData={nodeData} />
        case 'carbonapps':
            return <CarbonApplicationsSideDrawer nodeData={nodeData} />
        case 'data-services':
            return <DataServicesSideDrawer nodeData={nodeData} />
        case 'data-sources':
            return <DataSourcesSideDrawer nodeData={nodeData} />
        default :
            return <HomePageSideDrawer nodeData={nodeData} />
    }
}

const useStyles = makeStyles(() => ({
    tableCell : {
        padding: '1px',
        borderBottom: 'none',
        color: '#3f51b5',
        cursor: "pointer"
    },
    drawerPaper: {
        backgroundColor: '#fff',
    },
}));
