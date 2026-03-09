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

package trigger

import (
	"fmt"

	"github.com/spf13/cobra"
	"github.com/wso2/product-mi-tooling/cmd/utils"
)

func generateTriggerCmdShortDescForArtifact(artifact string) string {
	return "Trigger a " + artifact + " deployed in a Micro Integrator"
}

func generateTriggerCmdLongDescForArtifact(artifact, argument string) string {
	return "Trigger the " + artifact + " specified by the command line argument [" + argument + "] deployed in a Micro Integrator in the environment specified by the flag --environment, -e"
}

func generateTriggerCmdExamplesForArtifact(artifact, cmdLiteral, sampleResourceName string) string {
	return "To trigger a " + artifact + "\n" +
		"  " + utils.MiCmdLiteral + " " + triggerCmdLiteral + " " + cmdLiteral + " " + sampleResourceName + " -e dev\n" +
		"NOTE: The flag (--environment (-e)) is mandatory"
}

func printErrorForArtifact(artifactType, artifactName string, err error) {
	fmt.Println(utils.LogPrefixError+"Activating "+artifactType+" [ "+artifactName+" ]", err)
}

func printTriggerCmdVerboseLog(cmd string) {
	utils.Logln(utils.LogPrefixInfo + triggerCmdLiteral + " " + cmd + " called")
}

func setEnvFlag(cmd *cobra.Command, param *string, artifactType string) {
	cmd.Flags().StringVarP(param, "environment", "e", "", "Environment of the micro integrator in which the "+artifactType+" should be triggerd")
	cmd.MarkFlagRequired("environment")
}
