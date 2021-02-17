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
import { makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import { Table, TableCell, TableBody, TableRow } from '@material-ui/core';

export default function UserSideDrawer(props) {
    var user = props.user;
    console.log("user",user)
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Paper className={classes.sideDrawerHeading} square>
                        <Typography variant="h6" color="inherit" noWrap>
                            {user.userId}
                        </Typography>
                    </Paper>
                    <Paper className={classes.paper} elevation={0} square>
                            <UserDetailTable user={user}/>
                    </Paper>
                </Grid>
                <Grid item xs={12}>
                    <Paper className={classes.paper} square>
                        <Typography variant="h6" color="inherit" noWrap className={classes.subTopic}>
                            Roles
                        </Typography>
                        <hr className={classes.horizontalLine}></hr>
                    </Paper>
                    {/* <RolesTable roles={user.roles} /> */}
                </Grid>
            </Grid>
        </div>
    );
}

function UserDetailTable(props) {
    const user = props.user;

    return <Table>
                <TableRow>
                    <TableCell>User Id</TableCell>
                    <TableCell>{user.userId}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Is Admin</TableCell>
                    <TableCell>{user.isAdmin}</TableCell>
                </TableRow>
            </Table>
}

// function RolesTable(props) {
//     const roles = props.roles;
//     return <Table>
//                 <TableBody>
//                     {roles.map(role=><TableRow>
//                         <TableCell>{role}</TableCell>
//                     </TableRow>)}
//             </TableBody>
//          </Table>
// }

const useStyles = makeStyles((theme) => ({
    root: {
        flexGrow: 1,
    },
    paper: {
        padding: theme.spacing(2),
        color: theme.palette.text.secondary,
    },
    subTopic: {
        color: '#3f51b5'
    },
    horizontalLine : {
        backgroundColor : '#3f51b5',
        borderWidth: '0px',
        height: '1px'
    },
    sideDrawerHeading: {
        padding: theme.spacing(1),
        height: '64px',
        backgroundColor: theme.palette.background.appBar,
        color: '#ffffff'
    }
}));
