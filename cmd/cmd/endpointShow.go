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
	"github.com/wso2/product-mi-tooling/cmd/utils/artifactUtils"
	"strconv"
)

var endpointName string

// Show Endpoint command related usage info
const showEndpointCmdLiteral = "show"
const showEndpointCmdShortDesc = "Get information about endpoints"

const showEndpointCmdLongDesc = "Get information about the endpoint specified by command line argument [endpoint-name] If not specified, list all the endpoints\n"

var showEndpointCmdExamples = "Example:\n" +
	"To get details about a specific endpoint\n" +
	"  " + programName + " " + endpointCmdLiteral + " " + showEndpointCmdLiteral + " TestEndpoint\n\n" +
	"To list all the endpoints\n" +
	"  " + programName + " " + endpointCmdLiteral + " " + showEndpointCmdLiteral + "\n\n"

// endpointShowCmd represents the show endpoint command
var endpointShowCmd = &cobra.Command{
	Use:   showEndpointCmdLiteral,
	Short: showEndpointCmdShortDesc,
	Long:  showEndpointCmdLongDesc + showEndpointCmdExamples,
	Run: func(cmd *cobra.Command, args []string) {
		handleEndpointCmdArguments(args)
	},
}

func init() {
	endpointCmd.AddCommand(endpointShowCmd)
	endpointShowCmd.SetHelpTemplate(showEndpointCmdLongDesc + utils.GetCmdUsage(programName, endpointCmdLiteral,
		showEndpointCmdLiteral, "[endpoint-name]") + showEndpointCmdExamples + utils.GetCmdFlags(endpointCmdLiteral))
}

func handleEndpointCmdArguments(args []string) {
	utils.Logln(utils.LogPrefixInfo + "Show Endpoint called")
	if len(args) == 0 {
		executeListEndpointsCmd()
	} else if len(args) == 1 {
		if args[0] == "help" {
			printEndpointHelp()
		} else {
			endpointName = args[0]
			executeGetEndpointCmd(endpointName)
		}
	} else {
		fmt.Println("Too many arguments. See the usage below")
		printEndpointHelp()
	}
}

func printEndpointHelp() {
	fmt.Print(showEndpointCmdLongDesc + utils.GetCmdUsage(programName, endpointCmdLiteral, showEndpointCmdLiteral,
		"[endpoint-name]") + showEndpointCmdExamples + utils.GetCmdFlags(endpointCmdLiteral))
}

func executeGetEndpointCmd(endpointname string) {

	finalUrl, params := utils.GetUrlAndParams(utils.PrefixEndpoints, "endpointName", endpointname)

	resp, err := utils.UnmarshalData(finalUrl, nil, params, &artifactUtils.Endpoint{})

	if err == nil {
		// Printing the details of the Endpoint
		endpoint := resp.(*artifactUtils.Endpoint)
		printEndpoint(*endpoint)
	} else {
		fmt.Println(utils.LogPrefixError+"Getting Information of Endpoint", err)
	}
}

// Print the details of an Endpoint
// @param Endpoint : Endpoint object
func printEndpoint(endpoint artifactUtils.Endpoint) {

	if len(endpoint.Name) > 0 {
		fmt.Println("Name - " + endpoint.Name)
	}
	if len(endpoint.Type) > 0 {
		fmt.Println("Type - " + endpoint.Type)
	}
	if len(strconv.FormatBool(endpoint.Active)) > 0 {
		fmt.Println("Active - " + strconv.FormatBool(endpoint.Active))
	}
	if len(endpoint.Method) > 0 {
		fmt.Println("Method - " + endpoint.Method)
	}
	if len(endpoint.Address) > 0 {
		fmt.Println("Address - " + endpoint.Address)
	}
	if len(endpoint.URITemplate) > 0 {
		fmt.Println("URI Template - " + endpoint.URITemplate)
	}
	if len(endpoint.ServiceName) > 0 {
		fmt.Println("Service Name - " + endpoint.ServiceName)
	}
	if len(endpoint.PortName) > 0 {
		fmt.Println("Port Name - " + endpoint.PortName)
	}
	if len(endpoint.WsdlURI) > 0 {
		fmt.Println("WSDL URI - " + endpoint.WsdlURI)
	}

}

func executeListEndpointsCmd() {

	finalUrl := utils.GetRESTAPIBase() + utils.PrefixEndpoints

	resp, err := utils.UnmarshalData(finalUrl, nil, nil, &artifactUtils.EndpointList{})

	if err == nil {
		// Printing the list of available Endpoints
		list := resp.(*artifactUtils.EndpointList)
		utils.PrintItemList(list, []string{utils.Name, utils.Type, utils.IsActive}, "No endpoints found")
	} else {
		utils.Logln(utils.LogPrefixError+"Getting List of Endpoints", err)
	}
}
