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
import Dialog from '@material-ui/core/Dialog';
import TableRow from '@material-ui/core/TableRow'
import { makeStyles } from '@material-ui/core/styles';
import ProxyModal from './Modals/ProxyModal';
import EndpointModal from './Modals/EndpointModal';
import HomePageModal from './Modals/HomePageModal';
import ApiModal from './Modals/ApiModal';
import TemplatesModal from './Modals/TemplatesModal';
import SequenceModal from './Modals/SequenceModal';
import InboundEpModal from './Modals/InboundEpModal';
import MessageStoreModal from './Modals/MessageStoreModal';
import MessageProcessorModal from './Modals/MessageProcessorModal';
import TasksModal from './Modals/TasksModal';
import LocalEntriesModal from './Modals/LocalEntriesModal';
import CarbonApplicationsModal from './Modals/CarbonApplicationsModal';
import DataServicesModal from './Modals/DataServicesModal';
import DataSourcesModal from './Modals/DataSourcesModal';

export default function NodesCell(props) {
    const classes = useStyles();
    const { pageId, nodeData, retrieveData } = props;
    const [state, setState] = React.useState({
        openModal: false,
    });

    const toggleDrawer = (open) => (event) => {
        if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
            return;
        }

        setState({ ...state, openModal: open });
    };

    return <TableRow hover role="presentation">
        <TableCell onClick={toggleDrawer(true)} className={classes.tableCell}>{nodeData.nodeId}</TableCell>
        <Dialog classes={{ paper: classes.dialog }} onClose={toggleDrawer(false)} aria-labelledby="artifact-popup" open={state['openModal']} maxWidth="lg">
            <DetailModal pageId={pageId} nodeData={nodeData} retrieveData={retrieveData}/>
        </Dialog>
    </TableRow>;
}

function DetailModal(props) {
    const { nodeData, retrieveData } = props
    switch(props.pageId) {
        case 'proxy-services':
            return <ProxyModal nodeData={nodeData} retrieveData={retrieveData}/>
        case 'endpoints':
            return <EndpointModal nodeData={nodeData} retrieveData={retrieveData}/>
        case 'apis':
            return <ApiModal nodeData={nodeData} retrieveData={retrieveData}/>
        case 'templates':
            return <TemplatesModal nodeData={nodeData} />
        case 'sequences':
            return <SequenceModal nodeData={nodeData} retrieveData={retrieveData}/>
        case 'inbound-endpoints':
            return <InboundEpModal nodeData={nodeData} retrieveData={retrieveData}/>
        case 'message-stores':
            return <MessageStoreModal nodeData={nodeData} />
        case 'message-processors':
            return <MessageProcessorModal nodeData={nodeData} />
        case 'tasks':
            return <TasksModal nodeData={nodeData} />
        case 'local-entries':
            return <LocalEntriesModal nodeData={nodeData} />
        case 'carbonapps':
            return <CarbonApplicationsModal nodeData={nodeData} />
        case 'data-services':
            return <DataServicesModal nodeData={nodeData} />
        case 'data-sources':
            return <DataSourcesModal nodeData={nodeData} />
        default :
            return <HomePageModal nodeData={nodeData} />
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
    dialog: {
        height: 700,
    }
}));
