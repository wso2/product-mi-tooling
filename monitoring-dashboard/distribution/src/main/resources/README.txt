Monitoring Dashboard for WSO2 Micro Integrator
======================================================================

Welcome to the Monitoring Dashboard ${project.version} for WSO2 Micro Integrator ${mi.version}.
This is a lightweight UI server that hosts a React application which is used to monitor the Micro Integrator runtime.

Configure the MI servers
======================================================================
To connect the MI servers with the dashboard, add the following configuration to the deployment.toml file (stored in the <MI_HOME>/conf/ folder) of each server instance.

[dashboard_config]
dashboard_url = "https://{hostname/ip}:{9743}/dashboard/api/"

More information regarding the dashboard configurations can be found at
(https://apim.docs.wso2.com/en/latest/observe/mi-observe/working-with-monitoring-dashboard/)

Running the Monitoring Dashboard
======================================================================
1. Go to the DASHBOARD_HOME/bin directory and run the dashboard.sh file for Linux and Unix or the dashboard.bat file for Windows.
2. Access the dashboard login page found at https://localhost:9743/login

Known issues of WSO2 Micro Integrator ${mi.version}
======================================================================

     - https://github.com/wso2/micro-integrator/issues

Support
======================================================================

WSO2 Inc. offers a variety of development and production support
programs, ranging from Web-based support up through normal business
hours, to premium 24x7 phone support.

For additional support information please refer to http://wso2.com/support/

For more information on WSO2 Micro Integrator, visit the GitHub page (https://github.com/wso2/micro-integrator)

--------------------------------------------------------------------------------
(c) Copyright 2021 WSO2 Inc.