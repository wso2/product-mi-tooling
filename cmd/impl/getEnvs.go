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
)

const (
	envsNameHeader           = "NAME"
	envsMiManagementEndpoint = "MI MANAGEMENT ENDPOINT"
)

// endpoint contains information about endpoint of MI
type endpoints struct {
	name                 string
	miManagementEndpoint string
}

func newEndpointFromEnvEndpoints(name string, e utils.EnvEndpoints) *endpoints {
	return &endpoints{
		name:                 name,
		miManagementEndpoint: e.MiManagementEndpoint,
	}
}

// Name of endpoint
func (e endpoints) Name() string {
	return e.name
}

// MiManagementEndpoint
func (e endpoints) MiManagementEndpoint() string {
	return e.miManagementEndpoint
}

// MarshalJSON returns marshaled methods
func (e *endpoints) MarshalJSON() ([]byte, error) {
	return formatter.MarshalJSON(e)
}

// PrintEnvs
func PrintEnvs(envData map[string]utils.EnvEndpoints, format, defaulEnvsTableFormat string) {
	if format == "" {
		format = defaulEnvsTableFormat
	}

	// create api context with standard output
	envsContext := formatter.NewContext(os.Stdout, format)

	// create a new renderer function which iterate collection
	renderer := func(w io.Writer, t *template.Template) error {
		for name, endpointDef := range envData {
			if err := t.Execute(w, newEndpointFromEnvEndpoints(name, endpointDef)); err != nil {
				return err
			}
			_, _ = w.Write([]byte{'\n'})
		}
		return nil
	}

	// headers for table
	envsTableHeaders := map[string]string{
		"Name":                 envsNameHeader,
		"MiManagementEndpoint": envsMiManagementEndpoint,
	}

	// execute context
	if err := envsContext.Write(renderer, envsTableHeaders); err != nil {
		fmt.Println("Error executing template:", err.Error())
	}
}
