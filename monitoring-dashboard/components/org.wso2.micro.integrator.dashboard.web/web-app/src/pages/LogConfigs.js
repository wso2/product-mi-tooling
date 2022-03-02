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
import InputBase from '@material-ui/core/InputBase';
import SearchIcon from '@material-ui/icons/Search';
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
    const [logConfigs, setLogConfigs] = React.useState([]);
    const [filteredLogConfigs, setFilteredLogConfigs] = React.useState([]);
    const [searchQuery, setSearchQuery] = React.useState("");
    const globalGroupId = useSelector(state => state.groupId);

    const classes = useStyles();

    const getDefaultLogConfigs = () => {
        let allNodes = [{
            label: 'All',
            value: 'All'
        }]
        if (globalGroupId !== '') {
            HTTPClient.getNodes(globalGroupId).then(response => {
                response.data.filter(node => {
                    var node = {
                        label: node.nodeId,
                        value: node.nodeId
                    }
                    allNodes.push(node);
                })
                setNodeList(allNodes)
            })

            HTTPClient.getLogConfigs(globalGroupId).then(response => {
                setLogConfigs(response.data);
            })
        }
    }

    const getFilteredLogConfigs = (searchQuery) => {
        setSearchQuery(searchQuery);
        if (searchQuery){
            setFilteredLogConfigs(logConfigs.filter((logger)=>logger.componentName.includes(searchQuery)));
        }      
    }

    React.useEffect(() => {
        getDefaultLogConfigs();
    }, [globalGroupId])

    if (AuthManager.getUser().scope !== "admin") {
        return (
            <Redirect to={{pathname: '/'}}/>
        );
    }

    const getLogConfigByNodeId = (nodeId) => {
        setSearchQuery("");
        setSelectedNodeId(nodeId);
        if (nodeId !== 'All') {
            HTTPClient.getLogConfigs(globalGroupId, nodeId).then(response => {
                setLogConfigs(response.data)
            })
        }
        let param = {
            selectedNodeId: nodeId
        }
        setPageInfo({...pageInfo, additionalParams: param})
    }

    const retrieveLogConfig = (nodeId) => {
        if (nodeId == undefined) {
            getDefaultLogConfigs();
        } else {
            getLogConfigByNodeId(nodeId)
        }
        
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
        <div className={classes.buttonsRight}>
            <div className={classes.search}>
                <div className={classes.searchIcon}>
                <SearchIcon />
                </div>
                <InputBase
                placeholder="Search by component…"
                classes={{
                    root: classes.inputRoot,
                    input: classes.inputInput,
                }}
                inputProps={{ 'aria-label': 'search' }}
                value={searchQuery}
                onChange={(e)=>getFilteredLogConfigs(e.target.value)}
                />
            </div>
            <Button component={Link} to="/log-configs/add" variant="contained" color="primary">
                Add Logging Configuration
            </Button>
        </div>
        
        <EnhancedTable pageInfo={pageInfo} dataSet={searchQuery ? filteredLogConfigs : logConfigs} retrieveData={retrieveLogConfig}/></>;
}

const useStyles = makeStyles((theme) => ({
    selectRoot: {
        minHeight: '25px',
        lineHeight: '25px'
    },
    tabsAppBar: {
        backgroundColor: '#18202c'
    },
    buttonsRight: {
        float: "right",
        display: "flex",
        flexDirection:"row"
    },
    search: {
        position: 'relative',
        marginRight: 20        
    },
    searchIcon: {
        padding: theme.spacing(0, 1),
        height: '100%',
        position: 'absolute',
        pointerEvents: 'none',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color:'black'
    },
    inputRoot: {
        color: 'black'
    },
    inputInput: {
        padding: theme.spacing(1, 1, 1, 0),
        fontSize: "13px",
        paddingLeft: `calc(1em + ${theme.spacing(4)}px)`,
        transition: theme.transitions.create('width'),
        width: '100%',
        [theme.breakpoints.up('md')]: {
          width: '20ch'
        },
        borderBottom: "1px solid rgb(100, 100, 100)"
    },
}));
