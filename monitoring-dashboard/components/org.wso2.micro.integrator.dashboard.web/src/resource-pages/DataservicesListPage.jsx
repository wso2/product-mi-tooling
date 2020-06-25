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
import ListViewParent from '../common/ListViewParent';
import ResourceAPI from '../utils/apis/ResourceAPI';
import Link from '@material-ui/core/Link';

import MUIDataTable from "mui-datatables";

export default class DataservicesListPage extends Component {

    constructor(props) {
        super(props);
        this.dss = null;
        this.state = {
            data: [],
            error: null
        };
    }

    /**
     * Retrieve data services from the MI.
     */
    componentDidMount() {
       this.retrieveDataservices();
    }

    retrieveDataservices() {
        const data = [];

        new ResourceAPI().getResourceList(`/data-services`).then((response) => {
            this.dss = response.data.list || [];

            this.dss.forEach((element) => {
                const rowData = [];
                rowData.push(element.name);
                rowData.push(element.wsdl1_1);
                rowData.push(element.wsdl2_0);
                data.push(rowData);
            });
            this.setState({data: data});
        }).catch((error) => {
            this.setState({error: error});
        });
    }

    renderResourceList() {

        const columns = [
            {
                name: "Data Service",
                options: {
                    sortDirection: 'asc',
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return (
                            <Link component="button" variant="body2" onClick={() => {
                                this.props.history.push(`/dataservice/explore?name=${tableMeta.rowData[0]}`)
                            }}>
                                {tableMeta.rowData[0]}
                            </Link>
                        );
                    }
                }
            }, {
                name: "WSDL 1.1",
                options: {
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return (
                            <Link component="button" variant="body2" onClick={() => {
                                window.location = tableMeta.rowData[1];
                            }}>
                                {tableMeta.rowData[1]}
                            </Link>
                        );
                    }
                }
            }, {
                name: "WSDL 2.0",
                options: {
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return (
                            <Link component="button" variant="body2" onClick={() => {
                                window.location = tableMeta.rowData[2];
                            }}>
                                {tableMeta.rowData[2]}
                            </Link>
                        );
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
                title={"Data Services"}
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
