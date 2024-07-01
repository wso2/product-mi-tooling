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
	"strings"
	"text/template"

	"github.com/wso2/product-mi-tooling/cmd/formatter"
	"github.com/wso2/product-mi-tooling/cmd/utils"
	"github.com/wso2/product-mi-tooling/cmd/utils/artifactUtils"
)

const (
	defaultTemplateListTableFormat        = "table {{.TemplateName}}\t{{.TemplateType}}"
	defaultTemplateListByTypeTableFormat  = "table {{.Name}}"
	defaultSequenceTemplateDetailedFormat = "detail Name - {{.Name}}\n" +
		"Parameters :\n" +
		"{{ if eq (len .Parameters) 0 }}" +
		"No Parameters found\n" +
		"{{else}}" +
		"NAME\tDEFAULT VALUE\tMANDATORY\n" +
		"{{range .Parameters}}{{.Name}}\t{{.DefaultValue}}\t{{.IsMandatory}}\n{{end}}" +
		"{{ end }}"
	defaultEndpointTemplateDetailedFormat = "detail Name - {{.Name}}\n" +
		"Parameters : " +
		"{{ if eq (len .Parameters) 0 }}" +
		"No Parameters found\n" +
		"{{else}}" +
		"{{range $index, $param := .Parameters}}" +
		"{{if $index}}, {{end}}" +
		"{{$param}}" +
		"{{end}}" +
		"{{end}}"
)

// templateArtifact holds information about a template for outputting
type templateArtifact struct {
	templateName string
	templateType string
}

// creates a new api from utils.API
func newTemplateArtifactDefinitionFromTemplate(template artifactUtils.Template, templateType string) *templateArtifact {
	return &templateArtifact{template.Name, templateType}
}

// Name of template
func (a templateArtifact) TemplateName() string {
	return a.templateName
}

// Type of template
func (a templateArtifact) TemplateType() string {
	return a.templateType
}

// MarshalJSON marshals templateArtifact using custom marshaller which uses methods instead of fields
func (a *templateArtifact) MarshalJSON() ([]byte, error) {
	return formatter.MarshalJSON(a)
}

// GetTemplateList returns a list of Templates deployed in the micro integrator in a given environment
func GetTemplateList(env string) (*artifactUtils.TemplateList, error) {
	resp, err := getArtifactList(utils.MiManagementTemplateResource, env, &artifactUtils.TemplateList{})
	if err != nil {
		return nil, err
	}
	return resp.(*artifactUtils.TemplateList), nil
}

// PrintTemplateList print a list of Templates according to the given format
func PrintTemplateList(templateList *artifactUtils.TemplateList, format string) {
	var sequenceTemplatesCount = len(templateList.SequenceTemplates)
	var endpointTemplatesCount = len(templateList.EndpointTemplates)

	if sequenceTemplatesCount+endpointTemplatesCount > 0 {
		templates := make([]templateArtifact, 0, sequenceTemplatesCount+endpointTemplatesCount)

		for _, template := range templateList.SequenceTemplates {
			templateArtifact := templateArtifact{template.Name, "Sequence"}
			templates = append(templates, templateArtifact)
		}
		for _, template := range templateList.EndpointTemplates {
			templateArtifact := templateArtifact{template.Name, "Endpoint"}
			templates = append(templates, templateArtifact)
		}
		templateListContext := getContextWithFormat(format, defaultTemplateListTableFormat)

		renderer := func(w io.Writer, t *template.Template) error {
			for _, template := range templates {
				if err := t.Execute(w, template); err != nil {
					return err
				}
				_, _ = w.Write([]byte{'\n'})
			}
			return nil
		}
		templateListTableHeaders := map[string]string{
			"TemplateName": nameHeader,
			"TemplateType": typeHeader,
		}
		if err := templateListContext.Write(renderer, templateListTableHeaders); err != nil {
			fmt.Println("Error executing template:", err.Error())
		}
	} else {
		fmt.Println("No Templates found")
	}
}

