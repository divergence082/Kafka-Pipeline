package com.divergence.kafka

import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.Future
import scala.collection.JavaConversions._
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}


class Consumer[K, V](properties: Properties,
                     keyDeserializer: Deserializer[K],
                     valueDeserializer: Deserializer[V],
                     topics: List[String]) {

  private val _isClosed = new AtomicBoolean(false)
  private val _consumer = new KafkaConsumer[K, V](properties, keyDeserializer, valueDeserializer)
  _consumer.subscribe(topics)

  def next(): Future[ConsumerRecords[K, V]] = {
    if (!_isClosed.get) {
      try {
        Future(_consumer.poll(0))
      } catch {
        case e: Exception => Future.failed(e)
      }
    } else {
      Future.failed(new Exception("Consumer connection is closed"))
    }
  }

  def close(): Unit = {
    _isClosed.set(true)
    _consumer.wakeup()
  }
}
