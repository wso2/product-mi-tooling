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
	"github.com/wso2/product-mi-tooling/cmd/utils"
	"fmt"
)

const updateEndpointCmdLiteral = "update"
const endpointState = "state"
const updateEndpointCmdShortDesc = "Update state of a endpoint"

const updateEndpointCmdLongDesc = "Activate and Deactivate a specified endpoint \n"

var updateEndpointCmdUsage = "Usage:\n" +
	"  " + programName + " " + endpointCmdLiteral + " " + updateEndpointCmdLiteral + " [endpoint-name] " + endpointState + " [state]\n\n"

var updateEndpointCmdExamples = "Example:\n" +
	"  " + programName + " " + endpointCmdLiteral + " " + updateEndpointCmdLiteral + " testEndpoint state inactive\n\n"

var updateEndpointCmdHelpString = updateEndpointCmdShortDesc + updateEndpointCmdUsage + updateEndpointCmdExamples

var endpointUpdateCmd = &cobra.Command{
	Use:   updateEndpointCmdLiteral,
	Short: updateEndpointCmdShortDesc,
	Long:  updateEndpointCmdLongDesc + updateEndpointCmdExamples,
	Run: func(cmd *cobra.Command, args []string) {
		handleUpdateEndpointCmdArguments(args)
	},
}

func init() {
	endpointCmd.AddCommand(endpointUpdateCmd)
	endpointUpdateCmd.SetHelpTemplate(updateEndpointCmdHelpString + utils.GetCmdFlags(updateEndpointCmdLiteral))
}

func handleUpdateEndpointCmdArguments(args []string) {
	if len(args) == 3 {
		if args[0] == "help" || args[1] == "help" || args[2] == "help" {
			printUpdateEndpointHelp()
		} else if args[1] != endpointState {
			printInvalidEndpointUpdateCmdMessage(args)
		} else {
			updateEndpointState(args[0], args[2])
		}
	} else {
		printInvalidEndpointUpdateCmdMessage(args)
	}
}

func printInvalidEndpointUpdateCmdMessage(args []string) {
	fmt.Println("endpoint update:", args, "is not a valid command.\n"+
		programName, "endpoint update requires 3 arguments. See the usage below.")
	printUpdateEndpointHelp()
}

func printUpdateEndpointHelp() {
	fmt.Println(updateEndpointCmdHelpString)
}

func updateEndpointState(endpoint string, intendedState string)  {
	resp, err := utils.UpdateMIEndpoint(endpoint, intendedState)

	if err != nil {
		fmt.Println(utils.LogPrefixError + "Updating state of endpoint failed: ", err)
	} else {
		fmt.Println(resp)
	}
}
