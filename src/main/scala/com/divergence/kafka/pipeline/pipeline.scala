package com.divergence.kafka

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords}
import org.apache.kafka.clients.producer.RecordMetadata


package object pipeline {
  type Record[K, V] = (K, V)
  type Handle = (RecordMetadata) => Future[Unit]
  type ProcessConsumerRecords[K, V] = (ConsumerRecords[K, V]) => Future[Unit]
  type Process[IK, IV, OK, OV] = (ConsumerRecord[IK, IV]) => Future[Record[OK, OV]]

  def handle(meta: RecordMetadata): Future[Unit] = Future(Unit)
}
