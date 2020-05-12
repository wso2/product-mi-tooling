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

export default class LogFileListPage extends Component {

    constructor(props) {
        super(props);
        this.logFiles = null;
        this.state = {
            data: [],
            error: null
        };
    }

    /**
     * Retrieve LogFile details from the MI.
     */
    componentDidMount() {
        this.retrieveLogFiles();
    }

    retrieveLogFiles() {
        const data = [];

        new ResourceAPI().getResourceList(`/logs`).then((response) => {
            this.logFiles = response.data.list || [];

            this.logFiles.forEach((element) => {
                const rowData = [];
                rowData.push(element.FileName);
                rowData.push(element.Size);
                data.push(rowData);

            });
            this.setState({data: data});
        }).catch((error) => {
            this.setState({error:error});
        });
    }

    renderResourceList() {

        const columns = [
            {
                name: "Log File Name",
                options: {
                    sortDirection: 'asc',
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return (
                            <Link component="button" variant="body2" onClick={() => {
                                new ResourceAPI().getLogFileByName(tableMeta.rowData[0]).then((response) => {
                                    // Creating HTML link to download the file
                                    var element = document.createElement('a');
                                    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent
                                    (response.data));
                                    element.setAttribute('download', tableMeta.rowData[0]);
                                    element.style.display = 'none';
                                    document.body.appendChild(element);
                                    element.click();
                                    document.body.removeChild(element);
                                }).catch((error) => {
                                    this.setState({error:error});
                                });
                            }}>
                                {tableMeta.rowData[0]}
                            </Link>
                        );
                    }
                }
            }, "Size"];
        const options = {
            selectableRows: 'none',
            print: false,
            download: false,
        };

        return (
            <MUIDataTable
                title={"LOG FILES"}
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
