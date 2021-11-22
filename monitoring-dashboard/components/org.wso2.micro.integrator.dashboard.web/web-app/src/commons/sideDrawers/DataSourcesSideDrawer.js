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
import { makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Box from '@material-ui/core/Box';
import Grid from '@material-ui/core/Grid';
import { Table, TableCell, TableRow } from '@material-ui/core';
import HeadingSection from './commons/HeadingSection'
import SourceViewSection from './commons/SourceViewSection'
import Typography from '@material-ui/core/Typography';

export default function DataSourcesSideDrawer(props) {
    const { nodeData } = props;
    const nodeId = nodeData.nodeId;
    const artifactName = nodeData.details.name;
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <HeadingSection name={artifactName} nodeId={nodeId} />
                    <SourceViewSectionForDataSources nodeData={nodeData} classes={classes} />
                </Grid>
            </Grid>
        </div>
    );
}

function DataSourceDetailTable(props) {
    const nodeData = props.nodeData;
    return <Table>
        <TableRow>
            <TableCell>Type</TableCell>
            <TableCell>{nodeData.type}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>Description</TableCell>
            <TableCell>{nodeData.description}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>Driver</TableCell>
            <TableCell>{nodeData.driverClass}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>URL</TableCell>
            <TableCell>{nodeData.url}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>Username</TableCell>
            <TableCell>{nodeData.userName}</TableCell>
        </TableRow>
    </Table>
}

function SourceViewSectionForDataSources(props) {
    const nodeData = props.nodeData;
    const nodeId = nodeData.nodeId;
    const artifactName = nodeData.details.name;
    const classes = props.classes;
    return <SourceViewSection
        artifactType="data-sources" artifactName={artifactName} nodeId={nodeId}
        designContent={<>
            <Paper className={classes.paper} elevation={0} square>
                <DataSourceDetailTable nodeData={nodeData.details} />
            </Paper>
            <Box pl={4}>
                <Typography variant="h6" color="inherit" noWrap>
                    Parameters
                </Typography>
                <Box pr={2}>
                    <ParametersSection parameters={nodeData.details.configurationParameters} classes={classes} />
                </Box>
            </Box>
        </>}
    />
}

function ParametersSection(props) {
    const parameters = props.parameters;
    const classes = props.classes;
    return <Table size="small" className={classes.parameterTable}>
        {Object.entries(parameters).map(([key, value], index) =>
            <TableRow key={index}>
                <TableCell>{key}</TableCell>
                <TableCell>{String(value)}</TableCell>
            </TableRow>
        )}
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
