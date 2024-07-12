#compdef _mi mi


function _mi {
  local -a commands

  _arguments -C \
    '(-h --help)'{-h,--help}'[help for mi]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]' \
    "1: :->cmnds" \
    "*::arg:->args"

  case $state in
  cmnds)
    commands=(
      "activate:Activate artifacts deployed in a Micro Integrator instance"
      "add:Add new users, roles or loggers to a Micro Integrator instance"
      "deactivate:Deactivate artifacts deployed in a Micro Integrator instance"
      "delete:Delete users or roles from a Micro Integrator instance"
      "get:Get information about artifacts deployed in a Micro Integrator instance"
      "help:Help about any command"
      "login:Login to a Micro Integrator"
      "logout:Logout from a Micro Integrator"
      "secret:Manage sensitive information"
      "update:Update log level of Loggers in a Micro Integrator instance"
      "version:Display Version on current mi"
    )
    _describe "command" commands
    ;;
  esac

  case "$words[1]" in
  activate)
    _mi_activate
    ;;
  add)
    _mi_add
    ;;
  deactivate)
    _mi_deactivate
    ;;
  delete)
    _mi_delete
    ;;
  get)
    _mi_get
    ;;
  help)
    _mi_help
    ;;
  login)
    _mi_login
    ;;
  logout)
    _mi_logout
    ;;
  secret)
    _mi_secret
    ;;
  update)
    _mi_update
    ;;
  version)
    _mi_version
    ;;
  esac
}


function _mi_activate {
  local -a commands

  _arguments -C \
    '(-h --help)'{-h,--help}'[help for activate]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]' \
    "1: :->cmnds" \
    "*::arg:->args"

  case $state in
  cmnds)
    commands=(
      "endpoint:Activate a endpoint deployed in a Micro Integrator"
      "help:Help about any command"
      "message-processor:Activate a message processor deployed in a Micro Integrator"
      "proxy-service:Activate a proxy service deployed in a Micro Integrator"
    )
    _describe "command" commands
    ;;
  esac

  case "$words[1]" in
  endpoint)
    _mi_activate_endpoint
    ;;
  help)
    _mi_activate_help
    ;;
  message-processor)
    _mi_activate_message-processor
    ;;
  proxy-service)
    _mi_activate_proxy-service
    ;;
  esac
}

function _mi_activate_endpoint {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator in which the endpoint should be activated]:' \
    '(-h --help)'{-h,--help}'[help for endpoint]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_activate_help {
  _arguments \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_activate_message-processor {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator in which the message processor should be activated]:' \
    '(-h --help)'{-h,--help}'[help for message-processor]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_activate_proxy-service {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator in which the proxy service should be activated]:' \
    '(-h --help)'{-h,--help}'[help for proxy-service]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}


function _mi_add {
  local -a commands

  _arguments -C \
    '(-h --help)'{-h,--help}'[help for add]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]' \
    "1: :->cmnds" \
    "*::arg:->args"

  case $state in
  cmnds)
    commands=(
      "env:Add Environment to Config file"
      "help:Help about any command"
      "log-level:Add new Logger to a Micro Integrator"
      "role:Add new role to a Micro Integrator"
      "user:Add new user to a Micro Integrator"
    )
    _describe "command" commands
    ;;
  esac

  case "$words[1]" in
  env)
    _mi_add_env
    ;;
  help)
    _mi_add_help
    ;;
  log-level)
    _mi_add_log-level
    ;;
  role)
    _mi_add_role
    ;;
  user)
    _mi_add_user
    ;;
  esac
}

function _mi_add_env {
  _arguments \
    '(-h --help)'{-h,--help}'[help for env]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_add_help {
  _arguments \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_add_log-level {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator to which a new logger should be added]:' \
    '(-h --help)'{-h,--help}'[help for log-level]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_add_role {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator to which a new user should be added]:' \
    '(-h --help)'{-h,--help}'[help for role]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_add_user {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator to which a new user should be added]:' \
    '(-h --help)'{-h,--help}'[help for user]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}


function _mi_deactivate {
  local -a commands

  _arguments -C \
    '(-h --help)'{-h,--help}'[help for deactivate]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]' \
    "1: :->cmnds" \
    "*::arg:->args"

  case $state in
  cmnds)
    commands=(
      "endpoint:Deactivate a endpoint deployed in a Micro Integrator"
      "help:Help about any command"
      "message-processor:Deactivate a message processor deployed in a Micro Integrator"
      "proxy-service:Deactivate a proxy service deployed in a Micro Integrator"
    )
    _describe "command" commands
    ;;
  esac

  case "$words[1]" in
  endpoint)
    _mi_deactivate_endpoint
    ;;
  help)
    _mi_deactivate_help
    ;;
  message-processor)
    _mi_deactivate_message-processor
    ;;
  proxy-service)
    _mi_deactivate_proxy-service
    ;;
  esac
}

