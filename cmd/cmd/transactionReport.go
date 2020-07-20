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
	"path/filepath"
	"strconv"
	"strings"
	"time"
)

const transactionReportCmdLiteral = "report"
const transactionReportCmdShortDesc = "Generate transaction count summary report"
const transactionReportCmdLongDesc = "Generate the transaction count summary report at the given location for the " +
	"given period of time.\nIf a location not provided, generate the report in current directory.\nIf an end date " +
	"not provided, generate the report with values upto current date.\n\n"

var transactionReportCmdExamples = "Example:\n" +
	"To generate transaction count report consisting data within a specified time period at a specified location\n" +
	"  " + programName + " " + transactionCmdLiteral + " " + transactionReportCmdLiteral + " 2020-05 2020-06 --path=</dir_path>\n\n" +
	"To generate transaction count report with data from a given month upto the current month at a specified location\n" +
	"  " + programName + " " + transactionCmdLiteral + " " + transactionReportCmdLiteral + " 2020-01 --path=</dir_path>\n\n" +
	"To generate transaction count report at the current location with data between 2020-01 and 2020-05\n" +
	"  " + programName + " " + transactionCmdLiteral + " " + transactionReportCmdLiteral + " 2020-01 2020-05\n\n"

var transactionReportCmdArgs = []string{
	"[start] [end] --path=[destination-file-location]",
	"[start] [end]",
	"[start] --path=[destination-file-location]",
}

var transactionReportCmd = &cobra.Command{
	Use:   transactionReportCmdLiteral,
	Short: transactionReportCmdShortDesc,
	Long:  transactionReportCmdLongDesc + transactionReportCmdExamples,
	Run: func(cmd *cobra.Command, args []string) {
		destinationFileLocation, _ := cmd.Flags().GetString("path")
		if len(strings.TrimSpace(destinationFileLocation)) == 0 || destinationFileLocation == "." {
			destinationFileLocation, _ = os.Getwd()
		}
		handleTransactionReportCmdArguments(args, destinationFileLocation)
	},
}

func init() {
	transactionCmd.AddCommand(transactionReportCmd)
	transactionReportCmd.Flags().StringP("path", "p", "", "destination file location")
	transactionReportCmd.SetHelpTemplate(transactionReportCmdLongDesc +
		utils.GetCmdUsageForArgsOnly(programName, transactionCmdLiteral, transactionReportCmdLiteral, transactionReportCmdArgs) +
		transactionReportCmdExamples + utils.GetCmdFlags(transactionCmdLiteral))
}

// Check arguments for "start" and "end" and execute get transaction report generation command.
// Print an error if no valid number of arguments.
func handleTransactionReportCmdArguments(args []string, targetPath string) {
	// check for "start" and "end" args.
	if len(args) == 0 {
		fmt.Println("Mandatory argument [start] is missing. See the usage guide.\n\n" +
			utils.GetCmdUsageForArgsOnly(programName, transactionCmdLiteral, transactionReportCmdLiteral,
				transactionReportCmdArgs))
	} else if len(args) == 1 {
		executeTransactionReportGenerationCmd(targetPath, args[0], "")
	} else if len(args) == 2 {
		start := args[0]
		end := args[1]
		executeTransactionReportGenerationCmd(targetPath, start, end)
	} else {
		fmt.Println("Invalid number of arguments. See the usage guide.\n\n" +
			utils.GetCmdUsageForArgsOnly(programName, transactionCmdLiteral, transactionReportCmdLiteral, transactionReportCmdArgs) +
			transactionReportCmdExamples)
	}
}

// Invoke ../management/transactions/report?start=[start]&end=[end] URL.
// Generate the report from the response.
func executeTransactionReportGenerationCmd(targetDirectory string, start string, end string) {
	finalUrl, params := utils.GetUrlAndParams(utils.PrefixTransactions + "/" + utils.TransactionReportCmd,
		"start", start)
	params = utils.PutQueryParamsToMap(params, "end", end)
	resp, err := utils.UnmarshalData(finalUrl, nil, params, &artifactUtils.TransactionCountInfo{})

	if err == nil {
		transactionCount := resp.(*artifactUtils.TransactionCountInfo)
		fileName := "transaction-count-summary-" + strconv.FormatInt(time.Now().UnixNano(), 10) + ".csv"
		destinationFilePath := filepath.Join(targetDirectory, fileName)
		transactionCountLines := transactionCount.TransactionCounts
		utils.WriteLinesToCSVFile(transactionCountLines, destinationFilePath)
		fmt.Println("Transaction Count Report created in " + destinationFilePath)
	} else {
		errBody := resp.(string)
		if len(errBody) > 0 {
			fmt.Println(utils.LogPrefixError+errBody, err)
		} else {
			fmt.Println(utils.LogPrefixError+"Getting Information of Transaction Counts.", err)
		}
	}
}
