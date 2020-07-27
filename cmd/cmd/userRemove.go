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

// Remove user command related usage info
const removeUserCmdLiteral = "remove"
const removeUserCmdShortDesc = "Remove an existing user"
const removeUserCmdLongDesc = "Remove an existing user from the micro-integrator user store\n"

var removeUserCmdExamples = "Example:\n" +
    "To remove an existing user\n" +
    "  " + programName + " " + usersCmdLiteral + " " + removeUserCmdLiteral + " [user-id]\n"

// userRemoveCmd represents the remove users command
var userRemoveCmd = &cobra.Command{
    Use:   removeUserCmdLiteral,
    Short: removeUserCmdShortDesc,
    Long:  removeUserCmdLongDesc + removeUserCmdExamples,
    Run: func(cmd *cobra.Command, args []string) {
        handleRemoveUserCmdArguments(args)
    },
}

func init() {
    userRemoveCmd.SetHelpTemplate(removeUserCmdLongDesc +  "Usage:\n" +
        "  " + programName + " " + usersCmdLiteral + " " + removeUserCmdLiteral + " [user-id]\n" +
        removeUserCmdExamples + utils.GetCmdFlags(removeUserCmdLiteral))
    usersCmd.AddCommand(userRemoveCmd)
}

func handleRemoveUserCmdArguments(args []string) {
    utils.Logln(utils.LogPrefixInfo + "Remove user called")
    if len(args) == 0 {
        fmt.Println("Please provide a user-id to remove. See the usage below")
        printRemoveUserHelp()
    } else if len(args) == 1 {
        if args[0] == "help" {
            printRemoveUserHelp()
        } else {
            userId := args[0]
            executeRemoveUserCmd(userId)
        }
    } else {
        fmt.Println("Too many arguments. See the usage below")
        printRemoveUserHelp()
    }
}

func printRemoveUserHelp() {
    fmt.Print(removeUserCmdLongDesc + "Usage:\n" +
        "  " + programName + " " + usersCmdLiteral + " " + removeUserCmdLiteral + " [user-id]\n" +
        removeUserCmdExamples + utils.GetCmdFlags(usersCmdLiteral))
}

func executeRemoveUserCmd(userId string) {

    finalUrl := utils.GetRESTAPIBase() + utils.PrefixUsers + "/" + userId
    res, err := utils.InvokeDELETERequest(finalUrl, nil)
    var errString = "Error occurred while removing the user"
    if res.StatusCode() == 200 {
        fmt.Println(res)
    } else if res.StatusCode() == http.StatusUnauthorized {
        // not logged in to MI
        fmt.Println("User not logged in or session timed out. Please login to the current Micro Integrator instance")
        utils.HandleErrorAndExit("Execute 'mi remote login --help' for more information", nil)
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
