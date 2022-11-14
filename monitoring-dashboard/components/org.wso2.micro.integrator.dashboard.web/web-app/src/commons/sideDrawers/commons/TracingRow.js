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

import React, { useEffect, useState } from 'react';
import { TableCell, TableRow } from '@material-ui/core';
import { useSelector } from 'react-redux';
import Switch from "react-switch";
import HTTPClient from '../../../utils/HTTPClient';

export default function TracingRow(props) {
    const {pageId, artifactName, nodeId, tracing, retrieveUpdatedArtifact} = props;
    var isTracingEnabled = false;

    if(tracing === 'enabled') {
        isTracingEnabled = true;
    }

    const [tracingState, setTracingState] = useState(isTracingEnabled);
    
    const globalGroupId = useSelector(state => state.groupId);

    const changeTracingStatus = (checked) => {
        isTracingEnabled = checked;
        setTracingState(checked);
        updateArtifact();
    };

    const updateArtifact = () => {
        var payload = {
            "artifactName": artifactName,
            "nodeId": nodeId,
            "type": "tracing",
            "value": isTracingEnabled
        }
        HTTPClient.updateArtifact(globalGroupId, pageId, payload).then(response => {
            if (response.data.status === 'success') {
                retrieveUpdatedArtifact(nodeId, artifactName, isTracingEnabled);
            }
        });
    }

    useEffect(() => {
    },[tracingState]);

    return <TableRow>
                <TableCell>Tracing</TableCell>
                <TableCell>
                    <label>
                        <Switch checked={tracingState} onChange={changeTracingStatus} height={16} width={36} />
                    </label>
                </TableCell>
            </TableRow>
}
