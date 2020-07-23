/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package cmd

import (
	"bufio"
	"encoding/base64"
	"fmt"
	"github.com/spf13/cobra"
	"github.com/wso2/product-mi-tooling/cmd/utils"
	"golang.org/x/crypto/ssh/terminal"
	"os"
	"strings"
	"syscall"
)


// Show secret command related usage info
const secretInitCmdLiteral = "init"
const secretInitCmdShortDesc = "initialize keystore"
const secretInitCmdLongDesc = "Initialize key store information required for secret encryption\n\n"

var secretInitCmdExamples = "Example:\n" +
	"To initialize keystore information\n" +
	"  " + programName + " " + secretCmdLiteral + " " + secretInitCmdLiteral + "\n\n"

var secretInitCmd = &cobra.Command{
	Use:   secretInitCmdLiteral,
	Short: secretInitCmdShortDesc,
	Long:  secretInitCmdLongDesc + secretInitCmdExamples,
	Run: func(cmd *cobra.Command, args []string) {
		handleSecretInitCmdArgs(args)
	},
}

func init() {
	secretCmd.AddCommand(secretInitCmd)
	secretInitCmd.SetHelpTemplate(secretInitCmdLongDesc + utils.GetCmdUsage(programName, secretCmdLiteral,
		secretInitCmdLiteral, "") + secretInitCmdExamples + utils.GetCmdFlags(secretCmdLiteral))
}

func handleSecretInitCmdArgs(args []string) {

	if len(getEncryptionClientPath()) == 0 {
		utils.HandleErrorAndExit("[FATAL ERROR] Encryption client library is missing", nil)
	}
	if len(args) > 0 {
		fmt.Println("Invalid number of arguments. See the usage guide.\n\n" +
			utils.GetCmdUsage(programName, secretCmdLiteral, secretInitCmdLiteral, "") +
			secretInitCmdExamples + utils.GetCmdFlags(secretCmdLiteral))
	} else {
		startConsoleForKeyStore(args)
	}
}

func startConsoleForKeyStore(args []string) {
	reader := bufio.NewReader(os.Stdin)
	var inputs = make(map[string]string)

	fmt.Printf("Enter Key Store location: ")
	keystore, _ := reader.ReadString('\n')
	inputs["secret.keystore.location"] =  utils.NormalizeFilePath(keystore)

	fmt.Printf("Enter Key Store type: ")
	keystoreType, _ := reader.ReadString('\n')
	inputs["secret.keystore.type"] = strings.TrimSpace(keystoreType)

	fmt.Printf("Enter Key alias: ")
	keyAlias, _ := reader.ReadString('\n')
	inputs["secret.keystore.alias"] = strings.TrimSpace(keyAlias)

	// keystore password will be persisted as base64 encoded
	fmt.Printf("Enter Key Store password: ")
	bytePassword, _ := terminal.ReadPassword(int(syscall.Stdin))
	keyPassword := string(bytePassword)
	fmt.Println()
	inputs["secret.keystore.password"] = base64.StdEncoding.EncodeToString([]byte(strings.TrimSpace(keyPassword)))

	if utils.IsValidConsoleInput(inputs) {
		utils.MakeDirectoryIfNotExists(utils.GetSecurityDirectoryPath())
		keystorePropertiesPath := utils.GetkeyStoreInfoFileLocation()
		utils.SetProperties(inputs, keystorePropertiesPath)
		fmt.Println("secret initialization completed")
	} else {
		fmt.Println("secret initialization failed.")
	}
}
