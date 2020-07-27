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
    "os"
    "strings"
)

var logfileName string

// Show Logs command related usage info
const showLogsCmdLiteral = "show"
const showLogsCmdShortDesc = "List all the available log files"

const showLogsCmdLongDesc = "Download a log file by providing the file name and download location," +
    " if not provided, list all the log files\n"

var showLogsCmdExamples = "Example:\n" +
    "To list all the log files\n" +
    "  " + programName + " " + logsCmdLiteral + " " + showLogsCmdLiteral + "\n\n" +
    "To download a selected log file\n" +
    "  " + programName + " " + logsCmdLiteral + " " + showLogsCmdLiteral + " [file-name] --path=[download-location]\n" +
    "  " + programName + " " + logsCmdLiteral + " " + showLogsCmdLiteral + " [file-name] -p [download-location]\n\n"

// logsShowCmd represents the logs command
var logsShowCmd = &cobra.Command{
    Use:   showLogsCmdLiteral,
    Short: showLogsCmdShortDesc,
    Long:  showLogsCmdLongDesc + showLogsCmdExamples,
    Run: func(cmd *cobra.Command, args []string) {
        targetPath, _ := cmd.Flags().GetString("path")
        if targetPath == "." {
            targetPath, _ = os.Getwd()
        }
        handleLogsCmdArguments(args, targetPath)
    },
}

func init() {
    logsShowCmd.Flags().StringP("path", "p", ".", "Path the file should be downloaded")
    logsShowCmd.SetHelpTemplate(showLogsCmdLongDesc + utils.GetCmdUsage(programName, logsCmdLiteral,
        showLogsCmdLiteral, "[file-name] --path=[download-location]") +
        showLogsCmdExamples + utils.GetCmdFlags(showLogsCmdLiteral))
    logsCmd.AddCommand(logsShowCmd)
}

func handleLogsCmdArguments(args []string, targetPath string) {
    utils.Logln(utils.LogPrefixInfo + "Show logs called")
    if len(args) == 0 {
        executeListLogsCmd()
    } else if len(args) < 2 {
        if args[0] == "help" {
            printLogsHelp()
        } else {
            logfileName = args[0]
            executeGetLogsCmd(logfileName, targetPath)
        }
    } else {
        fmt.Println("Too many arguments. See the usage below")
        printLogsHelp()
    }
}

func printLogsHelp() {
    fmt.Print(showLogsCmdLongDesc + utils.GetCmdUsage(programName, logsCmdLiteral, showLogsCmdLiteral,
        "[file-name] --path=[download-location]") + showLogsCmdExamples + utils.GetCmdFlags(logsCmdLiteral))
}

func executeGetLogsCmd(filename string, targetPath string) {
    finalUrl, params := utils.GetUrlAndParams(utils.PrefixLogs, "file", filename)
    utils.UnmarshalLogFileData(finalUrl, nil, params, targetPath + "/" + filename)
}

func executeListLogsCmd() {
    finalUrl := utils.GetRESTAPIBase() + utils.PrefixLogs
    resp, err := utils.UnmarshalData(finalUrl, nil, nil, &artifactUtils.LogFileList{})
    if err == nil {
        // Printing the list of available log files
        list := resp.(*artifactUtils.LogFileList)
        filteredList := new(artifactUtils.LogFileList)
        for k := range list.LogFiles {
            if strings.HasSuffix(list.LogFiles[k].FileName, ".log") {
                filteredList.LogFiles = append(filteredList.LogFiles, list.LogFiles[k])
            }
        }
        filteredList.Count = int32(len(filteredList.LogFiles))
        utils.PrintItemList(filteredList, []string{utils.Name, utils.Size}, "No log files found")
    } else {
        utils.Logln(utils.LogPrefixError+"Getting List of log files", err)
    }
}
