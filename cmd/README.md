# CLI for WSO2 Integrator Runtime

Command Line tool for managing the WSO2 Integrator Runtime.

## Getting Started

### Building from source 

- ### Setting up the development environment
    1. Install [Go 1.12.x](https://golang.org/dl).
    2. Fork the [repository](https://github.com/wso2/product-mi-tooling).
    3. Clone your fork into any directory.
    5. Access the cloned directory and then navigate to `product-mi-tooling/cmd`.
    6. Execute `go mod vendor` or `go mod download` to download all the dependencies.
    
- ### Building from source
    Navigate to the product-mi-tooling home directory

    Execute `make install-cli VERSION=VESION_OF_CHOICE` to build the **Integrator Runtime CLI**.

    Created Command Line tool packages will be available at cmd/build directory.

- ### Running
    Extract the compressed archive generated to a desired location.
    
    Then execute `./ei` to start the application.
    
    Execute `./ei --help` for further instructions.

    NOTE: To execute the tool from anywhere, append the location of the executable (ei) to your $PATH variable.

- ### Command Autocompletion (For Bash Only)
    Copy the file `ei_bash_completion.sh` to `/etc/bash_completion.d/` and source it with `source 
    /etc/bash_completion.d/ei_bash_completion.sh` to enable bash auto-completion.

### Configuration 

- ### Management API Address and Port
    To configure the address and the port of the Management Api in the CLI use the [**remote**](#remote) command. If no configuration is done, the address and the port will have the default values

    NOTE: The default hostname is localhost and the port is 9164.

## Usage 
```bash
     ei [command]
```

#### Global Flags
```bash
    --verbose
        Enable verbose logs (Provides more information on execution)
    --help, -h
        Display information and example usage of a command
```

### Commands
   * #### remote
```bash
        Usage:
            ei remote [command] [arguments]
                       
        Available Commands:
            add [name] [host] [port]        Add a Integrator Runtime
            remove [name]                   Remove a Integrator Runtime
            update [name] [host] [port]     Update a Integrator Runtime
            select [name]                   Select a Integrator Runtime instance on which commands are to be executed
            show                            Show added Integrator Runtime instances
            login                           Login to the current Integrator Runtime instance
            logout                          Logout of the current Integrator Runtime instance

        Examples:
            # To add a Integrator Runtime
            ei remote add TestServer 192.168.1.15 9164
            
            # To remove a Integrator Runtime
            ei remote remove TestServer
            
            # To update a Integrator Runtime
            ei remote update TestServer 192.168.1.17 9164
            
            # To select a Integrator Runtime
            ei remote select TestServer
            
            # To show available Integrator Runtimes
            ei remote show
            
            # Get information about a specific Integrator Runtime instance
            ei remote show default
            ei remote show TestServer
            

            
            # login to the current (selected)  Integrator Runtime instance
            ei remote login     # will be prompted for username and password
            
            # login (with inline username and password)
            ei remote login username password # or
```
   * #### log-level
```bash
        Usage:
            ei log-level [command] [arguments]

        Available Commands:
            show [logger-name]                   Show information about a logger
            update [logger-name] [log-level]     Update the log level of a logger

        Examples:
            # Show information about a logger
            ei log-level show org.apache.coyote

            # Update the log level of a logger
            ei log-level update org.apache.coyote DEBUG
```
   * #### api
```bash
        Usage:
            ei api [command] [argument]

        Available Commands:
            show [api-name]                      Get information about one or more Apis

        Examples:
            # To List all the apis
            ei api show

            # To get details about a specific api
            ei api show sampleApi
```
   * #### compositeapp
```bash
        Usage:
            ei compositeapp [command] [argument]

        Available Commands:
            show [app-name]                      Get information about one or more Composite apps

        Examples:
            # To List all the composite apps
            ei compositeapp show

            # To get details about a specific composite app
            ei compositeapp show sampleApp
```
   * #### endpoint
```bash
        Usage:
            ei endpoint [command] [argument]

        Available Commands:
            show [endpoint-name]                 Get information about one or more Endpoints

        Examples:
            # To List all the endpoints
            ei endpoint show

            # To get details about a specific endpoint
            ei endpoint show sampleEndpoint
```
   * #### inboundendpoint
```bash
        Usage:
            ei inboundendpoint [command] [argument]

        Available Commands:
            show [inboundendpoint-name]          Get information about one or more Inbounds

        Examples:
            # To List all the inbound endpoints
            ei inboundendpoint show

            # To get details about a specific inbound endpoint
            ei inboundendpoint show sampleEndpoint
```
   * #### proxyservice
```bash
        Usage:
            ei proxyservice [command] [argument]

        Available Commands:
            show [proxyservice-name]             Get information about one or more Proxies

        Examples:
            # To List all the proxy services
            ei proxyservice show

            # To get details about a specific proxy service
            ei proxyservice show sampleProxy
```
   * #### sequence
```bash
        Usage:
            ei sequence [command] [argument]

        Available Commands:
            show [sequence-name]                 Get information about one or more Sequences

        Examples:
            # To List all the sequences
            ei sequence show

            # To get details about a specific sequence
            ei sequence show sampleProxy
```
   * #### task
```bash
        Usage:
            ei task [command] [argument]

        Available Commands:
            show [task-name]                     Get information about one or more Tasks

        Examples:
            # To List all the tasks
            ei task show

            # To get details about a specific task
            ei task show sampleProxy
```
   * #### dataservice
```bash
        Usage:
            ei dataservice [command] [argument]

        Available Commands:
            show [data-service-name]             Get information about one or more Dataservices

        Examples:
            # To List all the dataservices
            ei dataservice show

            # To get details about a specific task
            ei dataservice show SampleDataService
```
   * #### connectors
 ```bash
         Usage:
             ei connector [command]
 
         Available Commands:
             show             Get information about the connectors
 
         Examples:
             # To List all the connectors
             ei connector show
 ```
   * #### templates
 ```bash
         Usage:
             ei template [command] [template-type] [template-name]
 
         Available Commands:
             show  [template-type]                  Get information about the given template type
             show  [template-type] [template-name]  Get information about the specific template
 
         Examples:
             # To List all the templates
             ei template show

             # To List all the templates of given template type
             ei template show endpoint

             # To get details about a specific template
             ei template show endpoint sampleTemplate
 ```
   * #### messageprocessor
 ```bash
         Usage:
             ei messageprocessor [command] [messageprocessor-name]
 
         Available Commands:
             show  [messageprocessor-name]  Get information about one or more Message Processor
 
         Examples:
             # To List all the message processor
             ei messageprocessor show

             # To get details about a specific message processor
             ei messageprocessor show  sampleMessageProcessor
 ```
   * #### messagestore
 ```bash
         Usage:
             ei messagestore [command] [messagestore-name]
 
         Available Commands:
             show  [messagestore-name]  Get information about one or more Message Store
 
         Examples:
             # To List all the message store
             ei messagestore show

             # To get details about a specific message store
             ei messagestore show  sampleMessageStore
 ```
   * #### localentry
 ```bash
         Usage:
             ei localentry [command] [localentry-name]
 
         Available Commands:
             show  [localentry-name]  Get information about one or more Local Entries
 
         Examples:
             # To List all the local entries
             ei localentry show

             # To get details about a specific local entry
             ei localentry show  sampleLocalEntry
 ```
   * #### version
```bash
        ei version 
```
