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
import SourceViewSection from './commons/SourceViewSection'
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

export default function GenericDrawer(props) {
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
                        artifactType="service"
                        artifactName={artifactName}
                        nodeId={nodeId}
                        designContent={<>
                            <Paper className={classes.paper} elevation={0} square>
                                <DetailTable nodeData={nodeData} retrieveUpdatedArtifact = {retrieveUpdatedArtifact}/>
                            </Paper>
                            {Object.entries(nodeData.details)
                                   .filter(([_, value]) => typeof value === 'object')
                                   .map(([key, value]) =>
                                        <Box pl={2}>
                                            <Typography variant="h6" color="inherit" noWrap>
                                                {fromCamelCaseToWhiteSpaced(key)}
                                            </Typography>
                                            <Box pr={2}>
                                                <GenericDetailTable data={value} />
                                            </Box>
                                        </Box>
                                    )
                            }
                        </>}
                    />
                </Grid>

            </Grid>
        </div>
    );
}

function DetailTable(props) {
    const { nodeData } = props;
    return <Table>
        {Object.entries(nodeData.details)
               .filter(([_, value]) => typeof value === 'string')
               .map(([key, value]) =>
                    <TableRow key={key}>
                        <TableCell>{fromCamelCaseToWhiteSpaced(key)}</TableCell>
                        <TableCell>{value}</TableCell>
                    </TableRow>
                )
        }
    </Table>
}

function GenericDetailTable(props) {
    let { data } = props;
    if (!Array.isArray(data)) {
        data = [data];
    }
    return (data.map(entry => {
        const title = entry.name || entry.url;
        return <ExpansionPanel>
            {title && <ExpansionPanelSummary
                expandIcon={<ExpandMoreIcon />}
                aria-controls="panel1a-content"
                id="panel1a-header">
                {title}
            </ExpansionPanelSummary>}
            <ExpansionPanelDetails>
                <Table size="small">
                    <TableBody>
                        {Object.entries(entry)
                               .map((keyVal) => Array.isArray(keyVal[1]) ? [keyVal[0], keyVal[1].join(', ')] : keyVal)
                               .map(([key, value]) => typeof value === 'number' ? [key, value.toString()] : [key, value])
                               .filter(([_, value]) => typeof value === 'string')
                               .map(([key, value]) =>
                                    <TableRow key={key}>
                                        <TableCell>{fromCamelCaseToWhiteSpaced(key)}</TableCell>
                                        <TableCell>{value}</TableCell>
                                    </TableRow>
                                )
                        }
                    </TableBody>
                </Table>
            </ExpansionPanelDetails>
        </ExpansionPanel>}
    ));
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

function fromCamelCaseToWhiteSpaced(camelCase) {
    return camelCase.replace(/([a-z])([A-Z])/g, '$1 $2')
                    .replace(/([A-Z])([A-Z][a-z])/g, '$1 $2')
                    .replace(/^./, str => str.toUpperCase());
}
