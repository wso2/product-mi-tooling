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
import { makeStyles } from '@material-ui/core/styles';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import ListItemText from '@material-ui/core/ListItemText';
import FormHelperText from '@material-ui/core/FormHelperText';
import Select from '@material-ui/core/Select';
import Checkbox from '@material-ui/core/Checkbox';
import Chip from '@material-ui/core/Chip';
import { selectNode, deselectNode, currentGroupSelector } from '../redux/Actions';
import { useDispatch, useSelector } from 'react-redux';
import TypeIcon from '../commons/TypeIcon';

export default function NodeFilter () {
    const dispatch = useDispatch();
    const {selected, beenSelected, nodes, selectedType} = useSelector(currentGroupSelector);
    const classes = useStyles();

    const handleChange = (event) => {
        const oldValues = selected;
        const newValues = event.target.value;
        if (newValues.length > oldValues.length) {
            dispatch(selectNode(newValues.find(v => !oldValues.includes(v))));
        } else if (newValues.length < oldValues.length) {
            dispatch(deselectNode(oldValues.find(v => !newValues.includes(v))));
        }
    };

    return (
        <div>
            <FormControl className={classes.formControl}>
                <Select
                    classes={{root: classes.selectRoot}}
                    multiple
                    value={selected}
                    onChange={handleChange}
                    renderValue={(selected) => (
                        <div>
                            <TypeIcon type={selectedType} className={classes.icon}></TypeIcon>
                            {/* get rid of below dev */}
                            <div className={classes.chips}> 
                                {selected.map((value) => (
                                    <Chip key={value} label={value} className={classes.chip} />
                                ))}
                            </div>
                        </div>
                    )}
                >
                    {nodes.map((node) => (
                        <MenuItem key={node.nodeId} value={node.nodeId}>
                            <TypeIcon type={node.type}></TypeIcon>
                            <Checkbox checked={selected.indexOf(node.nodeId) > -1} />
                            <ListItemText primary={node.nodeId} />
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
        maxWidth: 500,
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
    icon: {
        "float": "left",
        "margin-top": "2px",
    }
}));
