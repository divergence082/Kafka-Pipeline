package space.divergence.kafka

import java.util.Properties
import java.io.FileInputStream
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.clients.consumer.ConsumerRecord


package object pipeline {
  type Record[K, V] = (K, V)
  type Records[K, V] = Seq[Record[K, V]]
  type Handle = (RecordMetadata) => Future[Unit]
  type ProcessConsumerRecord[K, V] = (ConsumerRecord[K, V]) => Future[Unit]
  type Process[IK, IV, OK, OV] = (ConsumerRecord[IK, IV]) => Future[Records[OK, OV]]

  def handle(meta: RecordMetadata): Future[Unit] = Future(Unit)

  def properties(path: String): Properties = {
    val properties = new Properties()
    properties.load(new FileInputStream(path))
    properties
  }
}
