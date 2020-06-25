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
import MUIDataTable from "mui-datatables";

export default class LogConfigsPage extends Component {

    constructor(props) {
        super(props);
        this.loggers = null;
        this.state = {
            data: [],
            error: null
        };
    }

    componentDidMount() {
        this.retrieveAllLoggers();
    }

    retrieveAllLoggers() {
        const data = [];
        new ResourceAPI().getResourceList(`/logging`).then((response) => {
            this.loggers = response.data || [];
            this.loggers.forEach((element) => {
                const rowData = [];
                rowData.push(element.loggerName);
                rowData.push(element.componentName);
                rowData.push(element.level);
                data.push(rowData);
            });
            this.setState({data: data});
            this.setState({errorOccurred: false});
        }).catch((error) => {
            this.setState({error: error});
        });
    }

    renderLoggersList() {

        const columns = [
            {
                name: "Logger Name",
                options: {
                    sortDirection: 'asc'
                }
            },
            "Component Name",
            {
                name: "Level",
                options: {
                    customBodyRender: (logLevel, tableMeta, updateValue) => {
                        let logLevels = ['OFF', 'TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL'],
                            MakeItem = function (X) {
                                return <option>{X}</option>;
                            };
                        return (
                            <select
                                value={logLevel}
                                onChange={(newLevel) => this.handleLevelChange(newLevel, tableMeta.rowData[0])}>
                                {logLevels.map(MakeItem)}
                            </select>
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
                title={"Logging Configurations"}
                data={this.state.data}
                columns={columns}
                options={options}
            />
        );
    }

    handleLevelChange(newLevel, loggerName) {

        let level = newLevel.target.value;
        new ResourceAPI().updateLogLevel(loggerName, level).then((response) => {
            this.retrieveAllLoggers();
        }).catch((error) => {
            this.setState({error: error});
        });
    }

    render() {
        return (
            <ListViewParent
                data={this.renderLoggersList()}
                error={this.state.error}
            />
        );
    }
}
