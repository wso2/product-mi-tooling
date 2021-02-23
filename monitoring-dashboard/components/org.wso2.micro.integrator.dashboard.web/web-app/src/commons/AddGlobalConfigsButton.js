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
import Drawer from '@material-ui/core/Drawer';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
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
import AddUserSideDrawer from './sideDrawers/AddUserSideDrawer'
import { Link } from 'react-router-dom'

export default function AddGlobalConfigsButton(props) {
    const { pageId } = props;
    const [state, setState] = React.useState({
        openSideDrawer: false
    });

    const toggleDrawer = (open) => (event) => {
        if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
            return;
        }
        setState({ ...state, openSideDrawer: open });
    };

    if (pageId === 'log-configs') {
        return <div>
                    <Button onClick={toggleDrawer(true)} variant="contained" color="primary">Add Loggers</Button>
                    <Drawer anchor='right' open={state['openSideDrawer']} onClose={toggleDrawer(false)} >
                        <LogConfigsSideDrawer />
                    </Drawer>
                </div>
    } else if (pageId === 'users') {
        return <div>
            <Button component={Link} to="/users/add" variant="contained" color="primary">
                Add User
            </Button>
                </div>
    } else {
        return <div />
    } 
}

function LogConfigsSideDrawer() {
    const classes = useStyles();
    const globalGroupId = useSelector(state => state.groupId);
    const basePath = useSelector(state => state.basePath);
    const [logConfig, setLogConfig] = React.useState({
        loggerName: "",
        loggerClass: "",
        loggerLevel: "OFF"
    });
    const [dialog, setDialog] = React.useState({
        open : false,
        title: '',
        message: ''
    });

    const handleUserInput = (event) => {
        const target = event.target;
        const name = target.name;
        const value = target.value;
        setLogConfig({ ...logConfig, [name]: value });
    }

    const handleDialogClose = () => {
        setDialog({
            open: false,
            title: '',
            message: ''
        })
    }

    const addLogger = () => {
        const {loggerName, loggerClass, loggerLevel} = logConfig;

        if (loggerName === '') {
            setDialog({
                open: true, 
                title: 'Error',
                message: 'Logger name is missing.'
            })
        } else if (loggerClass ==='') {
            setDialog({
                open: true, 
                title: 'Error',
                message: 'Logger class is missing.'
            })
        } else {
            const url = basePath.concat('/groups/').concat(globalGroupId).concat("/log-configs");
            axios.post(url, {
                "name": loggerName,
                "loggerClass": loggerClass,
                "level": loggerLevel
            }).then(response => {
                if (response.data.status === 'fail') {
                    setDialog({
                        open: true, 
                        title: 'Error',
                        message: response.data.message
                    })
                } else {
                    setDialog({
                        open: true, 
                        title: 'Success',
                        message: 'Successfully added logger'
                    })
                }
            });
        } 
    }
    return <div className={classes.root}>
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Paper className={classes.sideDrawerHeading} square>
                            <Typography variant="h6" color="inherit" noWrap>Add Loggers</Typography>
                        </Paper>
                        <Paper className={classes.paper}>
                            <TextField onChange={(e) => handleUserInput(e)} name="loggerName" label="Logger Name" value={logConfig.loggerName}/>   
                            <TextField onChange={(e) => handleUserInput(e)} name="loggerClass" label="Logger Class" value={logConfig.loggerClass}/>
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
                                <Button onClick={() => addLogger()} variant="contained" color="primary">Add Logger</Button>
                            </Box>

                            <Dialog open={dialog.open} onClose={() => handleDialogClose()}
                                    aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                                <DialogTitle id="alert-dialog-title">{dialog.title}</DialogTitle>
                                <DialogContent dividers>
                                    <DialogContentText id="alert-dialog-description">
                                        {dialog.message}
                                    </DialogContentText>
                                </DialogContent>
                                <DialogActions>
                                    <Button onClick={() => handleDialogClose()} variant="contained" autoFocus>
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
    }
}));
