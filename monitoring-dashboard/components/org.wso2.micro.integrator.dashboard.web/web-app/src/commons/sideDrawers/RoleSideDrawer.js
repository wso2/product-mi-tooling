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
import { makeStyles } from '@material-ui/core/styles';
import HeadingSection from './commons/HeadingSection'
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import Box from '@material-ui/core/Box';

import { Table, TableCell, TableBody, TableRow } from '@material-ui/core';

export default function RoleSideDrawer(props) {
    const { role } = props;
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <HeadingSection name={role.roleName} />
                    <RolesSection roles={role.details.users} classes={classes}/>
                </Grid>
            </Grid>
        </div>
    );
}

function RolesSection(props) {
    const roles = props.roles;
    const classes = props.classes;
    return <Box pl={4}>
                <Typography variant="h6" color="inherit" noWrap>
                    Users
                </Typography>
                <Box pr={2}>
                    <ParametersSection roles={roles} classes={classes} />
                </Box>
            </Box>
}

function ParametersSection(props) {
    const {roles, classes} = props;
    return <Table size="small" className={classes.parameterTable}>
        {roles.map(role=><TableRow><TableCell>{role}</TableCell></TableRow>)}
    </Table>
}

const useStyles = makeStyles((theme) => ({
    root: {
        flexGrow: 1,
        width: 700,
        overflowX: 'hidden',
    },
    paper: {
        padding: theme.spacing(2),
    },
    parameterTable: {
        width: '100%',
    },
}));
