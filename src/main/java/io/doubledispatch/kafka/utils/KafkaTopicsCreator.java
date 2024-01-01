package io.doubledispatch.kafka.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;

public class KafkaTopicsCreator {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Must provide path to properties file for configurations");
            System.exit(1);
        }
        var adminProps = PropertiesLoader.load(args[0]);

        var topicNames = List.of("avro-events-wrapped", "avro-events", "proto-events", "json-events", "output");
        try (Admin admin = Admin.create(adminProps)) {
            var newTopicsList = topicNames.stream()
                    .map(name -> new NewTopic(name, 1, (short) 1))
                    .collect(Collectors.toList());
            System.out.printf("Names of topics to create %s %n", newTopicsList);
            admin.createTopics(newTopicsList);
            System.out.println("Created all topics successfully");
        }
        System.out.println("Admin disconnected");
    }

}
