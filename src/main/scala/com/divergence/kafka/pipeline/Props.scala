package com.divergence.kafka.pipeline

import java.util.{Properties, UUID}
import com.typesafe.config.Config
import kafka.tools.UpdateOffsetsInZK
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig


object Props {

  def clientId(name: String): String =
    s"$name-${UUID.randomUUID()}"

  def autoOffsetReset(autoOffsetReset: String): String =
    autoOffsetReset match {
      case UpdateOffsetsInZK.Earliest | UpdateOffsetsInZK.Latest => autoOffsetReset
      case _ => UpdateOffsetsInZK.Latest
    }

  def consumer(config: Config): Properties = {
    val props = new Properties()
    props.put(
      ConsumerConfig.CLIENT_ID_CONFIG,
      clientId(config.getString(ConsumerConfig.CLIENT_ID_CONFIG)))
    props.put(
      ConsumerConfig.GROUP_ID_CONFIG,
      config.getString(ConsumerConfig.GROUP_ID_CONFIG))
    props.put(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
      config.getString(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG))
    props.put(
      ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
      config.getString(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG))
    props.put(
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
      autoOffsetReset(config.getString(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG)))

    props
  }

  def producer(config: Config): Properties = {
    val props = new Properties()
    props.put(
      ProducerConfig.CLIENT_ID_CONFIG,
      clientId(config.getString(ConsumerConfig.CLIENT_ID_CONFIG)))
    props.put(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
      config.getString(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG))

    props
  }
}
