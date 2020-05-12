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

var messageProcessorState string
// Show API command related usage info
const updateMessageProcessorCmdLiteral = "update"
const updateMessageProcessorCmdShortDesc = "Update state of messageprocessor"

const updateMessageProcessorCmdLongDesc = "Update state of messageprocessor in Micro Integrator\n"

var updateMessageProcessorCmdUsage = "Usage:\n" +
	"  " + programName + " " + messageProcessorCmdLiteral + " " + updateMessageProcessorCmdLiteral + " [messageprocessor-name] [state]\n\n"

var updateMessageProcessorCmdExamples = "Example:\n" +
	"  " + programName + " " + messageProcessorCmdLiteral + " " + updateMessageProcessorCmdLiteral + " TestMessageProcessor inactive\n\n"

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
	if len(args) == 2 {
		if args[0] == "help" || args[1] == "help" {
			printUpdateMessageProcessorHelp()
		} else {
			messageProcessorName = args[0]
			messageProcessorState = args[1]
			executeUpdateMessageProcessorCmd(messageProcessorName, messageProcessorState)
		}
	} else {
		fmt.Println(programName, "message processor update requires 2 arguments. See the usage below")
		printUpdateMessageProcessorHelp()
	}
}

func printUpdateMessageProcessorHelp() {
	fmt.Print(updateMessageProcessorCmdHelpString)
}

func executeUpdateMessageProcessorCmd(messageProcessorName, messageProcessorState string) {
	resp, err := utils.UpdateMIMessageProcessor(messageProcessorName, messageProcessorState)

	if err != nil {
		fmt.Println(utils.LogPrefixError + "Updating state of message processor: ", err)
	} else {
		fmt.Println("Message processor ", resp)
	}
}
