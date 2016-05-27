package com.divergence.kafka.pipeline

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.RecordMetadata


class Pipeline[IK, IV, OK, OV](consumer: Consumer[IK, IV],
                               process: Process[IK, IV, OK, OV],
                               producer: Producer[OK, OV],
                               handle: Handle) extends Runnable {

  private val _logger = LoggerFactory.getLogger(this.getClass)

  private def _produce(record: Record[OK, OV]): Future[RecordMetadata] = {
    val (key, value) = record
    _logger.trace(s"(produce) ($key, $value)")
    producer.put(key, value)
  }

  private def _process(record: ConsumerRecord[IK, IV]): Future[Unit] = {
    _logger.trace(s"(process) $record")

    for {
      records <- process(record)
      _ <- Future(records.foreach(_produce))
    } yield Unit
  }

  def run(): Unit =
    try {
      consumer.run(_process)
    } catch {
      case e: Exception =>
        _logger.info(s"(run) caught ${e.getMessage}")
        close()
    }

  def close(): Unit = {
    _logger.info("(close)")
    consumer.close()
    producer.close()
  }
}
