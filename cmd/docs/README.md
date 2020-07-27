# WSO2 Micro Integrator Command Line tool

You can view and manage the Micro Integrator instances using the “mi” command line tool. Some of the usages of the command line tool include,
1. Get a list of deployed artifacts in the Micro Integrator runtime.
2. Inspect details of each runtime artifact such as a proxy service or an API.
3. Get the invocation endpoint of an artifact.
4. View and manage users in an external user store.
5. Update log levels and download log files.

### Running

1. Add the MI CLI bin folder to PATH

    `$ export PATH=/path/to/mi/cli/directory/bin:$PATH`

2. Then execute,

    `$ mi`

Execute mi --help for further instructions.

### Command Autocompletion (For Bash Only)

Copy the file `mi_bash_completion.sh` to `/etc/bash_completion.d/` and source it with `source /etc/bash_completion.d/mi_bash_completion.sh` to enable bash auto-completion.

### Configuration

- ### Enabling the Management API

    By default, the Management API is enabled in Micro Integrator. If you have disabled it, you may have to re-enable it to use with the  CLI tool.

- ### Management API Address and Port
    To configure the address and the port of the Management Api in the CLI use the remote command. If no configuration is done, the address and the port will have the default values

    NOTE: The default hostname is localhost and the port is 9164.

### Usage

Have a look at the official documentation for the latest usage commands. https://ei.docs.wso2.com/en/7.1.0/micro-integrator/administer-and-observe/using-the-command-line-interface/