package com.divergence.kafka.pipeline.properties


object CompressionType extends Enumeration {
  type Type = String

  val GZIP = "gzip"
  val Snappy = "snappy"
  val LZ4 = "lz4"
  val NONE = "none"
}
