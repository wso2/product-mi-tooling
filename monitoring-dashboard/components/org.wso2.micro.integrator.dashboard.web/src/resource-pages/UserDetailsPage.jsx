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
import ResourceExplorerParent from "../common/ResourceExplorerParent";
import queryString from 'query-string';
import ResourceAPI from "../utils/apis/ResourceAPI";
import DataUtils from "../utils/DataUtils";
import {Link} from "react-router-dom";
import Box from "@material-ui/core/es/Box/Box";
import TableHeaderBox from "../common/TableHeaderBox";
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableRow from '@material-ui/core/TableRow';

export default class Users extends Component {

    constructor(props) {
        super(props);
        this.state = {
            metaData: [],
            roles:[],
            response: {},
            error: null
        };
    }

    componentDidMount() {
        let url = this.props.location.search;
        const values = queryString.parse(url) || {};
        this.retrieveUserInfo(values.name);
    }

    retrieveUserInfo(user) {
        const metaData = [];
        new ResourceAPI().getUserById(user).then((response) => {
            metaData.push(DataUtils.createData("User ID", response.data.userId));
            metaData.push(DataUtils.createData("Is Admin", response.data.isAdmin.toString()));
           console.log(response.data);
            this.setState(
                {
                    metaData: metaData,
                    roles: response.data.roles,
                    response: response.data,
                });
        }).catch((error) => {
            this.setState({error:error});
        });
    }

    renderUserDetails() {
        return(
            <Box>
                <Box pb={5}>
                    <TableHeaderBox title="User Details"/>
                    <Table size="small">
                        <TableBody>
                            {
                                this.state.metaData.map(row => (
                                    <TableRow>
                                        <TableCell>{row.name}</TableCell>
                                        <TableCell>{row.value}</TableCell>
                                    </TableRow>
                                ))
                            }
                        </TableBody>
                    </Table>
                </Box>
                <Box pb={5}>
                    <TableHeaderBox title="Roles"/>
                    <Table size="small">
                        <TableBody>
                            {
                                this.state.roles.map(role => (
                                    <TableRow>
                                        <TableCell>{role}</TableCell>
                                    </TableRow>
                                ))
                            }
                        </TableBody>
                    </Table>
                </Box>
            </Box>
        );
    }

    renderBreadCrumbs() {
        return (
            <div style={{display:"flex"}}>
                <Box color="inherit" component={Link} to="/users">Users</Box>
                <Box color="textPrimary">&nbsp;>&nbsp;</Box>
                <Box color="textPrimary"> {this.state.response.userId}</Box>
            </div>
        );
    }

    render() {

        return(<ResourceExplorerParent
            title={this.renderBreadCrumbs()}
            content={this.renderUserDetails()}
            error={this.state.error}/>);
    }
}
