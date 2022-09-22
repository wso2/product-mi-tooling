/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import FormControl from '@material-ui/core/FormControl';
import InputBase from '@material-ui/core/InputBase';
import SearchIcon from '@material-ui/icons/Search';
import lodash from 'lodash';


export default function SearchBox(props) {

    const classes = useStyles();
    const [searchQuery, setSearchQuery] = React.useState('');
    
    const debounceFn = React.useCallback(lodash.debounce(handleDebounceFn, 1000), []);

    function handleDebounceFn(finalQueryString) {
        props.passSearchQuery(finalQueryString);
    }

    function handleChange (query) {
        setSearchQuery(query);
        let finalQueryString = query.toLowerCase();
        if(query.trim().length === 0){
            finalQueryString = query;
        }
        debounceFn(finalQueryString);
    };

    return <FormControl style = {{ width: 200, float:'right'}}>
        <div className = {classes.search}>
            <div className = {classes.searchIcon}>
                <SearchIcon />
            </div>
            <InputBase
                placeholder = "Search resource..."
                classes = {{
                    root: classes.inputRoot,
                    input: classes.inputInput,
                }}
                inputProps = {{ 'aria-label': 'search' }}
                value = {searchQuery}
                onChange = {(e) => handleChange(e.target.value)}
            />
        </div>         
    </FormControl>;
}

const useStyles = makeStyles((theme) => ({
    search: {
        position: 'relative',
        marginRight: 20, 
        marginBottom: 10,
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
