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
  "type" : "object",
  "oneOf": [
    { "$ref": "page_view.json" },
    { "$ref": "purchase.json"}
  ]
}
```

## Bootstrapping the example

*This example needs Java 17 to run properly.* 

### Start environment

Start Kafka, the Confluent Schema-Registry and Redpanda Console Docker containers by running:
```
docker-compose up
```
Wait for all the containers to have started...

### Register Schemas and Create Topics
```
./gradlew registerSchemaTask
./gradlew createTopics
```
Have an inside view of your environment with either:
* Kafka Magic at http://localhost:8088 and configure the broker-address:`broker:9092` and the schema-registry url: `http://schema-registry:8081`.
* or the Redpanda Console at http://localhost:8080.

## Running the example

### Run the Producer
```
./gradlew runProducer
```
### Run the Consumer
```
./gradlew runConsumer
```
### Enjoy the results:
```
[Avro] Found a Purchase event {"item": "flux-capacitor", "amount": 437.83, "customer_id": "vandelay1234"} 
[Avro] Found a PageView event {"url": "https://acme.commerce/sale", "is_special": true, "customer_id": "vandelay1234"} 
[JSON Schema] Found a Purchase event io.doubledispatch.kafka.multiple_event_types.json.Purchase@6fd5717c[item=flux-capa...
[JSON Schema] Found a PageView event io.doubledispatch.kafka.multiple_event_types.json.PageView@7e2f86e6[url=https://acme.commerce/sale,...
```
