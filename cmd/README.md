# CLI for WSO2 Micro Integrator

Command Line tool for managing the WSO2 Micro Integrator.

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

    Execute `make install-cli VERSION=VESION_OF_CHOICE` to build the **MIcro Integrator CLI**.

    Created Command Line tool packages will be available at cmd/build directory.

- ### Running
    Extract the compressed archive generated to a desired location.
    
    Then execute `./mi` to start the application.
    
    Execute `./mi --help` for further instructions.

    NOTE: To execute the tool from anywhere, append the location of the executable (mi) to your $PATH variable.

- ### Command Autocompletion (For Bash Only)
    
    Copy the file `mi_bash_completion.sh` to `/etc/bash_completion.d/` and source it with `source /etc/bash_completion.d/mi_bash_completion.sh` to enable bash auto-completion.

### Configuration 

- ### Enabling the Management API

    By default, the Management API is enabled in Micro Integrator. If you have disabled it, you may have to re-enable it to use with the  CLI tool.

- ### Management API Address and Port
    To configure the address and the port of the Management Api in the CLI use the remote command. If no configuration is done, the address and the port will have the default values

    NOTE: The default hostname is localhost and the port is 9164.

### Usage
    
Have a look at the official documentation for the latest usage commands. https://ei.docs.wso2.com/en/7.1.0/micro-integrator/administer-and-observe/using-the-command-line-interface/
