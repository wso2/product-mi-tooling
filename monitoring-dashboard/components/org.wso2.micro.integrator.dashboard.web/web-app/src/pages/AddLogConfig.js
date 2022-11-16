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
import {useSelector} from "react-redux";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import TextField from "@material-ui/core/TextField";
import FormHelperText from '@material-ui/core/FormHelperText';
import Select from "@material-ui/core/Select";
import Box from "@material-ui/core/Box";
import {Button} from "@material-ui/core";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions";
import {makeStyles} from "@material-ui/core/styles";
import {Link, useHistory} from "react-router-dom";
import HTTPClient from '../utils/HTTPClient';

export default function AddLogConfig() {
    const globalGroupId = useSelector(state => state.groupId);
    const [nodeList, setNodeList] = React.useState([]);
    React.useEffect(() => {
        let allNodes = [{label: 'All', value: 'All'}]
        if (globalGroupId !== '') {
            HTTPClient.getAllNodes().then(response => {
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
    }, [globalGroupId])

    return <AddLogConfigsSection nodeList={nodeList}/>
}

function AddLogConfigsSection() {
    const classes = useStyles();
    const history = useHistory();
    const globalGroupId = useSelector(state => state.groupId);
    const [logConfig, setLogConfig] = React.useState({
        loggerName: "",
        loggerClass: "",
        loggerLevel: "OFF"
    });

    const [confirmationDialog, setConfirmationDialog] = React.useState({
        open: false,
        title: '',
        message: ''
    });

    const checkAndOpenConfirmationDialog = () => {
        const {loggerName, loggerClass, loggerLevel} = logConfig;
        if (loggerName === '') {
            setCompletionStatusDialog({
                open: true,
                title: 'Error',
                message: 'Logger name is missing.',
                isError: true
            })
        } else if (loggerClass === '') {
            setCompletionStatusDialog({
                open: true,
                title: 'Error',
                message: 'Logger class is missing.',
                isError: true
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
        open: false,
        title: '',
        message: '',
        isError: false
    });

    const handlecompletionStatusDialogClose = () => {
        const isError = completionStatusDialog.isError;
        setCompletionStatusDialog({
            open: false,
            title: '',
            message: '',
            isError: false
        })

        if (!isError) {
            history.push("/log-configs");
        }
    }

    const handleUserInput = (event) => {
        const target = event.target;
        const name = target.name;
        const value = target.value;
        setLogConfig({...logConfig, [name]: value});
    }

    const addLogger = () => {
        handleConfirmationDialogClose();
        const {loggerName, loggerClass, loggerLevel} = logConfig;
        var payload = {
            "name": loggerName,
            "loggerClass": loggerClass,
            "level": loggerLevel
        }
        HTTPClient.addLogConfig(globalGroupId, payload).then(response => {
            if (response.data.status === 'fail') {
                setCompletionStatusDialog({
                    open: true,
                    title: 'Error',
                    message: response.data.message,
                    isError: true
                })
            } else {
                HTTPClient.getPaginatedResults('', 0, 5, 'log-configs', 'asc', 'name', globalGroupId, 'All', true).then(() => {
                    setCompletionStatusDialog({
                        open: true,
                        title: 'Success',
                        message: 'Successfully added logger',
                        isError: false
                    })
                }) 
            }
        });
    }
    return <div className={classes.root}>
                <Paper className={classes.paper}>
                    <Grid container spacing={3}>
                        <Grid item xl={6} lg={6} md={6} sm={12} xs={12}>
                            <form>
                                <Box mb={2}>
                                    <TextField
                                        autoFocus
                                        margin='dense'
                                        variant='outlined'
                                        fullWidth
                                        helperText='Logger Name'
                                        required
                                        label="Logger Name"
                                        onChange={(e) => handleUserInput(e)}
                                        name="loggerName"
                                        value={logConfig.loggerName}
                                    />
                                </Box>
                                <Box mb={2}>
                                    <TextField
                                        margin='dense'
                                        variant='outlined'
                                        fullWidth
                                        helperText='Logger Class'
                                        required
                                        label="Logger Class"
                                        onChange={(e) => handleUserInput(e)}
                                        name="loggerClass"
                                        value={logConfig.loggerClass}
                                    />
                                </Box>

                                <Box mb={4}>
                                    <Select
                                        margin='dense'
                                        variant='outlined'
                                        fullWidth
                                        helperText='Logger level'
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
                                    <FormHelperText className={classes.selectHelperText}>Logger Level</FormHelperText>
                                </Box>
                                <Box mb={2} textAlign='left'>
                                    <Button onClick={() => checkAndOpenConfirmationDialog()} variant="contained" color="primary">Add
                                        Logger</Button>
                                    <Button component={Link} to="/log-configs" color="danger">
                                        Cancel
                                    </Button>
                                </Box>
                            </form>

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
                                        Continue
                                    </Button>

                                    <Button onClick={() => handleConfirmationDialogClose()} variant="contained" autoFocus>
                                        Cancel
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
                        </Grid>
                    </Grid>
                </Paper>
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
    selectHelperText: {
        paddingLeft: '15px'
    }
}));
