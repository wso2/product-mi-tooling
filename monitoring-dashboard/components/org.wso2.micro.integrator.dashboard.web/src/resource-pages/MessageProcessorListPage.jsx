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
import Switch from "react-switch";

import MUIDataTable from "mui-datatables";

export default class MessageProcessorListPage extends Component {

    constructor(props) {
        super(props);
        this.messageProcessors = null;
        this.state = {
            data: [],
            error: null
        };
    }

    /**
     * Retrieve message processors from the MI.
     */
    componentDidMount() {
        this.retrieveMessageProcessors();
    }

    handleMessageProcessorStateChange(processor, currentState) {
        new ResourceAPI().setMessageProcessorState(processor, !currentState).then((response) => {
            this.retrieveMessageProcessors();
        }).catch((error) => {
            this.setState({error:error});
        });
    }

    retrieveMessageProcessors() {
        const data = [];

        new ResourceAPI().getResourceList(`/message-processors`).then((response) => {
            this.messageProcessors = response.data.list || [];

            this.messageProcessors.forEach((element) => {
                const rowData = [];
                rowData.push(element.name);
                rowData.push(element.type);
                rowData.push(element.status);
                data.push(rowData);
            });
            this.setState({data: data});
        }).catch((error) => {
            this.setState({error:error});
        });
    }

    renderResourceList() {

        const columns = [{
            name: "Message Processor Name",
            options: {
                sortDirection: 'asc',
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <Link component="button" variant="body2" onClick={() => {
                            this.props.history.push(`/message-processor/explore?name=${tableMeta.rowData[0]}`)
                        }}>
                            {tableMeta.rowData[0]}
                        </Link>
                    );
                }
            }
        }, "Type", {
            name: "State",
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    var isActive = false;
                  if ("active" === tableMeta.rowData[2]) {
                      isActive = true;
                  } else {
                      isActive = false
                  }
                  return(<Switch height={25} width={50} onChange={e=>this.handleMessageProcessorStateChange(tableMeta.rowData[0], isActive)} checked={isActive}/>);
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
                title={"MESSAGE PROCESSORS"}
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
