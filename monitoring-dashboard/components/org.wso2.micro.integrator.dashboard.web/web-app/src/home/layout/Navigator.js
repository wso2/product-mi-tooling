import React, { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import PropTypes from 'prop-types';
import clsx from 'clsx';
import { withStyles } from '@material-ui/core/styles';
import Divider from '@material-ui/core/Divider';
import Drawer from '@material-ui/core/Drawer';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import { Link as MUILink } from '@material-ui/core';
import HomeIcon from '@material-ui/icons/Home';
import { categories } from './NavigatorLinks';
import AuthManager from "../../auth/AuthManager";


const styles = (theme) => ({
    categoryHeader: {
        paddingTop: theme.spacing(2),
        paddingBottom: theme.spacing(2),
    },
    categoryHeaderPrimary: {
        color: theme.palette.common.white,
    },
    item: {
        paddingTop: 1,
        paddingBottom: 1,
        color: 'rgba(255, 255, 255, 0.7)',
        '&:hover,&:focus': {
            backgroundColor: 'rgba(255, 255, 255, 0.08)',
        },
        '& a': {
            color: 'rgba(255, 255, 255, 0.7)',
        }
    },
    itemCategory: {
        backgroundColor: '#232f3e',
        boxShadow: '0 -1px 0 #404854 inset',
        paddingTop: 10,
        paddingBottom: 10,
    },
    versionLabel: {
        color: theme.palette.common.white,
        backgroundColor: '#232f3e',
        paddingTop: 10,
        paddingBottom: 10,
        textAlign: 'center',
    },
    firebase: {
        fontSize: 24,
        color: theme.palette.common.white,
    },
    itemActiveItem: {
        color: '#4fc3f7',
        '& a': {
            color: '#4fc3f7',
        }
    },
    itemPrimary: {
        fontSize: 'inherit',
    },
    itemIcon: {
        minWidth: 'auto',
        marginRight: theme.spacing(2),
    },
    divider: {
        marginTop: theme.spacing(2),
    },
});

function Navigator(props) {
    const { classes, ...other } = props;
    const location = useLocation();
    const [selected, setSelected] = useState('/');
    useEffect(() => {
        setSelected(location.pathname);
    }, [location]);

    const getCategories = () => {
        const userScope = AuthManager.getUser().scope;
        if(userScope !== "admin"){
            return categories.filter(category => category.id ===  "General");
        }
        if (AuthManager.getUser()?.sso) {
            return categories.map(category => ({
                ...category,
                children: category.children
                .filter(child => child.id !== "Users")
            }))
        }
        return categories;
    }

    return (
        <Drawer variant="permanent" {...other}>
            <List disablePadding>
                <ListItem className={clsx(classes.firebase, classes.item, classes.itemCategory)}>
                    <MUILink component={Link} to='/'>
                        <img
                            alt='logo'
                            src='/logo.svg'
                            width={200}
                        />
                    </MUILink>
                </ListItem>
                <ListItem className={classes.versionLabel}>
                    <ListItemText>
                        Version : 4.2.0
                    </ListItemText>
                </ListItem>
                <ListItem 
                    className={clsx(classes.item, classes.itemCategory, selected === '/' && classes.itemActiveItem)} 
                    to='/'
                    component={Link}>
                    <ListItemIcon className={classes.itemIcon}>
                        <HomeIcon />
                    </ListItemIcon>
                    <ListItemText
                        classes={{
                            primary: classes.itemPrimary,
                        }}
                    >
                        Nodes
                </ListItemText>
                </ListItem>
                {getCategories().map(({ id, children }) => (
                    <React.Fragment key={id}>
                        <ListItem className={classes.categoryHeader}>
                            <ListItemText
                                classes={{
                                    primary: classes.categoryHeaderPrimary,
                                }}
                            >
                                {id}
                            </ListItemText>
                        </ListItem>
                        {children.map(({ id: childId, icon, to }) => (
                            <ListItem
                                key={childId}
                                button
                                className={clsx(classes.item, selected === to && classes.itemActiveItem)}
                                to={to}
                                component={Link}
                            >
                                <ListItemIcon className={classes.itemIcon}>{icon}</ListItemIcon>
                                <ListItemText
                                    classes={{
                                        primary: classes.itemPrimary,
                                    }}
                                >
                                    <MUILink>{childId}</MUILink>
                                </ListItemText>
                            </ListItem>
                        ))}

                        <Divider className={classes.divider} />
                    </React.Fragment>
                ))}
            </List>
        </Drawer>
    );
}

Navigator.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Navigator);