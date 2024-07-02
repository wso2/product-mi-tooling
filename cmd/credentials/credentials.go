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

package credentials

import (
	"fmt"
	"path/filepath"

	"github.com/wso2/product-mi-tooling/cmd/utils"
)

// DefaultConfigFile name
var DefaultConfigFile = "keys.json"

// Credential for storing user details
type Credential struct {
	// Username of user
	Username string `json:"username"`
	// Password of user
	Password string `json:"password"`
}

// Credentials of cli
type Credentials struct {
	// Environments specific credentials
	Environments map[string]Environment `json:"environments"`
	// CredStore represent type of store to be used
	CredStore string `json:"credStore,omitempty"`
}

// Environment containing credentials of mi
type Environment struct {
	MI MiCredential `json:"mi"`
}

// GetCredentialStore from file
// Note to set a different store please use credStore variable
func GetCredentialStore(f string) (Store, error) {
	// load as a json store first
	js := NewJsonStore(f)
	err := js.Load()
	if err != nil {
		return nil, err
	}
	return js, nil
}

// GetDefaultCredentialStore returns store from default path
func GetDefaultCredentialStore() (Store, error) {
	return GetCredentialStore(filepath.Join(utils.LocalCredentialsDirectoryPath, DefaultConfigFile))
}

// GetBasicAuth returns basic auth username:password encoded in base64
func GetBasicAuth(credential Credential) string {
	return Base64Encode(fmt.Sprintf("%s:%s", credential.Username, credential.Password))
}
