package utils

import "fmt"

var KeyStoreData KeyStore


func (keyStore *KeyStore) SetKeyStore(file string, storeType string, alias string, password string) error  {

	fmt.Println("setting informartion")
	initializedKeyStore := KeyStore{Location:file, Type:storeType, Alias:alias, Password:password}
	KeyStoreData = initializedKeyStore

	return nil
}
