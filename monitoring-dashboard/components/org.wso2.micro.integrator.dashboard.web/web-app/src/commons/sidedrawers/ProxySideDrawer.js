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
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import { Button, Table, TableCell, TableRow } from '@material-ui/core';
import {CopyToClipboard} from 'react-copy-to-clipboard';
import FileCopyIcon from '@material-ui/icons/FileCopy';
import Switch from "react-switch";
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import XMLViewer from 'react-xml-viewer';
import Box from '@material-ui/core/Box';
import { useSelector } from 'react-redux';

export default function ProxySideDrawer(props) {
    const globalGroupId = useSelector(state => state.groupId);
    var nodeData = props.nodeData;
    if(Object.keys(nodeData) !==0 ) {
        if(nodeData.details['tracing'] == 'enabled') {
            nodeData.details['tracing'] = true
        } else {
            nodeData.details['tracing'] = false
        }
    }

    const params = {
        groupId: globalGroupId,
        nodeId: nodeData.nodeId,
        artifactType : "proxy-services",
        artifactName : nodeData.details.name
    };
      
    const url = "http://0.0.0.0:9743/api/rest/configuration";
    axios.get(url, {params}).then(response => {
        nodeData.details.source = response.data.configuration;
    })

    const [open, setOpen] = React.useState(false);

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

    const changeServiceStatus = () => {
        nodeData.details['isRunning'] = !nodeData.details['isRunning'];
        updateProxy("isRunning", nodeData.details['isRunning']);
    };

    const changeTracingStatus = () => {
        nodeData.details['tracing'] = !nodeData.details['tracing'];
        updateProxy("tracing", nodeData.details['tracing']);
    };

    const updateProxy = (type, value) => {
        const url = "http://0.0.0.0:9743/api/rest/groups/".concat(globalGroupId).concat("/proxy-services");
        axios.put(url, {
            "serviceName": nodeData.details.name,
            "nodeId": nodeData.nodeId,
            "type": type,
            "value": value
        });
    }

    return (
        <div className={classes.root}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Paper className={classes.sideDrawerHeading} square>
                        <Typography variant="h6" color="inherit" noWrap>
                            {nodeData.details.name}
                        </Typography>
                        <Typography variant="h8" color="inherit" noWrap>
                            {nodeData.nodeId}
                        </Typography>
                    </Paper>
                    <Paper className={classes.paper}>
                        <Table>
                            <TableRow>
                                <TableCell>Service Status</TableCell>
                                <TableCell><Switch checked={nodeData.details.isRunning} onChange={changeServiceStatus} /></TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Service Name</TableCell>
                                <TableCell>{nodeData.details.name}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Statistics</TableCell>
                                <TableCell>{nodeData.details.stats}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Tracing</TableCell>
                                <TableCell>
                                    <label>
                                        <Switch checked={nodeData.details.tracing} onChange={changeTracingStatus}/>
                                    </label>
                                </TableCell>
                            </TableRow>
                        </Table>
                    </Paper>
                </Grid>
                <Grid item xs={12}>
                    <Paper className={classes.paper} square>
                        <Typography variant="h6" color="inherit" noWrap className={classes.subTopic}>
                            Endpoints
                        </Typography>
                        <hr className={classes.horizontalLine}></hr>
                    </Paper>
                    <Paper className={classes.paper} square>
                        <Table>
                            {nodeData.details.eprs.map(ep =>
                                <TableRow>{ep}
                                    <CopyToClipboard text={ep} className={classes.clipboard}>
                                        <Button><FileCopyIcon/></Button>
                                    </CopyToClipboard>
                                </TableRow>)}
                        </Table>
                    </Paper><br/>
                    <Box textAlign='center'>
                        <Button onClick={() => openSourceViewPopup()} variant="contained" color="primary">Source View</Button>
                        <Dialog
                            open={open}
                            onClose={closeSourceViewPopup}
                            aria-labelledby="scroll-dialog-title"
                            aria-describedby="scroll-dialog-description"
                            classes={classes.sourceView}
                        >
                            <DialogTitle id="scroll-dialog-title">{nodeData.details.name}</DialogTitle>
                            <DialogContent>
                                <DialogContentText
                                    id="scroll-dialog-description"
                                    ref={descriptionElementRef}
                                    tabIndex={-1}>
                                    <XMLViewer xml={nodeData.details.source} />
                                </DialogContentText>
                            </DialogContent>
                        </Dialog>
                    </Box>
                </Grid>
            </Grid>
        </div>
    );
}


const useStyles = makeStyles((theme) => ({
    root: {
        flexGrow: 1,
    },
    paper: {
        padding: theme.spacing(2),
        color: theme.palette.text.secondary,
    },
    sideDrawerHeading: {
        padding: theme.spacing(1),
        height: '64px',
        backgroundColor: '#3f51b5',
        color: '#ffffff'
    },
    subTopic: {
        color: '#3f51b5'
    },
    horizontalLine : {
        backgroundColor : '#3f51b5',
        borderWidth: '0px',
        height: '1px'
    },
    clipboard: {
        color: '#3f51b5'
    },
    sourceView: {
        backgroundColor: 'red',
        width: '2000px',
    }
}));
