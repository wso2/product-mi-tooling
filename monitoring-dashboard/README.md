# Monitoring Dashboard for WSO2 Micro Integrator

## Building from the source.

### Setting up the development environment.
1. Install Node.js [14.X.X](https://nodejs.org/en/download/releases/).
2. Fork the [Micro Integrator Tooling repository](https://github.com/wso2/product-mi-tooling).
3. Clone your fork into any directory.
4. Access the cloned directory and then navigate to `product-mi-tooling/monitoring-dashboard`. This 
   will be the <DASHBOARD_REPO> for future reference.
5. Run the script available by doing the following Apache Maven command.
```mvn clean install```
6. wso2mi-monitoring-dashboard-version.zip can be found in
 `<DASHBOARD_REPO>/distribution/target`.
 
### Running.
- Extract the compressed archive generated to a desired location.
    ```
    cd to the <DASHBOARD_HOME>/bin
    
    Execute dashboard.sh or dashboard.bat as appropriate.
    ```

- Load the login page with the dashboard context. i.e: https://localhost:9743/dashboard.
    
### Management API Address and Port
   The Management API address and Port is required when logging into the dashboard.
   NOTE: The default hostname is localhost and the port is 9164.
