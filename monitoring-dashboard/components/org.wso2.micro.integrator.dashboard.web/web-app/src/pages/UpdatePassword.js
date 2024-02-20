/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React from 'react';
import { useSelector } from "react-redux";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import TextField from "@material-ui/core/TextField";
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
import AuthManager from "../auth/AuthManager";

export default function UpdatePassword() {
    const globalGroupId = useSelector(state => state.groupId);
    const [password, setPassword] = React.useState({
        currentPassword: "",
        newPassword: "",
        confirmPassword: ""
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
        setPassword({ ...password, [name]: value });
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
            history.push("/");
        }
    }

    const updatePassword = () => {

        const user = AuthManager.getUser().username;

        if (password.currentPassword === '') {
            setDialog({
                open: true,
                title: 'Error',
                message: 'Please enter current password.',
                isError: true
            })
        } else if (password.newPassword === '') {
            setDialog({
                open: true,
                title: 'Error',
                message: 'Please enter new password.',
                isError: true
            })
        } else if (password.newPassword !== password.confirmPassword) {
            setDialog({
                open: true,
                title: 'Error',
                message: 'New password does not match with the re-typed password.',
                isError: true
            })
        } else {
            var payload = {
                "oldPassword": password.currentPassword,
                "newPassword": password.newPassword,
                "confirmPassword": password.confirmPassword,
                "userId": user
            }
            HTTPClient.updateUserPassword(globalGroupId, payload).then(response => {
                setDialog({
                    open: true,
                    title: 'Success',
                    message: 'Password was updated successfully.',
                    isError: false
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
                                helperText='Enter Current Password'
                                required
                                onChange={(e) => handleUserInput(e)}
                                name="currentPassword"
                                label="Current Password"
                                autoComplete="off"
                                type="password"
                                value={password.currentPassword} />
                        </Box>
                        <Box mb={2}>
                            <TextField
                                margin='dense'
                                variant='outlined'
                                fullWidth
                                helperText='Enter New Password'
                                required
                                onChange={(e) => handleUserInput(e)}
                                name="newPassword"
                                label="New Password"
                                autoComplete="off"
                                type="password"
                                value={password.newPassword} />
                        </Box>
                        <Box mb={2}>
                            <TextField
                                margin='dense'
                                variant='outlined'
                                fullWidth
                                helperText='Re-type New Password'
                                required
                                onChange={(e) => handleUserInput(e)}
                                name="confirmPassword"
                                label="Re-type Password"
                                autoComplete="off"
                                type="password" value={password.confirmPassword} />
                        </Box>
                        <Box mb={2} textAlign='left'>
                            <Button onClick={() => updatePassword()} variant="contained" color="primary">Change Password</Button>
                            <Button component={Link} to="/" color="danger">
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
