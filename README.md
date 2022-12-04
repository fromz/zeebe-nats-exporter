# NATS Exporter

A Zeebe Exporter to publish to NATS.

# Publish jar

Publishing the JAR is handled by a github workflow. 

# Build jar for local testing

```bash
RELEASE_VERSION=1 mvn clean package
```

# Record Configuration

```
exporters:
  nats:
    args:
        record:
            command: false
            event: true
            rejection: false

            decisionRequirements: false
            decision: false
            decisionEvaluation: false
            deployment: false
            error: false
            incident: true
            job: false
            jobBatch: false
            message: false
            messageSubscription: false
            process: false
            processInstance: false
            processInstanceCreation: false
            processInstanceModification: false
            processMessageSubscription: false
            variable: false
            variableDocument: false
```

TODO: provide equivelant environment variable documentation 

# TODO:
- Filter record types (Don't want commands)
- Establish more connection configuration options
- Document better