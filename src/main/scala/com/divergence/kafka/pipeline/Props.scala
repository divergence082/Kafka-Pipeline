package com.divergence.kafka.pipeline

import java.util.{Properties, UUID}
import kafka.tools.UpdateOffsetsInZK
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig


object Props {
  type Config = Map[String, Any]
  type ConfigHandler[T] = (T) => T

  private def _nop[T](v: T): T = v
  private def _set[T](props: Properties,
                      config: Config,
                      key: String,
                      process: ConfigHandler[T] = Props._nop[T]): Properties =
    config.get(key) match {
      case Some(v: T) =>
        props.put(key, process(v))
        props
      case None => props
    }

  def clientId(name: String): String =
    s"$name-${UUID.randomUUID()}"

  def autoOffsetReset(autoOffsetReset: String): String =
    autoOffsetReset match {
      case UpdateOffsetsInZK.Earliest | UpdateOffsetsInZK.Latest => autoOffsetReset
      case _ => UpdateOffsetsInZK.Latest
    }

  def consumer(config: Config): Properties = {
    val props = new Properties()

    _set[String](props, config, ConsumerConfig.CLIENT_ID_CONFIG, clientId)
    _set[String](props, config, ConsumerConfig.GROUP_ID_CONFIG)
    _set[String](props, config, ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)
    _set[String](props, config, ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset)
    _set[Int](props, config, ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG)

    props
  }

  def producer(config: Config): Properties = {
    val props = new Properties()

    _set[String](props, config, ProducerConfig.CLIENT_ID_CONFIG, clientId)
    _set[String](props, config, ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)

    props
  }
}
