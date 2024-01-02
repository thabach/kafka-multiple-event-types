package io.doubledispatch.kafka.multiple_event_types;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializerConfig;
import io.doubledispatch.kafka.utils.PropertiesLoader;

public class MultiEventConsumer {
      public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Must provide path to properties file for configurations");
            System.exit(1);
        }
        var consumerProperties = PropertiesLoader.load(args[0]);
        var consumerConfigs = new HashMap<String, Object>();
        consumerProperties.forEach((k, v) -> consumerConfigs.put((String) k, v));

        consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumeAvroUnwrappedRecords(consumerConfigs);
        consumeJsonSchemaRecords(consumerConfigs);

    }

    static void consumeAvroUnwrappedRecords(final Map<String, Object> baseConfigs) {
        var consumerConfigs = new HashMap<>(baseConfigs);
        consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        consumerConfigs.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, "unwrapped-avro-group");
        try (final Consumer<String, SpecificRecord> unwrappedConsumer = new KafkaConsumer<>(consumerConfigs)) {
            final String topicName = (String) consumerConfigs.get("avro.topic");
            unwrappedConsumer.subscribe(Collections.singletonList(topicName));
            ConsumerRecords<String, SpecificRecord> records = unwrappedConsumer.poll(Duration.ofSeconds(5));
            records.forEach(record -> handleAvroRecord(record.value()));
        }
    }

    static void consumeJsonSchemaRecords(final Map<String, Object> baseConfigs) {
        var consumerConfigs = new HashMap<>(baseConfigs);
        consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaJsonSchemaDeserializer.class);
        consumerConfigs.put(KafkaJsonSchemaDeserializerConfig.TYPE_PROPERTY, "javaTypeName");
        consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, "json-schema-group");
        try (final Consumer<String, Object> jsonSchemaConsumer = new KafkaConsumer<>(consumerConfigs)) {
            final String topicName = (String) consumerConfigs.get("json.topic");
            jsonSchemaConsumer.subscribe(Collections.singletonList(topicName));
            ConsumerRecords<String, Object> records = jsonSchemaConsumer.poll(Duration.ofSeconds(5));
            records.forEach(jsonSchemaRecord -> handleJsonSchemaRecord(jsonSchemaRecord.value()));
        }
    }

    private static void handleJsonSchemaRecord(final Object jsonSchemaRecord) {
        if (jsonSchemaRecord instanceof io.doubledispatch.kafka.multiple_event_types.json.PageView) {
            io.doubledispatch.kafka.multiple_event_types.json.PageView pageView = (io.doubledispatch.kafka.multiple_event_types.json.PageView) jsonSchemaRecord;
            System.out.printf("[JSON Schema] Found a PageView event %s %n", pageView);
        } else if (jsonSchemaRecord instanceof io.doubledispatch.kafka.multiple_event_types.json.Purchase) {
            io.doubledispatch.kafka.multiple_event_types.json.Purchase purchase = (io.doubledispatch.kafka.multiple_event_types.json.Purchase) jsonSchemaRecord;
            System.out.printf("[JSON Schema] Found a Purchase event %s %n", purchase);
        } else {
            System.out.printf("[JSON Schema] !!!! Unrecognized type %s %n%n", jsonSchemaRecord.toString());
        }
    }

    private static void handleAvroRecord(final SpecificRecord avroRecord) {
        if (avroRecord instanceof io.doubledispatch.kafka.multiple_event_types.avro.PageView) {
            io.doubledispatch.kafka.multiple_event_types.avro.PageView pageView = (io.doubledispatch.kafka.multiple_event_types.avro.PageView) avroRecord;
            System.out.printf("[Avro] Found an embedded PageView event %s %n", pageView);
        } else if (avroRecord instanceof io.doubledispatch.kafka.multiple_event_types.avro.Purchase) {
            io.doubledispatch.kafka.multiple_event_types.avro.Purchase purchase = (io.doubledispatch.kafka.multiple_event_types.avro.Purchase) avroRecord;
            System.out.printf("[Avro] Found an Avro embedded Purchase event %s %n", purchase);
        } else {
            throw new IllegalStateException(String.format("Unrecognized type %s %n", avroRecord.getSchema().getFullName()));
        }
    }

}
