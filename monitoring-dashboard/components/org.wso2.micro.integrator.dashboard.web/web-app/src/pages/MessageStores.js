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
import EnhancedTable from '../commons/EnhancedTable';
import { useSelector } from 'react-redux';
import HTTPClient from '../utils/HTTPClient';

export default function MessageStores() {
    const [pageInfo] = React.useState({
        pageId: "message-stores",
        title: "Message Stores",
        headCells: [
            {id: 'name', label: 'Message Store Name'},
            {id: 'nodes', label: 'Nodes'},
            {id: 'type', label: 'Type'},
            {id: 'message_count', label: 'Message Count'}],
        tableOrderBy: 'name'
    });

    const [messageStoreList, setMessageStoreList] = React.useState([]);

    const globalGroupId = useSelector(state => state.groupId);
    const selectedNodeList = useSelector(state => state.nodeList);

    React.useEffect(() => {
        HTTPClient.getArtifacts("message-stores", globalGroupId, selectedNodeList).then(response => {
            setMessageStoreList(response.data)
        })
    },[globalGroupId, selectedNodeList])

    return <EnhancedTable pageInfo={pageInfo} dataSet={messageStoreList}/>
}
