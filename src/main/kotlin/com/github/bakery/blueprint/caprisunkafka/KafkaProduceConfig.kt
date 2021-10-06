package com.github.bakery.blueprint.caprisunkafka

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

/**
 * @see org.apache.kafka.clients.producer.ProducerConfig
 */
@Configuration
class KafkaProduceConfig(private val kafkaProperties: KafkaProperties) {
    @Bean
    fun producerFactory(): ProducerFactory<String, String> = DefaultKafkaProducerFactory(kafkaProperties.buildProducerProperties())

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }
}
