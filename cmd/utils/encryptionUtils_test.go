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

package utils

import "testing"

func TestGetKeyStoreType(t *testing.T) {
	validCases := map[string]string{
		"wso2carbon.jks":        JKSKeyStoreType,
		"wso2carbon.JKS":        JKSKeyStoreType,
		"wso2carbon.p12":        PKCS12KeyStoreType,
		"wso2carbon.pfx":        PKCS12KeyStoreType,
		"wso2carbon.pkcs12":     PKCS12KeyStoreType,
		"  wso2carbon.p12\n":    PKCS12KeyStoreType,
		"/a/b/c/wso2carbon.jks": JKSKeyStoreType,
	}
	for path, expected := range validCases {
		actual, err := GetKeyStoreType(path)
		if err != nil {
			t.Errorf("GetKeyStoreType(%q) returned error: %v", path, err)
		}
		if actual != expected {
			t.Errorf("GetKeyStoreType(%q) = %q, expected %q", path, actual, expected)
		}
	}

	invalidCases := []string{"wso2carbon.txt", "wso2carbon", ""}
	for _, path := range invalidCases {
		if _, err := GetKeyStoreType(path); err == nil {
			t.Errorf("GetKeyStoreType(%q) expected error, got nil", path)
		}
	}
}

func TestIsValidKeyStoreConfig(t *testing.T) {
	jksConfig := &KeyStoreConfig{
		KeyStorePath:     "wso2carbon.jks",
		KeyStoreType:     JKSKeyStoreType,
		KeyStorePassword: "cGFzcw==",
		KeyAlias:         "wso2carbon",
	}
	if IsValidKeyStoreConfig(jksConfig) {
		t.Error("JKS config without key password should be invalid")
	}
	jksConfig.KeyPassword = "cGFzcw=="
	if !IsValidKeyStoreConfig(jksConfig) {
		t.Error("JKS config with all fields should be valid")
	}

	pkcs12Config := &KeyStoreConfig{
		KeyStorePath:     "wso2carbon.p12",
		KeyStoreType:     PKCS12KeyStoreType,
		KeyStorePassword: "cGFzcw==",
		KeyAlias:         "wso2carbon",
	}
	if !IsValidKeyStoreConfig(pkcs12Config) {
		t.Error("PKCS12 config without key password should be valid")
	}
}
