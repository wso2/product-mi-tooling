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
import { makeStyles } from '@material-ui/core/styles';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import FormHelperText from '@material-ui/core/FormHelperText';
import MenuItem from '@material-ui/core/MenuItem';
import EnhancedTable from '../commons/EnhancedTable';
import AppBar from '@material-ui/core/AppBar';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import AddLogConfigs from '../commons/AddLogConfigs';
import AuthManager from '../auth/AuthManager';
import { Constants } from '../auth/Constants';
import { useSelector } from 'react-redux';
import {Redirect} from "react-router-dom";

export default function LogConfigs() {
    const [pageInfo, setPageInfo] = React.useState({
        pageId: "log-configs",
        title: "LogConfigs",
        headCells: [
            {id: 'name', label: 'Logger Name'},
            {id: 'componentName', label: 'Component Name'},
            {id: 'level', label: 'Level'}],
        tableOrderBy: 'name',
        additionalParams : {
            selectedNodeId : 'All'
        }
    });

    const [nodeList, setNodeList] = React.useState([]);
    const [selectedNodeId, setSelectedNodeId] = React.useState('All');
    const [logConfigs, setLogConfigs] = React.useState([]);
    const globalGroupId = useSelector(state => state.groupId);
    const [selectedTab, setSelectedTab] = React.useState(0);
    const changeTab = (e, tab) => {
        setSelectedTab(tab);
    }

    const classes = useStyles();

    React.useEffect(()=>{
        let allNodes = [{
            label: 'All',
            value: 'All'
        }]
        if (globalGroupId !== '') {
            var authBearer = "Bearer " + AuthManager.getCookie(Constants.JWT_TOKEN_COOKIE)
            const getNodeUrl = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/nodes");
            axios.get(getNodeUrl, { headers: { Authorization: authBearer }}).then(response => {
                response.data.filter(node => {
                    var node = {
                        label: node.nodeId,
                        value: node.nodeId
                    }
                    allNodes.push(node);
                })
                setNodeList(allNodes)
            })

            const getLogsUrl = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/log-configs");
            axios.get(getLogsUrl).then(response => {
                setLogConfigs(response.data)
            })
        }
    },[globalGroupId])

    if (AuthManager.getUser().scope !== "admin") {
        return (
            <Redirect to={{ pathname: '/' }} />
        );
    }

    const getLogConfigs = (nodeId) => {
        setSelectedNodeId(nodeId);
        if (selectedNodeId !== 'All') {
            const url = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/log-configs/nodes/").concat(nodeId);
            axios.get(url).then(response => {
                setLogConfigs(response.data)
            })
        }
        let param = {
            selectedNodeId : nodeId
        }
        setPageInfo({...pageInfo, additionalParams: param})
    }

    const viewLogs = <><FormControl style={{ width: 150 }}>
                            <Select
                                classes={{ root: classes.selectRoot }}
                                value={selectedNodeId}
                                labelId="node-id-select-label"
                                id="node-id-select"
                                onChange={(e) => getLogConfigs(e.target.value)}
                            >
                                {nodeList.map((option) => (
                                    <MenuItem value={option.value}>{option.label}</MenuItem>
                                ))}

                            </Select>
                            <FormHelperText>Node ID</FormHelperText>
                        </FormControl>
                        <EnhancedTable pageInfo={pageInfo} dataSet={logConfigs} /></>;

    return (<><AppBar position="static" classes={{root: classes.tabsAppBar}}>
                    <Tabs value={selectedTab} onChange={changeTab} aria-label="view add selection">
                        <Tab label="View Log Configs" />
                        <Tab label="Add Log Configs" />
                    </Tabs>
                </AppBar>
                {selectedTab === 0 && (<>{viewLogs}</>)}
                {selectedTab === 1 && (<AddLogConfigs pageId={pageInfo.pageId}/>)}
            </>)

    return <EnhancedTable pageInfo={pageInfo} dataSet={logConfigs} />

}

const useStyles = makeStyles((theme) => ({
    selectRoot: {
        minHeight: '25px',
        lineHeight: '25px',
    },
    tabsAppBar: {
        backgroundColor: '#18202c'
    }
}));
