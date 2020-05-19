/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
	"fmt"
	"github.com/wso2/product-mi-tooling/cmd/utils"
)

const updateProxyServiceCmdLiteral = "update"
const proxyServiceState = "state"
const updateProxyServiceCmdShortDesc = "Update state of a proxy service"

const updateProxyServiceCmdLongDesc = "Activate and Deactivate a specified proxy service \n"

var updateProxyServiceCmdUsage = "Usage:\n" +
	"  " + programName + " " + proxyServiceCmdLiteral + " " + updateProxyServiceCmdLiteral + " [proxy-name] " + proxyServiceState + " [state]\n\n"

var updateProxyServiceCmdExamples = "Example:\n" +
	"  " + programName + " " + proxyServiceCmdLiteral + " " + updateProxyServiceCmdLiteral + " testProxyService state inactive\n\n"

var updateProxyServiceCmdHelpString = updateProxyServiceCmdShortDesc + updateProxyServiceCmdUsage + updateProxyServiceCmdExamples

var proxyServiceUpdateCmd = &cobra.Command{
	Use:   updateProxyServiceCmdLiteral,
	Short: updateProxyServiceCmdShortDesc,
	Long:  updateProxyServiceCmdLongDesc + updateProxyServiceCmdExamples,
	Run: func(cmd *cobra.Command, args []string) {
		handleUpdateProxyServiceCmdArguments(args)
},
}

func init() {
	proxyServiceCmd.AddCommand(proxyServiceUpdateCmd)
	proxyServiceUpdateCmd.SetHelpTemplate(updateProxyServiceCmdHelpString + utils.GetCmdFlags(updateProxyServiceCmdLiteral))
}

func handleUpdateProxyServiceCmdArguments(args []string) {
	if len(args) == 3 {
		if args[0] == "help" || args[1] == "help" || args[2] == "help" {
			printUpdateProxyServiceHelp()
		} else if args[1] != proxyServiceState {
			printInvalidProxyUpdateCmdMessage(args)
		} else {
			updateProxyServiceState(args[0], args[2])
		}
	} else {
		printInvalidProxyUpdateCmdMessage(args)
	}
}

func printInvalidProxyUpdateCmdMessage(args []string) {
	fmt.Println("proxyservice update:", args, "is not a valid command.\n" +
		programName, "proxyservice update requires 3 arguments. See the usage below.")
	printUpdateProxyServiceHelp()
}

func printUpdateProxyServiceHelp()  {
	fmt.Println(updateProxyServiceCmdHelpString)
}

func updateProxyServiceState(proxyName string, intendedState string) {
	resp, err := utils.UpdateMIProxySerice(proxyName, intendedState)
	if err != nil {
		fmt.Println(utils.LogPrefixError + "Updating state of proxy service failed: ", err)
	} else {
		fmt.Println(resp)
	}
}
