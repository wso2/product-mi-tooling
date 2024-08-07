/*
*  Copyright (c) WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 LLC. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
 */

package delete

import (
	"errors"
	"fmt"

	"github.com/spf13/cobra"
	add "github.com/wso2/product-mi-tooling/cmd/cmd/add"
	"github.com/wso2/product-mi-tooling/cmd/credentials"
	"github.com/wso2/product-mi-tooling/cmd/utils"
)

var envToBeRemoved string // name of the environment to be removed

// RemoveEnv command related Info
const removeEnvCmdLiteral = "env [environment]"
const removeEnvCmdLiteralTrimmed = "env"
const removeEnvCmdShortDesc = "Delete Environment from Config file"

const removeEnvCmdLongDesc = `Delete Environment and its related endpoints from the config file`

var removeEnvCmdExamples = utils.MiCmdLiteral + ` ` + deleteCmdLiteral + ` ` + removeEnvCmdLiteralTrimmed + ` production`

// removeEnvCmd represents the removeEnv command
var removeEnvCmd = &cobra.Command{
	Use:     removeEnvCmdLiteral,
	Short:   removeEnvCmdShortDesc,
	Long:    removeEnvCmdLongDesc,
	Example: removeEnvCmdExamples,
	Args:    cobra.MinimumNArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		envToBeRemoved := args[0]

		utils.Logln(utils.LogPrefixInfo + removeEnvCmdLiteral + " called")
		executeRemoveEnvCmd(envToBeRemoved, utils.MainConfigFilePath, utils.EnvKeysAllFilePath)
	},
}

func executeRemoveEnvCmd(environment, mainConfigFilePath, envKeysAllFilePath string) {
	err := removeEnv(environment, mainConfigFilePath, envKeysAllFilePath)
	if err != nil {
		utils.HandleErrorAndExit("Error removing environment", err)
	}
}

// removeEnv
// to be used with 'remove-env' command
// @param envName : Name of the environment to be removed from the
// @param mainConfigFilePath : Path to file where env endpoints are stored
// @param envKeysFilePath : Path to file where env keys are stored
// @return error
func removeEnv(envName, mainConfigFilePath, envKeysFilePath string) error {
	if envName == "" {
		return errors.New("name of the environment cannot be blank")
	}
	if utils.EnvExistsInMainConfigFile(envName, mainConfigFilePath) {
		var err error
		if utils.EnvExistsInKeysFile(envName, utils.EnvKeysAllFilePath) {
			// environment exists in keys file, it has to be cleared first
			err = utils.RemoveEnvFromKeysFile(envName, envKeysFilePath, mainConfigFilePath)
			if err != nil {
				return err
			}
		}

		// remove keys also if user has already logged into this environment
		store, err := credentials.GetDefaultCredentialStore()

		if store.HasMI(envName) {
			err = credentials.RunMILogout(envName)
			if err != nil {
				utils.Logln("Log out is unsuccessful for MI.", err)
			}
		}

		// remove env from mainConfig file (endpoints file)
		err = utils.RemoveEnvFromMainConfigFile(envName, mainConfigFilePath)
		if err != nil {
			return err
		}

	} else {
		// environment does not exist in mainConfig file (endpoints file). Nothing to remove
		return errors.New("environment '" + envName + "' not found in " + mainConfigFilePath)
	}

	fmt.Println("Successfully deleted environment '" + envName + "'")
	fmt.Println("Execute '" + utils.MiCmdLiteral + " " + add.AddCmdLiteral + " " + add.AddEnvCmdLiteralTrimmed + " --help' to see how to add a new environment")

	return nil
}

// init using Cobra
func init() {

	DeleteCmd.AddCommand(removeEnvCmd)
}
