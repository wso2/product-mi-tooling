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
import SideDrawer from './SideDrawer';

export default function NodesCell(props) {
    const classes = useStyles();
    var nodeData = props.data;
    var proxyName = props.proxyName;

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
            <SideDrawer proxyName={proxyName} nodeData={nodeData} />
        </Drawer>
    </TableRow>;
}

const useStyles = makeStyles((theme) => ({
    tableCell : {
        padding: '1px',
        borderBottom: 'none',
        color: '#3f51b5'
    }
}));
