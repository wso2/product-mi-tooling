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
    "github.com/wso2/product-mi-tooling/cmd/utils/artifactUtils"
)

var userPattern string
var userRole string
var userId string

// Show Users command related usage info
const showUserCmdLiteral = "show"
const showUserCmdShortDesc = "Get information about users"
const showUserCmdLongDesc = "Get information about the users filtered by username pattern and role." +
    " If not provided list all users\n"

var showUsersCmdExamples = "Example:\n" +
    "To get the list of users with specific role\n" +
    "  " + programName + " " + usersCmdLiteral + " " + showUserCmdLiteral + " --role=[role-name]\n" +
    "  " + programName + " " + usersCmdLiteral + " " + showUserCmdLiteral + " -r [role-name]\n\n" +
    "To get the list of users with a username matching with the wild card Ex: \"*mi*\" matches with \"admin\"\n" +
    "  " + programName + " " + usersCmdLiteral + " " + showUserCmdLiteral + " --pattern=[pattern]\n" +
    "  " + programName + " " + usersCmdLiteral + " " + showUserCmdLiteral + " -p [pattern]\n\n" +
    "To get details about a user by providing the user-id \n" +
    "  " + programName + " " + usersCmdLiteral + " " + showUserCmdLiteral + " [user-id]\n\n" +
    "To list all the users\n" +
    "  " + programName + " " + usersCmdLiteral + " " + showUserCmdLiteral + "\n\n"

// userShowCmd represents the show users command
var userShowCmd = &cobra.Command{
    Use:   showUserCmdLiteral,
    Short: showUserCmdShortDesc,
    Long:  showUserCmdLongDesc + showUsersCmdExamples,
    Run: func(cmd *cobra.Command, args []string) {
        userRole, _ := cmd.Flags().GetString("role")
        userPattern, _ := cmd.Flags().GetString("pattern")
        handleUsersCmdArguments(args, userRole, userPattern)
    },
}

func init() {
    userShowCmd.Flags().StringP("role", "r", "", "Filter users by role")
    userShowCmd.Flags().StringP("pattern", "p", "", "Filter users by regex")
    userShowCmd.SetHelpTemplate(showUserCmdLongDesc + utils.GetCmdUsageMultipleArgs(programName, usersCmdLiteral,
        showUserCmdLiteral, []string {"[user-id]", "--role=[role-name]", "--pattern=[username regex]"}) +
        showUsersCmdExamples + utils.GetCmdFlags(showUserCmdLiteral))
    usersCmd.AddCommand(userShowCmd)
}

func handleUsersCmdArguments(args []string, userRole string, userPattern string) {
    utils.Logln(utils.LogPrefixInfo + "Show users called")
    if len(args) == 0 {
        if userRole != "" || userPattern != "" {
            executeGetUserCmd("", userRole, userPattern)
        } else {
            executeListUsersCmd()
        }
    } else if len(args) == 1 {
        if args[0] == "help" {
            printUsersHelp()
        } else {
            userId = args[0]
            if userId != "" && (userRole != "" || userPattern != "") {
                fmt.Println("Invalid combination of inputs. See the usage below")
                printUsersHelp()
            } else {
                executeGetUserCmd(userId, userRole, userPattern)
            }
        }
    } else {
        fmt.Println("Too many arguments. See the usage below")
        printUsersHelp()
    }
}

func printUsersHelp() {
    fmt.Print(showUserCmdLongDesc + utils.GetCmdUsageMultipleArgs(programName, usersCmdLiteral,
        showUserCmdLiteral, []string {"[user-id]", "--role=[role-name]", "--pattern=[username regex]"}) +
        showUsersCmdExamples + utils.GetCmdFlags(usersCmdLiteral))
}

func executeGetUserCmd(userId string, userRole string, userPattern string) {
    if userId != "" {
        finalUrl := utils.GetRESTAPIBase() + utils.PrefixUsers + "/" + userId
        resp, err := utils.UnmarshalData(finalUrl, nil, nil, &artifactUtils.UserSummary{})

        if err == nil {
            // Printing the details of the user
            userSummary := resp.(*artifactUtils.UserSummary)
            printUserSummary(*userSummary)
        } else {
            fmt.Println(utils.LogPrefixError+"Getting Information of the user " + userId, err)
        }
    } else {
        finalUrl := utils.GetRESTAPIBase() + utils.PrefixUsers
        params := make(map[string]string)
        if userRole != "" {
            params["role"] = userRole
        }
        if userPattern != "" {
            params["pattern"] = userPattern
        }
        resp, err := utils.UnmarshalData(finalUrl, nil, params, &artifactUtils.UserList{})
        if err == nil {
            // Printing the list of available users
            list := resp.(*artifactUtils.UserList)
            utils.PrintItemList(list, []string{utils.UserId}, "No users found")
        } else {
            utils.Logln(utils.LogPrefixError+"Getting List of users with role: " +
                userRole + " and user-id pattern: " + userPattern, err)
        }
    }
}

// Print the details of a User
// Name, Roles, and IsAdmin details
func printUserSummary(summary artifactUtils.UserSummary) {
    fmt.Println("Name - " + summary.UserId)
    fmt.Print("Roles - ")
    for _, role := range summary.Roles {
        fmt.Print(role + " ")
    }
    fmt.Println()
    fmt.Printf("Is admin - %t\n", summary.IsAdmin)
}

func executeListUsersCmd() {
    finalUrl := utils.GetRESTAPIBase() + utils.PrefixUsers
    resp, err := utils.UnmarshalData(finalUrl, nil, nil, &artifactUtils.UserList{})

    if err == nil {
        // Printing the list of available Users
        list := resp.(*artifactUtils.UserList)
        utils.PrintItemList(list, []string{utils.UserId}, "No Users found")
    } else {
        utils.Logln(utils.LogPrefixError + "Getting List of Users", err)
    }
}
