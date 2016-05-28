package com.divergence.kafka.pipeline.test

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.{StringSerializer, StringDeserializer}
import org.scalatest.{fixture, Outcome}
import org.scalatest.concurrent.AsyncAssertions.Waiter
import com.divergence.kafka.pipeline


class PipelineTest extends fixture.FunSuite {
  type K = String
  type V = String

  val logger = LoggerFactory.getLogger(this.getClass)

  case class FixtureParam(inConsumer: pipeline.Consumer[K, V],
                          inProducer: pipeline.Producer[K, V],
                          inTopic: String,
                          outConsumer: pipeline.Consumer[K, V],
                          outProducer: pipeline.Producer[K, V],
                          outTopic: String,
                          load: Int)

  override def withFixture(test: OneArgTest): Outcome = {
    val inTopic = test.configMap.getRequired[String]("intopic")
    val outTopic = test.configMap.getRequired[String]("intopic")

    val inConsumer = consumer(test.configMap.getRequired[String]("incpp"), inTopic)
    val inProducer = producer(test.configMap.getRequired[String]("inppp"), inTopic)

    val outProducer = producer(test.configMap.getRequired[String]("outppp"), outTopic)
    val outConsumer = consumer(test.configMap.getRequired[String]("outcpp"), outTopic)

    try {
      test(
        FixtureParam(inConsumer, inProducer, inTopic, outConsumer, outProducer, outTopic,
          test.configMap.getRequired[Int]("load")))
    } finally {
      inConsumer.close()
      outConsumer.close()
      inProducer.close()
      outProducer.close()
    }
  }

  def consumer(propertiesPath: String, topic: String): pipeline.Consumer[K, V] =
    new pipeline.Consumer[K, V](pipeline.properties(propertiesPath),
      new StringDeserializer, new StringDeserializer, List(topic))

  def producer(propertiesPath: String, topic: String): pipeline.Producer[K, V] =
    new pipeline.Producer[K, V](pipeline.properties(propertiesPath),
      new StringSerializer, new StringSerializer, topic)

  def pipelineThread(consumer: pipeline.Consumer[K, V],
                     process: pipeline.Process[K, V, K, V],
                     producer: pipeline.Producer[K, V],
                     handle: pipeline.Handle): Thread =
    new Thread(new pipeline.Pipeline[K, V, K, V](consumer, process, producer, handle))

  def consumerThread(consumer: pipeline.Consumer[K, V],
                     process: pipeline.ProcessConsumerRecord[K, V]): Thread =
    new Thread(new Runnable {
      override def run(): Unit = consumer.run(process)
    })

  def increment(record: ConsumerRecord[K, V]): Future[pipeline.Records[K, V]] =
    Future {
      val rec = (record.key(), (record.value().toInt + 1).toString)
      List(rec)
    }

  test("test") { f =>
    val done = Promise[Boolean]()
    val count = new AtomicInteger(0)
    val received = new Array[Int](f.load)
    val expected = 1.to(f.load)
    val w = new Waiter

    def handle(meta: RecordMetadata): Future[Unit] =
      Future {
        if (count.incrementAndGet() == f.load) {
          logger.trace("done")
          done.success(true)
        }
      }

    def consume(record: ConsumerRecord[K, V]): Future[Unit] =
      Future {
        val (k, v) = (record.key, record.value)
        logger.trace(s"received ($k, $v)")
        received(k.toInt) = v.toInt
      }

    def produce(i: Int): Future[Unit] = {
      logger.trace(s"produce ($i)")
      f.inProducer.put(i.toString, i.toString)
      Future(Unit)
    }

    val pThread = pipelineThread(f.inConsumer, increment, f.outProducer, handle)
    val cThread = consumerThread(f.outConsumer, consume)

    pThread.start()
    cThread.start()

    0.until(f.load).foreach(produce)
    done.future.map{ _ =>
      w(assert(received.sameElements(expected), "wrong received values"))
      w.dismiss()
    }

    w.await()
  }
}
