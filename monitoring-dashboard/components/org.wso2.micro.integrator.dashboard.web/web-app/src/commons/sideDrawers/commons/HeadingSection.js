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

import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';

export default function HeadingSection(props) {
    const {name, nodeId} = props;
    const classes = useStyles();
    return <Paper className={classes.sideDrawerHeading} square>
                <Typography variant="h6" color="inherit" noWrap>
                    {name}
                </Typography>
                <Typography variant="h8" color="inherit" noWrap>
                    {nodeId}
                </Typography>
            </Paper>
}

const useStyles = makeStyles((theme) => ({
    sideDrawerHeading: {
        padding: theme.spacing(1),
        height: '72px',
        backgroundColor: theme.palette.background.appBar,
        color: '#ffffff',
        width: '100%',
        paddingLeft: 40,
        paddingTop: 20,
    }
}));
