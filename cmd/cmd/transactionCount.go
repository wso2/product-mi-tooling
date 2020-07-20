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
	"strconv"

	"github.com/spf13/cobra"
	"github.com/wso2/product-mi-tooling/cmd/utils"
	"github.com/wso2/product-mi-tooling/cmd/utils/artifactUtils"
)

const transactionCountCmdLiteral = "count"
const transactionCountCmdShortDesc = "Retrieve transaction count"
const transactionCountCmdLongDesc = "Retrieve transaction count based on the given year and month. \nIf year and " +
	"month not provided, retrieve the count for the current year and month.\n\n"

var transactionCountCmdExamples = "Example:\n" +
	"To get the transaction count for the current month\n" +
	"  " + programName + " " + transactionCmdLiteral + " " + transactionCountCmdLiteral + "\n\n" +
	"To get the transaction count for a specific month\n" +
	"  " + programName + " " + transactionCmdLiteral + " " + transactionCountCmdLiteral + " 2020 06" + "\n\n"

var transactionCountCmdArgs = "[year] [month]"

var transactionCountCmd = &cobra.Command{
	Use:   transactionCountCmdLiteral,
	Short: transactionCountCmdShortDesc,
	Long:  transactionCountCmdLongDesc + transactionCountCmdExamples,
	Run: func(cmd *cobra.Command, args []string) {
		handleTransactionCountCmdArguments(args)
	},
}

func init() {
	transactionCmd.AddCommand(transactionCountCmd)
	transactionCountCmd.SetHelpTemplate(transactionCountCmdLongDesc + utils.GetCmdUsage(programName,
		transactionCmdLiteral, transactionCountCmdLiteral, transactionCountCmdArgs) + transactionCountCmdExamples +
		utils.GetCmdFlags(transactionCmdLiteral))
}

// Check arguments for year and month and execute get transaction count command.
// Print an error if no valid number of arguments.
func handleTransactionCountCmdArguments(args []string) {
	// check for the year and month
	if len(args) == 0 {
		// default will be current year and month
		executeGetTransactionCountCmd(nil)
	} else if len(args) == 2 {
		year := args[0]
		month := args[1]
		executeGetTransactionCountWithArgsCmd(year, month)
	} else {
		fmt.Println("Invalid number of arguments. See the usage guide.\n\n" +
			utils.GetCmdUsage(programName, transactionCmdLiteral, transactionCountCmdLiteral, transactionCountCmdArgs) +
			transactionCountCmdExamples)
	}
}

func executeGetTransactionCountWithArgsCmd(year string, month string) {
	params := make(map[string]string)
	params["year"] = year
	params["month"] = month
	executeGetTransactionCountCmd(params)
}

// Invoke ../management/transactions/count?year=[year]&month=[month]
func executeGetTransactionCountCmd(params map[string]string) {
	finalUrl := utils.GetRESTAPIBase() + utils.PrefixTransactions + "/" + utils.TransactionCountCmd
	resp, err := utils.UnmarshalData(finalUrl, nil, params, &artifactUtils.TransactionCount{})
	handleResponse(resp, err)
}

func handleResponse(resp interface{}, err error) {
	if err == nil {
		// Printing the details of the Transaction Count
		transactionCount := resp.(*artifactUtils.TransactionCount)
		printTransactionCountInfo(*transactionCount)
	} else {
		fmt.Println(utils.LogPrefixError+"Retrieving transactions count.", err)
	}
}

func printTransactionCountInfo(transactionCountInfo artifactUtils.TransactionCount) {
	fmt.Println("Year - " + strconv.Itoa(transactionCountInfo.Year))
	fmt.Println("Month - " + strconv.Itoa(transactionCountInfo.Month))
	fmt.Println("TransactionCount - " + strconv.FormatInt(transactionCountInfo.TransactionCount, 10))
}
