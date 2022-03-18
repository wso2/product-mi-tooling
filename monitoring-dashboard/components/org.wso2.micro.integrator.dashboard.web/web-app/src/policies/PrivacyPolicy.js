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

import {withStyles} from "@material-ui/core/styles";
import {Paper, Typography} from "@material-ui/core";
import React from "react";
import Link from "@material-ui/core/Link";
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

function PrivacyPolicy(props) {
    const {classes} = props;
    const history = useHistory();

    return (
        <Paper className={classes.policyContent}>
            <Typography variant="h3">WSO2 Micro Integrator Dashboard - Privacy Policy</Typography>
            <Link variant="h4"
                  href="https://apim.docs.wso2.com/en/latest/observe/mi-observe/working-with-monitoring-dashboard/"
                  style={{color: "#ff5000"}}>WSO2 Micro Integrator Dashboard</Link>

            <Typography>
                WSO2 Micro Integrator Dashboard monitors running Micro Integrator instances (Single or Cluster
                Mode) and facilitates
                performing various management and administration tasks related to deployed artifacts.
            </Typography>

            <h2 id="privacy-policy">Privacy Policy</h2>
            <Typography>This policy describes how WSO2 Micro Integrator Dashboard captures your personal information,
                the purposes of
                collection, and information about the retention of your personal information.</Typography>
            <Typography>Please note that this policy is for reference only, and is applicable for the software
                as a product. WSO2 Inc. and its developers have no access to the information held within WSO2 Micro
                Integrator Dashboard.
                Please see the Disclaimer section for more information
            </Typography>
            <Typography>Entities, organizations or individuals controlling the use and administration of WSO2 Micro
                Integrator Dashboard
                should
                create their own privacy policies setting out the manner in which data is controlled or
                processed by the respective entity, organization or individual.</Typography>

            <h2 id="what-is-personal-information">What is personal information?</h2>
            <Typography>WSO2 Micro Integrator Dashboard considers anything related to you, and by which you may be
                identified, as your
                personal information. This includes, but is not limited to:
            </Typography>
            <ul>
                <li>
                    <Typography>
                        Your user name (except in cases where the user name created by your employer is under contract)
                    </Typography>
                </li>
                <li><Typography>Your date of birth/age</Typography></li>
                <li><Typography>IP address used to log in</Typography></li>
                <li><Typography>Your device ID if you use a device (e.g., phone or tablet) to log
                    in</Typography></li>
            </ul>
            <Typography>However, WSO2 Micro Integrator Dashboard also collects the following information that is not
                considered personal
                information, but is used only for <strong>statistical</strong> purposes. The reason for this is
                that this information can not be used to track you.</Typography>
            <ul>
                <li><Typography> City/Country from which you originated the TCP/IP connection </Typography></li>
                <li><Typography>Time of the day that you logged in (year, month, week, hour or
                    minute) </Typography></li>
                <li><Typography>Type of device that you used to log in (e.g., phone or tablet)</Typography></li>
                <li><Typography>Operating system and generic browser information</Typography></li>
            </ul>

            <h2 id="collection-of-personal-information">Collection of personal information</h2>
            <Typography>WSO2 Micro Integrator Dashboard collects your information only to serve your access
                requirements. For example:
                <ul>
                    <li><Typography>WSO2 Micro Integrator Dashboard uses your IP address to detect any suspicious login
                        attempts to your
                        account.</Typography></li>
                    <li><Typography>WSO2 Micro Integrator Dashboard uses attributes like your first name, last name,
                        etc., to provide a
                        rich and personalized user experience.</Typography></li>
                    <li><Typography>WSO2 Micro Integrator Dashboard uses your security questions and answers only to
                        allow account
                        recovery.</Typography></li>
                </ul>
            </Typography>
            <h3 id="tracking-technologies">Tracking Technologies</h3>
            <Typography>WSO2 Micro Integrator Dashboard collects your information by:</Typography>
            <Typography>
                <ul>
                    <li>Collecting information from the user profile page where you enter your personal data.
                    </li>
                    <li>Tracking your IP address with HTTP request, HTTP headers, and TCP/IP.</li>
                    <li>Tracking your geographic information with the IP address.</li>
                    <li>Tracking your login history with browser cookies. Please see our <Link component="button" variant="body1"
                                                                                               onClick={() => {history.push('/cookie-policy')}}
                                                                                               style={{color: "#ff5000"}}> cookie policy</Link> for more information.
                    </li>
                </ul>
            </Typography>

            <h2 id="user-of-personal-information">Use of personal information</h2>
            <Typography>WSO2 Micro Integrator Dashboard will only use your personal information for the purposes for
                which it was
                collected (or for a use identified as consistent with that purpose).</Typography>
            <Typography>WSO2 Micro Integrator Dashboard uses your personal information only for the following
                purposes.</Typography>
            <Typography>
                <ul>
                    <li>To provide you with a personalized user experience. WSO2 Micro Integrator Dashboard uses your
                        name and uploaded
                        profile pictures for this purpose.
                    </li>
                    <li>To protect your account from unauthorized access or potential hacking attempts. WSO2 Micro
                        Integrator Dashboard
                        uses HTTP or TCP/IP Headers for this purpose.
                    </li>
                    <ul>
                        <li>This includes:</li>
                        <ul>
                            <li>IP address</li>
                            <li>Browser fingerprinting</li>
                            <li>Cookies</li>
                        </ul>
                    </ul>
                    <li>Derive statistical data for analytical purposes on system performance improvements. WSO2
                        IS will not keep any personal information after statistical calculations. Therefore, the
                        statistical report has no means of identifying an individual person.
                    </li>
                    <ul>
                        <li>WSO2 Micro Integrator Dashboard may use:</li>
                        <ul>
                            <li>IP Address to derive geographic information</li>
                            <li>Browser fingerprinting to determine the browser technology or/and version</li>
                        </ul>
                    </ul>
                </ul>
            </Typography>

            <h2 id="disclosure-of-personal-information">Disclosure of personal information</h2>
            <Typography>WSO2 Micro Integrator Dashboard only discloses personal information to the relevant applications
                (also known as
                “Service Providers”) that are registered with WSO2 Micro Integrator Dashboard. These applications are
                registered by the
                identity administrator of your entity or organization. Personal information is disclosed only
                for the purposes for which it was collected (or for a use identified as consistent with that
                purpose), as controlled by such Service Providers, unless you have consented otherwise or where
                it is required by law.</Typography>

            <h3 id="legal-process">Legal process</h3>
            <Typography>Please note that the organization, entity or individual running WSO2 Micro Integrator Dashboard
                may be compelled
                to disclose your personal information with or without your consent when it is required by law
                following due and lawful process.</Typography>

            <h2 id="storage-of-personal-information">Storage of personal information</h2>

            <h3 id="where-your-personal-information-stored">Where your personal information is stored</h3>
            <Typography>WSO2 Micro Integrator Dashboard stores your personal information in secured databases. WSO2
                Micro Integrator Dashboard exercises proper
                industry accepted security measures to protect the database where your personal information is
                held. WSO2 Micro Integrator Dashboard as a product does not transfer or share your data with any third
                parties or
                locations. </Typography>
            <Typography>
                WSO2 Micro Integrator Dashboard may use encryption to keep your personal data with an added level of
                security.
            </Typography>

            <h3 id="how-long-does-is-5.5-keep-your-personal-information">How long your personal information is
                retained</h3>
            <Typography>WSO2 Micro Integrator Dashboard retains your personal data as long as you are an active user of
                our system. You
                can update your personal data at any time using the given self-care user portals.</Typography>
            <Typography>WSO2 Micro Integrator Dashboard may keep hashed secrets to provide you with an added level of
                security. This
                includes:</Typography>
            <ul>
                <li><Typography>Current password</Typography></li>
                <li><Typography>Previously used passwords</Typography></li>
            </ul>

            <h3 id="how-to-request-removal-of-your-personal-information">How to request removal of your personal
                information</h3>
            <Typography>You can request the administrator to delete your account. The administrator is the
                administrator of the organization you are registered under, or the super-administrator if
                you do not use the organization feature.</Typography>
            <Typography>Additionally, you can request to anonymize all traces of your activities that WSO2 Micro
                Integrator Dashboard
                may have retained in logs, databases or analytical storage.</Typography>

            <h2 id="more-information">More information</h2>

            <h3 id="changes-to-this-policy">Changes to this policy</h3>
            <Typography>Upgraded versions of WSO2 Micro Integrator Dashboard may contain changes to this policy and
                revisions to this
                policy will
                be packaged within such upgrades. Such changes would only apply to users who choose to use
                upgraded versions.</Typography>
            <Typography>The organization running WSO2 Micro Integrator Dashboard may revise the Privacy Policy from time
                to time. You
                can find
                the most recent governing policy with the respective link provided by the organization running
                WSO2 Micro Integrator Dashboard. The organization will notify any changes to the privacy policy over our
                official public
                channels.</Typography>

            <h3 id="your-choices">Your choices</h3>
            <Typography>If you already have a user account within WSO2 Micro Integrator Dashboard, you have the
                right to deactivate your account if you find that this privacy policy is unacceptable to you.</Typography>
            <Typography>If you do not have an account and you do not agree with our privacy policy, you can choose not to
                create one.</Typography>

            <h3 id="contact-us">Contact us</h3>
            <Typography>Please contact WSO2 if you have any questions or concerns regarding this privacy policy.</Typography>
            <Typography><Link variant="body1"
                              href="https://wso2.com/contact/"
                              style={{color: "#ff5000"}}>https://wso2.com/contact/</Link></Typography>
            <h2 id="disclaimer">Disclaimer</h2>
            <ol>
                <li><Typography>WSO2, its employees, partners, and affiliates do not have access to and do not
                    require,
                    store, process or control any of the data, including personal data contained in WSO2 Micro
                    Integrator Dashboard. All
                    data, including personal data is controlled and processed by the entity or individual
                    running WSO2 Micro Integrator Dashboard. WSO2, its employees partners and affiliates are not a data
                    processor or a
                    data controller within the meaning of any data privacy regulations. WSO2 does not provide
                    any warranties or undertake any responsibility or liability in connection with the
                    lawfulness or the manner and purposes for which WSO2 Micro Integrator Dashboard is used by such
                    entities or persons.
                </Typography>
                </li>

                <li><Typography>This privacy policy is for the informational purposes of the entity or persons
                    running WSO2
                    IS and sets out the processes and functionality contained within WSO2 Micro Integrator Dashboard
                    regarding personal
                    data protection. It is the responsibility of entities and persons running WSO2 Micro Integrator
                    Dashboard to create
                    and administer its own rules and processes governing users’ personal data, and such rules
                    and processes may change the use, storage and disclosure policies contained herein.
                    Therefore users should consult the entity or persons running WSO2 Micro Integrator Dashboard for its
                    own privacy
                    policy for details governing users’ personal data.
                </Typography>
                </li>
            </ol>
        </Paper>
    );
}

export default withStyles(styles)(PrivacyPolicy);
