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
	"fmt"
	"github.com/spf13/cobra"
	"github.com/wso2/product-mi-tooling/cmd/utils"
)

var messageProcessorStateValue string
// Update message processor command related usage info
const updateMessageProcessorCmdLiteral = "update"
const messageProcessorState = "state"
const updateMessageProcessorCmdShortDesc = "Update state of a message processor"

const updateMessageProcessorCmdLongDesc = "Activate and deactivate a given message processor \n"

var updateMessageProcessorCmdUsage = "Usage:\n" +
	"  " + programName + " " + messageProcessorCmdLiteral + " " + updateMessageProcessorCmdLiteral + " [messageprocessor-name] " + messageProcessorState + " [state]\n\n"

var updateMessageProcessorCmdExamples = "Example:\n" +
	"  " + programName + " " + messageProcessorCmdLiteral + " " + updateMessageProcessorCmdLiteral + " TestMessageProcessor state inactive\n\n"

var updateMessageProcessorCmdHelpString = updateMessageProcessorCmdLongDesc + updateMessageProcessorCmdUsage + updateMessageProcessorCmdExamples

// messageProcessorUpdateCmd represents the update messageProcessor state command
var messageProcessorUpdateCmd = &cobra.Command{
	Use:   updateMessageProcessorCmdLiteral,
	Short: updateMessageProcessorCmdShortDesc,
	Long:  updateMessageProcessorCmdLongDesc + updateMessageProcessorCmdExamples,
	Run: func(cmd *cobra.Command, args []string) {
		handleUpdateMessageProcessorCmdArguments(args)
	},
}

func init() {
	messageProcessorCmd.AddCommand(messageProcessorUpdateCmd)
	messageProcessorUpdateCmd.SetHelpTemplate(updateMessageProcessorCmdHelpString + utils.GetCmdFlags(updateMessageProcessorCmdLiteral))
}

func handleUpdateMessageProcessorCmdArguments(args []string) {
	utils.Logln(utils.LogPrefixInfo + "Update message processor called")
	if len(args) == 3 {
		if args[0] == "help" || args[1] == "help" || args[2] == "help" {
			printUpdateMessageProcessorHelp()
		} else if args[1] != messageProcessorState {
			printInvalidCommandMessage(args)
		} else {
			messageProcessorName = args[0]
			messageProcessorStateValue = args[2]
			executeUpdateMessageProcessorCmd(messageProcessorName, messageProcessorStateValue)
		}
	} else {
		printInvalidCommandMessage(args)
	}
}

func printInvalidCommandMessage(args []string) {
	fmt.Println("messageprocessor update:", args, "is not a valid command.\n" +
		programName, "messageprocessor update requires 3 arguments. See the usage below.")
	printUpdateMessageProcessorHelp()
}
func printUpdateMessageProcessorHelp() {
	fmt.Print(updateMessageProcessorCmdHelpString)
}

func executeUpdateMessageProcessorCmd(messageProcessorName, messageProcessorStateValue string) {
	resp, err := utils.UpdateMIMessageProcessor(messageProcessorName, messageProcessorStateValue)

	if err != nil {
		fmt.Println(utils.LogPrefixError + "Updating state of message processor: ", err)
	} else {
		fmt.Println("Message processor ", resp)
	}
}
