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
import Box from '@material-ui/core/Box';
import AppBar from '@material-ui/core/AppBar';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import {makeStyles} from '@material-ui/core/styles';
import {useSelector} from 'react-redux';
import Editor from "@monaco-editor/react";
import HTTPClient from '../../../utils/HTTPClient';
import DownloadIcon from '@material-ui/icons/GetAppRounded';
import Tooltip from '@material-ui/core/Tooltip';
const formatXML = require('xml-formatter');

export default function RegistrySourceViewSection(props) {
    const {registryPath, designContent, data} = props;
    const registryName = data.childName;
    const globalGroupId = useSelector(state => state.groupId);
    const [source, setSource] = React.useState("");
    const [isLoading, setIsLoading] = React.useState(true);
    const [sourceLanguage, setSourceLanguage] = React.useState("text");
    const [selectedTab, setSelectedTab] = React.useState(0);
    const [downloadMessage, setDownloadMessage] = React.useState('Download Registry');

    const [open] = React.useState(false);

    React.useEffect(() => {
        if (registryName.endsWith('.json')) {
            setSourceLanguage('json');  
        } else if (registryName.endsWith('.xml')) {
            setSourceLanguage('xml');  
        } else {
            setSourceLanguage('text');
        }
    },[])

    const getResponseString = (response) => {
        if ( typeof response === 'object' && !Array.isArray(response) && response !== null) {
            const stringResponse = JSON.stringify(response,null,4);
            return stringResponse;
        } else if (sourceLanguage === 'xml'){
            return formatXML(response.toString());
        } else {
            return response.toString();
        }
    }

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
        if (tab === 1 && (!registryName.endsWith('.properties')) && (data.fileIcon !== 'folder')) {
            const resourcePath = '/groups/'.concat(globalGroupId).concat('/registry-resources/').concat('content?path=').concat(registryPath);
            HTTPClient.get(resourcePath).then(response => {
                setSource(getResponseString(response.data));
                setIsLoading(false);
            })
        } else if (tab === 0){
            setIsLoading(true);
        } else {
            setSource("");
            setDownloadMessage("Download is not supported");
            setIsLoading(false);
        }
        setSelectedTab(tab);
    }

    const classes = useStyles();

    function downloadRegistry() {
        if ((!registryName.endsWith('.properties')) && (data.fileIcon !== 'folder')) {
            var element = document.createElement('a');
            element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(source));
            element.setAttribute('download', registryName);
            element.style.display = 'none';
            document.body.appendChild(element);
            element.click();
            document.body.removeChild(element);
            setDownloadMessage('Downloaded');
            const caller = function () {
                setDownloadMessage('Download Registry');
            };
            setTimeout(caller, 2000);
        } 
    }

    if (designContent) {
        return (<><AppBar position="static" classes={{root: classes.tabsAppBar}}>
            {(!registryName.endsWith('.properties')) && (data.fileIcon !== 'folder') &&
            <Tabs value={selectedTab} onChange={changeTab} aria-label="design source selection">
                <Tab label="Overview"/>
                <Tab label="Source" />
            </Tabs>
            }
            {(registryName.endsWith('.properties')) || (data.fileIcon === 'folder') &&
            <Tabs value={selectedTab} onChange={changeTab} aria-label="design source selection">
                <Tab label="Overview"/>
                <Tab label="Source" disabled/>
            </Tabs>
            }
        </AppBar>
            {selectedTab === 0 && (<>{designContent}</>)}
            {(selectedTab === 1 && isLoading) && (<div>Loading...</div>)}
            {(selectedTab === 1 && !isLoading)&& (<Box p={5} overflow='auto'>
                <Editor
                    height="70vh"
                    defaultLanguage={sourceLanguage}
                    defaultValue={source}
                    options={{
                        readOnly: true
                    }}
                />
                <Tooltip title={downloadMessage}>
                        <DownloadIcon onClick={() => downloadRegistry()} className={classes.icon}/>
                </Tooltip>
            </Box>)
            }
        </>)
    }
}

const useStyles = makeStyles(() => ({
    tabsAppBar: {
        backgroundColor: '#000',
    },
    icon : {
        color: '#3f51b5',
        cursor: "pointer",
        padding: '1px',
        marginTop: '10px',
        float: 'right'
    }
}));
