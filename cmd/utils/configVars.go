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

import (
	"crypto/tls"
	"crypto/x509"
	"fmt"
	"io/ioutil"
	"os"
	"path/filepath"
)

var HttpRequestTimeout = DefaultHttpRequestTimeout
var Insecure bool

const PlainTextWarnMessage = "WARNING: Error importing the certificate %s\n"

// TLSRenegotiationMode : Defines TLS Renegotiation support mode, default is never
var TLSRenegotiationMode = tls.RenegotiateNever

// SetConfigVars
// @param mainConfigFilePath : Path to file where Configuration details are stored
// @return error
func SetConfigVars(mainConfigFilePath string) error {
	mainConfig := GetMainConfigFromFile(mainConfigFilePath)
	Logln(LogPrefixInfo + " reading '" + mainConfigFilePath + "'")

	// validate config vars
	if !(mainConfig.Config.HttpRequestTimeout >= 0) {
		Logln(LogPrefixWarning + "value of HttpRequestTimeout in '" + mainConfigFilePath + "' is less than zero")
		Logln(LogPrefixInfo + " setting HttpRequestTimeout to " + fmt.Sprint(DefaultHttpRequestTimeout))
	}

	HttpRequestTimeout = mainConfig.Config.HttpRequestTimeout
	Logln(LogPrefixInfo + "Setting HttpTimeoutRequest to " + fmt.Sprint(mainConfig.Config.HttpRequestTimeout))

	setTLSRenegotiationMode(mainConfig)

	return nil
}

// IsValid
// @param fp : FilePath
// Attempt to create a file and delete it right after
func IsValid(fp string) bool {
	// Check if file already exists
	if _, err := os.Stat(fp); err == nil {
		return true
	}

	// Attempt to create it
	var d []byte
	if err := ioutil.WriteFile(fp, d, 0644); err == nil {
		os.Remove(fp) // And delete it
		return true
	}

	return false
}

func setTLSRenegotiationMode(mainConfig *MainConfig) {
	modeMap := map[string]tls.RenegotiationSupport{
		TLSRenegotiationOnce:   tls.RenegotiateOnceAsClient,
		TLSRenegotiationFreely: tls.RenegotiateFreelyAsClient,
		TLSRenegotiationNever:  tls.RenegotiateNever,
	}

	if val, ok := modeMap[mainConfig.Config.TLSRenegotiationMode]; ok {
		if ok {
			TLSRenegotiationMode = val
			Logln(LogPrefixInfo + "Setting TLSRenegotiationMode : " + mainConfig.Config.TLSRenegotiationMode)
		} else {
			Logln(LogPrefixInfo + "Setting TLSRenegotiationMode : never")
		}
	}
}

func GetTlsConfigWithCertificate() *tls.Config {

	certs := ReadCertsFromDir()
	certs.AppendCertsFromPEM(WSO2PublicCertificate)

	return &tls.Config{
		InsecureSkipVerify: false,
		RootCAs:            certs,
		Renegotiation:      TLSRenegotiationMode,
	}
}

func ReadCertsFromDir() *x509.CertPool {
	certs, err := x509.SystemCertPool()
	if err != nil || certs == nil {
		//if the OS is windows, systemCertPool will return an error. For windows, CA certificates has to be added
		//to the .wso2mi/certs directory.
		certs = x509.NewCertPool()
	}

	certificates, err := ioutil.ReadDir(DefaultCertDirPath)
	if err == nil {
		for _, certificate := range certificates {
			extension := filepath.Ext(certificate.Name())
			if extension == ".pem" || extension == ".crt" || extension == ".cer" {
				certFilePath := filepath.Join(DefaultCertDirPath, certificate.Name())
				fileData, err := ioutil.ReadFile(certFilePath)
				if fileData != nil && err == nil {
					if c, err := x509.ParseCertificate(fileData); err == nil {
						//if the certificate is DER encoded, add it directly to the cert pool.
						certs.AddCert(c)
					} else {
						//if the certificate is PEM encoded.
						certs.AppendCertsFromPEM(fileData)
					}
				} else {
					fmt.Printf(PlainTextWarnMessage, certificate.Name())
				}
			}
		}
	}
	return certs
}
