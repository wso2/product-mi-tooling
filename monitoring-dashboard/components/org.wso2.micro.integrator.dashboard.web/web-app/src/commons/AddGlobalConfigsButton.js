/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import Drawer from '@material-ui/core/Drawer';
import { Button } from '@material-ui/core';
import AddUserSideDrawer from './sideDrawers/AddUserSideDrawer'
import { Link } from 'react-router-dom'

export default function AddGlobalConfigsButton(props) {
    const { pageId } = props;
    const [state, setState] = React.useState({
        openSideDrawer: false
    });

    const toggleDrawer = (open) => (event) => {
        if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
            return;
        }
        setState({ ...state, openSideDrawer: open });
    };

    if (pageId === 'users') {
        return <div>
            <Button component={Link} to="/users/add" variant="contained" color="primary">
                Add User
            </Button>
                </div>
    } else {
        return <div />
    } 
}
