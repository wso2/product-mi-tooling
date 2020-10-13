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
	"strings"
)

const vaultHashiCorpCmdLiteral = "hashicorp"
const vaultHashiCorpCmdShortDesc = "Update the secretid of the HashiCorp vault configuration"
const vaultHashiCorpCmdLongDesc = "Update the secretid of the HashiCorp vault configuration in Micro Integrator server runtime"

var vaultHashiCorpSecretIdCmdArg = "--secretId=<secret_id>"
var vaultHashiCorpCmdExamples = "Example:\n" +
	"To update the secretId of the HashiCorp configuration\n" +
	"  " + programName + " " + vaultCmdLiteral + " " + vaultHashiCorpCmdLiteral + " " + vaultHashiCorpSecretIdCmdArg + "\n\n"

var vaultHashiCorpCmd = &cobra.Command{
	Use:   vaultHashiCorpCmdLiteral,
	Short: vaultHashiCorpCmdShortDesc,
	Long:  vaultHashiCorpCmdLongDesc + vaultHashiCorpCmdExamples,
	Run: func(cmd *cobra.Command, args []string) {
		secretId, _ := cmd.Flags().GetString("secretId")
		if len(strings.TrimSpace(secretId)) == 0 {
			fmt.Println("Please provide the  --secretId to update the HashiCorp vault runtime configuration.\n\n" +
				utils.GetCmdUsage(programName, vaultCmdLiteral, vaultHashiCorpCmdLiteral,
					vaultHashiCorpSecretIdCmdArg) + vaultHashiCorpCmdExamples)
		}
		handleExternalVaultHashiCorpCmdArguments(args, secretId)
	},
}

func init() {
	vaultCmd.AddCommand(vaultHashiCorpCmd)
	vaultHashiCorpCmd.Flags().StringP("secretId", "s", "", "secretId value")
	vaultHashiCorpCmd.SetHelpTemplate(vaultHashiCorpCmdLongDesc + utils.GetCmdUsage(programName,
		vaultCmdLiteral, vaultHashiCorpCmdLiteral, vaultHashiCorpSecretIdCmdArg) +
		vaultHashiCorpCmdExamples + utils.GetCmdFlags(vaultCmdLiteral))
}

// Check arguments for secret_id and update the config in server runtime.
// Print an error if no valid number of arguments.
func handleExternalVaultHashiCorpCmdArguments(args []string, secretId string) {
	// check for the vault name argument
	if len(args) == 0 {
		executeSetHashiCorpSecretIdCmd(secretId)
	} else {
		fmt.Println("Invalid number of arguments. See the usage guide.\n\n" +
			utils.GetCmdUsage(programName, vaultCmdLiteral, vaultHashiCorpCmdLiteral,
				vaultHashiCorpSecretIdCmdArg) + vaultHashiCorpCmdExamples)
	}
}

// Invoke ../management/externalvault/hashicorp
func executeSetHashiCorpSecretIdCmd(secretid string) {
	resp, err := utils.UpdateHashiCorpSecretId(secretid)

	if err != nil {
		fmt.Println(utils.LogPrefixError + "Updating secretId of HashiCorp configuration: ", err)
	} else {
		fmt.Println("External vault: ", resp)
	}
}

