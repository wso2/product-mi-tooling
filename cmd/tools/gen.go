//go:build ignore
// +build ignore

package main

import (
	"log"
	"path/filepath"

	"github.com/spf13/cobra/doc"
	mi "github.com/wso2/product-mi-tooling/cmd/cmd"
)

func main() {
	log.Println("Generating docs...")
	mi.MICmd.DisableAutoGenTag = true

	err := doc.GenMarkdownTree(mi.MICmd, "docs")
	if err != nil {
		log.Fatal(err)
	}

	log.Println("Generating MI bash completions...")
	err = mi.MICmd.GenBashCompletionFile(filepath.FromSlash("./shell-completions/mi_bash_completion.sh"))
	if err != nil {
		log.Fatal(err)
	}

	log.Println("Generating MI zsh completions...")
	err = mi.MICmd.GenZshCompletionFile(filepath.FromSlash("./shell-completions/mi_zsh_completion.sh"))
	if err != nil {
		log.Fatal(err)
	}
}
