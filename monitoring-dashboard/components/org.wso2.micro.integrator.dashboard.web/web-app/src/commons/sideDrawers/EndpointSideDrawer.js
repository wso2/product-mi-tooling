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
import CopyToClipboardCell from './commons/CopyToClipBoardCell'
import TracingRow from './commons/TracingRow'
import SourceViewSection from './commons/SourceViewSection'

export default function EndpointSideDrawer(props) {
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
                        artifactType="endpoints"
                        artifactName={artifactName}
                        nodeId={nodeId}
                        designContent={<>
                            <Paper className={classes.paper} elevation={0} square>
                                <EndpointsDetailTable nodeData={nodeData} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
                            </Paper>
                        </>}
                    />
                </Grid>
            </Grid>
        </div>
    );
}

function EndpointsDetailTable(props) {
    const { nodeData, retrieveUpdatedArtifact } = props;
    const artifactName = nodeData.details.name
    const artifactType = nodeData.details.type
    const pageId = "endpoints";

    switch (artifactType) {
        case 'HTTP Endpoint':
            return <Table>
                <TableRow>
                    <TableCell>Endpoint Name</TableCell>
                    <TableCell>{artifactName}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Type</TableCell>
                    <TableCell>{artifactType}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Method</TableCell>
                    <TableCell>{nodeData.details.method}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>URI Template</TableCell>
                    <CopyToClipboardCell text={nodeData.details.uriTemplate} />
                </TableRow>
                <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
            </Table>
        case 'Address Endpoint':
            return <Table>
                <TableRow>
                    <TableCell>Name</TableCell>
                    <TableCell>{artifactName}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Type</TableCell>
                    <TableCell>{artifactType}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Address</TableCell>
                    <CopyToClipboardCell text={nodeData.details.address} />
                </TableRow>
                <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
            </Table>
        case 'WSDL Endpoint':
            return <Table>
                <TableRow>
                    <TableCell>Name</TableCell>
                    <TableCell>{artifactName}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Type</TableCell>
                    <TableCell>{artifactType}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>WSDL URI</TableCell>
                    <CopyToClipboardCell text={nodeData.details.wsdlUri} />
                </TableRow>
                <TableRow>
                    <TableCell>Service</TableCell>
                    <TableCell>{nodeData.details.serviceName}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Port</TableCell>
                    <TableCell>{nodeData.details.portName}</TableCell>
                </TableRow>
                <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
            </Table>
        case 'Template Endpoint':
            return <Table>
                <TableRow>
                    <TableCell>Name</TableCell>
                    <TableCell>{artifactName}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Type</TableCell>
                    <TableCell>{artifactType}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Template</TableCell>
                    <CopyToClipboardCell text={nodeData.details.template} />
                </TableRow>
                <TableRow>
                    <TableCell>URI</TableCell>
                    <TableCell>{nodeData.details.parameters.uri}</TableCell>
                </TableRow>
                <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
            </Table>
        case 'Indirect Endpoint':
            return <Table>
                <TableRow>
                    <TableCell>Name</TableCell>
                    <TableCell>{artifactName}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Type</TableCell>
                    <TableCell>{artifactType}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Key</TableCell>
                    <CopyToClipboardCell text={nodeData.details.key} />
                </TableRow>
                <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
            </Table>
        default:
            return <Table>
                <TableRow>
                    <TableCell>Name</TableCell>
                    <TableCell>{artifactName}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Type</TableCell>
                    <TableCell>{artifactType}</TableCell>
                </TableRow>
                <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing} retrieveUpdatedArtifact={retrieveUpdatedArtifact}/>
            </Table>
    }
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
