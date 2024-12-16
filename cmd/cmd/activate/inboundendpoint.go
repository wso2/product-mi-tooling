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

package activate

import (
	"fmt"

	"github.com/spf13/cobra"
	"github.com/wso2/product-mi-tooling/cmd/credentials"
	impl "github.com/wso2/product-mi-tooling/cmd/impl"
	miUtils "github.com/wso2/product-mi-tooling/cmd/utils"
)

var activateInboundEndpointCmdEnvironment string

const artifactInboundEndpoint = "inbound endpoint"
const activateInboundEndpointCmdLiteral = "inbound-endpoint [inboundendpoint-name]"

var activateInboundEndpointCmd = &cobra.Command{
	Use:     activateInboundEndpointCmdLiteral,
	Short:   generateActivateCmdShortDescForArtifact(artifactInboundEndpoint),
	Long:    generateActivateCmdLongDescForArtifact(artifactInboundEndpoint, "inboundendpoint-name"),
	Example: generateActivateCmdExamplesForArtifact(artifactInboundEndpoint, miUtils.GetTrimmedCmdLiteral(activateInboundEndpointCmdLiteral), "TestInboundEndpoint"),
	Args:    cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		handleActivateInboundEndpointCmdArguments(args)
	},
}

func init() {
	ActivateCmd.AddCommand(activateInboundEndpointCmd)
	setEnvFlag(activateInboundEndpointCmd, &activateInboundEndpointCmdEnvironment, artifactInboundEndpoint)
}

func handleActivateInboundEndpointCmdArguments(args []string) {
	printActivateCmdVerboseLog(miUtils.GetTrimmedCmdLiteral(activateInboundEndpointCmdLiteral))
	credentials.HandleMissingCredentials(activateInboundEndpointCmdEnvironment)
	executeActivateInboundEndpoint(args[0])
}

func executeActivateInboundEndpoint(inboundEndpointName string) {
	resp, err := impl.ActivateInboundEndpoint(activateInboundEndpointCmdEnvironment, inboundEndpointName)
	if err != nil {
		printErrorForArtifact(artifactInboundEndpoint, inboundEndpointName, err)
	} else {
		fmt.Println(resp)
	}
}
