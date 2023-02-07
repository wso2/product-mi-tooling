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
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import TextField from "@material-ui/core/TextField";
import FormHelperText from '@material-ui/core/FormHelperText';
import Select from "@material-ui/core/Select";
import Box from "@material-ui/core/Box";
import { Button } from "@material-ui/core";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions";
import { makeStyles } from "@material-ui/core/styles";
import { Link, useHistory } from "react-router-dom";
import HTTPClient from '../utils/HTTPClient';

export default function AddUsers() {
    const globalGroupId = useSelector(state => state.groupId);
    const [user, setUser] = React.useState({
        userId: "",
        domain: "PRIMARY",
        password: "",
        passwordRepeat: "",
        isAdmin: "false"
    });
    const history = useHistory();

    const [dialog, setDialog] = React.useState({
        open: false,
        title: '',
        message: '',
        isError: false
    });

    const handleUserInput = (event) => {
        const target = event.target;
        const name = target.name;
        const value = target.value;
        setUser({ ...user, [name]: value });
    }

    const handleDialogClose = () => {
        const isError = dialog.isError;
        setDialog({
            open: false,
            title: '',
            message: '',
            isError: false
        })
        if (!isError) {
            history.push("/users");
        }
    }

    const addUser = () => {
        if (user.domain.toLowerCase() !== "primary") {
            // only the primary userstore can have admin users.
            user.isAdmin = "false"
        }
        const { userId, domain, password, passwordRepeat, isAdmin } = user

        if (userId === '') {
            setDialog({
                open: true,
                title: 'Error',
                message: 'User id is missing.',
                isError: true
            })
        } else if (password === '') {
            setDialog({
                open: true,
                title: 'Error',
                message: 'Password is missing.',
                isError: true
            })
        } else if (password !== passwordRepeat) {
            setDialog({
                open: true,
                title: 'Error',
                message: 'Repeat password must match the password  ',
                isError: true
            })
        } else {
            var payload = {
                "userId": userId,
                "domain": domain,
                "password": password,
                "isAdmin": isAdmin
            }
            HTTPClient.addUser(globalGroupId, payload).then(response => {
                // Update the user list so user can also see added one
                HTTPClient.getPaginatedUsersAndRoles('', 0, 5, 'users', 'asc', 'name', globalGroupId, true).then(() => {
                    setDialog({
                        open: true,
                        title: 'Success',
                        message: 'Successfully added user',
                        isError: false
                    })
                })     
            }).catch(error => {
                setDialog({
                    open: true,
                    title: 'Error',
                    message: error.response.data.message,
                    isError: true
                })
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
                                margin='dense'
                                variant='outlined'
                                fullWidth
                                helperText='Enter User Store Domain Name'
                                label="User Store Domain"
                                autoComplete="off"
                                onChange={(e) => handleUserInput(e)}
                                name="domain"
                                value={user.domain}
                            />
                        </Box>
                        <Box mb={2}>
                            <TextField
                                autoFocus
                                margin='dense'
                                variant='outlined'
                                fullWidth
                                helperText='Enter User Name'
                                required
                                label="User Name"
                                autoComplete="off"
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
                                required
                                onChange={(e) => handleUserInput(e)}
                                name="password" 
                                label="Password"
                                autoComplete="off"
                                type="password"
                                value={user.password} />
                        </Box>
                        <Box mb={2}>
                            <TextField
                                margin='dense'
                                variant='outlined'
                                fullWidth
                                helperText='Re Enter The Same Password'
                                required
                                onChange={(e) => handleUserInput(e)}
                                name="passwordRepeat"
                                label="Repeat Password"
                                autoComplete="off"
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
                                disabled={user.domain.toLowerCase() !== "primary"}
                            >
                                <option value={"true"}>True</option>
                                <option value={"false"}>False</option>
                            </Select>
                            <FormHelperText className={classes.selectHelperText}>Is Admin</FormHelperText>
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
        'border-radius': '0px',
    },
    selectHelperText: {
        paddingLeft: '15px'
    }
}));
