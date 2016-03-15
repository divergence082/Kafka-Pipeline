package com.divergence

import scala.concurrent.Future
import org.apache.kafka.clients.consumer.ConsumerRecord


package object kafka {
  type Process[InK, InV, OutK, OutV] = (ConsumerRecord[InK, InV]) => Future[(OutK, OutV)]
}
