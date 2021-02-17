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
import AuthManager from '../auth/AuthManager';
import {Constants} from '../auth/Constants';

export default function NodeFilter () {

    const [nodeList, setNodeList] = React.useState([]);
    const globalGroupId = useSelector(state => state.groupId);
    const basePath = useSelector(state => state.basePath);
    var authBearer = "Bearer " + AuthManager.getCookie(Constants.JWT_TOKEN_COOKIE)

    React.useEffect(()=>{
        if (globalGroupId !== '') {
            const url = basePath.concat('/groups/').concat(globalGroupId).concat("/nodes");
            axios.get(url, { headers: { Authorization: authBearer }}).then(response => {
                var list = [];
                response.data.map(data => list.push(data.nodeId))
                setNodeList(list)
            })
        }
    },[globalGroupId])

    return <MultipleSelect nodeList={nodeList}/>
}

function MultipleSelect(props) {

    var NodeList = props.nodeList;
    const classes = useStyles();
    const [nodeList, setNodeList] = React.useState([]);
    const dispatch = useDispatch();

    const handleChange = (event) => {
        setNodeList(event.target.value);
    };

    return (
        <div>
            <FormControl className={classes.formControl}>
                <Select
                    classes={{root: classes.selectRoot}}
                    multiple
                    value={nodeList}
                    onChange={handleChange}
                    onClick={() => dispatch(filterNodes(nodeList))}
                    renderValue={(selected) => (
                        <div className={classes.chips}>
                            {selected.map((value) => (
                                <Chip key={value} label={value} className={classes.chip} />
                            ))}
                        </div>
                    )}
                >
                    {NodeList.map((name) => (
                        <MenuItem key={name} value={name}>
                            <Checkbox checked={nodeList.indexOf(name) > -1} />
                            <ListItemText primary={name} />
                        </MenuItem>
                    ))}
                </Select>
                <FormHelperText>Select Nodes</FormHelperText>
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
}));
