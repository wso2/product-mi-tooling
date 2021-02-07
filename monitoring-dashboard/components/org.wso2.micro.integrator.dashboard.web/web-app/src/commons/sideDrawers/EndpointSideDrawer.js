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
                            <EndpointsDetailTable nodeData={nodeData}/>
                    </Paper>
                </Grid>
                <SourceViewSection artifactType="endpoints" artifactName={artifactName} nodeId={nodeId}/>
            </Grid>
        </div>
    );
}

function EndpointsDetailTable(props) {
    const nodeData = props.nodeData;
    const artifactName=nodeData.details.name
    const artifactType=nodeData.details.type
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
                            <CopyToClipboardCell text={nodeData.details.uriTemplate}/>
                        </TableRow>
                        <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing}/>
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
                        <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing}/>
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
                        <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing}/>
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
                        <TracingRow pageId={pageId} artifactName={artifactName} nodeId={nodeData.nodeId} tracing={nodeData.details.tracing}/>
                    </Table>    
    }
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
