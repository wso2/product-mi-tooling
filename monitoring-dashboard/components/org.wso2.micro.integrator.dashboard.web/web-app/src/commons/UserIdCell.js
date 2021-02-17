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
import TableCell from "@material-ui/core/TableCell";
import Drawer from '@material-ui/core/Drawer';
import TableRow from '@material-ui/core/TableRow'
import { makeStyles } from '@material-ui/core/styles';
import UserSideDrawer from '../commons/sideDrawers/UserSideDrawer'
import { useSelector } from 'react-redux';

export default function UserIdCell(props) {
    const userId = props.id;
    const classes = useStyles();

    const globalGroupId = useSelector(state => state.groupId);
    const basePath = useSelector(state => state.basePath);

    const [user, setUser] = React.useState({})

    const [state, setState] = React.useState({
        openSideDrawer: false,
    });

    const toggleDrawer = (open) => (event) => {
        if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
            return;
        }
        console.log("user before", user);
        if (open) {      
            const url = basePath.concat('/groups/').concat(globalGroupId).concat("/users/").concat(userId);
            console.log("url", url)
            axios.get(url).then(response => {
                console.log("Ssssss")
                console.log("response.data", response.data);
                setUser(response.data);
            })
        }
        console.log("user after", user);
        setState({ ...state, openSideDrawer: open });
    };

    return <TableRow hover role="presentation">
        <TableCell onClick={toggleDrawer(true)} className={classes.tableCell}>{userId}</TableCell>
        <Drawer anchor='right' open={state['openSideDrawer']} onClose={toggleDrawer(false)} >
            <UserSideDrawer user={user} />
        </Drawer>
    </TableRow>;
}

function SideDrawer(props) {
    
}

const useStyles = makeStyles(() => ({
    tableCell : {
        padding: '1px',
        borderBottom: 'none',
        color: '#3f51b5'
    }
}));
