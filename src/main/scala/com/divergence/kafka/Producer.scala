package com.divergence.kafka

import java.util.Properties
import scala.concurrent.{Promise, Future}
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.Serializer


class Producer[K, V](properties: Properties,
                     keySerializer: Serializer[K],
                     valueSerializer: Serializer[V],
                     topic: String) {

  private val _producer = new KafkaProducer[K, V](properties, keySerializer, valueSerializer)

  private def _callback(promise: Promise[RecordMetadata]): Callback =
    new Callback() {
      def onCompletion(metadata: RecordMetadata, e: Exception): Unit =
        Option(e) match {
          case Some(ex) => promise.failure(ex)
          case _ => Option(metadata) match {
            case Some(meta) => promise.success(meta)
            case None => promise.failure(new Exception("Unknown error occurred"))
          }
        }
    }

  def put(key: K, value: V): Future[RecordMetadata] = {
    val promise = Promise[RecordMetadata]()
    _producer.send(new ProducerRecord(topic, key, value), _callback(promise))
    promise.future
  }

  def close(): Unit =
    _producer.close()
}
