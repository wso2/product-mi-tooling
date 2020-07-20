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
 */


import React, {Component} from 'react';
import Box from '@material-ui/core/Box';
import Typography from '@material-ui/core/Typography';
import PropTypes from 'prop-types';
import WarningIcon from '@material-ui/icons/Warning';
import defaultTheme from '../utils/Theme';

const styles = {
    titleSection: {
        paddingLeft: "10px"
    },
    box: {
        width: '100%',
        color: '#000000',
        backgroundColor: '#FF9800',
        height: '30px',
        paddingBottom: '20px',
        position: 'sticky'
    }
};

export default class InsecureWarnBanner extends Component {

    render() {
        return (
            <Box bgcolor={this.props.bgColor} color={this.props.color} style={styles.box}
                 zIndex={this.props.theme.zIndex.drawer + 100}>
                <Typography style={styles.titleSection}>
                    <WarningIcon/><b> Warning - Insecure connection to the server. This is not
                    recommended when deploying on production environments!</b>
                </Typography>
            </Box>

        );
    }
}

InsecureWarnBanner.propTypes = {
    title: PropTypes.string,
    bgColor: PropTypes.string,
    color: PropTypes.string,
    theme: PropTypes.shape({}),
};

InsecureWarnBanner.defaultProps = {
    title: 'No title',
    bgColor: "warning.main",
    color: "warning.contrastText",
    theme: defaultTheme,
};
