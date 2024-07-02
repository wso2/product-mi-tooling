## mi activate message-processor

Activate a message processor deployed in a Micro Integrator

### Synopsis

Activate the message processor specified by the command line argument [messageprocessor-name] deployed in a Micro Integrator in the environment specified by the flag --environment, -e

```
mi activate message-processor [messageprocessor-name] [flags]
```

### Examples

```
To activate a message processor
  mi activate message-processor TestMessageProcessor -e dev
NOTE: The flag (--environment (-e)) is mandatory
```

### Options

```
  -e, --environment string   Environment of the micro integrator in which the message processor should be activated
  -h, --help                 help for message-processor
```

### SEE ALSO

* [mi activate](mi_activate.md)	 - Activate artifacts deployed in a Micro Integrator instance

