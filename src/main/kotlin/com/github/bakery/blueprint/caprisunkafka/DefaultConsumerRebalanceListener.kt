package com.github.bakery.blueprint.caprisunkafka

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener
import org.apache.kafka.common.TopicPartition

class DefaultConsumerRebalanceListener : ConsumerRebalanceListener {
    override fun onPartitionsRevoked(partitions: MutableCollection<TopicPartition>?) {

    }

    override fun onPartitionsAssigned(partitions: MutableCollection<TopicPartition>?) {
    }
}
