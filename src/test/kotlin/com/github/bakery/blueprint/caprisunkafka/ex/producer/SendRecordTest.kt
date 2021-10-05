package com.github.bakery.blueprint.caprisunkafka.ex.producer

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class SendRecordTest {

    private val producer: Producer<String, String> = TODO()

    @DisplayName("return된 future를 무시하기 떄문에 성공 실패를 알수 없다.")
    @Test
    fun send() {
        val record = ProducerRecord("", "", "")
        kotlin.runCatching { producer.send(record) }
            .onFailure { it.printStackTrace() }
    }

    @DisplayName("return된 future를 기다리기 떄문에 실패하면 예외가 발생한다.")
    @Test
    fun sendBySync() {
        val record = ProducerRecord("", "", "")
        kotlin.runCatching { producer.send(record).get() }
            .onFailure { it.printStackTrace() }
    }

    @DisplayName("callback을 전달한다. return형은 마찬가지로 future지만 기다리지 않는다.")
    @Test
    fun sendByCallback() {
        val record = ProducerRecord("", "", "")
        kotlin.runCatching {
            producer.send(record) { meta, error ->
                error?.printStackTrace()
            }
        }.onFailure { it.printStackTrace() }
    }
}
