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

package deactivate

import (
	"fmt"

	"github.com/spf13/cobra"
	"github.com/wso2/product-mi-tooling/cmd/credentials"
	"github.com/wso2/product-mi-tooling/cmd/impl"
	miUtils "github.com/wso2/product-mi-tooling/cmd/utils"
)

var deactivateInboundEndpointCmdEnvironment string

const artifactInboundEndpoint = "inbound endpoint"
const deactivateInboundEndpointCmdLiteral = "inbound-endpoint [inboundendpoint-name]"

var deactivateInboundEndpointCmd = &cobra.Command{
	Use:     deactivateInboundEndpointCmdLiteral,
	Short:   generateDeactivateCmdShortDescForArtifact(artifactInboundEndpoint),
	Long:    generateDeactivateCmdLongDescForArtifact(artifactInboundEndpoint, "inboundendpoint-name"),
	Example: generateDeactivateCmdExamplesForArtifact(artifactInboundEndpoint, miUtils.GetTrimmedCmdLiteral(deactivateInboundEndpointCmdLiteral), "TestInboundEndpoint"),
	Args:    cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		handleDeactivateInboundEndpointCmdArguments(args)
	},
}

func init() {
	DeactivateCmd.AddCommand(deactivateInboundEndpointCmd)
	setEnvFlag(deactivateInboundEndpointCmd, &deactivateInboundEndpointCmdEnvironment, artifactInboundEndpoint)
}

func handleDeactivateInboundEndpointCmdArguments(args []string) {
	printDeactivateCmdVerboseLog(miUtils.GetTrimmedCmdLiteral(deactivateInboundEndpointCmdLiteral))
	credentials.HandleMissingCredentials(deactivateInboundEndpointCmdEnvironment)
	executeDeactivateInboundEndpoint(args[0])
}

func executeDeactivateInboundEndpoint(inboundEndpointName string) {
	resp, err := impl.DeactivateInboundEndpoint(deactivateInboundEndpointCmdEnvironment, inboundEndpointName)
	if err != nil {
		printErrorForArtifact(artifactInboundEndpoint, inboundEndpointName, err)
	} else {
		fmt.Println(resp)
	}
}
