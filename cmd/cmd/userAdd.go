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
    "bufio"
    "os"
    "golang.org/x/crypto/ssh/terminal"
    "syscall"
)

// Show user command related usage info
const addUserCmdLiteral = "add"
const addUserCmdShortDesc = "Add new user"
const addUserCmdLongDesc = "Add new user to the micro-integrator user store\n"

var addUserCmdExamples = "Example:\n" +
    "To Add a new user\n" +
    "  " + programName + " " + usersCmdLiteral + " " + addUserCmdLiteral + " user1\n"

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
        " [user-id]\n" +
        addUserCmdExamples + utils.GetCmdFlags(addUserCmdLiteral))
    usersCmd.AddCommand(userAddCmd)
}

func handleAddUserCmdArguments(args []string) {
    utils.Logln(utils.LogPrefixInfo + "Add user called")
    if len(args) == 1 {
        if args[0] == "help" {
            printAddUserHelp()
        } else {
            // arg[0] should be user name
            startConsoleToAddUser(args[0])
        }
    } else {
        fmt.Println("Invalid number of arguments. See the usage below")
        printAddUserHelp()
    }

}

func startConsoleToAddUser(userId string) {
    reader := bufio.NewReader(os.Stdin)

    fmt.Printf("Is " + userId + " an admin [y/N]: ")
    isAdmin, _ := reader.ReadString('\n')
    isAdmin = resolveIsAdminInput(isAdmin)

    fmt.Printf("Enter password for " + userId + ": ")
    byteUserPassword, _ := terminal.ReadPassword(int(syscall.Stdin))
    userPassword := string(byteUserPassword)
    fmt.Println()

    fmt.Printf("Re-Enter password for " + userId + ": ")
    byteUserConfirmationPassword, _ := terminal.ReadPassword(int(syscall.Stdin))
    userConfirmPassword := string(byteUserConfirmationPassword)
    fmt.Println()

    if userConfirmPassword == userPassword {
        executeAddUserCmd(userId, userPassword, isAdmin)
    } else {
        fmt.Println("Passwords are not matching.")
    }
}

func resolveIsAdminInput(isAdminConsoleInput string) string {
    if len(strings.TrimSpace(isAdminConsoleInput)) == 0 {
        return "false"
    }
    yesResponses := []string{"y", "yes"}
    if utils.ContainsString(yesResponses, strings.TrimSpace(isAdminConsoleInput)) {
        return "true"
    }
    return "false"
}

func printAddUserHelp() {
    fmt.Print(addUserCmdLongDesc + "Usage:\n" +
        "  " + programName + " " + usersCmdLiteral + " " + addUserCmdLiteral +
        " [new user-id]\n" +
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
