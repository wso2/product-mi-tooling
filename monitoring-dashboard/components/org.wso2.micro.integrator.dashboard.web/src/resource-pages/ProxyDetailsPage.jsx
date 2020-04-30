/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import ResourceAPI from '../utils/apis/ResourceAPI';
import queryString from 'query-string'
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableRow from '@material-ui/core/TableRow';
import TableHeaderBox from '../common/TableHeaderBox';
import SourceViewComponent from '../common/SourceViewComponent';
import { Link } from "react-router-dom";
import Switch from "react-switch";

import Box from '@material-ui/core/Box';

export default class ProxyDetailsPage extends Component {

    constructor(props) {
        super(props);
        this.state = {
            config: " ",
            tableData: [],
            endpoints: [],
            response: {},
            errorOccurred: false
        };
    }

    /**
     * Retrieve proxy details from the MI.
     */
    componentDidMount() {
        let url = this.props.location.search;
        const values = queryString.parse(url) || {};
        this.retrieveProxyInfo(values.name);
    }

    createData(name, value) {
        return {name, value};
    }

    retrieveProxyInfo(name) {
        const tableData = [];
        new ResourceAPI().getProxyServiceByName(name).then((response) => {

            tableData.push(this.createData("Service Name", response.data.name));
            tableData.push(this.createData("Statistics", response.data.stats));
            tableData.push(this.createData("Tracing", response.data.tracing));
            tableData.push(this.createData("Service Status", response.data.isRunning));

            const endpoints = response.data.eprs || []

            this.setState(
                {
                    tableData: tableData,
                    endpoints: endpoints,
                    response: response.data,
                });
            this.setState({errorOccurred: false});
        }).catch((error) => {
            if (error.request) {
                // The request was made but no response was received
                this.setState({errorOccurred: true}, function () {
                    // callback function to ensure state is set immediately
                });
            }
        });
    }

    handleProxyStateChange(proxy, currentState) {
        new ResourceAPI().setProxyState(proxy, !currentState).then((response) => {
            this.retrieveProxyInfo(proxy);
        }).catch((error) => {
            if (error.request) {
                // The request was made but no response was received
                this.setState({errorOccurred: true}, function () {
                    // callback function to ensure state is set immediately
                });
            }
        });
    }

    renderProxyDetails() {
        return (
            <Box>
                <Box pb={5}>
                    <TableHeaderBox title="Proxy Details"/>
                    <Table size="small">
                        <TableBody>
                            {
                                this.state.tableData.map(row => (

                                    row.name == "Service Status" ?
                                        (
                                            <TableRow>
                                                <TableCell>{row.name}</TableCell>
                                                <TableCell>
                                                    <Switch height={18} width={36}
                                                                   onChange={e => this.handleProxyStateChange(this.state.response.name, row.value)}
                                                                   checked={row.value}/>
                                                </TableCell>
                                            </TableRow>
                                        ) :
                                        (
                                            <TableRow>
                                                <TableCell>{row.name}</TableCell>
                                                <TableCell>{row.value}</TableCell>
                                            </TableRow>
                                        )
                                ))
                            }
                        </TableBody>
                    </Table>
                </Box>
                <Box pb={5}>
                    <TableHeaderBox title="Endpoints"/>
                    <Table size="small">
                        <TableBody>
                            {
                                this.state.endpoints.map(row => (
                                    <TableRow>
                                        <TableCell>{row}</TableCell>
                                    </TableRow>
                                ))
                            }
                        </TableBody>
                    </Table>
                </Box>
                <SourceViewComponent config={this.state.response.configuration}/>
            </Box>
        );
    }

    renderBreadCrumbs() {
        return (
            <div style={{display:"flex"}}>
                <Box color="inherit" component={Link} to="/proxy">Proxy Services</Box>
                <Box color="textPrimary">&nbsp;>&nbsp;</Box>
                <Box color="textPrimary"> {this.state.response.name}</Box>
            </div>
        );
    }

    render() {
        return (
            <ResourceExplorerParent title={this.renderBreadCrumbs()} content={this.renderProxyDetails()}
                connectionError={this.state.errorOccurred}/>
        );
    }
}