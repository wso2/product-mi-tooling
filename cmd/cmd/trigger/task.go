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
	"github.com/wso2/product-mi-tooling/cmd/credentials"
	"github.com/wso2/product-mi-tooling/cmd/impl"
	miUtils "github.com/wso2/product-mi-tooling/cmd/utils"
)

var triggerTaskCmdEnvironment string

const artifactTask = "task"
const triggerTaskCmdLiteral = "task [task-name]"

var triggerTaskCmd = &cobra.Command{
	Use:     triggerTaskCmdLiteral,
	Short:   generateTriggerCmdShortDescForArtifact(artifactTask),
	Long:    generateTriggerCmdLongDescForArtifact(artifactTask, "task-name"),
	Example: generateTriggerCmdExamplesForArtifact(artifactTask, miUtils.GetTrimmedCmdLiteral(triggerTaskCmdLiteral), "TestTask"),
	Args:    cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		handleTriggerTaskCmdArguments(args)
	},
}

func init() {
	TriggerCmd.AddCommand(triggerTaskCmd)
	setEnvFlag(triggerTaskCmd, &triggerTaskCmdEnvironment, artifactTask)
}

func handleTriggerTaskCmdArguments(args []string) {
	printTriggerCmdVerboseLog(miUtils.GetTrimmedCmdLiteral(triggerTaskCmdLiteral))
	credentials.HandleMissingCredentials(triggerTaskCmdEnvironment)
	executeTriggerTask(args[0])
}

func executeTriggerTask(taskName string) {
	resp, err := impl.TriggerTask(triggerTaskCmdEnvironment, taskName)
	if err != nil {
		printErrorForArtifact(artifactTask, taskName, err)
	} else {
		fmt.Println(resp)
	}
}
