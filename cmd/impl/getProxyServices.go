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
	defaultProxyServiceListTableFormat = "table {{.Name}}\t{{.Wsdl11}}\t{{.Wsdl20}}"
	defaultProxyServiceDetailedFormat  = "detail Name - {{.Name}}\n" +
		"WSDL 1.1 - {{.Wsdl11}}\n" +
		"WSDL 2.0 - {{.Wsdl20}}\n" +
		"Stats - {{.Stats}}\n" +
		"Tracing - {{.Tracing}}"
)

// GetProxyServiceList returns a list of proxy serives deployed in the micro integrator in a given environment
func GetProxyServiceList(env string) (*artifactUtils.ProxyServiceList, error) {
	resp, err := getArtifactList(utils.MiManagementProxyServiceResource, env, &artifactUtils.ProxyServiceList{})
	if err != nil {
		return nil, err
	}
	return resp.(*artifactUtils.ProxyServiceList), nil
}

// PrintProxyServiceList print a list of proxy serives according to the given format
func PrintProxyServiceList(proxyList *artifactUtils.ProxyServiceList, format string) {
	if proxyList.Count > 0 {
		proxies := proxyList.Proxies
		proxyListContext := getContextWithFormat(format, defaultProxyServiceListTableFormat)

		renderer := func(w io.Writer, t *template.Template) error {
			for _, proxy := range proxies {
				if err := t.Execute(w, proxy); err != nil {
					return err
				}
				_, _ = w.Write([]byte{'\n'})
			}
			return nil
		}
		proxyListTableHeaders := map[string]string{
			"Name":   nameHeader,
			"Wsdl11": wsdl11Header,
			"Wsdl20": wsdl20Header,
		}
		if err := proxyListContext.Write(renderer, proxyListTableHeaders); err != nil {
			fmt.Println("Error executing template:", err.Error())
		}
	} else {
		fmt.Println("No Proxy Services found")
	}
}

// GetProxyService returns a information about a specific proxy deployed in the micro integrator in a given environment
func GetProxyService(env, proxyName string) (*artifactUtils.Proxy, error) {
	resp, err := getArtifactInfo(utils.MiManagementProxyServiceResource, "proxyServiceName", proxyName, env, &artifactUtils.Proxy{})
	if err != nil {
		return nil, err
	}
	return resp.(*artifactUtils.Proxy), nil
}

// PrintProxyServiceDetails prints details about a proxy according to the given format
func PrintProxyServiceDetails(proxy *artifactUtils.Proxy, format string) {
	if format == "" {
		format = defaultProxyServiceDetailedFormat
	}

	proxyContext := formatter.NewContext(os.Stdout, format)
	renderer := getItemRendererEndsWithNewLine(proxy)

	if err := proxyContext.Write(renderer, nil); err != nil {
		fmt.Println("Error executing template:", err.Error())
	}
}
