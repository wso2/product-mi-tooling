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
 *
 *
 */

import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter} from 'react-router-dom';
import { Route, Switch } from 'react-router';
import { createStore } from 'redux';
import { Provider } from 'react-redux'
import Reducers from './redux/Reducers';

import Login from './auth/Login';
import Logout from './auth/Logout';
import Dashboard from './home/Dashboard'

const store = createStore (Reducers)

class App extends Component {
    constructor() {
        super();
    }
    render() {
        return (
            <BrowserRouter basename={window.contextPath}>
                <Switch>
                    {/* Authentication */}
                    <Route exact path='/login' component={Login} />
                    <Route exact path='/logout' component={Logout} />
                    {/* Secured routes */}
                    <Route component={Dashboard} />
                </Switch>
            </BrowserRouter>
        );
    }
};

ReactDOM.render(
  <Provider store = {store}>
    <App />
  </Provider>,
  document.getElementById('root')
);
