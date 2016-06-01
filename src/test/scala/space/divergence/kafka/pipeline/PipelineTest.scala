package space.divergence.kafka.pipeline

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.Future
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.{StringSerializer, StringDeserializer}
import org.scalatest.{fixture, Outcome}
import org.scalatest.time.SpanSugar._
import org.scalatest.concurrent.Waiters


class PipelineTest extends fixture.FunSuite with Waiters {
  type K = String
  type V = String

  val logger = LoggerFactory.getLogger(this.getClass)

  case class FixtureParam(inConsumer: Consumer[K, V],
                          inProducer: Producer[K, V],
                          inTopic: String,
                          outConsumer: Consumer[K, V],
                          outProducer: Producer[K, V],
                          outTopic: String,
                          load: Int,
                          timeToProcess: Long)

  override def withFixture(test: OneArgTest): Outcome = {
    val inTopic = test.configMap.getRequired[String]("intopic")
    val outTopic = test.configMap.getRequired[String]("outtopic")

    val inConsumer = consumer(test.configMap.getRequired[String]("incpp"), inTopic)
    val inProducer = producer(test.configMap.getRequired[String]("inppp"), inTopic)

    val outProducer = producer(test.configMap.getRequired[String]("outppp"), outTopic)
    val outConsumer = consumer(test.configMap.getRequired[String]("outcpp"), outTopic)

    try {
      test(
        FixtureParam(inConsumer, inProducer, inTopic, outConsumer, outProducer, outTopic,
          test.configMap.getRequired[String]("load").toInt,
          test.configMap.getRequired[String]("ttp").toLong))
    } finally {
      inConsumer.close()
      outConsumer.close()
      inProducer.close()
      outProducer.close()
    }
  }

  def consumer(propertiesPath: String, topic: String): Consumer[K, V] =
    new Consumer[K, V](properties(propertiesPath),
      new StringDeserializer, new StringDeserializer, List(topic))

  def producer(propertiesPath: String, topic: String): Producer[K, V] =
    new Producer[K, V](properties(propertiesPath),
      new StringSerializer, new StringSerializer, topic)

  def pipelineThread(consumer: Consumer[K, V],
                     process: Process[K, V, K, V],
                     producer: Producer[K, V],
                     handle: Handle): Thread =
    new Thread(
      new Pipeline[K, V, K, V](consumer, process, producer, handle),
      "pipelineThread")

  def consumerThread(consumer: Consumer[K, V],
                     process: ProcessConsumerRecord[K, V]): Thread =
    new Thread(
      new Runnable {
        override def run(): Unit = consumer.run(process)
      },
      "consumerThread")

  def increment(record: ConsumerRecord[K, V]): Future[Records[K, V]] =
    Future {
      val rec = (record.key(), (record.value().toInt + 1).toString)
      List(rec)
    }

  def checkIncrement(key: K, value: V): Boolean =
    value.toInt == key.toInt + 1

  test("All sent data should be processed correctly") { f =>
    val count = new AtomicInteger(0)
    val received = new ConcurrentHashMap[K, V]
    val keys = 0.until(f.load).map(_.toString)
    val w = new Waiter

    def handle(meta: RecordMetadata): Future[Unit] =
      Future {
        if (count.incrementAndGet == f.load) {

          received.entrySet().foreach { entry =>
            val (k, v) = (entry.getKey, entry.getValue)

            w(assert(keys.contains(k), "key should exist in sent set"))
            w(assert(checkIncrement(k, v), "key should be incremented"))
          }

          w.dismiss()
        }
      }

    def consume(record: ConsumerRecord[K, V]): Future[Unit] =
      Future {
        val (k, v) = (record.key, record.value)
        logger.trace(s"received ($k, $v)")
        received.put(k, v)
      }

    def produce(i: String): Future[Unit] = {
      logger.trace(s"produce ($i)")
      f.inProducer.put(i, i)
      Future(Unit)
    }

    val pThread = pipelineThread(f.inConsumer, increment, f.outProducer, handle)
    val cThread = consumerThread(f.outConsumer, consume)

    pThread.start()
    cThread.start()

    keys.foreach(produce)

    w.await(timeout((f.timeToProcess * f.load).millis), dismissals(1))
  }
}
