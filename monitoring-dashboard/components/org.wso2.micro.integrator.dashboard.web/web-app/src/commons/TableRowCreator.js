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
import axios from 'axios';
import TableRow from '@material-ui/core/TableRow';
import TableCell from '@material-ui/core/TableCell';
import Switch from "react-switch";
import ReactSelect from 'react-select';
import { makeStyles } from '@material-ui/core/styles';
import { useSelector, useDispatch } from 'react-redux';
import EnabledIcon from '@material-ui/icons/CheckCircleOutlineOutlined';
import DisabledIcon from '@material-ui/icons/BlockOutlined';
import AdminIcon from '@material-ui/icons/CheckRounded';
import NonAdminIcon from '@material-ui/icons/ClearRounded';
import DeleteIcon from '@material-ui/icons/DeleteOutline';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import { Button } from '@material-ui/core';
import Box from '@material-ui/core/Box';
import NodesCell from './NodesCell';
import LogsNodeCell from './LogsNodeCell';
import StatusCell from './statusCell/StatusCell';
import AuthManager from '../auth/AuthManager';
import { changeData } from '../redux/Actions';

export default function TableRowCreator(props) {
    const { pageInfo, data, headers } = props;
    const pageId = pageInfo.pageId
    return <TableRow>
        {headers.map(header => {switch(header.id) {
            // common
            case 'name':
                return <TableCell>{data.name}</TableCell>
            case 'nodes':
                return <TableCell><table>{data.nodes.map(node=><NodesCell pageId={pageId} nodeData={node} />)}</table></TableCell>
            case 'type':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.type} />)}</table></TableCell>

            // Proxy Services and dataservices
            case 'wsdlUrl':
                return <TableCell><table>{data.nodes.map(node=><LinkCell data={node.details.wsdl1_1} />)}</table></TableCell>

            // Proxy Services
            case 'isRunning':
                return <TableCell>{data.nodes.map(node=><SwitchStatusCell pageId={pageId} artifactName={node.details.name} 
                        nodeId={node.nodeId} status={node.details.isRunning}/>)}</TableCell>
            // Endpoints
            case 'state':
                return <TableCell>{data.nodes.map(node=><SwitchStatusCell pageId={pageId} artifactName={node.details.name} 
                        nodeId={node.nodeId} status={node.details.isActive}/>)}</TableCell>

            // Inbound Endpoints
            case 'protocol':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.protocol} />)}</table></TableCell>

            // Message Stores
            case 'message_count':
                return <TableCell>{data.nodes.map(node=><StringCell data={node.details.size === '-1' ? "Not Supported" : node.details.size}/>)}</TableCell>

            // Message Processors
            case 'status':
                return <TableCell>{data.nodes.map(node=><SwitchStatusCell pageId={pageId} artifactName={node.details.name} 
                        nodeId={node.nodeId} status={node.details.status === 'active' ? true : false}/>)}</TableCell>

            // Apis
            case 'url':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.url} />)}</table></TableCell>

            // Templates and Connectors
            case 'template_nodes':
            case 'connector_nodes':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.nodeId} />)}</table></TableCell>

            // Sequences
            case 'statistic':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.stats} />)}</table></TableCell>
            
            // Tasks
            case 'group':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.taskGroup} />)}</table></TableCell>

            // Connectors
            case 'package':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.package} />)}</table></TableCell>
            case 'description':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.description} />)}</table></TableCell>
            case 'connector_status':
                return <TableCell><table>{data.nodes.map(node=><ConnectorStatus status={node.details.status} />)}</table></TableCell>

            //carbon apps
            case 'version':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.details.version} />)}</table></TableCell>

            case 'size':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.size} />)}</table></TableCell>
            
            case 'data_source_status':
                return <TableCell><table>{data.nodes.map(node=><StatusCell data={node.data_source_status} />)}</table></TableCell>
            case 'port':
                return <TableCell>{data.nodes.map(node=><StringCell data={node.port}/>)}</TableCell>

            // log-configs page
            case 'componentName':
                return <TableCell><StringCell data={data.componentName}/></TableCell>
            
            case 'level':
                return <TableCell><LogConfigLevelDropDown selectedNode={pageInfo.additionalParams.selectedNodeId} name={data.name} level={data.level}/></TableCell>

            // users page
            case 'userId':
                return <TableCell>{data.userId}</TableCell>
            case 'isAdmin':
                return <TableCell>{data.details.isAdmin ? <AdminIcon style={{color:"green"}}/> : <NonAdminIcon style={{color:"red"}}/>}</TableCell>
            case 'action':
                return <TableCell><UserDeleteAction userId={data.userId}/></TableCell>
            
            // Node page
            case 'nodeId':
                return <TableCell><table><NodesCell pageId={pageId} nodeData={data}/></table></TableCell>
            case 'node_status':
                return <TableCell>Active</TableCell>
            case 'role':
                return <TableCell>Member</TableCell>

            // Log Files Page
            case 'nodes_logs':
                return <TableCell><table>{data.nodes.map(node=><LogsNodeCell nodeId={node.nodeId} fileName={data.name} />)}</table></TableCell>
            case 'log_size':
                return <TableCell><table>{data.nodes.map(node=><StringCell data={node.logSize} />)}</table></TableCell>
            default:
                <TableCell>Table data not available</TableCell>
        }})}
    </TableRow>
}

