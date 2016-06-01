package space.divergence.kafka.pipeline

import java.util.Properties
import scala.concurrent.{Future, Promise}
import org.slf4j.LoggerFactory
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization._


class Producer[K, V](properties: Properties,
                     keySerializer: Serializer[K],
                     valueSerializer: Serializer[V],
                     topic: String) {

  private val _logger = LoggerFactory.getLogger(this.getClass)
  private val _producer = new KafkaProducer(properties, keySerializer, valueSerializer)
  private val _clientId = properties.getProperty("client.id")

  private def _callback(promise: Promise[RecordMetadata]): Callback =
    new Callback {
      override def onCompletion(metadata: RecordMetadata, e: Exception): Unit =
        Option(e) match {
          case Some(ex) => promise.failure(ex)
          case _ => Option(metadata) match {
            case Some(m) => promise.success(m)
            case _ => promise.failure(new Error("Result metadata and exception is null"))
          }
        }
    }

  def put(key: K, value: V): Future[RecordMetadata] = {
    val promise = Promise[RecordMetadata]()
    _producer.send(new ProducerRecord[K, V](topic, key, value), _callback(promise))
    promise.future
  }

  def close(): Unit = {
    _logger.info(s"(close) [${_clientId}]")
    _producer.close()
  }
}
