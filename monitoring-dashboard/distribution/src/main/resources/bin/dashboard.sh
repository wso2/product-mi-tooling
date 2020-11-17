#!/bin/sh
# ---------------------------------------------------------------------------
#  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

PRG="$0"
PRGDIR=`dirname "$PRG"`

# Only set CARBON_HOME if not already set
[ -z "$DASHBOARD_HOME" ] && DASHBOARD_HOME=`cd "$PRGDIR/.." ; pwd`

declare -r config_file="$DASHBOARD_HOME/conf/deployment.toml"

# define default values
declare port=9743
declare context="/dashboard"

parse_config_file() {
  local line key val nr=0
  local config_err=()
  while IFS= read -r line; do
    # keep a running count of which line we're on
    (( ++nr ))
    # ignore empty lines and lines starting with a #
    [[ -z "$line" || "$line" = '#'* ]] && continue
    read -r key <<< "${line%% *}"   # grabs the first word and strips trailing whitespace
    read -r val <<< "${line#* = }"    # grabs everything after the first word and strips trailing whitespace
    if [[ -z "$val" ]]; then
      # store errors in an array
      config_err+=( "missing value for \"$key\" in config file on line $nr" )
      continue
    fi
    case "$key" in
      context)
        context="${val//\"}"
        context="${context//\/}"
        rm $DASHBOARD_HOME/jetty/webapps/*.war
        cp $DASHBOARD_HOME/lib/dashboard.war $DASHBOARD_HOME/jetty/webapps/$context.war;;
      port) port="$val" ;;
    esac
  done
  if (( ${#config_err[@]} > 0 )); then
    printf '%s\n' 'there were errors parsing the config file:' "${config_err[@]}"
  fi
}

[[ -s "$config_file" ]] && parse_config_file < "$config_file"

printf 'port is configured to "%s"\n' "$port"
printf 'context configured to "%s"\n' "$context"

JETTY_HOME=$DASHBOARD_HOME/jetty
cd $JETTY_HOME;
java -jar start.jar -Djetty.port=$port
