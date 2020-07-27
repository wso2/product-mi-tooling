/*
* Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
)

var logLevel string

// Show API command related usage info
const updateLogLevelCmdLiteral = "update"
const updateLogLevelCmdShortDesc = "Update log level"

const updateLogLevelCmdLongDesc = "Update log level of the Loggers in Micro Integrator\n"

var updateLogLevelCmdUsage = "Usage:\n" +
	" To update the level of an existing logger\n" +
	"  " + programName + " " + logLevelCmdLiteral + " " + updateLogLevelCmdLiteral + " [logger-name] [log-level]\n" +
	" To add a new logger\n" +
	"  " + programName + " " + logLevelCmdLiteral + " " + updateLogLevelCmdLiteral + " [logger-name] [class-name]" +
	" [log-level]\n\n"

var updateLogLevelCmdExamples = "Example:\n" +
	" To update the level of an existing logger\n" +
	"  " + programName + " " + logLevelCmdLiteral + " " + updateLogLevelCmdLiteral + " org-apache-coyote DEBUG\n" +
	" To add a new logger\n" +
	"  " + programName + " " + logLevelCmdLiteral + " " + updateLogLevelCmdLiteral + " synapse-api org.apache.synapse.rest.API DEBUG\n"

var updateLogLevelCmdHelpString = updateLogLevelCmdLongDesc + updateLogLevelCmdUsage + updateLogLevelCmdExamples

// apiupdateCmd represents the update api command
var loggerUpdateCmd = &cobra.Command{
	Use:   updateLogLevelCmdLiteral,
	Short: updateLogLevelCmdShortDesc,
	Long:  updateLogLevelCmdLongDesc + updateLogLevelCmdExamples,
	Run: func(cmd *cobra.Command, args []string) {
		handleUpdateLoggerCmdArguments(args)
	},
}

func init() {
	logLevelCmd.AddCommand(loggerUpdateCmd)
	loggerUpdateCmd.SetHelpTemplate(updateLogLevelCmdHelpString + utils.GetCmdFlags(logLevelCmdLiteral))
}

func handleUpdateLoggerCmdArguments(args []string) {
	utils.Logln(utils.LogPrefixInfo + "Update Logger called")
	if len(args) == 2 || len(args) == 3 {
		if args[0] == "help" || args[1] == "help" {
			printUpdateLoggerHelp()
		} else {
			loggerName = args[0]
			logLevel = args[1]
			var logClass = ""
			if len(args) == 3 {
				if args[2] == "help" {
					printUpdateLoggerHelp()
					return
				} else {
					logClass = args[1]
					logLevel = args[2]
				}
			}
			executeUpdateLoggerCmd(loggerName, logLevel, logClass)
		}
	} else {
		fmt.Println(programName, "log-level update accepts minimum of 2 and a maximum of 3 arguments. "+
			"See the usage below")
		printUpdateLoggerHelp()
	}
}

func printUpdateLoggerHelp() {
	fmt.Print(updateLogLevelCmdHelpString)
}

func executeUpdateLoggerCmd(loggerName, logLevel, logClass string) {
	resp, err := utils.UpdateMILogger(loggerName, logLevel, logClass)

	if err != nil {
		fmt.Println(utils.LogPrefixError + "Updating/adding the Logger.", err)
	} else {
		fmt.Println(resp)
	}
}