function _mi_deactivate_endpoint {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator in which the endpoint should be deactivated]:' \
    '(-h --help)'{-h,--help}'[help for endpoint]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_deactivate_help {
  _arguments \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_deactivate_message-processor {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator in which the message processor should be deactivated]:' \
    '(-h --help)'{-h,--help}'[help for message-processor]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_deactivate_proxy-service {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator in which the proxy service should be deactivated]:' \
    '(-h --help)'{-h,--help}'[help for proxy-service]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}


function _mi_delete {
  local -a commands

  _arguments -C \
    '(-h --help)'{-h,--help}'[help for delete]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]' \
    "1: :->cmnds" \
    "*::arg:->args"

  case $state in
  cmnds)
    commands=(
      "env:Delete Environment from Config file"
      "help:Help about any command"
      "role:Delete a role from the Micro Integrator"
      "user:Delete a user from the Micro Integrator"
    )
    _describe "command" commands
    ;;
  esac

  case "$words[1]" in
  env)
    _mi_delete_env
    ;;
  help)
    _mi_delete_help
    ;;
  role)
    _mi_delete_role
    ;;
  user)
    _mi_delete_user
    ;;
  esac
}

function _mi_delete_env {
  _arguments \
    '(-h --help)'{-h,--help}'[help for env]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_delete_help {
  _arguments \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_delete_role {
  _arguments \
    '(-d --domain)'{-d,--domain}'[Select the domain of the role]:' \
    '(-e --environment)'{-e,--environment}'[Environment of the Micro Integrator from which a role should be deleted]:' \
    '(-h --help)'{-h,--help}'[help for role]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_delete_user {
  _arguments \
    '(-d --domain)'{-d,--domain}'[select user'\''s domain]:' \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator from which a user should be deleted]:' \
    '(-h --help)'{-h,--help}'[help for user]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}


function _mi_get {
  local -a commands

  _arguments -C \
    '(-h --help)'{-h,--help}'[help for get]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]' \
    "1: :->cmnds" \
    "*::arg:->args"

  case $state in
  cmnds)
    commands=(
      "apis:Get information about apis deployed in a Micro Integrator"
      "composite-apps:Get information about composite apps deployed in a Micro Integrator"
      "connectors:Get information about connectors deployed in a Micro Integrator"
      "data-services:Get information about data services deployed in a Micro Integrator"
      "endpoints:Get information about endpoints deployed in a Micro Integrator"
      "envs:Display the list of environments"
      "help:Help about any command"
      "inbound-endpoints:Get information about inbound endpoints deployed in a Micro Integrator"
      "local-entries:Get information about local entries deployed in a Micro Integrator"
      "log-levels:Get information about a Logger configured in a Micro Integrator"
      "logs:List all the available log files"
      "message-processors:Get information about message processors deployed in a Micro Integrator"
      "message-stores:Get information about message stores deployed in a Micro Integrator"
      "proxy-services:Get information about proxy services deployed in a Micro Integrator"
      "roles:Get information about roles"
      "sequences:Get information about sequences deployed in a Micro Integrator"
      "tasks:Get information about tasks deployed in a Micro Integrator"
      "templates:Get information about templates deployed in a Micro Integrator"
      "transaction-counts:Retrieve transaction count"
      "transaction-reports:Generate transaction count summary report"
      "users:Get information about users"
    )
    _describe "command" commands
    ;;
  esac

  case "$words[1]" in
  apis)
    _mi_get_apis
    ;;
  composite-apps)
    _mi_get_composite-apps
    ;;
  connectors)
    _mi_get_connectors
    ;;
  data-services)
    _mi_get_data-services
    ;;
  endpoints)
    _mi_get_endpoints
    ;;
  envs)
    _mi_get_envs
    ;;
  help)
    _mi_get_help
    ;;
  inbound-endpoints)
    _mi_get_inbound-endpoints
    ;;
  local-entries)
    _mi_get_local-entries
    ;;
  log-levels)
    _mi_get_log-levels
    ;;
  logs)
    _mi_get_logs
    ;;
  message-processors)
    _mi_get_message-processors
    ;;
  message-stores)
    _mi_get_message-stores
    ;;
  proxy-services)
    _mi_get_proxy-services
    ;;
  roles)
    _mi_get_roles
    ;;
  sequences)
    _mi_get_sequences
    ;;
  tasks)
    _mi_get_tasks
    ;;
  templates)
    _mi_get_templates
    ;;
  transaction-counts)
    _mi_get_transaction-counts
    ;;
  transaction-reports)
    _mi_get_transaction-reports
    ;;
  users)
    _mi_get_users
    ;;
  esac
}