// GetTemplatesByType returns a list of Templates of specified type deployed in the micro integrator in a given environment
func GetTemplatesByType(env, templateType string) (*artifactUtils.TemplateListByType, error) {
	resp, err := getArtifactInfo(utils.MiManagementTemplateResource, "type", templateType, env, &artifactUtils.TemplateListByType{})
	if err != nil {
		return nil, err
	}
	return resp.(*artifactUtils.TemplateListByType), nil
}

// PrintTemplatesByType print a list of Templates of specified type according to the given format
func PrintTemplatesByType(templateList *artifactUtils.TemplateListByType, format string) {
	if templateList.Count > 0 {
		templates := templateList.Templates
		templateListByTypeContext := getContextWithFormat(format, defaultTemplateListByTypeTableFormat)

		renderer := func(w io.Writer, t *template.Template) error {
			for _, template := range templates {
				if err := t.Execute(w, template); err != nil {
					return err
				}
				_, _ = w.Write([]byte{'\n'})
			}
			return nil
		}
		templateListByTypeTableHeaders := map[string]string{
			"Name": nameHeader,
		}
		if err := templateListByTypeContext.Write(renderer, templateListByTypeTableHeaders); err != nil {
			fmt.Println("Error executing template:", err.Error())
		}
	} else {
		fmt.Println("No Templates found for the given type")
	}
}

// GetEndpointTemplate returns a information about a specific endpoint template deployed in the micro integrator in a given environment
func GetEndpointTemplate(env, templateName string) (*artifactUtils.TemplateEndpointListByName, error) {
	resp, err := getTemplate(env, "endpoint", templateName, &artifactUtils.TemplateEndpointListByName{})
	if err != nil {
		return nil, err
	}
	return resp.(*artifactUtils.TemplateEndpointListByName), nil
}

// GetSequenceTemplate returns a information about a specific sequence template deployed in the micro integrator in a given environment
func GetSequenceTemplate(env, templateName string) (*artifactUtils.TemplateSequenceListByName, error) {
	resp, err := getTemplate(env, "sequence", templateName, &artifactUtils.TemplateSequenceListByName{})
	if err != nil {
		return nil, err
	}
	return resp.(*artifactUtils.TemplateSequenceListByName), nil
}

func getTemplate(env, templateType, templateName string, model interface{}) (interface{}, error) {
	params := make(map[string]string)
	params["type"] = templateType
	params["name"] = templateName

	resp, err := callMIManagementEndpointOfResource(utils.MiManagementTemplateResource, params, env, model)
	if err != nil {
		return nil, err
	}
	return resp, nil
}

// PrintSequenceTemplateDetails prints details about a sequence template according to the given format
func PrintSequenceTemplateDetails(sequenceTemplate *artifactUtils.TemplateSequenceListByName, format string) {
	if format == "" || strings.HasPrefix(format, formatter.TableFormatKey) {
		format = defaultSequenceTemplateDetailedFormat
	}

	templateContext := formatter.NewContext(os.Stdout, format)
	renderer := getItemRenderer(sequenceTemplate)

	if err := templateContext.Write(renderer, nil); err != nil {
		fmt.Println("Error executing template:", err.Error())
	}
}

// PrintEndpointTemplateDetails prints details about a endpoint template according to the given format
func PrintEndpointTemplateDetails(endpointTemplate *artifactUtils.TemplateEndpointListByName, format string) {
	if format == "" || strings.HasPrefix(format, formatter.TableFormatKey) {
		format = defaultEndpointTemplateDetailedFormat
	}

	templateContext := formatter.NewContext(os.Stdout, format)
	renderer := getItemRendererEndsWithNewLine(endpointTemplate)

	if err := templateContext.Write(renderer, nil); err != nil {
		fmt.Println("Error executing template:", err.Error())
	}
}
