package com.divergence.kafka.pipeline.test

import com.typesafe.config._
import org.scalatest.FunSuite
import kafka.tools.UpdateOffsetsInZK
import com.divergence.kafka.pipeline.Props


class PropsTest extends FunSuite {

  private val _conf = ConfigFactory.load

  object Key extends Enumeration {
    val GroupId = "group.id"
    val ClientId = "client.id"
    val BootstrapServers = "bootstrap.servers"
    val AutoCommitIntervalMs = "auto.commit.interval.ms"
    val AutoOffsetReset = "auto.offset.reset"
  }

  test("Props.autoOffsetReset") {
    assert(Props.autoOffsetReset("") == UpdateOffsetsInZK.Latest)
    assert(Props.autoOffsetReset(UpdateOffsetsInZK.Latest) == UpdateOffsetsInZK.Latest)
    assert(Props.autoOffsetReset(UpdateOffsetsInZK.Earliest) == UpdateOffsetsInZK.Earliest)
  }

  test("Props.consumer") {
    val input = _conf.getConfig("consumer")
    val output = Props.consumer(input)

    List(
      Key.GroupId,
      Key.BootstrapServers,
      Key.AutoOffsetReset,
      Key.AutoCommitIntervalMs)
      .map(key => assert(output.getProperty(key) == input.getString(key), key))

    assert(output.getProperty(Key.ClientId).startsWith(input.getString(Key.ClientId)))
  }

  test("Props.producer") {
    val input = _conf.getConfig("producer")
    val output = Props.producer(input)

    assert(output.getProperty(Key.BootstrapServers) == input.getString(Key.BootstrapServers),
      Key.BootstrapServers)
    assert(output.getProperty(Key.ClientId).startsWith(input.getString(Key.ClientId)), Key.ClientId)
  }
}
