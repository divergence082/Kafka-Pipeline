package com.divergence.kafka.pipeline

import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean
import scala.collection.JavaConversions._
import org.slf4j.LoggerFactory
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.clients.consumer.KafkaConsumer


class Consumer[K, V](properties: Properties,
                     keyDeserializer: Deserializer[K],
                     valueDeserializer: Deserializer[V],
                     topics: List[String]) {

  private val _logger = LoggerFactory.getLogger(this.getClass)
  private val _isClosed = new AtomicBoolean(false)
  private val _consumer = new KafkaConsumer(properties, keyDeserializer, valueDeserializer)

  def run(process: ProcessConsumerRecords[K, V]): Unit =
    try {
      _consumer.subscribe(topics)

      while (!_isClosed.get()) {
        process(_consumer.poll(0))
      }
    } catch {
      case e: Exception => _logger.error("(run) caught ${e.getMessage}")
    }

  def close(): Unit = {
    _logger.info("(close)")
    _isClosed.set(true)
    _consumer.commitSync()
    _consumer.unsubscribe()
    _consumer.close()
  }
}
