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
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import { createStore } from 'redux';
import { Provider } from 'react-redux'
import Reducers from './redux/Reducers';
import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';
import { AuthProvider } from "@asgardeo/auth-react";

import Login from './auth/Login';
import Logout from './auth/Logout';
import Dashboard from './home/Dashboard'
import interceptor from "./auth/Interceptor";
import CookiePolicy from './policies/CookiePolicy';
import PrivacyPolicy from "./policies/PrivacyPolicy";
import Sso from './sso'

const store = createStore(Reducers)

let theme = createMuiTheme({
    palette: {
        primary: {
            light: '#63ccff',
            main: '#009be5',
            dark: '#006db3',
        },
        secondary: {
            light: '#0066ff',
            main: '#a2ecf5',
            // dark: will be calculated from palette.secondary.main,
            contrastText: '#ffcc00',
        },
        background: {
            default: '#f6f6f6',
            paper: '#ffffff',
            appBar: '#1d344f',
        },
    },
    typography: {
        fontFamily: '"Open Sans", "Helvetica", "Arial", sans-serif',
        fontSize: 12,
        subtitle2: {
            fontWeight: 600,
            fontSize: '0.875rem',
        },
        h4: {
            fontSize: '1.3rem',
        },
        h5: {
            fontWeight: 500,
            fontSize: 26,
            letterSpacing: 0.5,
        },
    },
    shape: {
        borderRadius: 8,
    },
    props: {
        MuiTab: {
            disableRipple: true,
        },
    },
    mixins: {
        toolbar: {
            minHeight: 48,
        },
    },
    custom: {
        drawerWidth: 256,
        logo: '/site/public/images/logo.svg',
        logoWidth: 180,
    },
    overrides: {
        MuiDrawer: {
            paper: {
                background: '#f6f6f6',
            }
        },
        MuiRadio: {
            colorSecondary: {
                '&$checked': { color: '#009be5' },
                '&$disabled': {
                    color: 'rgba(0, 0, 0, 0.26)',
                },
            },
        },
        MuiButton: {
            label: {
                textTransform: 'none',
            },
            contained: {
                boxShadow: 'none',
                '&:active': {
                    boxShadow: 'none',
                },
            },
            root: {
                '& .MuiSvgIcon-root': {
                    color: '#4fc3f7',
                    fontSize: '16px',
                }
            }
        },
        MuiTabs: {
            root: {
                marginLeft: 8,
            },
            indicator: {
                height: 3,
                borderTopLeftRadius: 3,
                borderTopRightRadius: 3,
                backgroundColor: '#ffffff',
            },
        },
        MuiTab: {
            root: {
                textTransform: 'none',
                margin: '0 16px',
                minWidth: 0,
                padding: 0,
            },
        },
        MuiIconButton: {
            root: {
                padding: 8,
            },
        },
        MuiTooltip: {
            tooltip: {
                borderRadius: 4,
            },
        },
        MuiDivider: {
            root: {
                backgroundColor: '#404854',
            },
        },
        MuiListItemText: {
            primary: {
                fontWeight: 500,
            },
        },
        MuiListItemIcon: {
            root: {
                color: 'inherit',
                marginRight: 0,
                '& svg': {
                    fontSize: 20,
                },
            },
        },
        MuiAvatar: {
            root: {
                width: 32,
                height: 32,
            },
        },
        MuiDrawer: {
            paper: {
                backgroundColor: '#18202c',
            },
        },
        MuiListItem: {
            root: {
                '&.itemCategory': {
                    backgroundColor: '#232f3e',
                    boxShadow: '0 -1px 0 #404854 inset',
                    paddingTop: 8,
                    paddingBottom: 8,
                },
            },
        },
    },
});

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            sso: window.sso,
            loggedOut: false
        }
        interceptor(this.logout);
    }

    logout = () => {
        this.setState({
            loggedOut: true
        });
    }

    render() {

        return (
            <ThemeProvider theme={theme}>
                <AuthProvider config={ this.state.sso.config }>
                    <BrowserRouter basename={window.contextPath}>
                        <Switch>
                            <Route exact path='/login' component={Login} />
                            <Route exact path='/logout' component={Logout} />
                            <Route exact path='/sso' component={ Sso }/>
                            <Route exact path='/cookie-policy' component={CookiePolicy}/>
                            <Route exact path='/privacy-policy' component={PrivacyPolicy}/>
                            <Route component={Dashboard} />
                        </Switch>
                    </BrowserRouter>
                </AuthProvider>
            </ThemeProvider>
        );

    }
}

ReactDOM.render(
    <Provider store={store}>
        <App />
    </Provider>,
    document.getElementById('root')
);
