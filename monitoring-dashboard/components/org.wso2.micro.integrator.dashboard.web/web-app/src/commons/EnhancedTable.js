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
import { useSelector } from 'react-redux';
import { makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableContainer from '@material-ui/core/TableContainer';
import TablePagination from '@material-ui/core/TablePagination';
import EnhancedTableHead from './TableHeaderCreater';
import TableRowCreator from './TableRowCreator';
import HTTPClient from '../utils/HTTPClient';
import SearchBox from '../commons/SearchBox';
import Progress from '../commons/Progress';

export default function EnhancedTable(props) {
    const { pageInfo} = props;
    var headCells = pageInfo.headCells;
    const classes = useStyles();
    const [order, setOrder] = React.useState('asc');
    const [orderBy, setOrderBy] = React.useState(pageInfo.tableOrderBy);
    const [rowsPerPage, setRowsPerPage] = React.useState(5);
    const [page, setPage] = React.useState(0);
    const [rowCount, setRowCount] = React.useState(0);
    const globalGroupId = useSelector(state => state.groupId);
    const selectedNodeList = useSelector(state => state.nodeList);

    var selectedNodeId = pageInfo.additionalParams?.selectedNodeId;

    const [data, setData] = React.useState(null);

    const [queryString, setQueryString] = React.useState('');

    var pageId=pageInfo.pageId;

    const retrieveResources = (query = '', isUpdate = false) => {
        if(query !== queryString) {
            setPage(0);
        }
        setQueryString(query);
        if(pageId === 'nodesPage') {
            HTTPClient.getNodes(globalGroupId,page * rowsPerPage, page * rowsPerPage + rowsPerPage ).then(response => {
                response.data.resourceList.map(data => data.details = JSON.parse(data.details))
                setData(response.data.resourceList)
                setRowCount(response.data.count)
            }).catch(error => {
                console.log(error.response.data.message);
            });
        } else if(pageId === 'users' || pageId === 'roles') {
            HTTPClient.getPaginatedUsersAndRoles(query, page * rowsPerPage, page * rowsPerPage + rowsPerPage, 
                pageId, order, orderBy, globalGroupId, isUpdate).then(response => {
                    response.data.resourceList.map(data => data.details = JSON.parse(data.details))
                    setData(response.data.resourceList)
                    setRowCount(response.data.count)
            }).catch(error => {
                console.log(error.response.data.message);
            });
        } else if (pageId === 'log-configs') {
            HTTPClient.getPaginatedResults(query, page * rowsPerPage, page * rowsPerPage + rowsPerPage, 
                pageId, order, orderBy, globalGroupId, selectedNodeId, isUpdate).then(response => {
                setData(response.data.resourceList)
                setRowCount(response.data.count)
            }).catch(error => {
                console.log(error.response.data.message);
            });
        } else {
            if (pageId === 'data-sources') {
                pageId = 'datasources';
            }
            if (pageId === 'carbonapps') {
                pageId = 'capps';
            }
            HTTPClient.getPaginatedResults(query, page * rowsPerPage, page * rowsPerPage + rowsPerPage, 
                pageId, order, orderBy, globalGroupId, selectedNodeList, isUpdate).then(response => {
                    setData(response.data.resourceList)
                    setRowCount(response.data.count)
            }).catch(error => {
                console.log(error.response.data.message);
            });
        }
    };    


    React.useEffect(() => {
        if ((data !== null || globalGroupId !== '') ||
            (pageId === 'log-configs' && selectedNodeId !== null) ||
            (pageId !== 'log-configs' && pageId !== 'users' && pageId!== 'roles' && pageId !== 'nodesPage' && selectedNodeList.length > 0)
            ) {
            retrieveResources(queryString);
        }
    },[globalGroupId, selectedNodeList, selectedNodeId, rowsPerPage, page, pageInfo, order, orderBy])

    

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

    if ((data === null || globalGroupId === '') ||
        (pageId === 'log-configs' && selectedNodeId === null) ||
        (pageId !== 'log-configs' && pageId !== 'users' && pageId!== 'roles' && pageId !== 'nodesPage' && selectedNodeList === null)) {

        return <Progress/>
    }
    
    return (
        <div className={classes.root}>
            {pageInfo.pageId === 'nodesPage' ? null :<SearchBox passSearchQuery = {retrieveResources}/>}
            <Paper className={classes.paper}>
                <TableContainer>
                    <Table
                        className={classes.table}
                    >
                        <EnhancedTableHead
                            headCells = {headCells}
                            order = {order}
                            orderBy = {orderBy}
                            onRequestSort = {handleRequestSort}
                            rowCount = {rowCount}
                        />
                        <TableBody>
                            {data.map(row => <TableRowCreator groupId = {globalGroupId} pageInfo = {pageInfo} data = {row} headers = {headCells} retrieveData = {retrieveResources}/>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
                <TablePagination
                    rowsPerPageOptions = {[5, 10, 25]}
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
