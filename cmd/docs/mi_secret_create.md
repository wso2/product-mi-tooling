## mi secret create

Encrypt secrets

### Synopsis

Create secrets based on given arguments

```
mi secret create [flags]
```

### Examples

```
To encrypt secret and get output on console
  mi secret create
To encrypt secret and get output as a .properties file (stored in the security folder in apictl executable directory)
  mi secret create -o file
To encrypt secret and get output as a .yaml file (stored in the security folder in apictl executable directory)
  mi secret create -o k8
To bulk encrypt secrets defined in a properties file
  mi secret create -f <file_path>
To bulk encrypt secrets defined in a properties file and get a .yaml file (stored in the security folder in apictl executable directory)
  mi secret create -o k8 -f <file_path>
```

### Options

```
  -c, --cipher string      Encryption algorithm (default "RSA/ECB/OAEPWithSHA1AndMGF1Padding")
  -f, --from-file string   Path to the properties file which contains secrets to be encrypted
  -h, --help               help for create
  -o, --output string      Get the output in yaml (k8) or properties (file) format. By default the output is printed to the console (default "console")
```

### Options inherited from parent commands

```
  -k, --insecure   Allow connections to SSL endpoints without certs
      --verbose    Enable verbose mode
```

### SEE ALSO

* [mi secret](mi_secret.md)	 - Manage sensitive information

