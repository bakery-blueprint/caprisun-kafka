package com.github.bakery.blueprint.caprisunkafka

import org.springframework.stereotype.Indexed

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Indexed
annotation class KafkaListenerComponent
