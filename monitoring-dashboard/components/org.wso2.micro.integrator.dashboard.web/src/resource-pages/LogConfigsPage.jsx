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
import ResourceAPI from '../utils/apis/ResourceAPI';
import MUIDataTable from "mui-datatables";
import ResourceExplorerParent from "../common/ResourceExplorerParent";
import Tabs from 'react-bootstrap/Tabs'
import Tab from 'react-bootstrap/Tabs'
import Form from 'react-bootstrap/Form'
import Row from "react-bootstrap/Row";
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button'
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

const styles = {
    form: {
        paddingTop: "10px"
    },
};

export default class LogConfigsPage extends Component {

    constructor(props) {
        super(props);
        this.loggers = null;
        this.state = {
            data: [],
            error: null,
            key: 'list',
            loggerName: '',
            loggerClass: '',
            loggerLevel: 'OFF',
        };

        this.handleUserInput = this.handleUserInput.bind(this);
        this.handleFormSubmission = this.handleFormSubmission.bind(this);
        this.handleDialogClose = this.handleDialogClose.bind(this);
    }

    componentDidMount() {
        this.retrieveAllLoggers();
    }

    handleFormSubmission(event) {

        const {loggerName, loggerClass, loggerLevel} = this.state;
        new ResourceAPI().addLogger(loggerName, loggerClass, loggerLevel).then((response) => {
            this.setState({dialogMessage: 'Logger added successfully.', dialogOpen: true, loggerName:'', loggerClass:''});
            this.retrieveAllLoggers();
        }).catch((error) => {
            this.handleErrorResponses(error);
        });
        event.preventDefault();
    }

    isSubmissionInvalid() {
        const {loggerName, loggerClass, loggerLevel} = this.state;
        if (loggerName === '' || loggerClass === '' || loggerLevel === '') {
            return true;
        }
        return false;
    }

    handleDialogClose() {
        this.setState({dialogMessage: '', dialogOpen: false});
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

    handleErrorResponses(error) {
        this.setState({error: error, loggerName:'', loggerClass:''});
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
            elevation: 0,
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

    handleUserInput(event) {
        const target = event.target;
        const name = target.name;
        const value = target.value;
        this.setState({[name]: value});
    }

    renderLogConfigPageView() {
        return (
            <Tabs
                id="controlled-tab-example"
                activeKey={this.state.key}
                onSelect={key => this.setState({key})}
            >
                <Tab eventKey="list" title="Loggers">
                    {this.renderLoggersList()}
                </Tab>
                <Tab eventKey="add" title="Add Loggers">
                    {this.renderAddLogger()}
                </Tab>
            </Tabs>
        );
    }

    renderAddLogger() {
        return (
            <div>
                <Form onSubmit={this.handleFormSubmission} style={styles.form}>
                    <Form.Group as={Row} controlId="formPlaintextUser">
                        <Form.Label column sm="2">
                            Logger Name
                        </Form.Label>
                        <Col sm="3">
                            <Form.Control onChange={this.handleUserInput} placeholder="Logger Name" name="loggerName" value={this.state.loggerName}/>
                        </Col>
                    </Form.Group>

                    <Form.Group as={Row} controlId="formPlaintextPassword">
                        <Form.Label column sm="2">
                            Class
                        </Form.Label>
                        <Col sm="3">
                            <Form.Control type="text" placeholder="Class" onChange={this.handleUserInput} name="loggerClass" value={this.state.loggerClass}/>
                        </Col>
                    </Form.Group>

                    <Form.Group as={Row} controlId="formPlaintextIsAdmin">
                        <Form.Label column sm="2">
                            Log Level
                        </Form.Label>
                        <Col sm="3">
                            <Form.Control as="select" onChange={this.handleUserInput} name="loggerLevel">
                                <option>OFF</option>
                                <option>TRACE</option>
                                <option>DEBUG</option>
                                <option>INFO</option>
                                <option>WARN</option>
                                <option>ERROR</option>
                                <option>FATAL</option>
                            </Form.Control>
                        </Col>
                    </Form.Group>

                    <Form.Group as={Row} controlId="submitBtn">
                        <Button size="sm" type="submit" variant="contained" disabled={this.isSubmissionInvalid()}>
                            Add Logger
                        </Button>
                    </Form.Group>
                </Form>
                <Dialog open={this.state.dialogOpen} onClose={this.handleDialogClose}
                        aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                    <DialogTitle id="alert-dialog-title">{"Action Complete"}</DialogTitle>
                    <DialogContent dividers>
                        <DialogContentText id="alert-dialog-description">
                            {this.state.dialogMessage}
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleDialogClose} variant="contained" autoFocus>
                            OK
                        </Button>
                    </DialogActions>
                </Dialog>
            </div>
        );
    }

    render() {
        return (
            <ResourceExplorerParent
                title='Logging Management'
                content={this.renderLogConfigPageView()}
                error={this.state.error}
            />);
    }
}
