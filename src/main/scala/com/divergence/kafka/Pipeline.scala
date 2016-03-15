package com.divergence.kafka

import scala.concurrent.Future
import org.apache.kafka.clients.consumer._


class Pipeline[InK, InV, OutK, OutV](consumer: Consumer[InK, InV],
                                     process: Process[InK, InV, OutK, OutV],
                                     producer: Producer[OutK, OutV]) extends Runnable {

  private def _handleRecord(record: (OutK, OutV)): Unit = {
    val (key, value) = record
    producer.put(key, value)
  }

  private def _handleRecords(records: ConsumerRecords[InK, InV]): Unit =
    Future.traverse(records.iterator())(process(_).map(_handleRecord))

  def run(): Unit =
    Iterator.continually(consumer.next().map(_handleRecords))
}