function StringCell(props) {
    var data = props.data
    return <tr><td>{data}</td></tr>
}

function LinkCell(props) {
    const classes = useStyles();
    var data = props.data
    return <tr><td><a className={classes.tableCell} href={data}>{data}</a></td></tr>
}

function ConnectorStatus(props) {
    return (
        <tr>
            {props.status === 'enabled' ? <EnabledIcon style={{color:"green"}}/> : <DisabledIcon style={{color:"red"}}/>}
        </tr>
    )
}

function SwitchStatusCell(props) {
    const { pageId, artifactName, nodeId, status } = props;
    var isActive = status;
    const globalGroupId = useSelector(state => state.groupId);

    const changeState = () => {
        isActive = !isActive
        updateArtifact()
    };

    const updateArtifact = () => {
        const url = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/").concat(pageId);
        axios.patch(url, {
            "artifactName": artifactName,
            "nodeId": nodeId,
            "type": "status",
            "value": isActive
        });
    }

    return <tr><td><Switch checked={isActive} onChange={changeState} height={16} width={36} /></td></tr>
}

function LogConfigLevelDropDown(props) {
    const { selectedNode, name } = props;
    const globalGroupId = useSelector(state => state.groupId);

    var [ level, setLevel ] = React.useState(
                { label: props.level, value: props.level }
        );
    var [ tmpLevel, setTmpLevel ] = React.useState(
            { label: props.level, value: props.level }
    );

    const [confirmationDialog, setConfirmationDialog] = React.useState({
        open : false,
        title: '',
        message: ''
    });

    const [completionStatusDialog, setCompletionStatusDialog] = React.useState({
        open : false,
        title: '',
        message: ''
    });

    React.useEffect(()=>{
        setLevel({ label: props.level, value: props.level })
    },[props.level]);

    const options = [
        {
            label: 'OFF',
            value: 'OFF'
        },
        {
            label: 'TRACE',
            value: 'TRACE'
        },
        {
            label: 'DEBUG',
            value: 'DEBUG'
        },
        {
            label: 'INFO',
            value: 'INFO'
        },
        {
            label: 'WARN',
            value: 'WARN'
        },
        {
            label: 'ERROR',
            value: 'ERROR'
        },
        {
            label: 'FATAL',
            value: 'FATAL'
        }
    ];

    const handleConfirmationDialogClose = () => {
        setConfirmationDialog({
            open: false,
            title: '',
            message: ''
        })
    }

    const handlecompletionStatusDialogClose = () => {
        setCompletionStatusDialog({
            open: false,
            title: '',
            message: ''
        })
    }

    const changeLogLevel = (loggerLevel) => {
        setTmpLevel(loggerLevel)
        var message = 'Are you sure you want to change '.concat(name).concat(' log level to ').concat(loggerLevel.value).concat('?')
        setConfirmationDialog({
            open: true,
            title: 'Confirmation',
            message: message
        })
    }

    const updateAllNodes = () => {
        handleConfirmationDialogClose();
        const url = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/log-configs");
        axios.patch(url, {
            "name": name,
            "level": tmpLevel.value
        }).then(response => {
            if (response.data.status === 'success') {
                setLevel(tmpLevel)
                setCompletionStatusDialog({
                    open: true, 
                    title: 'Success',
                    message: "Successfully completed log config change."
                })
            } else {
                setCompletionStatusDialog({
                    open: true, 
                    title: 'Error',
                    message: response.data.message
                })
            }
        })
    }

    const updateSelected = () => {
        handleConfirmationDialogClose();
        const url = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/log-configs/nodes/".concat(selectedNode));
        axios.patch(url, {
            "name": name,
            "level": tmpLevel.value
        }).then(response => {
            if (response.data.status === 'success') {
                setLevel(tmpLevel)
                setCompletionStatusDialog({
                    open: true, 
                    title: 'Success',
                    message: "Successfully completed log config change."
                })
            } else {
                setCompletionStatusDialog({
                    open: true, 
                    title: 'Error',
                    message: response.data.message
                })
            }
        })
    }

    return <div><ReactSelect
                value={level}
                onChange={(e) => changeLogLevel(e)}
                options={options}
            />
            <Dialog open={confirmationDialog.open} onClose={() => handleConfirmationDialogClose()}
                                    aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">{confirmationDialog.title}</DialogTitle>

                <DialogContent dividers>
                    <DialogContentText id="alert-dialog-description">
                        {confirmationDialog.message}
                    </DialogContentText>
                </DialogContent>

                <DialogActions>
                    <Button onClick={() => updateAllNodes()} variant="contained" autoFocus>
                        UPDATE ALL NODES
                    </Button>

                    {selectedNode !== 'All' && <Button onClick={() => updateSelected()} variant="contained" autoFocus>
                        UPDATE ONLY {selectedNode}
                    </Button>}

                    <Button onClick={() => handleConfirmationDialogClose()} variant="contained" autoFocus>
                        CANCEL
                    </Button>
                </DialogActions>
            </Dialog>

            <Dialog open={completionStatusDialog.open} onClose={() => handlecompletionStatusDialogClose()}
                                    aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">{completionStatusDialog.title}</DialogTitle>

                <DialogContent dividers>
                    <DialogContentText id="alert-dialog-description">
                        {completionStatusDialog.message}
                    </DialogContentText>
                </DialogContent>

                <DialogActions>
                    <Button onClick={() => handlecompletionStatusDialogClose()} variant="contained" autoFocus>
                        OK
                    </Button>
                </DialogActions>
                
            </Dialog>
        </div>
}

