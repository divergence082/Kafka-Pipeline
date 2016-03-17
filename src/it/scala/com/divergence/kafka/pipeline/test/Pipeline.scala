package com.divergence.kafka.pipeline.test

import java.util.Properties
import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.Future
import org.slf4j.LoggerFactory
import org.apache.kafka.common.serialization._
import org.apache.kafka.clients.consumer.ConsumerRecord
import com.divergence.kafka.pipeline.{Pipeline => KafkaPipeline, Handle, Consumer, Producer}


class Pipeline(consumerProperties: Properties,
               consumerTopic: String,
               producerProperties: Properties,
               producerTopic: String,
               handle: Handle) extends Runnable {
  type IK = String
  type IV = String
  type OK = String
  type OV = String

  private val _logger = LoggerFactory.getLogger(this.getClass)
  private val _processed = new ConcurrentHashMap[IK, Unit]()
  private val _consumer = new Consumer[IK, IV](
    consumerProperties, new StringDeserializer, new StringDeserializer, List(consumerTopic))
  private val _producer = new Producer[OK, OV](
    consumerProperties, new StringSerializer, new StringSerializer, producerTopic)
  private val _pipeline = new KafkaPipeline[IK, IV, OK, OV](_consumer, _process, _producer, handle)

  private def _process(record: ConsumerRecord[IK, IV]): Future[(OK, OV)] = {
    val input = (record.key, record.value)
    _processed.put(record.key, Unit)
    _logger.info(s"(process) $input")
    Future(input)
  }

  def processed: ConcurrentHashMap[IK, Unit] = _processed

  def run(): Unit =
    try {
      _pipeline.run()
    } catch {
      case ex: Exception => _logger.error(ex.getMessage)
    } finally {
      _pipeline.close()
    }
}
