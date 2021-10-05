package com.github.bakery.blueprint.caprisunkafka.ex.producer

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.Test

internal class SendRecordTest {

    val producer: Producer<String, String> = TODO()

    @Test
    fun send() {
        val record = ProducerRecord("", "", "")
        kotlin.runCatching {  producer.send(record) }
            .onFailure { it.printStackTrace() }
    }
}
