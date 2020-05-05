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
    "fmt"
    "github.com/spf13/cobra"
    "github.com/wso2/product-mi-tooling/cmd/utils"
    "net/http"
    "strings"
)

// Show user command related usage info
const addUserCmdLiteral = "add"
const addUserCmdShortDesc = "Add new user"
const addUserCmdLongDesc = "Add new user to the micro-integrator user store\n"

var addUserCmdExamples = "Example:\n" +
    "To Add a new user\n" +
    "  " + programName + " " + usersCmdLiteral + " " + addUserCmdLiteral + " [new user-id] [password] [is-admin]\n" +
    "  " + programName + " " + usersCmdLiteral + " " + addUserCmdLiteral + " user1 passw0rd true\n"

// userAddCmd represents the add users command
var userAddCmd = &cobra.Command{
    Use:   addUserCmdLiteral,
    Short: addUserCmdShortDesc,
    Long:  addUserCmdLongDesc + addUserCmdExamples,
    Run: func(cmd *cobra.Command, args []string) {
        handleAddUserCmdArguments(args)
    },
}

func init() {
    userAddCmd.SetHelpTemplate(addUserCmdLongDesc + "Usage:\n" +
        "  " + programName + " " + usersCmdLiteral + " " + addUserCmdLiteral +
        " [new user-id] [password] [is-admin]\n" +
        addUserCmdExamples + utils.GetCmdFlags(addUserCmdLiteral))
    usersCmd.AddCommand(userAddCmd)
}

func handleAddUserCmdArguments(args []string) {
    utils.Logln(utils.LogPrefixInfo + "Add user called")
    if len(args) == 1 {
        if args[0] == "help" {
            printAddUserHelp()
        } else {
            fmt.Println("Invalid arguments. See the usage below")
            printAddUserHelp()
        }
    } else if len(args) == 3 {
        var userId = args[0]
        var password = args[1]
        var isAdmin = args[2]
        if !(isAdmin == "false" || isAdmin == "true") {
            fmt.Println("[is-admin] parameter should be either true or false")
        }
        executeAddUserCmd(userId, password, isAdmin)
    } else {
        fmt.Println("Invalid number of arguments. See the usage below")
        printAddUserHelp()
    }
}

func printAddUserHelp() {
    fmt.Print(addUserCmdLongDesc + "Usage:\n" +
        "  " + programName + " " + usersCmdLiteral + " " + addUserCmdLiteral +
        " [new user-id] [password] [is-admin]\n" +
        addUserCmdExamples + utils.GetCmdFlags(usersCmdLiteral))
}

func executeAddUserCmd(userId string, password string, isAdmin string) {
    headers := make(map[string]string)
    headers["Content-Type"] = "application/json"

    body := make(map[string]string)
    body["userId"] = userId
    body["password"] = password
    body["isAdmin"] = isAdmin

    finalUrl := utils.GetRESTAPIBase() + utils.PrefixUsers
    res, err := utils.InvokePOSTRequest(finalUrl, headers, body)
    var errString = "Error occurred while adding the new user"
    if res.StatusCode() == 200 {
        fmt.Println(res)
    } else if res.StatusCode() == http.StatusUnauthorized {
        // not logged in to MI
        fmt.Println("User not logged in or session timed out. Please login to the current Micro Integrator instance")
    } else {
        if err == nil {
            fmt.Println(utils.LogPrefixError + errString)
            // print the response in an error scenario, only if the response contains an error message.
            if strings.Contains(res.String(), "Error") {
                fmt.Println(res)
            }
        } else {
            fmt.Println(utils.LogPrefixError + errString, err)
        }
    }
}
