# Kafka Multiple Event-Types

An example project illustrating the strong-typed publishing of events to **a singular Kafka topic** for proper temporal ordering.

Both JSON-encoded events as well as AVRO/binary-encoded events are illustrated, exemplifying JSONSchema or Avro unions respectively.

Avro: 
```
[
  "io.doubledispatch.kafka.multiple_event_types.avro.Purchase",
  "io.doubledispatch.kafka.multiple_event_types.avro.PageView"
]
```

JSONSchema:
```
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Schema references",
  "description": "List of schema references for multiple types in a single topic",
  "oneOf": [
    { "$ref": "page_view.json" },
    { "$ref": "purchase.json"}
  ]
}
```