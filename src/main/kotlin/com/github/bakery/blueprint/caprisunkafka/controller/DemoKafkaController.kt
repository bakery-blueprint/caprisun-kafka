package com.github.bakery.blueprint.caprisunkafka.controller

import org.apache.kafka.clients.producer.BufferExhaustedException
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.errors.TimeoutException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Properties

@RestController
class DemoKafkaController {

    @GetMapping("/kafka/test")
    fun kafkaTest(): String {
        val kafkaProperties = Properties()
        kafkaProperties["bootstrap.servers"] = "localhost:9092"
        kafkaProperties["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        kafkaProperties["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"

        val kafkaProducer = KafkaProducer<String, String>(kafkaProperties)

        val record = ProducerRecord("quickstart-events", "Precision Products", "France")

        try {
            // fire-and-forget 방식
            // val send = kafkaProducer.send(record)

            // 동기식 전송
            // val send = kafkaProducer.send(record).get()

            val send = kafkaProducer.send(
                record
            ) { metadata, exception ->
                run {
                    exception?.printStackTrace()
                    println("metadata : $metadata")
                }
            }
        } catch (se: SerializationException) {
            // 메시지 직렬화 실패
            se.printStackTrace()
        } catch (be: BufferExhaustedException) {
            // 버퍼 full exception
            be.printStackTrace()
        } catch (te: TimeoutException) {
            // time exception
            te.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "hello"
    }
}
