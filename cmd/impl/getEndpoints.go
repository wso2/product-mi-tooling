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
	"fmt"
	"io"
	"os"
	"text/template"

	"github.com/wso2/product-mi-tooling/cmd/formatter"
	"github.com/wso2/product-mi-tooling/cmd/utils"
	"github.com/wso2/product-mi-tooling/cmd/utils/artifactUtils"
)

const (
	defaultEndpointListTableFormat = "table {{.Name}}\t{{.Type}}\t{{.Active}}"
	defaultEndpointDetailedFormat  = "detail Name - {{.Name}}\n" +
		"{{if .Type}}Type - {{.Type}}\n{{ end }}" +
		"{{if .Active}}Active - {{.Active}}\n{{ end }}" +
		"{{if .Method}}Method - {{.Method}}\n{{ end }}" +
		"{{if .Address}}Address - {{.Address}}\n{{ end }}" +
		"{{if .URITemplate}}URI Template - {{.URITemplate}}\n{{ end }}" +
		"{{if .ServiceName}}Service Name - {{.ServiceName}}\n{{ end }}" +
		"{{if .PortName}}Port Name - {{.PortName}}\n{{ end }}" +
		"{{if .WsdlURI}}WSDL URI - {{.WsdlURI}}\n{{ end }}"
)

// GetEndpointList returns a list of endpoints
func GetEndpointList(env string) (*artifactUtils.EndpointList, error) {
	resp, err := getArtifactList(utils.MiManagementEndpointResource, env, &artifactUtils.EndpointList{})
	if err != nil {
		return nil, err
	}
	return resp.(*artifactUtils.EndpointList), nil
}

// PrintEndpointList print a list of endpoints
func PrintEndpointList(endpointList *artifactUtils.EndpointList, format string) {
	if endpointList.Count > 0 {
		endpoints := endpointList.Endpoints
		endpointListContext := getContextWithFormat(format, defaultEndpointListTableFormat)

		renderer := func(w io.Writer, t *template.Template) error {
			for _, endpoint := range endpoints {
				if err := t.Execute(w, endpoint); err != nil {
					return err
				}
				_, _ = w.Write([]byte{'\n'})
			}
			return nil
		}
		endpointListTableHeaders := map[string]string{
			"Name":   nameHeader,
			"Type":   typeHeader,
			"Active": activeHeader,
		}
		if err := endpointListContext.Write(renderer, endpointListTableHeaders); err != nil {
			fmt.Println("Error executing template:", err.Error())
		}
	} else {
		fmt.Println("No Endpoints found")
	}
}

// GetEndpoint returns information about a specific endpoint
func GetEndpoint(env, endpointName string) (*artifactUtils.Endpoint, error) {
	resp, err := getArtifactInfo(utils.MiManagementEndpointResource, "endpointName", endpointName, env, &artifactUtils.Endpoint{})
	if err != nil {
		return nil, err
	}
	return resp.(*artifactUtils.Endpoint), nil
}

// PrintEndpointDetails prints details about an endpoint
func PrintEndpointDetails(endpoint *artifactUtils.Endpoint, format string) {
	if format == "" {
		format = defaultEndpointDetailedFormat
	}

	endpointContext := formatter.NewContext(os.Stdout, format)
	renderer := getItemRenderer(endpoint)

	if err := endpointContext.Write(renderer, nil); err != nil {
		fmt.Println("Error executing template:", err.Error())
	}
}
