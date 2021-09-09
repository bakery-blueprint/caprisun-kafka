package com.github.bakery.blueprint.caprisunkafka

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.test.context.EmbeddedKafka

@EmbeddedKafka(partitions = 3, brokerProperties = [ "listeners=PLAINTEXT://localhost:9092", "port=9092"])
@Profile("local")
@Configuration
class EmbeddedKafkaConfig {
}
