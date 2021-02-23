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
import { useSelector } from 'react-redux';
import AuthManager from "../auth/AuthManager";
import {Redirect} from "react-router-dom";

export default function LogConfigs() {
    const [pageInfo] = React.useState({
        pageId: "log-configs",
        title: "LogConfigs",
        headCells: [
            {id: 'name', label: 'Logger Name'},
            {id: 'componentName', label: 'Component Name'},
            {id: 'level', label: 'Level'}],
        tableOrderBy: 'name'
    });

    const [logConfigs, setLogConfigs] = React.useState([]);

    const globalGroupId = useSelector(state => state.groupId);
    const basePath = useSelector(state => state.basePath);

    React.useEffect(() => {
        const url = basePath.concat('/groups/').concat(globalGroupId).concat("/log-configs");
        axios.get(url).then(response => {
            setLogConfigs(response.data)
        })
    },[globalGroupId])

    if (AuthManager.getUser().scope !== "admin") {
        return (
            <Redirect to={{ pathname: '/' }} />
        );
    }

    return <EnhancedTable pageInfo={pageInfo} dataSet={logConfigs} />
}
