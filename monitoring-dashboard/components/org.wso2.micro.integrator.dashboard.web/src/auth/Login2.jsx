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
import AuthManager from './utils/AuthManager';
import "@fortawesome/fontawesome-free/css/all.min.css";
import "bootstrap-css-only/css/bootstrap.min.css";
import "mdbreact/dist/css/mdb.css";
import {TextField} from 'material-ui';
import {MuiThemeProvider} from 'material-ui/styles';
import Header from '../common/Header';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';

import {
    MDBContainer,
    MDBRow,
    MDBCol,
    MDBCard,
    MDBCardBody,
    MDBCardHeader,
    MDBBtn
} from "mdbreact";
import defaultTheme from "../utils/Theme";
import lightTheme from "../utils/LightTheme";

/**
 * Login page.
 */

const styles = {
    LoginForm: {
        margin: '0 auto',
        paddingTop: '240px'
    },
    formHeader: {
        backgroundColor:'#ffffff'
    }
};
export default class Login2 extends Component {
    /**
     * Constructor.
     *
     * @param {{}} props Props
     */
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            host: 'localhost',
            port: '9164',
            authenticated: false,
            rememberMe: false,
            loginError: false,
            loginErrorMessage: ''
        };
        this.authenticate = this.authenticate.bind(this);
        this.handleLoginErrorDialogClose = this.handleLoginErrorDialogClose.bind(this);
    }


    componentWillMount() {
        this.initAuthenticationFlow();
    }

    /**
     * Check if the user has already signed in and remember me is set
     */
    initAuthenticationFlow() {
        if (!AuthManager.isLoggedIn()) {
            this.setState({authenticated: false})
        } else {
            this.setState({authenticated: true})
        }
    }

    authenticate(e) {
        const { intl } = this.context;
        const {username, password, host, port, rememberMe} = this.state;
        e.preventDefault();
        AuthManager.authenticate(host, port, username, password, rememberMe)
            .then(() => this.setState({authenticated: true}))
            .catch((error) => {
                var errorMessage;
                if (error.response && error.response.status === 401)  {
                    errorMessage = 'Incorrect username or password!';
                } else {
                    errorMessage = 'Error occurred in communication.';
                    window.open(`https://${host}:${port}/management`,  'sharer', 'toolbar=0,status=0,width=250,height=100');
                }
                this.setState({
                    username: '',
                    password: '',
                    loginErrorMessage: errorMessage,
                    loginError: true,
                });
            });

    }

    handleLoginErrorDialogClose() {
        this.setState({loginError:false, loginErrorMessage:''});
    }

    /**
     * Render default login page.
     *
     * @return {XML} HTML content
     */
    renderDefaultLogin() {
        const {username, password, host, port} = this.state;
        return (
            <MuiThemeProvider muiTheme={defaultTheme}>
                <Header
                    title={'MICRO INTEGRATOR'}
                    rightElement={<span/>}
                />
            <MDBContainer>
                <MDBRow>
                    <MDBCol md="6" style={styles.LoginForm}>
                        <MDBCard style={{boxShadow:'0 3px 5px 0 rgba(0, 0, 0, 0), 0 2px 5px 0 rgba(3, 0, 0, 0)', border: 'solid', borderWidth: 'thin', borderColor: 'silver'}}>
                            <MDBCardBody>
                                <MDBCardHeader className="form-header rgba-blue-grey-light rounded">
                                    <h3 className="my-3">
                                        SIGN IN
                                    </h3>
                                </MDBCardHeader>
                                <MuiThemeProvider muiTheme={lightTheme}>
                                <TextField
                                    id="txt-host"
                                    autoFocus
                                    fullWidth
                                    autoComplete="off"
                                    margin="normal"
                                    variant="outlined"
                                    floatingLabelText={"Host"}
                                    value={host}
                                    onChange={(e) => {
                                        this.setState({
                                            host: e.target.value,
                                        });
                                    }}
                                />

                                <TextField
                                    id="txt-port"
                                    type="email"
                                    autoFocus
                                    fullWidth
                                    autoComplete="off"
                                    margin="normal"
                                    variant="outlined"
                                    floatingLabelText={"Port"}
                                    value={port}
                                    onChange={(e) => {
                                        this.setState({
                                            port: e.target.value,
                                        });
                                    }}
                                />

                                <TextField
                                    id="txt-username"
                                    type="email"
                                    autoFocus
                                    fullWidth
                                    autoComplete="off"
                                    margin="normal"
                                    variant="outlined"
                                    floatingLabelText={"User"}
                                    value={username}
                                    onChange={(e) => {
                                        this.setState({
                                            username: e.target.value,
                                        });
                                    }}
                                />

                                <TextField
                                    type="password"
                                    id="defaultFormPasswordEx"
                                    autoFocus
                                    fullWidth
                                    autoComplete="off"
                                    margin="normal"
                                    variant="outlined"
                                    floatingLabelText={"Password"}
                                    value={password}
                                    onChange={(e) => {
                                        this.setState({
                                            password: e.target.value,
                                        });
                                    }}
                                />

                                </MuiThemeProvider>
                                <div className="text-center mt-4">
                                    <MDBBtn color="blue-grey"
                                            className="mb-3"
                                            type="submit"
                                            id="btn-submit"
                                            disabled={username === '' || password === '' || host === '' || port === ''}
                                            onClick={this.authenticate}
                                    >
                                        Sign In
                                    </MDBBtn>
                                </div>
                            </MDBCardBody>
                        </MDBCard>
                    </MDBCol>
                </MDBRow>
            </MDBContainer>
                <Dialog open={this.state.loginError} onClose={this.handleLoginErrorDialogClose}
                        aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                    <DialogTitle id="alert-dialog-title">{"Login Failed"}</DialogTitle>
                    <DialogContent dividers>
                        <DialogContentText id="alert-dialog-description">
                            {this.state.loginErrorMessage}
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleLoginErrorDialogClose} color="primary" autoFocus>
                            OK
                        </Button>
                    </DialogActions>
                </Dialog>
            </MuiThemeProvider>
        );
    }


    /**
     * Renders the login page.
     *
     * @return {XML} HTML content
     */
    render() {
         const authenticated = this.state.authenticated;
         if (authenticated) {
                location.href = '/dashboard/home';
        }
        return this.renderDefaultLogin();
    }
}
