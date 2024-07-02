## mi deactivate endpoint

Deactivate a endpoint deployed in a Micro Integrator

### Synopsis

Deactivate the endpoint specified by the command line argument [endpoint-name] deployed in a Micro Integrator in the environment specified by the flag --environment, -e

```
mi deactivate endpoint [endpoint-name] [flags]
```

### Examples

```
To deactivate a endpoint
  mi deactivate endpoint TestEP -e dev
NOTE: The flag (--environment (-e)) is mandatory
```

### Options

```
  -e, --environment string   Environment of the micro integrator in which the endpoint should be deactivated
  -h, --help                 help for endpoint
```

### SEE ALSO

* [mi deactivate](mi_deactivate.md)	 - Deactivate artifacts deployed in a Micro Integrator instance

