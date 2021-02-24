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
import { Table, TableCell, TableRow } from '@material-ui/core';
import HeadingSection from './commons/HeadingSection'
import SourceViewSection from './commons/SourceViewSection'

export default function MessageStoreSideDrawer(props) {
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
                                <MessageStoreDetailTable nodeData={nodeData} />
                            </Paper>
                        </>}
                        artifactType="message-stores" artifactName={artifactName} nodeId={nodeId} />
                </Grid>
            </Grid>
        </div>
    );
}

function MessageStoreDetailTable(props) {
    const nodeData = props.nodeData;
    const artifactName = nodeData.details.name

    return <Table>
        <TableRow>
            <TableCell>Message Store Name</TableCell>
            <TableCell>{artifactName}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>Message Count</TableCell>
            <TableCell>{nodeData.details.size === '-1' ? "Not Supported" : nodeData.details.size}</TableCell>
        </TableRow>
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
}));
