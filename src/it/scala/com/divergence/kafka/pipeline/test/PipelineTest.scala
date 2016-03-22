package com.divergence.kafka.pipeline.test

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration._
import scala.concurrent.{Promise, Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.concurrent.AsyncAssertions.Waiter
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import com.divergence.kafka.pipeline.{Producer, Props}


class PipelineTest extends FunSuite with BeforeAndAfterAll {
  type IK = String
  type IV = String
  type OK = String
  type OV = String

  private val _logger = LoggerFactory.getLogger(this.getClass)
  private val _done = Promise[Boolean]()
  private val _count = new AtomicInteger(0)
  private val _produced = new ConcurrentHashMap[IK, Unit]

  private val _config = ConfigFactory.load()
  private val _consumerConfig = _config.getConfig("consumer")
  private val _producerConfig = _config.getConfig("producer")
  private val _testCount = _config.getInt("test_count")
  private val _testDuration = _config.getInt("process_time") * _testCount

  private val _pipeline = new Pipeline(
    Props.consumer(_consumerConfig.getConfig("config")),
    _consumerConfig.getString("topic"),
    Props.producer(_producerConfig.getConfig("config")),
    _producerConfig.getString("topic"), _handle)

  private val _producer = new Producer[OK, OV](
    Props.producer(_consumerConfig),
    new StringSerializer, new StringSerializer,
    _consumerConfig.getString("topic"))

  private val _thread = new Thread(_pipeline)
  _thread.start()

  def _handle(meta: RecordMetadata): Future[Unit] =
    Future {
      val count = _count.incrementAndGet()

      _logger.info(s"(handle) $count")
      if (count == _testCount) _done.success(true)
    }

  private def _produce(i: Int): Future[Unit] = {
    _logger.info(s"(produce) $i")
    val key = UUID.randomUUID().toString
    _producer.put(key, i.toString).map(meta => _produced.put(key, Unit))
  }

  private def _assert(w: Waiter): Unit = {
    _logger.info("(assert)\n" +
      s"${_pipeline.processed} ==\n" +
      s"${_produced}")
    w { assert(_pipeline.processed == _produced, "wrong handled records") }
    w { assert(_count.get == _testCount, "wrong handled records count") }
    w.dismiss()
  }

  override def afterAll(): Unit =  {
    _producer.close()
    _thread.stop()
    while (_thread.isAlive) {}
    _logger.info("BYE!!!")
  }

  test("Produce And Handle") {
    val w = new Waiter

    (0 until _testCount).map { i => Await.ready(_produce(i), _testDuration.millis) }
    Await.ready(_done.future.map(_ => _assert(w)), _testDuration.millis)

    w.await
  }
}
