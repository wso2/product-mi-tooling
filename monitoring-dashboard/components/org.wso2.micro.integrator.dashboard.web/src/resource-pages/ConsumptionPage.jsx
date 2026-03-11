/*
 * Copyright (c) 2026, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import CircularProgress from '@material-ui/core/CircularProgress';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import DownloadIcon from '@material-ui/icons/CloudDownload';
import RefreshIcon from '@material-ui/icons/Refresh';
import ResourceExplorerParent from '../common/ResourceExplorerParent';
import ResourceAPI from '../utils/apis/ResourceAPI';

const styles = {
    downloadButton: {
        marginTop: 20,
        marginRight: 10,
        backgroundColor: '#065e9b',
        color: 'white',
        '&:hover': {
            backgroundColor: '#054a7a'
        }
    },
    dateField: {
        marginRight: 20,
        width: 200
    },
    dateRangeSection: {
        marginTop: 20,
        marginBottom: 20
    },
    error: {
        color: 'red'
    },
    infoText: {
        marginTop: 10,
        color: '#666',
        fontSize: '0.875rem'
    }
};

export default class ConsumptionPage extends Component {

    getDefaultDateRange() {
        const end = new Date();
        const start = new Date(end);
        start.setFullYear(end.getFullYear() - 1);

        return {
            startDate: this.formatDate(start),
            endDate: this.formatDate(end)
        };
    }

    formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    constructor(props) {
        super(props);
        const defaultDateRange = this.getDefaultDateRange();
        this.state = {
            downloading: false,
            startDate: defaultDateRange.startDate,
            endDate: defaultDateRange.endDate,
            dateError: '',
            dialogOpen: false,
            dialogMessage: ''
        };
        this.handleDownload = this.handleDownload.bind(this);
        this.handleDateChange = this.handleDateChange.bind(this);
        this.handleClearDates = this.handleClearDates.bind(this);
        this.validateDateRange = this.validateDateRange.bind(this);
        this.handleDialogClose = this.handleDialogClose.bind(this);
    }

    handleDialogClose() {
        this.setState({dialogOpen: false, dialogMessage: ''});
    }

    validateDateRange() {
        const {startDate, endDate} = this.state;
        
        if ((startDate && !endDate) || (!startDate && endDate)) {
            this.setState({dateError: 'Both start and end dates must be provided or leave both empty'});
            return false;
        }
        
        if (startDate && endDate) {
            const start = new Date(startDate);
            const end = new Date(endDate);
            
            if (start > end) {
                this.setState({dateError: 'Start date must be before or equal to end date'});
                return false;
            }
        }
        
        this.setState({dateError: ''});
        return true;
    }

    handleDateChange(field, value) {
        this.setState({[field]: value}, () => {
            this.validateDateRange();
        });
    }

    handleClearDates() {
        const defaultDateRange = this.getDefaultDateRange();
        this.setState({
            startDate: defaultDateRange.startDate,
            endDate: defaultDateRange.endDate,
            dateError: ''
        });
    }

    handleDownload() {
        if (!this.validateDateRange()) {
            return;
        }

        const {startDate, endDate} = this.state;
        this.setState({downloading: true});
        
        new ResourceAPI().downloadConsumptionData(startDate || null, endDate || null).then((response) => {
            // Create blob from binary ZIP response
            const blob = new Blob([response.data], { type: 'application/zip' });

            // Create download link
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;

            // Create filename with date range if provided
            let filename = 'consumption';
            if (startDate && endDate) {
                filename += `_${startDate}_to_${endDate}`;
            } else {
                filename += `_${Date.now()}`;
            }
            filename += '.zip';

            link.download = filename;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);

            this.setState({downloading: false});
        }).catch((error) => {
            const statusCode = error && error.response && error.response.status;
            const errorMessage = statusCode === 404
                ? 'This feature is not available in this edition.'
                : 'Cannot generate at the moment. Please try again later.';

            this.setState({
                downloading: false,
                dialogOpen: true,
                dialogMessage: errorMessage
            });
        });
    }

    renderConsumptionView() {
        const {downloading, startDate, endDate, dateError, dialogOpen, dialogMessage} = this.state;

        return (
            <div>
                <Typography variant="h5" gutterBottom>
                    Export Consumption Report
                </Typography>

                <div style={styles.dateRangeSection}>
                    <Typography variant="body2" style={styles.infoText}>
                        Select the date range for which you want to export the consumption data.
                    </Typography>

                    <div style={{marginTop: 20, display: 'flex', alignItems: 'center'}}>
                        <TextField
                            label="Start Date"
                            type="date"
                            value={startDate}
                            onChange={(e) => this.handleDateChange('startDate', e.target.value)}
                            style={styles.dateField}
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />
                        <TextField
                            label="End Date"
                            type="date"
                            value={endDate}
                            onChange={(e) => this.handleDateChange('endDate', e.target.value)}
                            style={styles.dateField}
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />
                        <Button
                            variant="outlined"
                            startIcon={<RefreshIcon />}
                            onClick={this.handleClearDates}
                            disabled={!startDate && !endDate}
                        >
                            Clear
                        </Button>
                    </div>

                    {dateError && (
                        <Typography variant="body2" style={{...styles.error, marginTop: 10}}>
                            {dateError}
                        </Typography>
                    )}
                </div>

                <Button
                    variant="contained"
                    startIcon={downloading ? <CircularProgress size={20} /> : <DownloadIcon />}
                    onClick={this.handleDownload}
                    disabled={downloading || !!dateError}
                    style={styles.downloadButton}
                >
                    {downloading ? 'Downloading...' : 'Download the Report'}
                </Button>

                <Dialog
                    open={dialogOpen}
                    onClose={this.handleDialogClose}
                    aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description"
                >
                    <DialogTitle id="alert-dialog-title">{"Action Failed"}</DialogTitle>
                    <DialogContent dividers>
                        <DialogContentText id="alert-dialog-description">
                            {dialogMessage}
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleDialogClose} variant="contained" autoFocus>
                            OK
                        </Button>
                    </DialogActions>
                </Dialog>
            </div>
        );
    }

    render() {
        return (
            <ResourceExplorerParent
                title='Consumption'
                content={this.renderConsumptionView()}
            />
        );
    }
}
