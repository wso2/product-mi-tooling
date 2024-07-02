## mi login

Login to a Micro Integrator

### Synopsis

Login to a Micro Integrator using credentials

```
mi login [environment] [flags]
```

### Examples

```
mi login dev -u admin -p admin
mi login dev -u admin
cat ~/.mypassword | mi login dev -u admin
```

### Options

```
  -h, --help              help for login
  -p, --password string   Password for login
      --password-stdin    Get password from stdin
  -u, --username string   Username for login
```

### SEE ALSO

* [mi](mi.md)	 - Micro Integrator commands

