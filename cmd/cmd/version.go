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

// Version of the latest release of MI CLI
var Version = "v1.0.0"

// BuildDate of the latest release of MI CLI
var BuildDate = ""

const versionCmdLiteral = "version"

var versionCmdShortDesc = "Display Version on current " + utils.MiCmdLiteral

const versionCmdLongDesc = "Display the current version of this command line tool"

var versionCmdExamples = utils.MiCmdLiteral + " " + versionCmdLiteral

// VersionCmd represents the version command
var VersionCmd = &cobra.Command{
	Use:     versionCmdLiteral,
	Short:   versionCmdShortDesc,
	Long:    versionCmdLongDesc,
	Example: versionCmdExamples,
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Println("Version:", Version)
		fmt.Println("Build Date:", BuildDate)
	},
}

// init using Cobra
func init() {
	MICmd.AddCommand(VersionCmd)
}
