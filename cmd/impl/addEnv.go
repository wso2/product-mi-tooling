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
	"errors"
	"fmt"

	"github.com/wso2/product-mi-tooling/cmd/utils"
)

// AddEnv adds a new environment and its endpoints and writes to config file
// @param envName : Name of the Environment
// @param mainConfigFilePath : Path to file where env endpoints are stored
// @return error

func AddMIEnv(envName string, envEndpoints *utils.EnvEndpoints, mainConfigFilePath, addEnvCmdLiteral string) error {

	if envName == "" {
		// name of the environment is blank
		return errors.New("Name of the environment cannot be blank")
	}

	mainConfig := utils.GetMainConfigFromFile(mainConfigFilePath)

	var validatedEnvEndpoints = utils.EnvEndpoints{
		MiManagementEndpoint: envEndpoints.MiManagementEndpoint,
	}
	if envEndpoints.MiManagementEndpoint != "" {
		validatedEnvEndpoints.MiManagementEndpoint = envEndpoints.MiManagementEndpoint
	}

	mainConfig.Environments[envName] = validatedEnvEndpoints
	utils.WriteConfigFile(mainConfig, mainConfigFilePath)

	fmt.Printf("Successfully added environment '%s'\n", envName)

	return nil
}
