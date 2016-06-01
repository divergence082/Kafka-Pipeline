package space.divergence.kafka.pipeline

import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.Future
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Deserializer


class Consumer[K, V](properties: Properties,
                     keyDeserializer: Deserializer[K],
                     valueDeserializer: Deserializer[V],
                     topics: List[String]) {

  private val _logger = LoggerFactory.getLogger(this.getClass)
  private val _isClosed = new AtomicBoolean(false)
  private val _consumer = new KafkaConsumer(properties, keyDeserializer, valueDeserializer)
  private val _clientId = properties.getProperty("client.id")

  def run(process: ProcessConsumerRecord[K, V]): Unit =
    try {
      _consumer.subscribe(topics)

      while (!_isClosed.get()) {
        val it = _consumer.poll(0).iterator()
        Future(it.foreach(process))
      }
    } catch {
      case e: WakeupException =>
        if (!_isClosed.get) {
          throw e
        } else {
          _logger.info(s"(run) [${_clientId}] exception caught: ${e.getMessage}")
        }
    } finally {
      try {
        _isClosed.set(true)
        _consumer.commitSync()
        _consumer.unsubscribe()
      } finally {
        _consumer.close()
      }
    }

  def close(): Unit = {
    _logger.info(s"(close) [${_clientId}]")
    _isClosed.set(true)
    _consumer.wakeup()
  }
}
