/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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
import EnhancedTableRegistry from '../commons/EnhancedTableRegistry';
import { useSelector } from 'react-redux';
import HTTPClient from '../utils/HTTPClient';
import RegistryBreadCrumbs from '../commons/RegistryBreadCrumbs';

export default function RegistryResources() {
    const [pageInfo] = React.useState({
        pageId: "registry-resources",
        title: "Registry Resources",
        headCells: [
            {id: 'childName', label: 'Name'},
            {id: 'mediaType', label: 'Media Type'}
        ],
        tableOrderBy: 'childName'
    });
    const [registryList, setRegistryList] = React.useState([]);
    const [registryPath, setRegistryPath] = React.useState('registry');
    const globalGroupId = useSelector(state => state.groupId);
    const selectedNodeList = useSelector(state => state.nodeList);

    const retrieveRegistryList = () => {
        HTTPClient.getRegistryArtifacts(globalGroupId,registryPath).then(response => {
            setRegistryList(response.data)
        })
    }

    const handleDoubleClick = (name,iconType,path) => {
        if (iconType === 'folder') {
            const newPath = path.concat('/').concat(name);
            setRegistryPath(newPath);
        }
    }

    const handleBreadCrumbClick = (index, pathArray) => {
        const newPath = (pathArray.slice(0,index+1)).map(el => el[0]).join("/");
        setRegistryPath(newPath);
    }

    React.useEffect(() => {
        retrieveRegistryList();
    },[globalGroupId, selectedNodeList, registryPath])

    return (
        <>
        <RegistryBreadCrumbs registryPath={registryPath} handleBreadCrumbClick={handleBreadCrumbClick}/>
        <EnhancedTableRegistry pageInfo={pageInfo} dataSet={registryList} handleDoubleClick={handleDoubleClick} registryPath={registryPath}/>
        </>
        )
}
