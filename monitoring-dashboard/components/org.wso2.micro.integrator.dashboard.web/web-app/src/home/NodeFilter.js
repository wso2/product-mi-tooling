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

import React, {useEffect, useState} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import ListItemText from '@material-ui/core/ListItemText';
import FormHelperText from '@material-ui/core/FormHelperText';
import Select from '@material-ui/core/Select';
import Checkbox from '@material-ui/core/Checkbox';
import Chip from '@material-ui/core/Chip';
import { filterNodes } from '../redux/Actions';
import { useDispatch, useSelector } from 'react-redux';
import Divider from "@material-ui/core/Divider";
import HTTPClient from '../utils/HTTPClient';

export default function NodeFilter () {

    const [nodeList, setNodeList] = useState([]);
    const globalGroupId = useSelector(state => state.groupId);

    React.useEffect(()=>{
        if (globalGroupId !== '') {
            HTTPClient.getAllNodes(globalGroupId).then(response => {
                var list = [];
                response.data.map(data => list.push(data.nodeId))
                setNodeList(list)
            })
        }
    },[globalGroupId])

    return <MultipleSelect nodeList={nodeList}/>
}

function MultipleSelect(props) {

    var nodeList = props.nodeList;
    const classes = useStyles();
    const [selectedNodeList, setSelectedNodeList] = useState([]);
    const globalGroupId = useSelector(state => state.groupId);
    const [selectedAll, setSelectedAll] = useState(false);
    const dispatch = useDispatch();

    useEffect(()=>{
        setSelectedNodeList([])
    },[globalGroupId]);

    useEffect(()=>{
        if (nodeList.length !== 0 && selectedNodeList.length == 0) {
            setSelectedAll(true)
            setSelectedNodeList(nodeList);
            dispatch(filterNodes(nodeList))
        }
    },[nodeList]);

    useEffect(()=> {
        dispatch(filterNodes(selectedNodeList));
    },[selectedNodeList])

    const handleChange = (event) => {
        const selectAllLabel = "select all";
        const selectAllValueArray = event.target.value.filter(nodeName => nodeName === selectAllLabel)
        if (selectAllValueArray.length !== 0) {
           handleSelectAllChange();
        } else {
            setSelectedAll(false);
            setSelectedNodeList(event.target.value.filter(nodeName => nodeName !== selectAllLabel));
        }
    };

    const handleSelectAllChange = () => {
        if (selectedAll) {
            setSelectedNodeList([]);
        }else{
            setSelectedNodeList([...nodeList])
        }
        setSelectedAll(!selectedAll);
    }

    return (
        <div>
            <FormControl className={classes.formControl}>
                <Select
                    classes={{root: classes.selectRoot}}
                    multiple
                    value={selectedNodeList}
                    onChange={handleChange}
                    renderValue={(selected) => (
                        <div className={classes.chips}>
                            {selected.map((value) => (
                                <Chip key={value} label={value} className={classes.chip} />
                            ))}
                        </div>
                    )}
                >
                    <MenuItem key={"select-all"} value={"select all"}>
                        <Checkbox checked={selectedAll} />
                        <ListItemText primary={"Select All"}  classes={{ primary: classes.selectAllText }}/>
                    </MenuItem>
                    <Divider />
                    {nodeList.map((name) => (
                        <MenuItem key={name} value={name}>
                            <Checkbox checked={selectedNodeList.indexOf(name) > -1} />
                            <ListItemText primary={name} />
                        </MenuItem>
                    ))}
                </Select>
                <FormHelperText>Node IDs</FormHelperText>
            </FormControl>
        </div>
    );
}

const useStyles = makeStyles((theme) => ({
    selectRoot: {
        minHeight: 25,
    },
    formControl: {
        margin: theme.spacing(1),
        minWidth: 120,
        maxWidth: 300,
    },
    chips: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    chip: {
        margin: 2,
        height: 20,
    },
    noLabel: {
        marginTop: theme.spacing(3),
    },
    selectAllText: {
        "font-weight": "bold"
    },
}));
