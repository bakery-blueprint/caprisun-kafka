package com.github.bakery.blueprint.caprisunkafka.core

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer

class KafkaConsumerDecorator<K, V>(private val kafkaConsumer: KafkaConsumer<K, V>) : Consumer<K, V> by kafkaConsumer
