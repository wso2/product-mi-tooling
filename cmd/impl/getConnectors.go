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
	"text/template"

	"github.com/wso2/product-mi-tooling/cmd/utils"
	"github.com/wso2/product-mi-tooling/cmd/utils/artifactUtils"
)

const defaultConnectorListTableFormat = "table {{.Name}}\t{{.Status}}\t{{.Package}}\t{{.Description}}"

// GetConnectorList returns a list of connector artifacts deployed in the micro integrator in a given environment
func GetConnectorList(env string) (*artifactUtils.ConnectorList, error) {
	resp, err := getArtifactList(utils.MiManagementConnectorResource, env, &artifactUtils.ConnectorList{})
	if err != nil {
		return nil, err
	}
	return resp.(*artifactUtils.ConnectorList), nil
}

// PrintConnectorList print a list of connectors according to the given format
func PrintConnectorList(connectorList *artifactUtils.ConnectorList, format string) {
	if connectorList.Count > 0 {
		connectors := connectorList.Connectors
		connectorListContext := getContextWithFormat(format, defaultConnectorListTableFormat)

		renderer := func(w io.Writer, t *template.Template) error {
			for _, connector := range connectors {
				if err := t.Execute(w, connector); err != nil {
					return err
				}
				_, _ = w.Write([]byte{'\n'})
			}
			return nil
		}
		connectorListTableHeaders := map[string]string{
			"Name":        nameHeader,
			"Status":      statsHeader,
			"Package":     packageHeader,
			"Description": descriptionHeader,
		}
		if err := connectorListContext.Write(renderer, connectorListTableHeaders); err != nil {
			fmt.Println("Error executing template:", err.Error())
		}
	} else {
		fmt.Println("No Connectors found")
	}
}
