## mi delete user

Delete a user from the Micro Integrator

### Synopsis

Delete a user with the name specified by the command line argument [user-name] from a Micro Integrator in the environment specified by the flag --environment, -e

```
mi delete user [user-name] [flags]
```

### Examples

```
To delete a user
  mi delete user [user-id] -e dev
To delete a user in a secondary user store
  mi delete user [user-id] -d [domain] -e dev
NOTE: The flag (--environment (-e)) is mandatory
```

### Options

```
  -d, --domain string        select user's domain
  -e, --environment string   Environment of the micro integrator from which a user should be deleted
  -h, --help                 help for user
```

### Options inherited from parent commands

```
  -k, --insecure   Allow connections to SSL endpoints without certs
      --verbose    Enable verbose mode
```

### SEE ALSO

* [mi delete](mi_delete.md)	 - Delete users or roles from a Micro Integrator instance

