package com.github.bakery.blueprint.caprisunkafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.TestConstructor
import java.util.Locale
import java.util.Properties
import java.util.regex.Pattern

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092" ])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class WordCountExampleIT(val kafkaTemplate: KafkaTemplate<String, String>) {

    val topic: String = "wordcount-input"

    @Test
    fun send() {
        kafkaTemplate.send(topic, "Sending with own simple KafkaProducer")
        stream()
    }

    fun stream() {
        val props = Properties()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "wordcount-id"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name

        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        // Note: To re-run the demo, you need to use the offset reset tool:
        // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool

        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        // Note: To re-run the demo, you need to use the offset reset tool:
        // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        // work-around for an issue around timing of creating internal topics
        // Fixed in Kafka 0.10.2.0
        // don't use in large production apps - this increases network load
        // props.put(CommonClientConfigs.METADATA_MAX_AGE_CONFIG, 500);

        // work-around for an issue around timing of creating internal topics
        // Fixed in Kafka 0.10.2.0
        // don't use in large production apps - this increases network load
        // props.put(CommonClientConfigs.METADATA_MAX_AGE_CONFIG, 500);
        val builder = StreamsBuilder()

        val source = builder.stream<String, String>(topic)

        val pattern: Pattern = Pattern.compile("\\W+")

        val counts = source.flatMapValues { value: String -> pattern.split(value.lowercase(Locale.getDefault())).toList() }
            .map { _: Any?, value: String -> KeyValue(value, value) }
            .filter { _: String, value: String -> value != "the" }
            .groupByKey()
            .count()
            .mapValues { value: Long -> value.toString() }
            .toStream()
            .map { key: String, value: String? ->
                println(key)
                println(value)
                KeyValue(value, value)
            }
        counts.to("wordcount-output")

        KafkaStreams(builder.build(), props).use { streams ->
            // This is for reset to work. Don't use in production - it causes the app to re-load the state from Kafka on every start
            streams.cleanUp()
            streams.start()

            // usually the stream application would be running forever,
            // in this example we just let it run for some time and stop since the input data is finite.
            Thread.sleep(5000L)
        }
    }
}
