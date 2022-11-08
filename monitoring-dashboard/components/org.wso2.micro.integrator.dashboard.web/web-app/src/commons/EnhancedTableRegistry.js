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
import { makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableContainer from '@material-ui/core/TableContainer';
import TablePagination from '@material-ui/core/TablePagination';
import EnhancedTableHead from './TableHeaderCreater';
import TableRowCreator from './TableRowCreator';
import SearchBox from '../commons/SearchBox';
import { useSelector } from 'react-redux';
import HTTPClient from '../utils/HTTPClient';
import Progress from '../commons/Progress';

export default function EnhancedTableRegistry(props) {
    const { pageInfo, registryPath, handleDoubleClick, } = props;
    const [queryString, setQueryString] = React.useState('');
    const [registryList, setRegistryList] = React.useState([]);
    const globalGroupId = useSelector(state => state.groupId);
    const selectedNodeList = useSelector(state => state.nodeList);

    const newDataSet = registryList.map(data => ({...data, fileIcon: addIconType(data)}));
    const dataSetFiles = newDataSet.filter(data => data.mediaType !== 'directory');
    const dataSetFolders = (newDataSet.filter(data => data.mediaType === 'directory')).map(folder => ({...folder, mediaType:''}));
    const [rowCount, setRowCount] = React.useState(0);
    var headCells = pageInfo.headCells;

    const classes = useStyles();
    const [order, setOrder] = React.useState('asc');
    const [orderBy, setOrderBy] = React.useState(pageInfo.tableOrderBy);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);
    const [page, setPage] = React.useState(0);


    const retrieveResources = React.useCallback((query = '') => {
        if(query !== queryString) {
            setPage(0);
        }
        setQueryString(query);

        HTTPClient.getPaginatedRegistryArtifacts(query, page * rowsPerPage, page * rowsPerPage + rowsPerPage, 
            order, orderBy, globalGroupId, registryPath).then(response => {
            setRegistryList(response.data.resourceList)
            setRowCount(response.data.count)
        }).catch(error => {
            console.log(error.response.data.message);
        });
    }, [globalGroupId, registryPath, rowsPerPage,page, order, orderBy, queryString ]);
    
    React.useEffect(() => {
        retrieveResources(queryString);
    },[retrieveResources, queryString, rowCount]);

    const handleRequestSort = (event, property) => {
        const isAsc = orderBy === property && order === 'asc';
        setOrder(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    function addIconType(data) {
        if (data.mediaType === 'directory') {
            return('folder');
        } else if (data.childName.endsWith('.txt')){
            return('text');       
        } else if (data.childName.endsWith('.xml')){
            return('xml');    
        } else if (data.childName.endsWith('.csv')){
            return('csv');    
        } else if (data.childName.endsWith('.json')){
            return('json');        
        } else if (data.childName.endsWith('.yaml')){
            return('yaml');        
        } else if (data.childName.endsWith('.xslt')){
            return('xslt');        
        } else if (data.childName.endsWith('.properties')){
            return('property');        
        } else {
            return('other');            
        }       
    }

    if ((registryList === null || globalGroupId === '') || selectedNodeList === null) {
        return <Progress/>
    }
    
    return (
        <div className = {classes.root}>
            <SearchBox passSearchQuery = {retrieveResources}/>
            <Paper className = {classes.paper}>
                <TableContainer>
                    <Table
                        className = {classes.table}
                    >
                        <EnhancedTableHead
                            headCells = {headCells}
                            order = {order}
                            orderBy = {orderBy}
                            onRequestSort = {handleRequestSort}
                            rowCount = {rowCount}
                        />
                        <TableBody>
                            {dataSetFolders.concat(dataSetFiles).map(row => <TableRowCreator groupId = {globalGroupId} pageInfo = {pageInfo} data = {row} headers = {headCells} handleDoubleClick = {handleDoubleClick} registryPath = {registryPath} retrieveData = {null}/>)}
                        </TableBody>
                    </Table>
                </TableContainer>
                <TablePagination
                    rowsPerPageOptions = {[10, 25, 50]}
                    component = "div"
                    count = {rowCount}
                    rowsPerPage = {rowsPerPage}
                    page = {page}
                    onChangePage = {handleChangePage}
                    onChangeRowsPerPage = {handleChangeRowsPerPage}
                />
            </Paper>
        </div>
    );
}

const useStyles = makeStyles((theme) => ({
    root: {
        width: '100%',
    },
    paper: {
        width: '100%',
        marginBottom: theme.spacing(2),
    },
    table: {
        minWidth: 750,
        size: 'small'
    },
    tableHead: {
        backgroundColor: '#E0E0E0',
    },
    visuallyHidden: {
        border: 0,
        clip: 'rect(0 0 0 0)',
        height: 1,
        margin: -1,
        overflow: 'hidden',
        padding: 0,
        position: 'absolute',
        top: 20,
        width: 1,
    },
    divider: {
        height: 0
    },
}));
