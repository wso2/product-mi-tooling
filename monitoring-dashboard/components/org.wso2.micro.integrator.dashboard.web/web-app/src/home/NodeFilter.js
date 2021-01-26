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
import Select from '@material-ui/core/Select';
import Checkbox from '@material-ui/core/Checkbox';
import Chip from '@material-ui/core/Chip';

export default class NodeFilter extends React.Component {
    componentDidMount() {
        const url = "http://0.0.0.0:9743/api/rest/groups/mi_dev/nodes";
        axios.get(url).then(response => {
            var list = [];
            response.data.map(data => 
                list.push(data.nodeId)
            )
            const nodeList = list
            this.setState({nodeList})
        })
        
    }

    constructor(props){
        super(props)
        this.state = {nodeList : []};
    }

    render() {
        return <MultipleSelect nodeList={this.state.nodeList}/>
    }
}

function MultipleSelect(props) {
    var NodeList = props.nodeList;
    const classes = useStyles();
    const [nodeId, setNodeId] = React.useState([]);

    const handleChange = (event) => {
        setNodeId(event.target.value);
    };

    return (
        <div>
            <FormControl className={classes.formControl}>
                <InputLabel>Select Nodes</InputLabel>
                <Select
                    multiple
                    value={nodeId}
                    onChange={handleChange}
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
                            <Checkbox checked={nodeId.indexOf(name) > -1} />
                            <ListItemText primary={name} />
                        </MenuItem>
                    ))}
                </Select>
            </FormControl>
        </div>
    );
}

const useStyles = makeStyles((theme) => ({
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
    },
    noLabel: {
        marginTop: theme.spacing(3),
    },
}));
