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
import { Table, TableBody, TableCell, TableRow } from '@material-ui/core';
import HeadingSection from './commons/HeadingSection'
import SourceViewSection from './commons/SourceViewSection'
import Typography from '@material-ui/core/Typography';

export default function MessageProcessorSideDrawer(props) {
    var nodeData = props.nodeData;
    const nodeId = nodeData.nodeId;
    const artifactName = nodeData.details.name; 
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <HeadingSection name={artifactName} nodeId={nodeId}/>
                    <Paper className={classes.paper}>
                        <MessageProcessorDetailTable nodeData={nodeData}/>
                    </Paper>
                </Grid>
                <ParametersSection parameters={nodeData.details.parameters}/>
                <SourceViewSection artifactType="message-processors" artifactName={artifactName} nodeId={nodeId}/>
            </Grid>
        </div>
    );
}

function MessageProcessorDetailTable(props) {
    const nodeData = props.nodeData;
    return <Table>
                <TableRow>
                    <TableCell>Message Processor Name</TableCell>
                    <TableCell>{nodeData.details.name}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Message Store</TableCell>
                    <TableCell>{nodeData.details.messageStore}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Type</TableCell>
                    <TableCell>{nodeData.details.type}</TableCell>
                </TableRow>
            </Table> 
}

function ParametersSection(props) {
    const parameters = props.parameters;
    const classes = useStyles();
    return <Grid item xs={12}>
                <Paper className={classes.paper} square>
                    <Typography variant="h6" color="inherit" noWrap className={classes.subTopic}>
                        Parameters
                    </Typography>
                    <hr className={classes.horizontalLine}></hr>
                    <Table size="small">
                        <TableBody>
                            {Object.keys(parameters).map(key => (
                                <TableRow>
                                    <TableCell>{key}</TableCell>
                                    <TableCell>{parameters[key]}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </Paper>
            </Grid>
}
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
    }
}));
