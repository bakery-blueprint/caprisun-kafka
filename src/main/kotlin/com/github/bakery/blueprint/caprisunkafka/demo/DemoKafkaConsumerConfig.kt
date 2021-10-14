package com.github.bakery.blueprint.caprisunkafka.demo

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.*

@Configuration
class DemoKafkaConsumerConfig(private val kafkaProperties: KafkaProperties) {
    @Bean
    fun demoConsumerFactory(): ConsumerFactory<String, String> = DefaultKafkaConsumerFactory(kafkaProperties.buildConsumerProperties())
}
