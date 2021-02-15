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

import React from 'react';
import { Switch, Route, BrowserRouter as Router, Redirect } from 'react-router-dom';
import PropTypes from 'prop-types';
import { createMuiTheme, ThemeProvider, withStyles } from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import Hidden from '@material-ui/core/Hidden';
import Typography from '@material-ui/core/Typography';
import Navigator from './layout/Navigator';
import Content from './layout/Content';
import Header from './layout/Header';
import { useDispatch } from 'react-redux';
import { setBasePath } from '../redux/Actions';
import ProxyService from '../pages/ProxyService';
import Endpoints from '../pages/Endpoints';
import Nodes from '../pages/Nodes';
import InboundEndpoints from '../pages/InboundEndpoints';
import MessageProcessors from '../pages/MessageProcessors'
import MessageStores from '../pages/MessageStores'
import APIs from '../pages/APIs'
import Templates from '../pages/Templates'
import Sequences from '../pages/Sequences';
import DataServices from '../pages/DataServices';
import Datasources from '../pages/Datasources';
import Connectors from '../pages/Connectors';
import CarbonApplications from '../pages/CarbonApplications';
import LogFiles from '../pages/LogFiles'
import AuthManager from '../auth/AuthManager';


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

const drawerWidth = 256;

const styles = {
    root: {
        display: 'flex',
        minHeight: '100vh',
    },
    drawer: {
        [theme.breakpoints.up('sm')]: {
            width: drawerWidth,
            flexShrink: 0,
        },
    },
    app: {
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
    },
    main: {
        flex: 1,
        padding: theme.spacing(6, 4),
        background: '#eaeff1',
    },
    footer: {
        padding: theme.spacing(2),
        background: '#eaeff1',
    },
};

function Layout(props) {
    const { classes } = props;
    const [mobileOpen, setMobileOpen] = React.useState(false);

    const handleDrawerToggle = () => {
        setMobileOpen(!mobileOpen);
    };
    const windowLocation = window.location.href;
    const dispatch = useDispatch();
    dispatch(setBasePath(windowLocation));
    // if the user is not logged in Redirect to login
    if (!AuthManager.isLoggedIn()) {
        return (
            <Redirect to={{pathname: '/login'}}/>
        );
    }

    return (
        <ThemeProvider theme={theme}>
                            <Router>

            <div className={classes.root}>
                <CssBaseline />
                <nav className={classes.drawer}>
                    <Hidden smUp implementation="js">
                        <Navigator
                            PaperProps={{ style: { width: drawerWidth } }}
                            variant="temporary"
                            open={mobileOpen}
                            onClose={handleDrawerToggle}
                        />
                    </Hidden>
                    <Hidden xsDown implementation="css">
                        <Navigator PaperProps={{ style: { width: drawerWidth } }} />
                    </Hidden>
                </nav>
                <div className={classes.app}>
                    <Header onDrawerToggle={handleDrawerToggle} />
                    <main className={classes.main}>
                        <Content>
                                <Switch>
                                    <Route exact path='/' component={Nodes} />
                                    <Route exact path='/proxy_services' component={ProxyService} />
                                    <Route exact path='/endpoints' component={Endpoints} />
                                    <Route exact path='/inbound_endpoints' component={InboundEndpoints} />
                                    <Route exact path='/message_processors' component={MessageProcessors} />
                                    <Route exact path='/message_stores' component={MessageStores} />
                                    <Route exact path='/apis' component={APIs} />
                                    <Route exact path='/templates' component={Templates} />
                                    <Route exact path='/sequences' component={Sequences} />
                                    <Route exact path='/data_services' component={DataServices} />
                                    <Route exact path='/datasources' component={Datasources} />
                                    <Route exact path='/connectors' component={Connectors} />
                                    <Route exact path='/carbon_applications' component={CarbonApplications} />
                                    <Route exact path='/log_files' component={LogFiles} />
                                </Switch>
                        </Content>
                    </main>
                    <footer className={classes.footer}>
                        <Typography variant="body2" color="textSecondary" align="center">
                            {`© 2005 - ${new Date().getFullYear()} WSO2 Inc. All Rights Reserved.`}
                        </Typography>
                    </footer>
                </div>
            </div>
            </Router>

        </ThemeProvider>
    );
}

Layout.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Layout);
