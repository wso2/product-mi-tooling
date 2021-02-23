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
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Select from '@material-ui/core/Select';
import Box from '@material-ui/core/Box';
import TextField from '@material-ui/core/TextField';
import { Button } from '@material-ui/core';
import { useSelector } from 'react-redux';
import AuthManager from '../../auth/AuthManager';

export default function AddUserSideDrawer() {
    const globalGroupId = useSelector(state => state.groupId);
    const [user, setUser] = React.useState({
        userId: "",
        password: "",
        passwordRepeat: "",
        isAdmin: "false"
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
        setUser({ ...user, [name]: value });
    }

    const handleDialogClose = () => {
        setDialog({
            open: false,
            title: '',
            message: ''
        })
    }

    const addUser = () => {
        const {userId, password, passwordRepeat, isAdmin} = user

        if (userId === '') {
            setDialog({
                open: true, 
                title: 'Error',
                message: 'User id is missing.'
            })
        } else if (password ==='') {
            setDialog({
                open: true, 
                title: 'Error',
                message: 'Password is missing.'
            })
        } else if (password !== passwordRepeat) {
            setDialog({
                open: true, 
                title: 'Error',
                message: 'Repeat password must match the password  '
            })
        } else {
            const url = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/users");
            axios.post(url, {
                "userId": userId,
                "password": password,
                "isAdmin": isAdmin
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
                        message: 'Successfully added user'
                    })
                }
            })
        }
    }

    const classes = useStyles();

    return <div className={classes.root}>
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Paper className={classes.sideDrawerHeading} square>
                            <Typography variant="h6" color="inherit" noWrap>Add Users</Typography>
                        </Paper>
                        <Paper className={classes.paper}>
                            <TextField onChange={(e) => handleUserInput(e)} name="userId" label="User" value={user.userId}/>   
                            <TextField onChange={(e) => handleUserInput(e)} name="password" label="Password" type="password" value={user.password}/>
                            <TextField onChange={(e) => handleUserInput(e)} name="passwordRepeat" label="Repeat Password" type="password" value={user.passwordRepeat}/>
                            <Select
                                native
                                name="isAdmin"
                                value={user.isAdmin}
                                onChange={(e) => handleUserInput(e)}
                                label="Is Admin"
                            >
                                <option value={"true"}>True</option>
                                <option value={"false"}>False</option>
                            </Select>
                            <br /> <br/>
                            <Box textAlign='right'>
                                <Button onClick={() => addUser()} variant="contained" color="primary">Add User</Button>
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
