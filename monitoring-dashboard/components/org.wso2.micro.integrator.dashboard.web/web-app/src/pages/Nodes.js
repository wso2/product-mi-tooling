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
import EnhancedTable from '../commons/EnhancedTable';
import { useSelector } from 'react-redux';

export default function Nodes () {

    const [pageInfo, setpageInfo] = React.useState({
        pageId: "nodesPage",
        title: "Nodes",
        headCells: [
            {id: 'nodeId', label: 'Node ID'},
            {id: 'node_status', label: 'Status'},
            {id: 'role', label: 'Role'}],
        tableOrderBy: 'service'
    });
    const [nodeList, setNodeList] = React.useState([]);

    const globalGroupId = useSelector(state => state.groupId);
    const basePath = useSelector(state => state.basePath);

    React.useEffect(()=>{
        if (globalGroupId !== "") {
            const url = basePath.concat('/groups/').concat(globalGroupId).concat("/nodes");
            axios.get(url).then(response => {
                response.data.map(data => data.details = JSON.parse(data.details))
                setNodeList(response.data)
            })
        }
    },[globalGroupId])

    return (<EnhancedTable pageInfo={pageInfo} dataSet={nodeList}/>);
}
