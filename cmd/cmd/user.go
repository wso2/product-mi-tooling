/*
* Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
 */

package cmd

import (
    "github.com/spf13/cobra"
)

// Logs command related usage info
const usersCmdLiteral = "user"
const usersCmdShortDesc = "Manage users"
const usersCmdLongDesc = "Manage users in the micro-integrator"

// usersCmd represents the users command
var usersCmd = &cobra.Command{
    Use:   usersCmdLiteral,
    Short: usersCmdShortDesc,
    Long:  usersCmdLongDesc,
}

func init() {
    RootCmd.AddCommand(usersCmd)
}
