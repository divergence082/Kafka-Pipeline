package com.divergence.kafka

import java.util.Properties
import kafka.server.KafkaConfig
import org.apache.kafka.clients.consumer.{OffsetResetStrategy, ConsumerConfig}
import org.apache.kafka.clients.producer.ProducerConfig


object Config {

  def consumer(groupId: String, zkConnect: String, offsetReset: OffsetResetStrategy): Properties = {
    val properties = new Properties
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset)
    properties.put(KafkaConfig.ZkConnectProp, zkConnect)
    properties
  }

  def producer(addresses: String, clientId: String): Properties = {
    val properties = new Properties
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, addresses)
    properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId)
    properties
  }
}
