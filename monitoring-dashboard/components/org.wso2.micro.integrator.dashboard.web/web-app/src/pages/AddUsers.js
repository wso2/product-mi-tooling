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
import { useSelector } from "react-redux";
import axios from "axios";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import TextField from "@material-ui/core/TextField";
import Select from "@material-ui/core/Select";
import Box from "@material-ui/core/Box";
import { Button } from "@material-ui/core";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions";
import AuthManager from '../auth/AuthManager';
import { makeStyles } from "@material-ui/core/styles";
import { Link, useHistory } from "react-router-dom";

export default function AddUsers() {
    const globalGroupId = useSelector(state => state.groupId);
    const [user, setUser] = React.useState({
        userId: "",
        password: "",
        passwordRepeat: "",
        isAdmin: "false"
    });
    const history = useHistory();

    const [dialog, setDialog] = React.useState({
        open: false,
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
        history.push("/users");
    }

    const addUser = () => {
        const { userId, password, passwordRepeat, isAdmin } = user

        if (userId === '') {
            setDialog({
                open: true,
                title: 'Error',
                message: 'User id is missing.'
            })
        } else if (password === '') {
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
                                helperText='Enter User Name'
                                required
                                label="User Name"
                                onChange={(e) => handleUserInput(e)}
                                name="userId"
                                value={user.userId}
                            />
                        </Box>
                        <Box mb={2}>
                            <TextField
                                margin='dense'
                                variant='outlined'
                                fullWidth
                                helperText='Enter Password'
                                onChange={(e) => handleUserInput(e)}
                                name="password" l
                                abel="Password"
                                type="password"
                                value={user.password} />
                        </Box>
                        <Box mb={2}>
                            <TextField
                                margin='dense'
                                variant='outlined'
                                fullWidth
                                helperText='Re Enter The Same Password'
                                onChange={(e) => handleUserInput(e)}
                                name="passwordRepeat"
                                label="Repeat Password"
                                type="password" value={user.passwordRepeat} />
                        </Box>
                        <Box mb={4}>
                            <Select
                                margin='dense'
                                variant='outlined'
                                fullWidth
                                helperText='Select Role'
                                native
                                name="isAdmin"
                                value={user.isAdmin}
                                onChange={(e) => handleUserInput(e)}
                                label="Is Admin"
                            >
                                <option value={"true"}>True</option>
                                <option value={"false"}>False</option>
                            </Select>
                        </Box>
                        <Box mb={2} textAlign='left'>
                            <Button onClick={() => addUser()} variant="contained" color="primary">Add User</Button>
                            <Button component={Link} to="/users" color="danger">
                                Cancel
                                </Button>
                        </Box>
                    </form>

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
                </Grid>
            </Grid>
        </Paper>

    </div >

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
        'border-radius': '0px',
    }


}));