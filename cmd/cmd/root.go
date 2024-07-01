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

package cmd

import (
	"fmt"
	"os"
	"time"

	"github.com/spf13/cobra"
	miActivateCmd "github.com/wso2/product-mi-tooling/cmd/cmd/activate"
	miAddCmd "github.com/wso2/product-mi-tooling/cmd/cmd/add"
	miDeactivateCmd "github.com/wso2/product-mi-tooling/cmd/cmd/deactivate"
	miDeleteCmd "github.com/wso2/product-mi-tooling/cmd/cmd/delete"
	miGetCmd "github.com/wso2/product-mi-tooling/cmd/cmd/get"
	miUpdateCmd "github.com/wso2/product-mi-tooling/cmd/cmd/update"
	"github.com/wso2/product-mi-tooling/cmd/utils"
)

var verbose bool
var cfgFile string
var insecure bool

const miCmdShortDesc = "Micro Integrator commands"

var miCmdLongDesc = getMiCmdLongDesc()

func getMiCmdLongDesc() string {

	return utils.MiCmdLiteral + " is a Command Line Tool for Managing WSO2 Micro Integrator"
}

// MICmd represents the mi command
var MICmd = &cobra.Command{
	Use:   utils.MiCmdLiteral,
	Short: miCmdShortDesc,
	Long:  miCmdLongDesc,
	// Example: miCmdExamples,
	Run: func(cmd *cobra.Command, args []string) {
		utils.Logln(utils.LogPrefixInfo + utils.MiCmdLiteral + " called")
		cmd.Help()
	},
}

func init() {
	MICmd.AddCommand(miGetCmd.GetCmd)
	MICmd.AddCommand(miAddCmd.AddCmd)
	MICmd.AddCommand(miDeleteCmd.DeleteCmd)
	MICmd.AddCommand(miUpdateCmd.UpdateCmd)
	MICmd.AddCommand(miActivateCmd.ActivateCmd)
	MICmd.AddCommand(miDeactivateCmd.DeactivateCmd)
}

func createConfigFiles() {
	err := utils.CreateDirIfNotExist(utils.GetConfigDirPath())
	if err != nil {
		utils.HandleErrorAndExit("Error creating config directory: "+utils.ConfigDirPath, err)
	}

	if !utils.IsFileExist(utils.MainConfigFilePath) {
		var mainConfig = new(utils.MainConfig)
		mainConfig.Config = utils.Config{HttpRequestTimeout: utils.DefaultHttpRequestTimeout,
			ExportDirectory: utils.DefaultExportDirPath}

		utils.WriteConfigFile(mainConfig, utils.MainConfigFilePath)
	}

	err = utils.CreateDirIfNotExist(utils.LocalCredentialsDirectoryPath)
	if err != nil {
		utils.HandleErrorAndExit("Error creating local directory: "+utils.LocalCredentialsDirectoryName, err)
	}
}

// initConfig reads in config file and ENV variables if set.
func initConfig() {
	if verbose {
		utils.EnableVerboseMode()
		t := time.Now()
		utils.Logf("Executed MI CLI (%s) on %v\n", utils.MiCmdLiteral, t.Format(time.RFC1123))
	}

	utils.Logln(utils.LogPrefixInfo+"Insecure:", insecure)
	if insecure {
		utils.Insecure = true
	}
}

// Execute adds all child commands to the root command sets flags appropriately.
// This is called by main.main(). It only needs to happen once to the rootCmd.
func Execute() {
	if err := MICmd.Execute(); err != nil {
		fmt.Println(err)
		os.Exit(-1)
	}
}