function _mi_get_apis {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for apis]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_composite-apps {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for composite-apps]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_connectors {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for connectors]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_data-services {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for data-services]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_endpoints {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for endpoints]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_envs {
  _arguments \
    '--format[Pretty-print environments using go templates]:' \
    '(-h --help)'{-h,--help}'[help for envs]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_help {
  _arguments \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_inbound-endpoints {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for inbound-endpoints]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_local-entries {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for local-entries]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_log-levels {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for log-levels]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_logs {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for logs]' \
    '(-p --path)'{-p,--path}'[Path the file should be downloaded]:' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_message-processors {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for message-processors]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_message-stores {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for message-stores]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_proxy-services {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for proxy-services]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_roles {
  _arguments \
    '(-d --domain)'{-d,--domain}'[Filter roles by domain]:' \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for roles]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_sequences {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for sequences]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_tasks {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for tasks]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_templates {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for templates]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_transaction-counts {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for transaction-counts]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_transaction-reports {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '(-h --help)'{-h,--help}'[help for transaction-reports]' \
    '(-p --path)'{-p,--path}'[destination file location]:' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_get_users {
  _arguments \
    '(-d --domain)'{-d,--domain}'[Filter users by domain]:' \
    '(-e --environment)'{-e,--environment}'[Environment to be searched]:' \
    '--format[Pretty-print using Go Templates. Use "{{ jsonPretty . }}" to list all fields]:' \
    '(-h --help)'{-h,--help}'[help for users]' \
    '(-p --pattern)'{-p,--pattern}'[Filter users by regex]:' \
    '(-r --role)'{-r,--role}'[Filter users by role]:' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_help {
  _arguments \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_login {
  _arguments \
    '(-h --help)'{-h,--help}'[help for login]' \
    '(-p --password)'{-p,--password}'[Password for login]:' \
    '--password-stdin[Get password from stdin]' \
    '(-u --username)'{-u,--username}'[Username for login]:' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_logout {
  _arguments \
    '(-h --help)'{-h,--help}'[help for logout]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}


function _mi_secret {
  local -a commands

  _arguments -C \
    '(-h --help)'{-h,--help}'[help for secret]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]' \
    "1: :->cmnds" \
    "*::arg:->args"

  case $state in
  cmnds)
    commands=(
      "create:Encrypt secrets"
      "help:Help about any command"
      "init:Initialize Key Store"
    )
    _describe "command" commands
    ;;
  esac

  case "$words[1]" in
  create)
    _mi_secret_create
    ;;
  help)
    _mi_secret_help
    ;;
  init)
    _mi_secret_init
    ;;
  esac
}

function _mi_secret_create {
  _arguments \
    '(-c --cipher)'{-c,--cipher}'[Encryption algorithm]:' \
    '(-f --from-file)'{-f,--from-file}'[Path to the properties file which contains secrets to be encrypted]:' \
    '(-h --help)'{-h,--help}'[help for create]' \
    '(-o --output)'{-o,--output}'[Get the output in yaml (k8) or properties (file) format. By default the output is printed to the console]:' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_secret_help {
  _arguments \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_secret_init {
  _arguments \
    '(-h --help)'{-h,--help}'[help for init]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}


function _mi_update {
  local -a commands

  _arguments -C \
    '(-h --help)'{-h,--help}'[help for update]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]' \
    "1: :->cmnds" \
    "*::arg:->args"

  case $state in
  cmnds)
    commands=(
      "hashicorp-secret:Update the secret ID of HashiCorp configuration in a Micro Integrator"
      "help:Help about any command"
      "log-level:Update log level of a Logger in a Micro Integrator"
      "user:Update roles of a user in a Micro Integrator"
    )
    _describe "command" commands
    ;;
  esac

  case "$words[1]" in
  hashicorp-secret)
    _mi_update_hashicorp-secret
    ;;
  help)
    _mi_update_help
    ;;
  log-level)
    _mi_update_log-level
    ;;
  user)
    _mi_update_user
    ;;
  esac
}

function _mi_update_hashicorp-secret {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator of which the HashiCorp secret ID should be updated]:' \
    '(-h --help)'{-h,--help}'[help for hashicorp-secret]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_update_help {
  _arguments \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_update_log-level {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the micro integrator of which the logger should be updated]:' \
    '(-h --help)'{-h,--help}'[help for log-level]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_update_user {
  _arguments \
    '(-e --environment)'{-e,--environment}'[Environment of the Micro Integrator of which the user'\''s roles should be updated]:' \
    '(-h --help)'{-h,--help}'[help for user]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

function _mi_version {
  _arguments \
    '(-h --help)'{-h,--help}'[help for version]' \
    '(-k --insecure)'{-k,--insecure}'[Allow connections to SSL endpoints without certs]' \
    '--verbose[Enable verbose mode]'
}

