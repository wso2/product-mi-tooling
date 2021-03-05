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
import Badge from '@material-ui/core/Badge';
import {withStyles} from '@material-ui/core/styles';
import PropTypes from "prop-types";

const styles = theme => ({
    margin: {
        margin: theme.spacing(2)
    },
    greenBadge: {
        backgroundColor: "#00ff00",
        color: "white"
    },
    redBadge: {
        backgroundColor: "#ff0000",
        color: "white"
    }
});

function StatusCell(props) {
    const {classes} = props;
    const nodeData = props.data;
    var stateClass;
    if (nodeData === "Active") {
        stateClass = classes.greenBadge;
    } else {
        stateClass = classes.redBadge;
    }
    return <tr>
        <td><Badge classes={{badge: stateClass}}
                   className={classes.margin}
                   badgeContent={10} color="secondary" variant="dot"/>{nodeData}</td>
    </tr>;
}

export default withStyles(styles)(StatusCell);
StatusCell.propTypes = {
    classes: PropTypes.object.isRequired
};
