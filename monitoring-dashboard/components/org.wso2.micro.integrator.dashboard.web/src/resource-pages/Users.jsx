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
import Tabs from 'react-bootstrap/Tabs'
import Tab from 'react-bootstrap/Tabs'
import Link from '@material-ui/core/Link';
import ResourceAPI from "../utils/apis/ResourceAPI";
import MUIDataTable from "mui-datatables";
import DeleteIcon from '@material-ui/icons/Delete';
import Form from 'react-bootstrap/Form'
import Row from 'react-bootstrap/Row';
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

export default class Users extends Component {

    constructor(props) {
        super(props);
        this.users = null;
        this.state = {
            data: [],
            key: 'user',
            networkErrorOccurred: false,
            newUserId: '',
            newUserPassword: '',
            newUserPasswordRepeat: '',
            newUserIsAdmin: false,
            isSubmitBtnDisabled: true,
            dialogMessage: '',
            dialogOpen: false,
            deleteConfirmDialogOpen: false,
            deleteUserId: '',
            error: null
        };
        this.handleUserInput = this.handleUserInput.bind(this);
        this.handleFormSubmission = this.handleFormSubmission.bind(this);
        this.handleDialogClose = this.handleDialogClose.bind(this);
        this.handleDeleteConfirmation = this.handleDeleteConfirmation.bind(this);
        this.handleConfirmationClose = this.handleConfirmationClose.bind(this);
    }

    componentDidMount() {
        this.retrieveAllUsers();
    }

    handleErrorResponses(error) {
      this.setState({error:error});
    }

    retrieveAllUsers() {
        const data = [];
        new ResourceAPI().getResourceList(`/users`).then((response) => {
            this.users = response.data.list || [];
            this.users.forEach((element) => {
                const rowData = [];
                rowData.push(element.userId);
                data.push(rowData);
            });
            this.setState({data: data});
        }).catch((error) => {
            this.handleErrorResponses(error);
        });
    }

    renderUsersView() {
        return (
            <Tabs
                id="controlled-tab-example"
                activeKey={this.state.key}
                onSelect={key => this.setState({key})}
            >
                <Tab eventKey="user" title="Users">
                    {this.renderUserList()}
                </Tab>
                <Tab eventKey="add" title="Add User">
                    {this.renderAddUser()}
                </Tab>
            </Tabs>
        );
    }

    renderUserList() {
        const columns = [{
            name: "User ID",
            options: {
                sortDirection: 'asc',
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <Link component="button" variant="body2" onClick={() => {
                            this.props.history.push(`/users/explore?name=${tableMeta.rowData[0]}`)
                        }}>
                            {tableMeta.rowData[0]}
                        </Link>
                    );
                }
            }
        }, {
            name: "Action",
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div>
                            <span style={{cursor: 'pointer'}}
                                  onClick={() => {
                                      // Open confirmation and set the userId to be deleted
                                      this.setState({
                                          deleteConfirmDialogOpen: true,
                                          deleteUserId: tableMeta.rowData[0]
                                      });
                                  }}>
                                <DeleteIcon/> Delete </span>
                        </div>
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
            <div>
                <MUIDataTable
                    data={this.state.data}
                    columns={columns}
                    options={options}
                />
                <Dialog
                    open={this.state.deleteConfirmDialogOpen}
                    onClose={this.handleConfirmationClose}
                    aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description"
                >
                    <DialogTitle id="alert-dialog-title">{"Confirm Action"}</DialogTitle>
                    <DialogContent dividers>
                        <DialogContentText id="alert-dialog-description">
                            Are you sure you want to delete this user?
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleConfirmationClose} color="default" variant="contained">
                            Close
                        </Button>
                        <Button onClick={this.handleDeleteConfirmation} autoFocus color="default" variant="contained">
                            Confirm
                        </Button>
                    </DialogActions>
                </Dialog>
            </div>
        );
    }

    handleUserInput(event) {
        const target = event.target;
        const name = target.name;
        const value = target.value;
        this.setState({[name]: value});
    }

    handleConfirmationClose() {
        this.setState({deleteConfirmDialogOpen: false, deleteUserId: ''});
    }

    handleDeleteConfirmation() {
        const {deleteUserId} = this.state;
        this.setState({deleteConfirmDialogOpen: false, deleteUserId: ''});

        new ResourceAPI().deleteUser(deleteUserId).then((response) => {
            this.setState({
                dialogMessage: 'User deleted successfully.',
                dialogOpen: true
            });
            this.retrieveAllUsers();
        }).catch((error) => {
            this.handleErrorResponses(error);
        });
    }

    handleFormSubmission(event) {

        const {newUserId, newUserPassword, newUserIsAdmin} = this.state;
        new ResourceAPI().addNewUser(newUserId, newUserPassword, newUserIsAdmin).then((response) => {
            this.setState({dialogMessage: 'User added successfully.', dialogOpen: true});
            this.retrieveAllUsers();
        }).catch((error) => {
            this.handleErrorResponses(error);
        });
        event.preventDefault();
    }

    isSubmissionInValid() {
        const {newUserPassword, newUserPasswordRepeat, newUserId} = this.state;
        if (newUserId === '' || newUserPassword === '' || newUserPasswordRepeat === '') {
            return true;
        } else {
            if (newUserPassword === newUserPasswordRepeat) {
                return false;
            }
        }
        return true;
    }

    handleDialogClose() {
        this.setState({dialogMessage: '', dialogOpen: false});
    }

    renderAddUser() {
        return (
            <div>
                <Form onSubmit={this.handleFormSubmission} style={styles.form}>
                    <Form.Group as={Row} controlId="formPlaintextUser">
                        <Form.Label column sm="2">
                            User
                        </Form.Label>
                        <Col sm="3">
                            <Form.Control onChange={this.handleUserInput} placeholder="User Name" name="newUserId"/>
                        </Col>
                    </Form.Group>

                    <Form.Group as={Row} controlId="formPlaintextPassword">
                        <Form.Label column sm="2">
                            Password
                        </Form.Label>
                        <Col sm="3">
                            <Form.Control type="password" placeholder="Password" onChange={this.handleUserInput}
                                          name="newUserPassword"/>
                        </Col>
                    </Form.Group>

                    <Form.Group as={Row} controlId="formPlaintextPasswordRepeat">
                        <Form.Label column sm="2">
                            Repeat Password
                        </Form.Label>
                        <Col sm="3">
                            <Form.Control type="password" placeholder="Re-type password" onChange={this.handleUserInput}
                                          name="newUserPasswordRepeat"/>
                        </Col>
                    </Form.Group>

                    <Form.Group as={Row} controlId="formPlaintextIsAdmin">
                        <Form.Label column sm="2">
                            isAdmin
                        </Form.Label>
                        <Col sm="3">
                            <Form.Control as="select" onChange={this.handleUserInput} name="newUserIsAdmin">
                                <option>true</option>
                                <option>false</option>
                            </Form.Control>
                        </Col>
                    </Form.Group>

                    <Form.Group as={Row} controlId="submitBtn">
                        <Button size="sm" type="submit" variant="contained" disabled={this.isSubmissionInValid()}>
                            Add User
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
            title='User Management'
            content={this.renderUsersView()}
            error={this.state.error}
        />);
    }
}
