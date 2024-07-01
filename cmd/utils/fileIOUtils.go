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

package utils

import (
	"errors"
	"fmt"
	"io/ioutil"
	"os"
	"path/filepath"

	"gopkg.in/yaml.v2"
)

// Check whether the file exists.
func IsFileExist(path string) bool {
	if _, err := os.Stat(path); err != nil {
		if os.IsNotExist(err) {
			return false
		} else {
			HandleErrorAndExit("Unable to find file "+path, err)
		}
	}
	return true
}

func GetRemoteConfigFilePath() string {

	userHomeDir, err := os.UserHomeDir()
	if err != nil {
		HandleErrorAndExit("Error getting user home directory: ", err)
	}
	configDirectory := filepath.Join(userHomeDir, ConfigDirName)
	MakeDirectoryIfNotExists(configDirectory)
	remoteConfigFilePath := filepath.Join(configDirectory, RemoteConfigFileName)
	return remoteConfigFilePath
}

func GetFileContent(filePath string) string {
	data, err := ioutil.ReadFile(filePath)
	if err != nil {
		HandleErrorAndExit("Error reading: "+filePath, err)
	}

	return string(data)
}

func GetConfigFilePath(configFileName string) string {

	userHomeDir, err := os.UserHomeDir()
	if err != nil {
		HandleErrorAndExit("Error getting user home directory: ", err)
	}
	configFilePath := filepath.Join(userHomeDir, configFileName)
	return configFilePath
}

// WriteConfigFile
// @param c : data
// @param envConfigFilePath : Path to file where env endpoints are stored
func WriteConfigFile(c interface{}, configFilePath string) {
	data, err := yaml.Marshal(&c)
	if err != nil {
		HandleErrorAndExit("Unable to write configuration to file.", err)
	}

	err = ioutil.WriteFile(configFilePath, data, 0644)
	if err != nil {
		HandleErrorAndExit("Unable to write configuration to file.", err)
	}
}

// Read and return EnvKeysAll
func GetEnvKeysAllFromFile(envKeysAllFilePath string) *EnvKeysAll {
	data, err := ioutil.ReadFile(envKeysAllFilePath)
	if err != nil {
		fmt.Println("Error reading " + envKeysAllFilePath)
		os.Create(envKeysAllFilePath)
		data, err = ioutil.ReadFile(envKeysAllFilePath)
	}

	var envKeysAll EnvKeysAll
	if err := envKeysAll.ParseEnvKeysFromFile(data); err != nil {
		fmt.Println(LogPrefixError + "parsing " + envKeysAllFilePath)
		return nil
	}

	return &envKeysAll
}

// Read and validate contents of env_keys_all.yaml
// will throw errors if the any of the lines is blank
func (envKeysAll *EnvKeysAll) ParseEnvKeysFromFile(data []byte) error {
	if err := yaml.Unmarshal(data, envKeysAll); err != nil {
		return err
	}
	for name, keys := range envKeysAll.Environments {
		if keys.ClientID == "" {
			return errors.New("Blank ClientID for " + name)
		}
		if keys.ClientSecret == "" {
			return errors.New("Blank ClientSecret for " + name)
		}
	}
	return nil
}

func CreateDirIfNotExist(path string) (err error) {
	if _, err := os.Stat(path); os.IsNotExist(err) {
		os.MkdirAll(path, os.ModePerm)
	}
	return err
}

// Read and validate contents of main_config.yaml
// will throw errors if the any of the lines is blank
func (mainConfig *MainConfig) ParseMainConfigFromFile(data []byte) error {
	if err := yaml.Unmarshal(data, mainConfig); err != nil {
		return err
	}
	return nil
}
