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
import Grid from '@material-ui/core/Grid';
import Box from '@material-ui/core/Box';
import { Button } from '@material-ui/core';
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import XMLViewer from 'react-xml-viewer';
import { makeStyles } from '@material-ui/core/styles';
import { useSelector } from 'react-redux';

export default function SourceViewSection(props) {
    const { artifactType, artifactName, nodeId } = props;
    const globalGroupId = useSelector(state => state.groupId);
    const basePath = useSelector(state => state.basePath);
    const [source, setSource] = React.useState("");
    
    const params = {
        groupId: globalGroupId,
        nodeId: nodeId,
        artifactType : artifactType,
        artifactName : artifactName
    };

    const [open, setOpen] = React.useState(false);

    React.useEffect(() => {
        const url = basePath.concat('/configuration');
        
        axios.get(url, {params}).then(response => {
            setSource(response.data.configuration);
        })
    },[])

    const openSourceViewPopup = () => {
        setOpen(true);
    }

    const closeSourceViewPopup = () => {
        setOpen(false);
    };

    const descriptionElementRef = React.useRef(null);
    React.useEffect(() => {
        if (open) {
            const { current: descriptionElement } = descriptionElementRef;
            if (descriptionElement !== null) {
                descriptionElement.focus();
            }
        }
    }, [open]);

    const classes = useStyles();

    return <Grid item xs={12}>
                <Box textAlign='center'>
                    <Button onClick={() => openSourceViewPopup()} variant="contained" color="primary">Source View</Button>
                    <Dialog
                        open={open}
                        onClose={closeSourceViewPopup}
                        aria-labelledby="scroll-dialog-title"
                        aria-describedby="scroll-dialog-description"
                        classes={classes.sourceView}
                    >
                        <DialogTitle id="scroll-dialog-title">{artifactName}</DialogTitle>
                        <DialogContent>
                            <DialogContentText
                                id="scroll-dialog-description"
                                ref={descriptionElementRef}
                                tabIndex={-1}>
                                <XMLViewer xml={source} />
                            </DialogContentText>
                        </DialogContent>
                    </Dialog>
                </Box>
            </Grid>
}

const useStyles = makeStyles((theme) => ({
    sourceView: {
        backgroundColor: 'red',
        width: '2000px',
    }
}));
