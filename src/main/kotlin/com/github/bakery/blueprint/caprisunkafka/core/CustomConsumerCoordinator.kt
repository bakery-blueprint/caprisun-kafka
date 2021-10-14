package com.github.bakery.blueprint.caprisunkafka.core

import org.apache.kafka.clients.GroupRebalanceConfig
import org.apache.kafka.clients.consumer.internals.AbstractCoordinator
import org.apache.kafka.clients.consumer.internals.ConsumerCoordinator
import org.apache.kafka.clients.consumer.internals.ConsumerNetworkClient
import org.apache.kafka.common.metrics.Metrics
import org.apache.kafka.common.utils.LogContext
import org.apache.kafka.common.utils.Time

/**
 * @see org.apache.kafka.clients.consumer.internals.ConsumerCoordinator.poll
 * @see org.apache.kafka.clients.consumer.internals.ConsumerCoordinator.ensureActiveGroup
 * @see org.apache.kafka.clients.consumer.internals.ConsumerCoordinator.joinGroupIfNeeded
 * @see org.apache.kafka.clients.consumer.internals.ConsumerCoordinator.initiateJoinGroup
 * @see org.apache.kafka.clients.consumer.internals.ConsumerCoordinator.sendJoinGroupRequest
 */
abstract class CustomConsumerCoordinator(
    private val coordinator: ConsumerCoordinator,
    rebalanceConfig: GroupRebalanceConfig?,
    logContext: LogContext?,
    client: ConsumerNetworkClient?,
    metrics: Metrics?,
    metricGrpPrefix: String?,
    time: Time?
) : AbstractCoordinator(rebalanceConfig, logContext, client, metrics, metricGrpPrefix, time) {
}
