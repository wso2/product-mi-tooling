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

package impl

import (
	"github.com/wso2/product-mi-tooling/cmd/utils"
)

// ActivateTask activates a task deployed in the micro integrator in a given environment
func ActivateTask(env, taskName string) (interface{}, error) {
	return updateTaskServiceState(env, taskName, "active")
}

// DeactivateTask deactivates a task deployed in the micro integrator in a given environment
func DeactivateTask(env, taskName string) (interface{}, error) {
	return updateTaskServiceState(env, taskName, "inactive")
}

// TriggerTask triggers a task deployed in the micro integrator in a given environment
func TriggerTask(env, taskName string) (interface{}, error) {
	return updateTaskServiceState(env, taskName, "trigger")
}

func updateTaskServiceState(env, taskName, state string) (interface{}, error) {
	url := utils.GetMIManagementEndpointOfResource(utils.MiManagementTaskResource, env, utils.MainConfigFilePath)
	return updateArtifactState(url, taskName, state, env)
}
