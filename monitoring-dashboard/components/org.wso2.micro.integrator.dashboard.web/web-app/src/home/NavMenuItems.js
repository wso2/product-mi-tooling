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
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import ListSubheader from '@material-ui/core/ListSubheader';
import ProxyIcon from '@material-ui/icons/Notes';
import EndpointIcon from '@material-ui/icons/Send';
import InbountEpIcon from '@material-ui/icons/Transform';
import MessageProcessorIcon from '@material-ui/icons/Message';
import MessageStoreIcon from '@material-ui/icons/StoreMallDirectory';
import ApiIcon from '@material-ui/icons/Apps';
import Event from '@material-ui/icons/Event';
import TemplateIcon from '@material-ui/icons/WrapText';
import SequenceIcon from '@material-ui/icons/CompareArrows';
import LocalEntriesIcon from '@material-ui/icons/Assignment';
import DssIcon from '@material-ui/icons/DeviceHub';
import ConnectorIcon from '@material-ui/icons/CastConnected';
import CappIcon from '@material-ui/icons/Dashboard';
import UserIcon from '@material-ui/icons/People';
import LogFileIcon from '@material-ui/icons/Description';
import LayersIcon from '@material-ui/icons/Layers';

export const mainListItems = (
    <div>
        <ListItem button>
            <ListItemIcon>
                <ProxyIcon />
            </ListItemIcon>
            <ListItemText primary="Proxy Services" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <EndpointIcon/>
            </ListItemIcon>
            <ListItemText primary="Endpoints" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <InbountEpIcon />
            </ListItemIcon>
            <ListItemText primary="Inbound Endpoints" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <MessageProcessorIcon />
            </ListItemIcon>
            <ListItemText primary="Message Processors" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <MessageStoreIcon />
            </ListItemIcon>
            <ListItemText primary="Message Stores" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <ApiIcon />
            </ListItemIcon>
            <ListItemText primary="API" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <TemplateIcon />
            </ListItemIcon>
            <ListItemText primary="Templates" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <SequenceIcon />
            </ListItemIcon>
            <ListItemText primary="Sequences" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <Event />
            </ListItemIcon>
            <ListItemText primary="Tasks" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <LocalEntriesIcon />
            </ListItemIcon>
            <ListItemText primary="Local Entries" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <DssIcon />
            </ListItemIcon>
            <ListItemText primary="Data Services" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <LayersIcon />
            </ListItemIcon>
            <ListItemText primary="Datasources" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <ConnectorIcon />
            </ListItemIcon>
            <ListItemText primary="Connectors" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <CappIcon />
            </ListItemIcon>
            <ListItemText primary="Carbon Applications" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <LogFileIcon />
            </ListItemIcon>
            <ListItemText primary="Log Files" />
        </ListItem>
    </div>
);

export const globalSettings = (
    <div>
        <ListSubheader>GLOBAL SETTINGS</ListSubheader>
        <ListItem button>
            <ListItemIcon>
                <LocalEntriesIcon />
            </ListItemIcon>
            <ListItemText primary="Log Configs" />
        </ListItem>
        <ListItem button>
            <ListItemIcon>
                <UserIcon />
            </ListItemIcon>
            <ListItemText primary="Users" />
        </ListItem>
    </div>
);
