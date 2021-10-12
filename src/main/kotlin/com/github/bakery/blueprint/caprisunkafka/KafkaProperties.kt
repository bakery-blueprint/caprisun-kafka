package com.github.bakery.blueprint.caprisunkafka

import org.springframework.boot.autoconfigure.kafka.KafkaProperties

/**
 *  @see org.springframework.boot.autoconfigure.kafka.KafkaProperties
 *  @see org.springframework.boot.autoconfigure.kafka.KafkaProperties.Producer
 *  @see org.springframework.boot.autoconfigure.kafka.KafkaProperties.Consumer
 */
class CustomKafkaProperties : KafkaProperties() {
    fun producer(): Producer = producer
    fun consumer(): Consumer = consumer
}
