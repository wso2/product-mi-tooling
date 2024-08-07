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
	"encoding/json"
	"fmt"
	"io/ioutil"
	"os"
)

// PlainTextWarnMessage warning message
const PlainTextWarnMessage = "WARNING: credentials are stored as a plain text in %s\n"

// JsonStore is storing keys in json format
type JsonStore struct {
	// Path to file
	Path string

	// internal usage
	credentials Credentials
}

// NewJsonStore creates a new store
func NewJsonStore(path string) *JsonStore {
	return &JsonStore{Path: path}
}

// Load json store
func (s *JsonStore) Load() error {
	if info, err := os.Stat(s.Path); err == nil && !info.IsDir() {
		data, err := ioutil.ReadFile(s.Path)
		if err != nil {
			return err
		}

		var cred Credentials
		err = json.Unmarshal(data, &cred)
		if err != nil {
			return err
		}

		s.credentials = cred
		return nil
	} else if err == nil && info.IsDir() {
		return fmt.Errorf("%s is a directory", s.Path)
	}

	s.credentials = Credentials{
		Environments: make(map[string]Environment),
	}
	return nil
}

// saves to disk
func (s *JsonStore) persist() error {
	data, err := json.MarshalIndent(s.credentials, "", "  ")
	if err != nil {
		return err
	}
	err = ioutil.WriteFile(s.Path, data, os.ModePerm)
	if err != nil {
		return err
	}
	return nil
}

// GetMICredentials returns credentials for micro integrator from the store or an error
func (s *JsonStore) GetMICredentials(env string) (MiCredential, error) {
	if environment, ok := s.credentials.Environments[env]; ok {
		username, err := Base64Decode(environment.MI.Username)
		if err != nil {
			return MiCredential{}, err
		}
		password, err := Base64Decode(environment.MI.Password)
		if err != nil {
			return MiCredential{}, err
		}
		accessToken, err := Base64Decode(environment.MI.AccessToken)
		if err != nil {
			return MiCredential{}, err
		}
		credential := MiCredential{
			username, password, accessToken,
		}
		return credential, nil
	}
	return MiCredential{}, fmt.Errorf("credentials not found for Mi in %s, use login", env)
}

// SetMICredentials set credentials for mi using username, password, accessToken
func (s *JsonStore) SetMICredentials(env, username, password, accessToken string) error {
	environment := s.credentials.Environments[env]
	environment.MI = MiCredential{
		Username:    Base64Encode(username),
		Password:    Base64Encode(password),
		AccessToken: Base64Encode(accessToken),
	}
	s.credentials.Environments[env] = environment
	err := s.persist()
	if err != nil {
		return err
	}
	fmt.Printf(PlainTextWarnMessage, s.Path)
	return nil
}

// EraseMI remove mi credentials from the store
func (s *JsonStore) EraseMI(env string) error {
	environment, ok := s.credentials.Environments[env]
	if !ok {
		return fmt.Errorf("%s was not found", env)
	}
	environment.MI = MiCredential{}
	s.credentials.Environments[env] = environment
	return s.persist()
}

// HasMI return the existance of mi credentials in the store for a given environment
func (s *JsonStore) HasMI(env string) bool {
	if environment, ok := s.credentials.Environments[env]; ok {
		return miCredentialsExists(environment.MI)
	}
	return false
}

func miCredentialsExists(miCred MiCredential) bool {
	return miCred.AccessToken != "" && miCred.Username != "" && miCred.Password != ""
}
