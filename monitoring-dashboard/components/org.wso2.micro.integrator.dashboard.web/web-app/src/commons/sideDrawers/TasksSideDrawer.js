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
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
import { Table, TableCell, TableRow } from '@material-ui/core';
import HeadingSection from './commons/HeadingSection'
import SourceViewSection from './commons/SourceViewSection'

export default function TasksSideDrawer(props) {
    var nodeData = props.nodeData;
    const nodeId = nodeData.nodeId;
    const artifactName = nodeData.details.name;
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <HeadingSection name={artifactName} nodeId={nodeId} />
                    <SourceViewSection
                        designContent={<>
                            <Paper className={classes.paper} elevation={0} square>
                                <TasksDetailTable nodeData={nodeData} />
                            </Paper>
                            <Box pl={4}>
                                <TriggerDetails details={nodeData.details} />
                            </Box>
                        </>}
                        artifactType="tasks" artifactName={artifactName} nodeId={nodeId} />
                </Grid>
            </Grid>
        </div>
    );
}

function TasksDetailTable(props) {
    const nodeData = props.nodeData;

    return <Table>
        <TableRow>
            <TableCell>Task Name</TableCell>
            <TableCell>{nodeData.details.name}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>Task Group</TableCell>
            <TableCell>{nodeData.details.taskGroup}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>Task Implementation</TableCell>
            <TableCell>{nodeData.details.implementation}</TableCell>
        </TableRow>
    </Table>
}

function TriggerDetails(props) {
    const task = props.details;
    return <>
        <Typography variant="h6" color="inherit" noWrap>
            TriggerDetails
        </Typography>
        <Box pr={2}>
            <Table size="small">
                <TableRow>
                    <TableCell>Trigger</TableCell>
                    <TableCell>{task.triggerType}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Count</TableCell>
                    <TableCell>{task.triggerCount}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Interval (In seconds)</TableCell>
                    <TableCell>{task.triggerInterval}</TableCell>
                </TableRow>
            </Table>
        </Box>

    </>
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
}));
