import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import { TableCell } from "@material-ui/core/";
import DownloadIcon from '@material-ui/icons/GetAppRounded';
import { useSelector } from 'react-redux';
import HTTPClient from '../utils/HTTPClient';

export default function LogsNodeCell(props) {
    const globalGroupId = useSelector(state => state.groupId);
    const { nodeId, fileName } = props;
    function downloadLog() {
        const resourcePath = '/groups/'.concat(globalGroupId).concat('/nodes/').concat(nodeId).concat('/logs/').concat(fileName);
        HTTPClient.get(resourcePath).then(response => {
            var element = document.createElement('a');
            element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(response.data));
            element.setAttribute('download', fileName);
            element.style.display = 'none';
            document.body.appendChild(element);
            element.click();
            document.body.removeChild(element);
        })
    }

    const classes = useStyles();
    return <div>
                <TableCell className={classes.tableCell}>{nodeId}<DownloadIcon onClick={() => downloadLog()} className={classes.icon}/></TableCell>
            </div>
}

const useStyles = makeStyles(() => ({
    icon : {
        color: '#3f51b5',
        cursor: "pointer",
        padding: '1px'
    },
    tableCell : {
        padding: '1px',
        borderBottom: 'none',
        display: 'flex',
    }
}));