function UserDeleteAction(props) {
    const userId = props.userId;
    console.log(userId)
    const globalGroupId = useSelector(state => state.groupId);
    const dispatch = useDispatch();

    const [confirmationDialog, setConfirmationDialog] = React.useState({
        open : false,
        title: '',
        message: ''
    });

    const [completionStatusDialog, setCompletionStatusDialog] = React.useState({
        open : false,
        title: '',
        message: ''
    });

    const handleConfirmationDialogClose = () => {
        setConfirmationDialog({
            open: false,
            title: '',
            message: ''
        })
    }

    const handlecompletionStatusDialogClose = () => {
        setCompletionStatusDialog({
            open: false,
            title: '',
            message: ''
        })
        const url = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/users");
        axios.get(url).then(response => {
            response.data.map(data => data.details = JSON.parse(data.details))
            dispatch(changeData(response.data))
        })
    }

    const confirmDelete = () => {
        var message = 'Are you sure you want to delete user '.concat(userId).concat('?')
        setConfirmationDialog({
            open: true,
            title: 'Confirmation',
            message: message
        })
    }

    const deleteUser = () => {
        console.log("deleting user", userId)
        handleConfirmationDialogClose();
        const url = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat("/users/").concat(userId);
        axios.delete(url).then(response => {
            if (response.data.status === 'success') {
                setCompletionStatusDialog({
                    open: true, 
                    title: 'Success',
                    message: "Successfully deleted user."
                })
            } else {
                setCompletionStatusDialog({
                    open: true, 
                    title: 'Error',
                    message: response.data.message
                })
            }
        })

    }

    return <div><tr><td>
        <Box display='flex' alignItems='center'>
            <DeleteIcon onClick={() => confirmDelete()}/> 
            Delete
        </Box>
        
    </td></tr>
            <Dialog open={confirmationDialog.open} onClose={() => handleConfirmationDialogClose()}
                                    aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">{confirmationDialog.title}</DialogTitle>

                <DialogContent dividers>
                    <DialogContentText id="alert-dialog-description">
                        {confirmationDialog.message}
                    </DialogContentText>
                </DialogContent>

                <DialogActions>
                    <Button onClick={() => deleteUser()} variant="contained" autoFocus>
                        CONFIRM
                    </Button>

                    <Button onClick={() => handleConfirmationDialogClose()} variant="contained" autoFocus>
                        CANCEL
                    </Button>
                </DialogActions>
            </Dialog>

            <Dialog open={completionStatusDialog.open} onClose={() => handlecompletionStatusDialogClose()}
                                    aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">{completionStatusDialog.title}</DialogTitle>

                <DialogContent dividers>
                    <DialogContentText id="alert-dialog-description">
                        {completionStatusDialog.message}
                    </DialogContentText>
                </DialogContent>

                <DialogActions>
                    <Button onClick={() => handlecompletionStatusDialogClose()} variant="contained" autoFocus>
                        OK
                    </Button>
                </DialogActions>
                
            </Dialog>
            </div>
}

const useStyles = makeStyles((theme) => ({
    tableCell : {
        paddingLeft: '15px',
        color: '#3f51b5'
    },
    formControl: {
        minWidth: 10,
        minHeight: 10
    }
}));
