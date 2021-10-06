package com.github.bakery.blueprint.caprisunkafka

import org.apache.kafka.clients.producer.ProducerRecord

/**
 *  @see org.apache.kafka.clients.producer.ProducerRecord
 */
class ProducerRecordDecorator<T, R>(topic: String, key: T?, value: R) : ProducerRecord<T, R>(topic, key, value)
