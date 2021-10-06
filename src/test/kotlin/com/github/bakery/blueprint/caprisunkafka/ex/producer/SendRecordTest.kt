package com.github.bakery.blueprint.caprisunkafka.ex.producer

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.concurrent.Future

internal class SendRecordTest {

    @DisplayName("return된 future를 무시하기 떄문에 성공 실패를 알수 없다.")
    @Test
    fun send() {
        // given 
        val producer: Producer<String, String> = mockk()
        val record = ProducerRecord("", "", "")

        every { producer.send(record) } returns mockk()

        // when
        kotlin.runCatching { producer.send(record) }
            .onFailure { it.printStackTrace() }

        // then
        verify { producer.send(record) }
        confirmVerified(producer)
    }

    @DisplayName("return된 future를 기다리기 떄문에 실패하면 예외가 발생한다.")
    @Test
    fun sendBySync() {
        // given
        val producer: Producer<String, String> = mockk()
        val record = ProducerRecord("", "", "")
        val future: Future<RecordMetadata> = mockk()

        every { producer.send(record) } returns future
        every { future.get() } returns mockk()

        // when 
        kotlin.runCatching { producer.send(record).get() }
            .onFailure { it.printStackTrace() }

        // then 
        verify {
            producer.send(record)
            future.get()
        }
        confirmVerified(producer, future)
    }

    @DisplayName("callback을 전달한다. return형은 마찬가지로 future지만 기다리지 않는다.")
    @Test
    fun sendByCallback() {
        // given 
        val producer: Producer<String, String> = mockk()
        val record = ProducerRecord("", "", "")

        every { producer.send(eq(record), any()) } returns mockk()

        // when
        kotlin.runCatching {
            producer.send(record) { _, error ->
                error?.printStackTrace()
            }
        }.onFailure { it.printStackTrace() }

        // then
        verify { producer.send(eq(record), any()) }
        confirmVerified(producer)
    }
}
