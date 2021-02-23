import React from 'react';
import axios from 'axios';
import { makeStyles } from '@material-ui/core/styles';
import TableCell from "@material-ui/core/TableCell";
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import { useSelector } from 'react-redux';
import AuthManager from '../auth/AuthManager';

export default function LogsNodeCell(props) {
    const globalGroupId = useSelector(state => state.groupId);
    const { nodeId, fileName } = props;
    const [open, setOpen] = React.useState(false);
    const [logContent, setLogContent] = React.useState("");

    function getLogContent() {
        const url = AuthManager.getBasePath().concat('/groups/').concat(globalGroupId).concat('/nodes/').concat(nodeId).concat('/logs/').concat(fileName);
        axios.get(url).then(response => {
            setLogContent(response.data)
        })
        openLogViewer();
    }

    const openLogViewer = () => {
        setOpen(true);
    }

    const closeLogViewer = () => {
        setOpen(false);
    };

    const descriptionElementRef = React.useRef(null);
    React.useEffect(() => {
        if (open) {
            const { current: descriptionElement } = descriptionElementRef;
            if (descriptionElement !== null) {
                descriptionElement.focus();
            }
        }
    }, [open]);

    const classes = useStyles();
    return <div>
                <TableCell onClick={() => getLogContent()} className={classes.tableCell}>{nodeId}</TableCell>
                <Dialog
                        open={open}
                        onClose={closeLogViewer}
                        aria-labelledby="scroll-dialog-title"
                        aria-describedby="scroll-dialog-description"
                        classes={classes.sourceView}
                    >
                        <DialogTitle id="scroll-dialog-title">{fileName} - {nodeId}</DialogTitle>
                        <DialogContent>
                            <DialogContentText
                                id="scroll-dialog-description"
                                ref={descriptionElementRef}
                                tabIndex={-1}>
                                <div>{logContent}</div>
                            </DialogContentText>
                        </DialogContent>
                    </Dialog>
            </div>
}

const useStyles = makeStyles(() => ({
    tableCell : {
        padding: '1px',
        borderBottom: 'none',
        color: '#3f51b5'
    }
}));