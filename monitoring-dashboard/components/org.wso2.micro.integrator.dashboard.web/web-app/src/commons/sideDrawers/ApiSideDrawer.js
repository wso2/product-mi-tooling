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
import Box from '@material-ui/core/Box';
import Grid from '@material-ui/core/Grid';
import { Table, TableCell, TableBody, TableRow } from '@material-ui/core';
import HeadingSection from './commons/HeadingSection'
import CopyToClipboardCell from './commons/CopyToClipBoardCell'
import TracingRow from './commons/TracingRow'
import SourceViewSection from './commons/SourceViewSection'
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

export default function ApiSideDrawer(props) {
    const { nodeData, retrieveUpdatedArtifact } = props;
    const nodeId = nodeData.nodeId;
    const artifactName = nodeData.details.name;
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <HeadingSection name={artifactName} nodeId={nodeId} />
                    <SourceViewSection
                        artifactType="apis"
                        artifactName={artifactName}
                        nodeId={nodeId}
                        designContent={<>
                            <Paper className={classes.paper} elevation={0} square>
                                <ApiDetailTable nodeData={nodeData} retrieveUpdatedArtifact = {retrieveUpdatedArtifact}/>
                            </Paper>
                            <Box pl={2}>
                                <Typography variant="h6" color="inherit" noWrap>
                                    Resources
                                </Typography>
                                <Box pr={2}>
                                    <ResourcesDetailTable resources={nodeData.details.resources} />
                                </Box>
                            </Box>
                        </>}
                    />
                </Grid>

            </Grid>
        </div>
    );
}

function ApiDetailTable(props) {
    const { nodeData, retrieveUpdatedArtifact } = props;
    const artifactName = nodeData.details.name
    const pageId = "apis";

    return <Table>
        <TableRow>
            <TableCell>API Name</TableCell>
            <TableCell>{artifactName}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>Context</TableCell>
            <TableCell>{nodeData.details.context}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>URL</TableCell>
            <CopyToClipboardCell text={nodeData.details.url} />
        </TableRow>
        <TableRow>
            <TableCell>Statistics</TableCell>
            <TableCell>{nodeData.details.stats}</TableCell>
        </TableRow>
        <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
    </Table>
}

function ResourcesDetailTable(props) {
    const resources = props.resources;
    return (resources.map(resource => (
        <ExpansionPanel>
            <ExpansionPanelSummary
                expandIcon={<ExpandMoreIcon />}
                aria-controls="panel1a-content"
                id="panel1a-header">
                {resource.url}
            </ExpansionPanelSummary>
            <ExpansionPanelDetails>
                <Table size="small">
                    <TableBody>
                        <TableRow>
                            <TableCell>Methods</TableCell>
                            <TableCell>{resource.methods.toString()}</TableCell>
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
