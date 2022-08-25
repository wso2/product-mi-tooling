/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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
import TableCell from "@material-ui/core/TableCell";
import Drawer from '@material-ui/core/Drawer';
import Button from '@material-ui/core/Button';
import TableRow from '@material-ui/core/TableRow';
import { makeStyles } from '@material-ui/core/styles';
import RegistryResourceSideDrawer from './sideDrawers/RegistryResourceSideDrawer';
import { FolderFill, FiletypeJson, FiletypeXml, FiletypeTxt, FiletypeCsv, FileEarmark, Gear } from 'react-bootstrap-icons';

export default function IconCell(props) {
    const classes = useStyles();
    const { pageId, handleDoubleClick, registryPath, data } = props;
    const name = data.childName;
    const iconType= data.fileIcon;
    const [state, setState] = React.useState({
        openSideDrawer: false,
    });

    const toggleDrawer = (open,name) => (event) => {
        if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
            return;
        }
        setState({ ...state, openSideDrawer: open });
    };

    let timer = 0;
    let delay = 200;
    let prevent = false;

    const handleSingleClick = () => {

        timer = setTimeout(() => {
            if (!prevent) {
                setState({ ...state, openSideDrawer: true });
            }
          }, delay);
    }

    const handleDoubleClickCell = (name,iconType,registryPath,handleDoubleClick) => {
        
        clearTimeout(timer);
        prevent = true;
        if(iconType === 'folder'){
            handleDoubleClick(name,iconType,registryPath);
        } else {
            setState({ ...state, openSideDrawer: true });
        }
        setTimeout(() => {prevent = false}, delay);
    }

    return <TableRow hover role="presentation">
        <TableCell onClick={() => handleSingleClick()} onDoubleClick={() => handleDoubleClickCell(name,iconType,registryPath,handleDoubleClick)} className={classes.tableCell}> 
            <Button variant="text" startIcon={<FileIcon className={classes.icon} iconType={iconType}/>}>
                {name}
            </Button>   
        </TableCell>
        <Drawer anchor='right' open={state['openSideDrawer']} onClose={toggleDrawer(false)} classes={{paper: classes.drawerPaper}}>
            <RegistryResourceSideDrawer pageId={pageId} data={data} registryPath={registryPath}/>
        </Drawer>
    </TableRow>;
}

function FileIcon(props) {
    var className = props.className;
    var iconType = props.iconType;
    if (iconType === 'folder'){
        return <FolderFill className={className}/>
    } else if (iconType === 'xml'){
        return <FiletypeXml className={className}/>
    } else if (iconType === 'json'){
        return <FiletypeJson className={className}/>
    } else if (iconType === 'csv'){
        return <FiletypeCsv className={className}/>
    } else if (iconType === 'text'){
        return <FiletypeTxt className={className}/>
    } else if (iconType === 'property'){
        return <Gear className={className}/>
    } else {
        return <FileEarmark className={className}/>
    }
}

const useStyles = makeStyles(() => ({
    icon : {
        color: '#334d9c',
        cursor: "pointer",
        padding: '1px'
    },
    tableCell : {
        padding: '1px',
        borderBottom: 'none',
        color: '#3f51b5',
        cursor: "pointer"
    },
    drawerPaper: {
        backgroundColor: '#fff',
    },
}));

