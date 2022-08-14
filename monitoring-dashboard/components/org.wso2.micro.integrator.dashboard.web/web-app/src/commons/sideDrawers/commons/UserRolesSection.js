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
import { Table, TableCell, TableRow } from '@material-ui/core';
import { Button, Typography } from "@material-ui/core";
import { useSelector } from 'react-redux';
import Switch from "react-switch";
import HTTPClient from '../../../utils/HTTPClient';
import Box from '@material-ui/core/Box';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

export default function UserRolesSection(props) {
    const globalGroupId = useSelector(state => state.groupId);
    const { user, retrieveData, classes } = props;
    const [assignedRoles, setAssignedRoles] = React.useState(user.details.roles);
    const [allRoles, setAllRoles] = React.useState([]);
    var [addedRoles, setAddedRoles] = React.useState([]);
    var [removedRoles, setRemovedRoles] = React.useState([]);
    const [unassignedRoles, setUnassignedRoles] = React.useState([]);

    React.useEffect(() => {
        HTTPClient.getAllRoles(globalGroupId).then(response => {
            const allRoles = [];
            response.data.resourceList.map(data => allRoles.push(data.roleName));
            const unassignedRoles = allRoles.filter(n => !assignedRoles.includes(n));
            setAllRoles(allRoles);
            setUnassignedRoles(unassignedRoles);
        })
    }, [])

    React.useEffect(() => {
        setUnassignedRoles(allRoles.filter(n => !assignedRoles.includes(n)));
    }, [assignedRoles])

    const updateRoleList = (role, isRole) => {
        if(isRole) {
            if(removedRoles.includes(role)) {
                const index = removedRoles.indexOf(role);
                removedRoles.splice(index);
                setRemovedRoles(removedRoles);
            } else {
                setAddedRoles(addedRoles.concat(role));
            }
        } else {
            if(addedRoles.includes(role)) {
                const index = addedRoles.indexOf(role);
                addedRoles.splice(index);
                setAddedRoles(addedRoles);
            } else {
                setRemovedRoles(removedRoles.concat(role));
            }
        }
    }

    return <Box pl={4}>
                <Typography variant="h6" color="inherit" noWrap>
                    Roles
                </Typography>
                <Box pr={2}>
                    <RoleListSection assignedRoles={assignedRoles} unassignedRoles={unassignedRoles} updateRoleList={updateRoleList} classes={classes} />
                </Box>
                <UpdateRolesButton globalGroupId={globalGroupId} userId={user.userId} addedRoles={addedRoles} removedRoles={removedRoles} retrieveData={retrieveData}/>
            </Box>
}

function RoleListSection(props) {
    const {assignedRoles, unassignedRoles, updateRoleList, classes} = props;
    const assignedRolesDetails = [];
    assignedRoles.map(role => assignedRolesDetails.push({"name":role, "isAssigned":true}));
    const unassignedRolesDetails = [];
    unassignedRoles.map(role => unassignedRolesDetails.push({"name":role, "isAssigned":false}));
    const allRoles = assignedRolesDetails.concat(unassignedRolesDetails);
    return <Table size="small" className={classes.parameterTable}>
        {allRoles.map(role=>
            <TableRow>
                <TableCell>{role.name}</TableCell>
                <TableCell>
                    <label>
                        <UpdateRoleSwitch role={role} updateRoleList={updateRoleList}/>
                    </label>
                </TableCell>
            </TableRow>)}
    </Table>
}

function UpdateRoleSwitch(props) {
    const { role, updateRoleList } = props;
    var [isChecked, setIsChecked] = React.useState(role.isAssigned);
    const changeRole = () => {
        setIsChecked(!isChecked);
        updateRoleList(role.name, !isChecked);
    };

    return <Switch checked={isChecked} onChange={changeRole} height={16} width={36} disabled={role.name==='Internal/everyone'}/>;
}

function UpdateRolesButton(props) {
    const {globalGroupId, userId, addedRoles, removedRoles, retrieveData} = props;

    const [confirmationDialog, setConfirmationDialog] = React.useState({
        open : false,
        title: '',
        message: ''
    });

    const [completionStatusDialog, setCompletionStatusDialog] = React.useState({
        open : false,
        title: '',
        message: ''
    });

    const handleConfirmationDialogClose = () => {
        setConfirmationDialog({
            open: false,
            title: '',
            message: ''
        })
    }

    const handlecompletionStatusDialogClose = () => {
        setCompletionStatusDialog({
            open: false,
            title: '',
            message: ''
        })
    }

    const confirmUpdate = () => {
        var message = 'Are you sure you want to update '.concat(userId).concat(' ?');
        setConfirmationDialog({
            open: true,
            title: 'Confirmation',
            message: message
        })
    }

    const updateUserRoles = () => {
        handleConfirmationDialogClose();
        var payload = {
            "userId" : userId,
            "removedRoles" : removedRoles,
            "addedRoles": addedRoles
        }
        HTTPClient.updateUserRoles(globalGroupId, payload).then(response => {
            if (response.data.status === 'success') {
                retrieveData('', true);
                setCompletionStatusDialog({
                    open: true, 
                    title: 'Success',
                    message: "Successfully updated user ".concat(userId).concat(".")
                })
            } else {
                setCompletionStatusDialog({
                    open: true, 
                    title: 'Error',
                    message: response.data.message
                })
            }
        });
    }

    return <div><tr><td><Box mb={2} textAlign='right'>
                <Button onClick={() => confirmUpdate()} variant="contained" color="primary">Update</Button>
            </Box></td></tr>
            <Dialog open={confirmationDialog.open} onClose={() => handleConfirmationDialogClose()}
                                    aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">{confirmationDialog.title}</DialogTitle>

                <DialogContent dividers>
                    <DialogContentText id="alert-dialog-description">
                        {confirmationDialog.message}
                    </DialogContentText>
                </DialogContent>

                <DialogActions>
                    <Button onClick={() => updateUserRoles()} variant="contained" autoFocus>
                        Confirm
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
            </div>

}
