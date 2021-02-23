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
import axios from 'axios';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import { makeStyles } from '@material-ui/core/styles';
import { Button } from '@material-ui/core';
import Box from '@material-ui/core/Box';
import TextField from '@material-ui/core/TextField';
import Select from '@material-ui/core/Select';
import { useSelector } from 'react-redux';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import AuthManager from '../auth/AuthManager';

export default function AddLogConfigs(props) {
    const { pageId } = props;
    const globalGroupId = useSelector(state => state.groupId);
    const [nodeList, setNodeList] = React.useState([]);
    React.useEffect(()=>{
        let allNodes = [{label:'All', value:'All'}]
        if (globalGroupId !== '') {
            const getNodeUrl = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/nodes");
            axios.get(getNodeUrl).then(response => {
                response.data.filter(node => {
                    var node = {
                        label: node.nodeId,
                        value: node.nodeId
                    }
                    allNodes.push(node);
                })
                setNodeList(allNodes)
            })
        }
    },[globalGroupId])

    if (pageId === 'log-configs') {
        return <AddLogConfigsSection nodeList={nodeList} />
    } 
}

function AddLogConfigsSection() {
    const classes = useStyles();
    const globalGroupId = useSelector(state => state.groupId);
    const [logConfig, setLogConfig] = React.useState({
        loggerName: "",
        loggerClass: "",
        loggerLevel: "OFF"
    });

    const [confirmationDialog, setConfirmationDialog] = React.useState({
        open : false,
        title: '',
        message: ''
    });

    const checkAndOpenConfirmationDialog = () => {
        const {loggerName, loggerClass, loggerLevel} = logConfig;
        if (loggerName === '') {
            setCompletionStatusDialog({
                open: true, 
                title: 'Error',
                message: 'Logger name is missing.'
            })
        } else if (loggerClass ==='') {
            setCompletionStatusDialog({
                open: true, 
                title: 'Error',
                message: 'Logger class is missing.'
            })
        } else {
            var message = 'This would add logger to all nodes of group '.concat(globalGroupId).concat('. Do you want to continue?');
            setConfirmationDialog({
                open: true,
                title: 'Confirmation',
                message: message
            })
        }
    }

    const handleConfirmationDialogClose = () => {
        setConfirmationDialog({
            open: false,
            title: '',
            message: ''
        })
    }

    const [completionStatusDialog, setCompletionStatusDialog] = React.useState({
        open : false,
        title: '',
        message: ''
    });

    const handlecompletionStatusDialogClose = () => {
        setCompletionStatusDialog({
            open: false,
            title: '',
            message: ''
        })
    }

    const handleUserInput = (event) => {
        const target = event.target;
        const name = target.name;
        const value = target.value;
        setLogConfig({ ...logConfig, [name]: value });
    }

    const addLogger = () => {
        handleConfirmationDialogClose();
        const {loggerName, loggerClass, loggerLevel} = logConfig;
        const url = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/log-configs");
        axios.post(url, {
            "name": loggerName,
            "loggerClass": loggerClass,
            "level": loggerLevel
        }).then(response => {
            if (response.data.status === 'fail') {
                setCompletionStatusDialog({
                    open: true, 
                    title: 'Error',
                    message: response.data.message
                })
            } else {
                setCompletionStatusDialog({
                    open: true, 
                    title: 'Success',
                    message: 'Successfully added logger'
                })
            }
        });
    }
    return <div className={classes.root}>
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Paper className={classes.paper}>
                            <TextField onChange={(e) => handleUserInput(e)} name="loggerName" label="Logger Name" value={logConfig.loggerName}/>  <br/> 
                            <TextField onChange={(e) => handleUserInput(e)} name="loggerClass" label="Logger Class" value={logConfig.loggerClass}/> <br/>
                            <Select
                                native
                                name="loggerLevel"
                                value={logConfig.loggerLevel}
                                onChange={(e) => handleUserInput(e)}
                                label="Level"
                            >
                                <option value={'OFF'}>OFF</option>
                                <option value={'TRACE'}>TRACE</option>
                                <option value={'DEBUG'}>DEBUG</option>
                                <option value={'INFO'}>INFO</option>
                                <option value={'WARN'}>WARN</option>
                                <option value={'ERROR'}>ERROR</option>
                                <option value={'FATAL'}>FATAL</option>
                            </Select>
                            <br /> <br/>
                            <Box textAlign='right'>
                                <Button onClick={() => checkAndOpenConfirmationDialog()} variant="contained" color="primary">Add Logger</Button>
                            </Box>

                            <Dialog open={confirmationDialog.open} onClose={() => handleConfirmationDialogClose()}
                                    aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                                <DialogTitle id="alert-dialog-title">{confirmationDialog.title}</DialogTitle>
                                <DialogContent dividers>
                                    <DialogContentText id="alert-dialog-description">
                                        {confirmationDialog.message}
                                    </DialogContentText>
                                </DialogContent>

                                <DialogActions>
                                    <Button onClick={() => addLogger()} variant="contained" autoFocus>
                                        CONTINUE
                                    </Button>

                                    <Button onClick={() => handleConfirmationDialogClose()} variant="contained" autoFocus>
                                        CANCEL
                                    </Button>
                                </DialogActions>
                            </Dialog>

                            <Dialog open={completionStatusDialog.open} onClose={() => handlecompletionStatusDialogClose()}
                                    aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                                <DialogTitle id="alert-dialog-title">{completionStatusDialog.title}</DialogTitle>
                                <DialogContent dividers>
                                    <DialogContentText id="alert-dialog-description">
                                        {completionStatusDialog.message}
                                    </DialogContentText>
                                </DialogContent>

                                <DialogActions>
                                    <Button onClick={() => handlecompletionStatusDialogClose()} variant="contained" autoFocus>
                                        OK
                                    </Button>
                                </DialogActions>
                            </Dialog>
                        </Paper>
                    </Grid>
                </Grid>
            </div>
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
    selectRoot: {
        minHeight: 25,
    },
    formControl: {
        margin: theme.spacing(1),
        minWidth: 120,
        maxWidth: 300,
    },
}));
