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

type Store interface {
	// HasMI return the existance of mi credentials in the store for a given environment
	HasMI(env string) bool
	// GetMICredentials returns credentials for micro integrator from the store or an error
	GetMICredentials(env string) (MiCredential, error)
	// SetMICredentials sets credentials for micro integrator using username, password and access token
	SetMICredentials(env, username, password, accessToken string) error
	// Erase mi credentials in a given environment
	EraseMI(env string) error
	// Load store
	Load() error
}
