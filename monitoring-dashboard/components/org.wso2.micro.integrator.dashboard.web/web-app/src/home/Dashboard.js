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
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import {Redirect} from 'react-router';
import { Button, Menu, MenuItem, Popover } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import Typography from '@material-ui/core/Typography';
import Drawer from '@material-ui/core/Drawer';
import Divider from '@material-ui/core/Divider';
import List from '@material-ui/core/List';
import Container from '@material-ui/core/Container';
import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import Box from '@material-ui/core/Box';
import Link from '@material-ui/core/Link';
import { NavMenuItems, globalSettings } from './NavMenuItems';
import clsx from 'clsx';
import GroupSelector from './GroupSelector';
import NodeFilter from './NodeFilter';
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
import PageNotFound from '../pages/NotFound'
import Login from '../auth/Login'
import AuthManager from '../auth/AuthManager';
import logo from '../images/logo.svg';
import { useDispatch } from 'react-redux';
import { setBasePath } from '../redux/Actions';

export default function Dashboard() {
    const classes = useStyles();
    const [open, setOpen] = React.useState(true);
    const handleDrawerOpen = () => {
        setOpen(true);
    };
    const handleDrawerClose = () => {
        setOpen(false);
    };

    const [anchorEl, setAnchorEl] = React.useState(null);
    const handlePopOverClick = event => {
        setAnchorEl(event.currentTarget);
    };
    const handlePopOverClose = () => {
        setAnchorEl(null);
    };

    const windowLocation = window.location.href;
    const dispatch = useDispatch();
    dispatch(setBasePath(windowLocation))

    // if the user is not logged in Redirect to login
    if (!AuthManager.isLoggedIn()) {
        return (
            <Redirect to={{pathname: '/login'}}/>
        );
    }

    return (
        <div className={classes.root}>
            <CssBaseline />
            <Router>
            <AppBar position="absolute" className={clsx(classes.appBar, open && classes.appBarShift)}>
                <Toolbar className={classes.toolbar}>
                    <div style={{ width: '100%' }}>
                        <Box display="flex" p={1}>
                            <Box flexGrow={0} p={1}>
                                <IconButton
                                    edge="start"
                                    color="inherit"
                                    aria-label="open drawer"
                                    onClick={handleDrawerOpen}
                                    className={clsx(classes.menuButton, open && classes.menuButtonHidden)}
                                >
                                    <MenuIcon />
                                </IconButton>
                                <Link style={{height: '17px'}} to={'/'}><img
                                    height='17'
                                    src={logo}
                                    alt='logo'
                                /></Link>
                            </Box>
                            <Box flexGrow={10}>
                                <Typography component="h1" variant="h6" color="inherit" noWrap className={classes.typography}>
                                    Micro Integrator Dashboard
                                </Typography>
                            </Box>
                            <GroupSelector/>
                            <span>
                                <Button variant="contained" color="primary" onClick={handlePopOverClick}>
                                    Admin
                                </Button>
                                <Popover
                                    open={anchorEl}
                                    anchorEl={anchorEl}
                                    onClose={handlePopOverClose}
                                    anchorOrigin={{ horizontal: 'center', vertical: 'top' }}
                                    targetOrigin={{ horizontal: 'right', vertical: 'top' }}
                                >
                                    <Menu
                                        anchorEl={anchorEl}
                                        keepMounted
                                        open={Boolean(anchorEl)}
                                        onClose={handlePopOverClose}>
                                        <MenuItem component="a" href="/logout">Logout</MenuItem>
                                    </Menu>
                                </Popover>
                            </span>
                        </Box>
                    </div>
                </Toolbar>
            </AppBar>
            <Drawer
                variant="permanent"
                classes={{
                    paper: clsx(classes.drawerPaper, !open && classes.drawerPaperClose),
                }}
                open={open}
            >
                <div className={classes.toolbarIcon}>
                    <div><NodeFilter/></div>
                    <IconButton onClick={handleDrawerClose}>
                        <ChevronLeftIcon />
                    </IconButton>
                </div>
                <Divider />
                <List><NavMenuItems></NavMenuItems></List>
                <Divider />
                <List>{globalSettings}</List>
            </Drawer>
            <main className={classes.content}>
                <div className={classes.appBarSpacer} />
                <Container maxWidth="lg" className={classes.container}>
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Paper className={classes.paper}>
                                    <Switch>
                                        <Route exact path='/login' component={Login} />
                                        <Route exact path='/' component={Nodes}/>
                                        <Route exact path='/proxy_services' component={ProxyService}/>
                                        <Route exact path='/endpoints' component={Endpoints}/>
                                        <Route exact path='/inbound_endpoints' component={InboundEndpoints}/>
                                        <Route exact path='/message_processors' component={MessageProcessors}/>
                                        <Route exact path='/message_stores' component={MessageStores}/>
                                        <Route exact path='/apis' component={APIs}/>
                                        <Route exact path='/templates' component={Templates}/>
                                        <Route exact path='/sequences' component={Sequences}/>
                                        <Route exact path='/data_services' component={DataServices}/>
                                        <Route exact path='/datasources' component={Datasources}/>
                                        <Route exact path='/connectors' component={Connectors}/>
                                        <Route exact path='/carbon_applications' component={CarbonApplications}/>
                                        <Route exact path='/log_files' component={LogFiles}/>
                                        <Route path="" component={PageNotFound} />
                                    </Switch>
                            </Paper>
                        </Grid>
                    </Grid>
                    <Box>
                        <Typography variant="body2" color="textSecondary" className={classes.fixedFooter}>
                            {'© 2005 - 2020 WSO2 Inc. All Rights Reserved.'}
                        </Typography>
                    </Box>
                </Container>
            </main>
            </Router>
        </div>
    )
}

const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex',
    },
    toolbar: {
        paddingRight: 24, // keep right padding when drawer closed
    },
    typography: {
        marginLeft: '12px',
    },
    toolbarIcon: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end',
        padding: '0 8px',
        ...theme.mixins.toolbar,
    },
    appBar: {
        zIndex: theme.zIndex.drawer + 1,
        transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),
    },
    appBarShift: {
        marginLeft: 240,
        width: `calc(100% - 240px)`,
        transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
        }),
    },
    menuButton: {
        marginRight: 36,
    },
    menuButtonHidden: {
        display: 'none',
    },
    title: {
        flexGrow: 1,
        fontSize: 50
    },
    drawerPaper: {
        position: 'relative',
        whiteSpace: 'nowrap',
        width: 240,
        transition: theme.transitions.create('width', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
        }),
    },
    drawerPaperClose: {
        overflowX: 'hidden',
        transition: theme.transitions.create('width', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),
        width: theme.spacing(7),
        [theme.breakpoints.up('sm')]: {
            width: theme.spacing(9),
        },
    },
    appBarSpacer: theme.mixins.toolbar,
    content: {
        flexGrow: 1,
        height: '100vh',
        overflow: 'auto',
    },
    container: {
        paddingTop: theme.spacing(4),
        paddingBottom: theme.spacing(4),
    },
    paper: {
        padding: theme.spacing(2),
        display: 'flex',
        overflow: 'auto',
        flexDirection: 'column',
    },
    fixedHeight: {
        height: 240,
    },
    fixedFooter: {
        height: '40px',
        padding: '10px 0',
        bottom: '0',
        marginBottom: '1px',
        textAlign: 'center',
        position: 'fixed',
        left: '50%',
    },

}));
