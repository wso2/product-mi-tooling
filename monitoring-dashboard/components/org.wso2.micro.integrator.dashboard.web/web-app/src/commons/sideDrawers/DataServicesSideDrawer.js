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
import Box from '@material-ui/core/Box';
import { Table, TableCell, TableRow, TableBody } from '@material-ui/core';
import HeadingSection from './commons/HeadingSection'
import SourceViewSection from './commons/SourceViewSection'
import Typography from '@material-ui/core/Typography';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

export default function DataServicesSideDrawer(props) {
    var nodeData = props.nodeData;
    const artifactName = nodeData.details.serviceName;
    const nodeId = nodeData.nodeId;
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <HeadingSection name={artifactName} nodeId={nodeId} />
                    <SourceViewSection
                        artifactType="data-services"
                        artifactName={artifactName}
                        nodeId={nodeId}
                        designContent={<>
                            <Paper className={classes.paper} elevation={0} square>
                                <DataServicesDetailTable nodeData={nodeData.details} />
                            </Paper>
                            <Box pl={4}>
                                <DataSourcesSection dataSources={nodeData.details.dataSources} />
                                <QueriesSection queries={nodeData.details.queries} />
                                <ResourcesSection resources={nodeData.details.resources} />
                                <OperationsSection operations={nodeData.details.operations} />
                            </Box>
                        </>}
                    />
                </Grid>
            </Grid>
        </div>
    );
}

function DataServicesDetailTable(props) {
    const nodeData = props.nodeData;
    const wsdl1_1 = nodeData.wsdl1_1;
    const swagger_url = nodeData.swagger_url;
    return <Table>
        <TableBody>
            <TableRow>
                <TableCell>Data Service Name</TableCell>
                <TableCell>{nodeData.serviceName}</TableCell>
            </TableRow>
            <TableRow>
                <TableCell>WSDL 1.1</TableCell>
                <TableCell><a href={wsdl1_1}>{wsdl1_1}</a></TableCell>
            </TableRow>
            <TableRow>
                <TableCell>Swagger URL</TableCell>
                <TableCell><a href={swagger_url}>{swagger_url}</a></TableCell>
            </TableRow>
        </TableBody>
    </Table>
}

function DataSourcesSection(props) {
    const classes = useStyles();
    return <Grid item xs={12}>
        <Paper className={classes.paper} square>
            <Typography variant="h6" color="inherit" noWrap className={classes.subTopic}>
                Data Sources
                    </Typography>
            <hr className={classes.horizontalLine}></hr>
        </Paper>
        <DataSourcesDetailTable dataSources={props.dataSources} />
    </Grid>
}

function DataSourcesDetailTable(props) {
    const dataSources = props.dataSources;
    return (dataSources.map(dataSource => (
        <ExpansionPanel>
            <ExpansionPanelSummary
                expandIcon={<ExpandMoreIcon />}
                aria-controls="panel1a-content"
                id="panel1a-header">
                {dataSource.dataSourceId}
            </ExpansionPanelSummary>
            <ExpansionPanelDetails>
                <Table size="small">
                    <TableBody>
                        <TableRow>
                            <TableCell>Data Source ID</TableCell>
                            <TableCell>{dataSource.dataSourceId}</TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>Data Source Type</TableCell>
                            <TableCell>{dataSource.dataSourceType}</TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </ExpansionPanelDetails>
        </ExpansionPanel>
    ))
    );
}

function QueriesSection(props) {
    return <>
        <Typography variant="h6" color="inherit" noWrap>
            Queries
            </Typography>
        <Box pr={2}>
            <QueriesDetailTable queries={props.queries} />
        </Box>
    </>
}

function QueriesDetailTable(props) {
    const queries = props.queries;

    return (
        queries.map(query => (
            <ExpansionPanel>
                <ExpansionPanelSummary
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls="panel1a-content"
                    id="panel1a-header">
                    {query.id}
                </ExpansionPanelSummary>
                <ExpansionPanelDetails>
                    <Table size="small">
                        <TableBody>
                            <TableRow>
                                <TableCell>Query ID</TableCell>
                                <TableCell>{query.id}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Data Source</TableCell>
                                <TableCell>{query.dataSourceId}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Namespace</TableCell>
                                <TableCell>{query.namespace}</TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </ExpansionPanelDetails>
            </ExpansionPanel>
        ))
    );
}

function ResourcesSection(props) {
    return <>
        <Typography variant="h6" color="inherit" noWrap>
            Resources
            </Typography>
        <Box pr={2}>
            <ResourcesDetailTable resources={props.resources} />
        </Box>
    </>
}

function ResourcesDetailTable(props) {
    const resources = props.resources;

    return (
        resources.map(resource => (
            <ExpansionPanel>
                <ExpansionPanelSummary
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls="panel1a-content"
                    id="panel1a-header">
                    {resource.resourcePath}
                </ExpansionPanelSummary>
                <ExpansionPanelDetails>
                    <Table size="small">
                        <TableBody>
                            <TableRow>
                                <TableCell>Resource Path</TableCell>
                                <TableCell>{resource.resourcePath}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Resource Method</TableCell>
                                <TableCell>{resource.resourceMethod}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Query ID</TableCell>
                                <TableCell>{resource.resourceQuery}</TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </ExpansionPanelDetails>
            </ExpansionPanel>
        ))
    );
}

function OperationsSection(props) {
    const classes = useStyles();
    return <Grid item xs={12}>
        <Paper className={classes.paper} square>
            <Typography variant="h6" color="inherit" noWrap className={classes.subTopic}>
                Operations
                    </Typography>
            <hr className={classes.horizontalLine}></hr>
        </Paper>
        <OperationsDetailTable operations={props.operations} />
    </Grid>
}

function OperationsDetailTable(props) {
    const operations = props.operations;

    return (
        operations.map(operation => (
            <ExpansionPanel>
                <ExpansionPanelSummary
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls="panel1a-content"
                    id="panel1a-header">
                    {operation.operationName}
                </ExpansionPanelSummary>
                <ExpansionPanelDetails>
                    <Table size="small">
                        <TableBody>
                            <TableRow>
                                <TableCell>Operation Name</TableCell>
                                <TableCell>{operation.operationName}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Query ID</TableCell>
                                <TableCell>{operation.queryName}</TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </ExpansionPanelDetails>
            </ExpansionPanel>
        ))
    );
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
