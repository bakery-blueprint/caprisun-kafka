package com.github.bakery.blueprint.caprisunkafka.core

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

internal class KafkaConsumerDecoratorTest {

    @Test
    fun poll() {
        // given
        val kafkaConsumerDecorator = KafkaConsumerDecorator(mock(KafkaConsumer::class.java))

        // when
        kafkaConsumerDecorator.poll()
    }
}