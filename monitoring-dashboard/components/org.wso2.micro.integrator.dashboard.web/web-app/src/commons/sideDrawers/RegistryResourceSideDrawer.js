/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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
import { Table, TableCell, TableRow } from '@material-ui/core';
import HeadingSection from './commons/HeadingSection';
import TableHead from '@material-ui/core/TableHead';
import RegistrySourceViewSection from './commons/RegistrySourceViewSection'
import HTTPClient from '../../commons/../utils/HTTPClient';

export default function RegistryResourceSideDrawer(props) {
    const {groupId, data,registryPath } = props;
    const registryName = data.childName;
    const [properties, setProperties] = React.useState([]);

    const retrieveProperties = (query = '') => {
        HTTPClient.getRegistryProperty(groupId, registryPath).then(response => {
            setProperties(response.data)
        })
    };

    React.useEffect(() => {
        retrieveProperties(registryPath);
    },[registryPath])


    const classes = useStyles();
    return (
        <div className={classes.root}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <HeadingSection name={registryName} nodeId={registryPath} />
                    <RegistrySourceViewSection
                        designContent={<>
                            <Paper className={classes.paper} elevation={0} square>
                                <RegistryResourceDetailPage data={data} />
                            </Paper>
                            <Paper className={classes.paper} elevation={0} square>
                                <PropertiesSection properties={properties} />
                            </Paper>
                        </>}
                        registryPath={registryPath} data={data}/>
                </Grid>
            </Grid>
        </div>
    );
}

function RegistryResourceDetailPage(props) {
    const { data } = props;
    const registryName = data.childName;
    return <Table>
                <TableRow>
                    <TableCell>Registry Name</TableCell>
                    <TableCell>{registryName}</TableCell>
                </TableRow>
                <TableRow>
                    <TableCell>Media Type</TableCell>
                    <TableCell>{data.mediaType}</TableCell>
                </TableRow>
            </Table>
}

function PropertiesSection(props) {

    const properties = props.properties;
    const classes = useStyles();
    if (properties.length!==0){
        return <>
        <Typography variant="h6" color="inherit" noWrap>
            Properties
        </Typography>
        <Table>
            <TableHead className={classes.tableHead}>
              <TableRow>
                <TableCell>Property Name</TableCell>
                <TableCell>Property Value</TableCell>
              </TableRow>
            </TableHead>
            {properties.map((property) => 
                <TableRow>
                    <TableCell>{property.propertyName}</TableCell>
                    <TableCell>{property.propertyValue}</TableCell>
                </TableRow>
                )
            }       
        </Table>
    </>
    } else {
        return <>
            <Typography variant="h6" color="inherit" noWrap>
                Properties
            </Typography>
            <Typography variant="h8" color="inherit" noWrap>
                No properties found.
            </Typography>
        </>
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
    tableHead: {
        backgroundColor: '#E0E0E0',
    },
    clipboard: {
        color: '#3f51b5'
    }
}));
