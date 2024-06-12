/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import {Paper, TableBody, TableCell, TableHead, TableRow, Typography} from "@material-ui/core";
import {withStyles} from "@material-ui/core/styles";
import Link from "@material-ui/core/Link";
import React from "react";
import Table from "@material-ui/core/Table";
import {useHistory} from "react-router-dom";

const styles = theme => ({
    policyContent: {
        paddingLeft: '25%',
        paddingRight: '25%',
        paddingTop: '3%',
        paddingBottom: '3%',
        fontsize: '14px'
    },
});

function CookiePolicy(props) {

    const {classes} = props;
    const history = useHistory();
    return (
        <Paper className={classes.policyContent}>

            <Typography variant="h3">WSO2 Integration Control Plane - Cookie Policy</Typography>
            <Link variant="h4"
                  href="https://apim.docs.wso2.com/en/latest/observe/mi-observe/working-with-monitoring-dashboard/"
                  style={{color: "#ff5000"}}>WSO2 Integration Control Plane</Link>

            <Typography>
                WSO2 Integration Control Plane monitors running Micro Integrator instances (Single or Cluster
                Mode) and facilitates
                performing various management and administration tasks related to deployed artifacts.
            </Typography>

            <h2 id="cookie-policy">Cookie Policy</h2>
            <Typography>
                WSO2 Integration Control Plane uses cookies so that it can provide the best user experience for
                you and identify you for security purposes. If you disable cookies, some of the services will be
                inaccessible to you.
            </Typography>

            <h2 id="how-wso2-is-5.8.0-processes-cookies">How does WSO2 Integration Control Plane process
                cookies?</h2>
            <Typography>
                WSO2 Integration Control Plane stores and retrieves information on your browser using cookies.
                This information is used to provide a better experience. Some cookies serve the primary purposes
                of allowing a user to
                log in to the system, maintaining sessions, and keeping track of activities you do within the
                login session.
            </Typography>
            <Typography>
                The primary purpose of some cookies used in WSO2 Integration Control Plane is to personally
                identify you. However the cookie lifetime ends once your session
                ends i.e., after you log-out, or after the session expiry time has elapsed.
            </Typography>
            <Typography>
                Some cookies are simply used to give you a more personalized web experience and these cookies
                can not be used to personally identify you or your activities.
            </Typography>
            <Typography>
                This cookie policy is part of the <Link component="button"
                                                        variant="body1"
                                                        onClick={() => {
                                                            history.push('/privacy-policy')
                                                        }}
                                                        style={{color: "#ff5000"}}>WSO2 Integration Control Plane
                Privacy Policy.</Link>
            </Typography>

            <h2 id="what-is-a-cookie">What is a cookie?</h2>
            <Typography>
                A browser cookie is a small piece of data that is stored on your device to help websites and
                mobile apps remember things about you. Other technologies, including web storage and identifiers
                associated with your device, may be used for similar purposes. In this policy, we use the
                term &ldquo;cookies&rdquo; to discuss all of these technologies.
            </Typography>

            <h2 id="what-does-wso2-is-5.8.0-use-cookies-for">What does WSO2 Integration Control Plane use
                cookies for?</h2>
            <Typography>Cookies are used for two purposes in WSO2 Integration Control Plane.</Typography>
            <ol>
                <li>
                    <Typography>
                        To identify you and provide security (as this is the main function of WSO2
                        Integration Control Plane).
                    </Typography>
                </li>
                <li>
                    <Typography>
                        To provide a satisfying user experience.
                    </Typography><
                            /li>
            </ol>

            <Typography>WSO2 Integration Control Plane uses cookies for the following purposes listed
                below.</Typography>
            <h3 id="preferences">Preferences</h3>
            <Typography>
                WSO2 Integration Control Plane uses these cookies to remember your settings and preferences,
                and to auto-fill the form
                fields to make your interactions with the site easier.
            </Typography>
            <Typography>These cookies can not be used to personally identify you.</Typography>
            <h3 id="security">Security</h3>
            <ul>
                <li>
                    <Typography>
                        WSO2 Integration Control Plane uses selected cookies to identify and prevent security
                        risks. For example, WSO2 Integration Control Plane may use these cookies to store your
                        session
                        information in order to prevent others from changing your password without your username
                        and password.
                    </Typography>
                    <br/><br/>
                </li>
                <li>
                    <Typography>
                        WSO2 Integration Control Plane uses session cookies to maintain your active
                        session.
                    </Typography>
                    <br/><br/>
                </li>
                <li>
                    <Typography>
                        WSO2 Integration Control Plane may use temporary cookies when performing multi-factor
                        authentication and
                        federated authentication.
                    </Typography>
                    <br/><br/>
                </li>
                <li>
                    <Typography>
                        WSO2 Integration Control Plane may use permanent cookies to detect that you have
                        previously
                        used the same device to log in. This is to to calculate the &ldquo;risk
                        level&rdquo; associated
                        with your current login attempt. This is primarily to protect you and your account
                        from possible attack.
                    </Typography>
                </li>
            </ul>
            <h3 id="performance">Performance</h3>
            <Typography>WSO2 Integration Control Plane may use cookies to allow &ldquo;Remember
                Me&rdquo; functionalities.</Typography>
            <h3 id="analytics">Analytics</h3>
            <Typography>WSO2 Integration Control Plane as a product does not use cookies for analytical
                purposes.</Typography>

            <h2 id="what-type-of-cookies-does-5.8.0-use">What type of cookies does WSO2 Integration Control
                 Plane use?</h2>
            <Typography>WSO2 Integration Control Plane uses persistent cookies and session cookies. A
                persistent cookie helps WSO2 Integration Control Plane to
                recognize you as an existing user so that it is easier to return to WSO2 or interact with WSO2
                IS without signing in again. After you sign in, a persistent cookie stays in your browser and
                will be read by WSO2 Integration Control Plane when you return to WSO2 Integration Control
                Plane.</Typography>
            <Typography>A session cookie is a cookie that is erased when the user closes the web browser. The
                session
                cookie is stored in temporary memory and is not retained after the browser is closed. Session
                cookies do not collect information from the user's computer.</Typography>

            <h2 id="how-do-i-control-my-cookies">How do I control my cookies?</h2>
            <Typography>
                Most browsers allow you to control cookies through their settings preferences. However, if you
                limit the given ability for websites to set cookies, you may worsen your overall user experience
                since it will no longer be personalized to you. It may also stop you from saving customized
                settings like login information.
            </Typography>
            <Typography>
                Most likely, disabling cookies will make you unable to use authentication and authorization
                functionalities in WSO2 Integration Control Plane.
            </Typography>
            <Typography>
                If you have any questions or concerns regarding the use of cookies, please contact the entity or
                individuals (or their data protection officer, if applicable) of the organization running this
                WSO2 Integration Control Plane instance.
            </Typography>

            <h2 id="what-are-the-cookies-used">What are the cookies used?</h2>
            <Table className="ui celled table">
                <TableHead>
                    <TableRow>
                        <TableCell>
                            <Typography>Cookie Name</Typography>
                        </TableCell>
                        <TableCell>
                            <Typography>Purpose</Typography>
                        </TableCell>
                        <TableCell>
                            <Typography>Retention</Typography>
                        </TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    <TableRow>
                        <TableCell>
                            <Typography>SESSION_USER_COOKIE</Typography>
                        </TableCell>
                        <TableCell>
                            <Typography>To keep the information of the logged in user.</Typography>
                        </TableCell>
                        <TableCell>
                            <Typography>Session</Typography>
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>
                            <Typography>JWT_TOKEN_COOKIE</Typography>
                        </TableCell>
                        <TableCell>
                            <Typography>To keep the security token of the active session.</Typography>
                        </TableCell>
                        <TableCell>
                            <Typography>Session</Typography>
                        </TableCell>
                    </TableRow>
                </TableBody>
            </Table>

            <h2 id="disclaimer">Disclaimer</h2>
            <Typography>
                This cookie policy is only for the illustrative purposes of the product WSO2 Integration Control
                Plane. The content in
                the policy is technically correct at the time of the product shipment. The organization which
                runs
                this WSO2 Integration Control Plane instance has full authority and responsibility with regard
                to the effective Cookie
                Policy.
            </Typography>
            <Typography>
                WSO2, its employees, partners, and affiliates do not have access to and do not require, store,
                process or control any of the data, including personal data contained in WSO2 Integration Control
                Plane. All data,
                including personal data is controlled and processed by the entity or individual running the
                dashboard.
                WSO2, its employees, partners and affiliates are not a data processor or a data controller
                within
                the meaning of any data privacy regulations. WSO2 does not provide any warranties or undertake
                any
                responsibility or liability in connection with the lawfulness or the manner and purposes for
                which WSO2 Integration Control Plane is used by such entities or persons.
            </Typography>
        </Paper>
    );
}

export default withStyles(styles)(CookiePolicy);
