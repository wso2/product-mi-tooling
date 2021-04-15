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
import axios from 'axios';
import EnhancedTable from '../commons/EnhancedTable';
import { useDispatch, useSelector } from 'react-redux';
import AuthManager from "../auth/AuthManager";
import {Link, Redirect} from "react-router-dom";
import {Button} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import { changeData } from '../redux/Actions';
import Progress from '../commons/Progress';

export default function Users() {
    const [pageInfo] = React.useState({
        pageId: "users",
        title: "Users",
        headCells: [
            {id: 'userId', label: 'User Id'},
            {id: 'isAdmin', label: 'Admin'},
            {id: 'action', label: 'Action'}],
        tableOrderBy: 'name'
    });

    const [users, setUsers] = React.useState(null);
    const classes = useStyles();
    const globalGroupId = useSelector(state => state.groupId);
    const dataSet = useSelector(state => state.data);

    React.useEffect(() => {
        const url = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/users");
        axios.get(url).then(response => {
            response.data.map(data => data.details = JSON.parse(data.details));
            setUsers(response.data)
        })
    }, [globalGroupId, dataSet]);

    if (AuthManager.getUser().scope !== "admin") {
        return (
            <Redirect to={{pathname: '/'}}/>
        );
    }

    if (!users) {
        return(<Progress/>);
    }

    return <>
        <div style={{height: "30px"}}>
        <Button classes={{root: classes.buttonRight}} component={Link} to="/users/add" variant="contained"
                color="primary">
            Add New User
        </Button>
        </div>
        <br/>
        <EnhancedTable pageInfo={pageInfo} dataSet={users}/>
    </>
}

const useStyles = makeStyles((theme) => ({
    buttonRight: {
        float: "right"
    }
}));
