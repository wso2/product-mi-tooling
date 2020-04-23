package cmd

import (
	"github.com/spf13/cobra"
)

// API command related usage info
const secretCmdLiteral = "secret"
const secretCmdShortDesc = "Manage sensitive information"
const secretCmdLongDesc = "Encrypt secrets to be used in the Micro Integrator"

// apiCmd represents the api command
var secretCmd = &cobra.Command{
	Use:   secretCmdLiteral,
	Short: secretCmdShortDesc,
	Long:  secretCmdLongDesc,
}

func init() {
	RootCmd.AddCommand(secretCmd)
}