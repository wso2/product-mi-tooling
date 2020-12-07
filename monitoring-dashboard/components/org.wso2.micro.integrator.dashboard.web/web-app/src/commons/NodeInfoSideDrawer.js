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
import { Button, Table, TableCell, TableRow } from '@material-ui/core';
import FileCopyIcon from '@material-ui/icons/FileCopy';
import {CopyToClipboard} from 'react-copy-to-clipboard';

export default function NodeInfoSideDrawer(props) {
    const { data } = props;
    console.log("data");
    console.log(data);
    var details = data.details;

    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Paper className={classes.sideDrawerHeading} square>
                        <Typography variant="h6" color="inherit" noWrap>
                            {data.id} Information
                        </Typography>
                    </Paper>
                    <Paper className={classes.paper}>
                        <Table>
                            <TableRow>
                                <TableCell>Server Name</TableCell>
                                <TableCell>{details.serverName}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Version</TableCell>
                                <TableCell>{details.version}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Micro Integrator Home</TableCell>
                                <TableCell>{details.miHome}
                                    <CopyToClipboard text={details.miHome} className={classes.clipboard}>
                                        <Button><FileCopyIcon/></Button>
                                    </CopyToClipboard>
                                </TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Java Home</TableCell>
                                <TableCell>{details.javaHome}
                                    <CopyToClipboard text={details.javaHome} className={classes.clipboard}>
                                        <Button><FileCopyIcon/></Button>
                                    </CopyToClipboard>
                                </TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Java Version</TableCell>
                                <TableCell>{details.javaVersion}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Java Vender</TableCell>
                                <TableCell>{details.javaVendor}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>OS Name</TableCell>
                                <TableCell>{details.os}</TableCell>
                            </TableRow>
                        </Table>
                    </Paper>
                </Grid>

            </Grid>
        </div>
    );
}

const useStyles = makeStyles((theme) => ({
    root: {
        flexGrow: 1,
    },
    paper: {
        padding: theme.spacing(2),
        color: theme.palette.text.secondary,
    },
    sideDrawerHeading: {
        padding: theme.spacing(1),
        height: '64px',
        backgroundColor: '#3f51b5',
        color: '#ffffff'
    },
    subTopic: {
        color: '#3f51b5'
    },
    horizontalLine : {
        backgroundColor : '#3f51b5',
        borderWidth: '0px',
        height: '1px'
    },
    clipboard: {
        color: '#3f51b5'
    }
}));
