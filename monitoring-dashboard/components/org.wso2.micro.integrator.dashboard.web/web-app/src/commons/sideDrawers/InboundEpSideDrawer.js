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
import Box from '@material-ui/core/Box';
import Grid from '@material-ui/core/Grid';
import { Table, TableCell, TableRow } from '@material-ui/core';
import HeadingSection from './commons/HeadingSection'
import TracingRow from './commons/TracingRow'
import SourceViewSection from './commons/SourceViewSection'
import Typography from '@material-ui/core/Typography';

export default function InboundEpSideDrawer(props) {
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
                        artifactType="inbound-endpoints" artifactName={artifactName} nodeId={nodeId}
                        designContent={<>
                            <Paper className={classes.paper} elevation={0} square>
                                <InboundEpDetailTable nodeData={nodeData} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
                            </Paper>
                            <Box pl={4}>
                                <Typography variant="h6" color="inherit" noWrap>
                                    Parameters
                                </Typography>
                                <Box pr={2}>
                                    <ParametersSection parameters={nodeData.details.parameters} />
                                </Box>
                            </Box>
                        </>}
                    />
                </Grid>
            </Grid>
        </div>
    );
}

function InboundEpDetailTable(props) {
    const { nodeData, retrieveUpdatedArtifact } = props;
    const artifactName = nodeData.details.name
    const pageId = "inbound-endpoints";

    return <Table>
        <TableRow>
            <TableCell>Inbound Endpoint Name</TableCell>
            <TableCell>{artifactName}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>Protocol</TableCell>
            <TableCell>{nodeData.details.protocol}</TableCell>
        </TableRow>
        <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
        <TableRow>
            <TableCell>Statistics</TableCell>
            <TableCell>{nodeData.details.stats}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>Sequence</TableCell>
            <TableCell>{nodeData.details.sequence}</TableCell>
        </TableRow>
        <TableRow>
            <TableCell>On Error</TableCell>
            <TableCell>{nodeData.details.error}</TableCell>
        </TableRow>
    </Table>
}

function ParametersSection(props) {
    const parameters = props.parameters;
    return <Table size="small" style={{width: '100%'}}>
        {parameters.map(parameter => <TableRow>
            <TableCell>{parameter.name}</TableCell>
            <TableCell>{parameter.value}</TableCell>
        </TableRow>)}
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
