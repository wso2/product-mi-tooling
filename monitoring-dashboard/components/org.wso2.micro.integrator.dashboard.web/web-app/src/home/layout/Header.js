import React, { useEffect, useState } from 'react';
import { Helmet } from "react-helmet";
import { useLocation, useHistory } from 'react-router-dom';
import PropTypes from 'prop-types';
import AppBar from '@material-ui/core/AppBar';
import Grid from '@material-ui/core/Grid';
import Hidden from '@material-ui/core/Hidden';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import { withStyles } from '@material-ui/core/styles';
import { Menu, MenuItem, Popover, Tooltip } from '@material-ui/core';
import NodeFilter from '../NodeFilter';
import GroupSelector from '../GroupSelector';
import { categories, getIdFromRoute } from './NavigatorLinks';
import AuthManager from '../../auth/AuthManager';
import Chip from '@material-ui/core/Chip';
import AccountCircle from '@material-ui/icons/AccountCircle';
import { useAuthContext } from "@asgardeo/auth-react";
import { useSelector } from 'react-redux';

const lightColor = 'rgba(255, 255, 255, 0.7)';

const styles = (theme) => ({
  secondaryBar: {
    zIndex: 0,
  },
  menuButton: {
    marginLeft: -theme.spacing(1),
  },
  iconButtonAvatar: {
    padding: 4,
  },
  link: {
    textDecoration: 'none',
    color: lightColor,
    '&:hover': {
      color: theme.palette.common.white,
    },
  },
  button: {
    borderColor: lightColor,
  },
});

function Header(props) {
  const history = useHistory();
  const { classes, onDrawerToggle } = props;
  const location = useLocation();
  const [selected, setSelected] = useState(null);
  const [user, setUser] = useState(null);
  const { signOut } = useAuthContext();
  const superAdminUserName = useSelector((state) => state.superAdmin);

  useEffect(() => {
    if (!user) {
        setUser(AuthManager.getUser());
    }
    if (location.pathname !== '/') {
      setSelected(getIdFromRoute(location.pathname));
    } else {
      setSelected(null);
    }
  }, [location]);

  useEffect(() => {
    handlePopOverClose();
  }, [user]);

  const [anchorEl, setAnchorEl] = React.useState(null);
  const handlePopOverClick = event => {
    setAnchorEl(event.currentTarget);
  };
  const handlePopOverClose = () => {
    setAnchorEl(null);
  };

  const showNodeSelector = () => {
    return !(location.pathname.startsWith("/log-configs") || location.pathname.startsWith("/update-password") || location.pathname.startsWith("/users") || location.pathname.startsWith("/roles") || location.pathname === "/");
  };

  const handleLogout = () => {
    if (AuthManager.getUser()?.sso) {
      signOut();
      AuthManager.discardSession();
    } else {
      window.location.href = "/logout";
    }
  }

  return (
    <>
      <Helmet>
        <title>
          {selected ? `${selected.id} - ` : "Nodes - "}Integration Control Plane
        </title>
      </Helmet>
      <AppBar color="default" position="sticky" elevation={0}>
        <Toolbar>
          <Grid container spacing={1} alignItems="center">
            <Hidden smUp>
              <Grid item>
                <IconButton
                  color="inherit"
                  aria-label="open drawer"
                  onClick={onDrawerToggle}
                  className={classes.menuButton}
                >
                  <MenuIcon />
                </IconButton>
              </Grid>
            </Hidden>
            {!location.pathname.startsWith("/update-password") && (
              <GroupSelector />
            )}
            {showNodeSelector() && <NodeFilter />}
            {!showNodeSelector() && <div style={{ height: "74px" }}></div>}
            <Grid item xs />
            <Grid item>
              <Chip
                icon={<AccountCircle />}
                label={user ? user.username.toUpperCase() : "Anonymous"}
                onClick={handlePopOverClick}
                variant="outlined"
              />
              <Menu
                anchorEl={anchorEl}
                key={user ? user.username : "anonymous"}
                open={Boolean(anchorEl)}
                onClose={handlePopOverClose}
              >
                {user && user.username === superAdminUserName ? (
                  <Tooltip title="Super admin is not allowed to update credentials">
                    <span>
                      <MenuItem
                        className="Mui-disabled"
                        onClick={() => {
                          history.push("/update-password");
                          handlePopOverClose();
                        }}
                      >
                        Change Password
                      </MenuItem>
                    </span>
                  </Tooltip>
                ) : (
                  <MenuItem
                    onClick={() => {
                      history.push("/update-password");
                      handlePopOverClose();
                    }}
                  >
                    Change Password
                  </MenuItem>
                )}
                <MenuItem onClick={handleLogout}>Logout</MenuItem>
              </Menu>
            </Grid>
          </Grid>
        </Toolbar>
      </AppBar>
      <AppBar
        component="div"
        className={classes.secondaryBar}
        color="default"
        position="static"
        elevation={0}
      >
        <Toolbar>
          <Grid container alignItems="center" spacing={1}>
            <Grid item xs>
              <Typography color="inherit" variant="h5" component="h1">
                {selected ? selected.id : "Nodes"}
              </Typography>
            </Grid>
          </Grid>
        </Toolbar>
      </AppBar>
    </>
  );
}

Header.propTypes = {
  classes: PropTypes.object.isRequired,
  onDrawerToggle: PropTypes.func.isRequired,
};

export default withStyles(styles)(Header);
