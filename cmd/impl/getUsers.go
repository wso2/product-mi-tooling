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
	defaultUserListTableFormat = "table {{.UserId}}"
	defaultUserDetailedFormat  = "detail Name - {{.UserId}}\n" +
		"Is Admin - {{.IsAdmin}}\n" +
		"Roles - " +
		"{{range $index, $role := .Roles}}" +
		"{{if $index}}, {{end}}" +
		"{{$role}}" +
		"{{end}}"
)

// GetUserList returns a list of users in the micro integrator in a given environment
func GetUserList(env, role, pattern string) (*artifactUtils.UserList, error) {
	params := make(map[string]string)
	putNonEmptyValueToMap(params, "role", role)
	putNonEmptyValueToMap(params, "pattern", pattern)

	resp, err := callMIManagementEndpointOfResource(utils.MiManagementUserResource, params, env, &artifactUtils.UserList{})
	if err != nil {
		return nil, err
	}
	return resp.(*artifactUtils.UserList), nil
}

// PrintUserList print a list of mi users according to the given format
func PrintUserList(userList *artifactUtils.UserList, format string) {
	if userList.Count > 0 {
		users := userList.Users
		userListContext := getContextWithFormat(format, defaultUserListTableFormat)

		renderer := func(w io.Writer, t *template.Template) error {
			for _, user := range users {
				if err := t.Execute(w, user); err != nil {
					return err
				}
				_, _ = w.Write([]byte{'\n'})
			}
			return nil
		}
		userListTableHeaders := map[string]string{
			"UserId": userIDHeader,
		}
		if err := userListContext.Write(renderer, userListTableHeaders); err != nil {
			fmt.Println("Error executing template:", err.Error())
		}
	} else {
		fmt.Println("No Users found")
	}
}

// GetUserInfo returns a information about a specific user in the micro integrator in a given environment
func GetUserInfo(env, userID, domain string) (*artifactUtils.UserSummary, error) {
	var userInfoResource = utils.MiManagementUserResource + "/" + userID
	params := make(map[string]string)
	putNonEmptyValueToMap(params, "domain", domain)
	resp, err := callMIManagementEndpointOfResource(userInfoResource, params, env, &artifactUtils.UserSummary{})
	if err != nil {
		return nil, err
	}
	return resp.(*artifactUtils.UserSummary), nil
}

// PrintUserDetails prints details about a mi user according to the given format
func PrintUserDetails(userInfo *artifactUtils.UserSummary, format string) {
	if format == "" || strings.HasPrefix(format, formatter.TableFormatKey) {
		format = defaultUserDetailedFormat
	}

	userInfoContext := formatter.NewContext(os.Stdout, format)
	renderer := getItemRendererEndsWithNewLine(userInfo)

	if err := userInfoContext.Write(renderer, nil); err != nil {
		fmt.Println("Error executing template:", err.Error())
	}
}
