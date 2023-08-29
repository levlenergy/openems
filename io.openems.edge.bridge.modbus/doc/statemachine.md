# State-Machine

```mermaid
graph TD
ON_BEFORE_PROCESS_IMAGE>ON_BEFORE_PROCESS_IMAGE]
ON_EXECUTE_WRITE>ON_EXECUTE_WRITE]

ON_EXECUTE_WRITE ==> WRITE
INITIAL_WAIT -->|sleep| READ_BEFORE_WRITE
INITIAL_WAIT -.- ON_EXECUTE_WRITE
READ_BEFORE_WRITE -->|read finished early| WAIT_FOR_WRITE
READ_BEFORE_WRITE -.- ON_EXECUTE_WRITE
WAIT_FOR_WRITE -.- ON_EXECUTE_WRITE

WRITE -->|write finished| WAIT_BEFORE_READ
WAIT_BEFORE_READ -->|sleep| READ_AFTER_WRITE
READ_AFTER_WRITE -->|read finished| FINISHED

ON_BEFORE_PROCESS_IMAGE ==> INITIAL_WAIT
FINISHED -.-o ON_BEFORE_PROCESS_IMAGE
```

View using Mermaid, e.g. https://mermaid-js.github.io/mermaid-live-editor