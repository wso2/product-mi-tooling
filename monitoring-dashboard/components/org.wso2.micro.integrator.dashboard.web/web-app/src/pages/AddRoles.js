/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import Box from "@material-ui/core/Box";
import { Button } from "@material-ui/core";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions";
import { makeStyles } from "@material-ui/core/styles";
import { Link, useHistory, Redirect } from "react-router-dom";
import HTTPClient from '../utils/HTTPClient';
import Roles from './Roles';

export default function AddRoles() {
    const globalGroupId = useSelector(state => state.groupId);
    const [role, setRole] = React.useState({
        role: "",
        domain: "PRIMARY"
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
        setRole({ ...role, [name]: value });
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
            history.push("/roles");
        }
    }

    const addRole = () => {
        const { roleName, domain } = role
        
        if (roleName === '') {
            setDialog({
                open: true,
                title: 'Error',
                message: 'roleName is missing.',
                isError: true
            })
        } else {
            var payload = {
                "roleName": roleName,
                "domain": domain
            }
            
            HTTPClient.addRole(globalGroupId, payload).then(response => {
                // Update the role list so user can also see added one
                HTTPClient.getPaginatedUsersAndRoles('', 0, 5, 'roles', 'asc', 'name', globalGroupId, true).then(() => {
                    setDialog({
                        open: true,
                        title: 'Success',
                        message: 'Successfully added role',
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
                                value={role.domain}
                            />
                        </Box>
                        <Box mb={2}>
                            <TextField
                                autoFocus
                                margin='dense'
                                variant='outlined'
                                fullWidth
                                helperText='Enter Role Name'
                                required
                                label="Role Name"
                                autoComplete="off"
                                onChange={(e) => handleUserInput(e)}
                                name="roleName"
                                value={role.roleName}
                            />
                        </Box>
                        <Box mb={2} textAlign='left'>
                            <Button onClick={() => addRole()} variant="contained" color="primary">Add Role</Button>
                            <Button component={Link} to="/roles" color="danger">
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
