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
import {makeStyles} from '@material-ui/core/styles';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import FormHelperText from '@material-ui/core/FormHelperText';
import MenuItem from '@material-ui/core/MenuItem';
import EnhancedTable from '../commons/EnhancedTable';
import AuthManager from '../auth/AuthManager';
import {useSelector} from 'react-redux';
import {Link, Redirect} from "react-router-dom";
import {Button} from "@material-ui/core";
import HTTPClient from '../utils/HTTPClient';

export default function LogConfigs() {
    const [pageInfo, setPageInfo] = React.useState({
        pageId: "log-configs",
        title: "LogConfigs",
        headCells: [
            {id: 'name', label: 'Logger Name'},
            {id: 'componentName', label: 'Component Name'},
            {id: 'level', label: 'Level'}],
        tableOrderBy: 'name',
        additionalParams: {
            selectedNodeId: 'All'
        }
    });

    const [nodeList, setNodeList] = React.useState([]);
    const [selectedNodeId, setSelectedNodeId] = React.useState('All');
    const globalGroupId = useSelector(state => state.groupId);

    const classes = useStyles();

    const getDefaultLogConfigs = () => {
        let allNodes = [{
            label: 'All',
            value: 'All'
        }]
        if (globalGroupId !== '') {
            HTTPClient.getAllNodes(globalGroupId).then(response => {
                response.data.filter(node => {
                    var node = {
                        label: node.nodeId,
                        value: node.nodeId
                    }
                    allNodes.push(node);
                })
                setNodeList(allNodes)
            })
        }
    }

    React.useEffect(() => {
        getDefaultLogConfigs();
    }, [globalGroupId, selectedNodeId])

    if (AuthManager.getUser().scope !== "admin") {
        return (
            <Redirect to={{pathname: '/'}}/>
        );
    }

    const getLogConfigByNodeId = (nodeId) => {
        setSelectedNodeId(nodeId);

        let param = {
            selectedNodeId: nodeId
        }
        setPageInfo({...pageInfo, additionalParams: param})
    }


    return <>

        <FormControl style={{width: 150}}>
            <Select
                classes={{root: classes.selectRoot}}
                value={selectedNodeId}
                labelId="node-id-select-label"
                id="node-id-select"
                onChange={(e) => getLogConfigByNodeId(e.target.value)}
            >
                {nodeList.map((option) => (
                    <MenuItem value={option.value}>{option.label}</MenuItem>
                ))}
            </Select>
            <FormHelperText>Node ID</FormHelperText>
        </FormControl>
        <Button classes={{root: classes.buttonRight}} component={Link} to="/log-configs/add" variant="contained" color="primary">
            Add Logging Configuration
        </Button>
        <EnhancedTable pageInfo={pageInfo}/></>;
}

const useStyles = makeStyles((theme) => ({
    selectRoot: {
        minHeight: '25px',
        lineHeight: '25px',
    },
    tabsAppBar: {
        backgroundColor: '#18202c'
    },
    buttonRight: {
        float: "right"
    }
}));
