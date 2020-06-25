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
 */


import React, {Component} from 'react';
import ResourceExplorerParent from '../common/ResourceExplorerParent';
import SourceViewComponent from '../common/SourceViewComponent';
import ResourceAPI from '../utils/apis/ResourceAPI';
import {Link} from "react-router-dom";
import Box from '@material-ui/core/Box';
import queryString from 'query-string'
import TableHeaderBox from '../common/TableHeaderBox';
import DataUtils from "../utils/DataUtils";
import TableCell from '@material-ui/core/TableCell';
import TableRow from '@material-ui/core/TableRow';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

export default class DataserviceDetailsPage extends Component {

    constructor(props) {
        super(props);
        this.state = {
            response: {},
            error: null,
        };
    }

    /**
     * Retrieve DSS details from the MI.
     */
    componentDidMount() {
        let url = this.props.location.search;
        const values = queryString.parse(url) || {};
        this.retrieveDataServiceDetails(values.name);
    }

    retrieveDataServiceDetails(name) {
        new ResourceAPI().getDataServiceByName(name).then((response) => {
            this.setState(
                {
                    response: response.data,
                });
        }).catch((error) => {
            this.setState({error: error})
        });
    }

    renderDSSContent() {
        return (
            <Box>
                <Box pb={5}>
                    <TableHeaderBox title="Data Service Summary Details"/>
                    {this.renderGeneralDetails()}
                </Box>
                <Box pb={5}>
                    <TableHeaderBox title="Data Sources"/>
                    {this.renderDataSourceDetails()}
                </Box>
                <Box pb={5}>
                    <TableHeaderBox title="Queries"/>
                    {this.renderQueryDetails()}
                </Box>
                <Box pb={5}>
                    <TableHeaderBox title="Resources"/>
                    {this.renderResourceDetails()}
                </Box>
                <Box pb={5}>
                    <TableHeaderBox title="Operations"/>
                    {this.renderOperationDetails()}
                </Box>
                <SourceViewComponent config={this.state.response.configuration}/>
            </Box>
        );
    }

    renderGeneralDetails() {
        const generalDetails = [];
        const dss = this.state.response;
        generalDetails.push(DataUtils.createData("Name", dss.serviceName));
        generalDetails.push(DataUtils.createData("WSDL 1.1", dss.wsdl1_1));
        generalDetails.push(DataUtils.createData("WSDL 2.0", dss.wsdl2_0));
        return (
            <Table size="small">
                <TableBody>
                    {this.renderRowsFromData(generalDetails)}
                </TableBody>
            </Table>);
    }

    renderDataSourceDetails() {

        const dataSources = this.state.response.dataSources;

        if (dataSources !== undefined) {
            return (
                dataSources.map(dataSource => (
                    <ExpansionPanel>
                        <ExpansionPanelSummary
                            expandIcon={<ExpandMoreIcon/>}
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
    }

    renderQueryDetails() {
        const queries = this.state.response.queries;

        if (queries !== undefined) {

            return (
                queries.map(query => (
                    <ExpansionPanel>
                        <ExpansionPanelSummary
                            expandIcon={<ExpandMoreIcon/>}
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
    }

    renderResourceDetails() {
        const resources = this.state.response.resources;
        if (resources !== undefined) {

            return (
                resources.map(resource => (
                    <ExpansionPanel>
                        <ExpansionPanelSummary
                            expandIcon={<ExpandMoreIcon/>}
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
    }

    renderOperationDetails() {
        const operations = this.state.response.operations;
        if (operations !== undefined) {

            return (
                operations.map(operation => (
                    <ExpansionPanel>
                        <ExpansionPanelSummary
                            expandIcon={<ExpandMoreIcon/>}
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
    }

    renderRowsFromData(data) {
        return (
            data.map(row => (
                <TableRow>
                    <TableCell>{row.name}</TableCell>
                    <TableCell>{row.value}</TableCell>
                </TableRow>
            ))
        );
    }

    renderBreadCrumbs() {
        return (
            <div style={{display: "flex"}}>
                <Box color="inherit" component={Link} to="/dataservice">Data Services</Box>
                <Box color="textPrimary">&nbsp;>&nbsp;</Box>
                <Box color="textPrimary"> {this.state.response.serviceName}</Box>
            </div>
        );
    }

    render() {
        return (
            <ResourceExplorerParent
                title={this.renderBreadCrumbs()}
                content={this.renderDSSContent()}
                error={this.state.error}/>
        );
    }
}
