package com.github.bakery.blueprint.caprisunkafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

@Profile("kafka")
@Configuration
class KafkaConsumerConfig(private val kafkaProperties: KafkaProperties) {
    @Bean
    fun consumerConfigs(): Map<String, Any> {
        return kafkaProperties.buildConsumerProperties()
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory(consumerConfigs())
    }

    @Bean
    fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        val props = factory.containerProperties
        props.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.consumerFactory = consumerFactory()
        factory.setErrorHandler { error: Exception, data: ConsumerRecord<*, *>? ->
//            log.error(
//                "error : {}, data : {}",
//                error.message,
//                data,
//                error
//            )
        }
        val retryTemplate = RetryTemplate()
        val backOffPolicy = ExponentialBackOffPolicy()
        backOffPolicy.initialInterval = 1000L
        backOffPolicy.maxInterval = 1000L
        retryTemplate.setBackOffPolicy(backOffPolicy)
        retryTemplate.setRetryPolicy(SimpleRetryPolicy(2))
        factory.setRetryTemplate(retryTemplate)
        return factory
    }
}
