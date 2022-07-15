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
import { Button, TableCell } from '@material-ui/core';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import FileCopyIcon from '@material-ui/icons/FileCopy';
import { makeStyles } from '@material-ui/core/styles';
import Tooltip from '@material-ui/core/Tooltip';

export default function CopyToClipboardCell(props) {
    const [copyMessage, setCopyMessage] = React.useState('Copy to Clipboard');

    const onCopy = () => {
        setCopyMessage('Copied');
        const caller = function () {
            setCopyMessage('Copy to Clipboard');
        };
        setTimeout(caller, 2000);
    }
    const text = props.text;
    const classes = useStyles();

    return <TableCell>{text}
        <CopyToClipboard
            text={text}
            className={classes.clipboard}
            onCopy={onCopy}
        >
            <Tooltip title={copyMessage}>
                <Button><FileCopyIcon /></Button>
            </Tooltip>
        </CopyToClipboard>
    </TableCell>
}

const useStyles = makeStyles((theme) => ({
    clipboard: {
        color: '#3f51b5'
    }
}));
