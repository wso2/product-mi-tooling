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
import EnhancedTable from '../commons/EnhancedTable';
import {useSelector } from 'react-redux';
import AuthManager from "../auth/AuthManager";
import { Redirect } from "react-router-dom";

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

    const globalGroupId = useSelector(state => state.groupId);
    const dataSet = useSelector(state => state.data);


    React.useEffect(() => {
    }, [globalGroupId, dataSet]);

    if (AuthManager.getUser().scope !== "admin" || AuthManager.getUser().sso) {
        return (
            <Redirect to={{pathname: '/'}}/>
        );
    }

    return (
        <EnhancedTable pageInfo={pageInfo}/>
    );
}
