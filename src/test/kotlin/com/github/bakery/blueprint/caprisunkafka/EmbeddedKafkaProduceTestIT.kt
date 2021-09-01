package com.github.bakery.blueprint.caprisunkafka

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.TestConstructor

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092" ])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class EmbeddedKafkaProduceTestIT(val kafkaTemplate: KafkaTemplate<String, String>) {

    @Test
    fun send() {
        kafkaTemplate.send("topic", "Sending with own simple KafkaProducer")
    }
}
