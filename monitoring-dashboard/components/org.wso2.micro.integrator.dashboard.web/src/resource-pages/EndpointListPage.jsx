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
import ListViewParent from '../common/ListViewParent';
import ResourceAPI from '../utils/apis/ResourceAPI';
import Link from '@material-ui/core/Link';

import MUIDataTable from "mui-datatables";
import Switch from "react-switch";

export default class EndpointListPage extends Component {

    constructor(props) {
        super(props);
        this.endpoints = null;
        this.state = {
            data: [],
            error: null
        };
    }

    /**
     * Retrieve endpoints from the MI.
     */
    componentDidMount() {
        this.retrieveEndpoints();
    }

    retrieveEndpoints() {
        const data = [];

        new ResourceAPI().getResourceList(`/endpoints`).then((response) => {
            this.endpoints = response.data.list || [];

            this.endpoints.forEach((element) => {
                const rowData = [];
                rowData.push(element.name);
                rowData.push(element.type);
                rowData.push(element.isActive);
                data.push(rowData);
            });
            this.setState({data: data});
            this.setState({errorOccurred: false});
        }).catch((error) => {
            this.setState({error:error});
        });
    }

    handleEndpointStateChange(endpoint, currentState) {
        new ResourceAPI().setEndpointState(endpoint, !currentState).then((response) => {
            this.retrieveEndpoints();
        }).catch((error) => {
            this.setState({error:error});
        });
    }

    renderResourceList() {

        const columns = [{
            name: "Endpoint Name",
            options: {
                sortDirection: 'asc',
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <Link component="button" variant="body2" onClick={() => {
                            this.props.history.push(`/endpoint/explore?name=${tableMeta.rowData[0]}`)
                        }}>
                            {tableMeta.rowData[0]}
                        </Link>
                    );
                }
            }
        }, "Type",
            {
                name: "State",
                options: {
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return(<Switch height={25} width={50} onChange={e=>this.handleEndpointStateChange(tableMeta.rowData[0], tableMeta.rowData[2])} checked={tableMeta.rowData[2]}/>);
                    }
                }
            }];
        const options = {
            selectableRows: 'none',
            print: false,
            download: false,
        };

        return (
            <MUIDataTable
                title={"ENDPOINTS"}
                data={this.state.data}
                columns={columns}
                options={options}
            />
        );
    }

    render() {
        return (
            <ListViewParent
                data={this.renderResourceList()}
                error={this.state.error}
            />
        );
    }
}
