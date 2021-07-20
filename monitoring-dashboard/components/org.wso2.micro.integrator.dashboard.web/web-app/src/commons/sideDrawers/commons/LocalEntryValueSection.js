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
import Box from '@material-ui/core/Box';
import AppBar from '@material-ui/core/AppBar';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import {makeStyles} from '@material-ui/core/styles';
import {useSelector} from 'react-redux';
import Editor from "@monaco-editor/react";
import HTTPClient from '../../../utils/HTTPClient';
const format = require('xml-formatter');

export default function LocalEntryValueSection(props) {
    const {name, type, nodeId, designContent} = props;
    const globalGroupId = useSelector(state => state.groupId);
    const [value, setValue] = React.useState("");
    const [selectedTab, setSelectedTab] = React.useState(0);

    const [open] = React.useState(false);

    React.useEffect(() => {
        HTTPClient.getLocalEntryValue(globalGroupId, nodeId, name).then(response => {
            setValue(response.data.value);
        })
    }, [])

    const descriptionElementRef = React.useRef(null);
    React.useEffect(() => {
        if (open) {
            const {current: descriptionElement} = descriptionElementRef;
            if (descriptionElement !== null) {
                descriptionElement.focus();
            }
        }
    }, [open]);

    const changeTab = (e, tab) => {
        setSelectedTab(tab);
    }
    const classes = useStyles();
    if (designContent) {
        return (<><AppBar position="static" classes={{root: classes.tabsAppBar}}>
            <Tabs value={selectedTab} onChange={changeTab} aria-label="design source selection">
                <Tab label="Overview"/>
                <Tab label="Value"/>
            </Tabs>
        </AppBar>
            {selectedTab === 0 && (<>{designContent}</>)}
            {selectedTab === 1 && (<Box p={5} overflow='auto'>
            <EditorSection type={type} value={value}/>
            </Box>)}
        </>)
    }
}

function EditorSection(props) {
    const {type, value} = props;
    if (type == 'Inline XML') {
        return <Editor
                    height="70vh"
                    defaultLanguage="xml"
                    defaultValue={format(value)}
                    options={{ readOnly: true }}
                />
    } else {
        return <Editor 
                    height="70vh" 
                    defaultLanguage="string" 
                    defaultValue={value} 
                    options={{ readOnly: true }} 

                />
    }
}

const useStyles = makeStyles(() => ({
    tabsAppBar: {
        backgroundColor: '#000',
    }
}));
