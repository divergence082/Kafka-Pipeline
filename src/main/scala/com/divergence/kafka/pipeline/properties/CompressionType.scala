package com.divergence.kafka.pipeline.properties

import kafka.message._


object CompressionType extends Enumeration {
  type Type = String

  val GZIP = GZIPCompressionCodec.name
  val Snappy = SnappyCompressionCodec.name
  val LZ4 = LZ4CompressionCodec.name
  val NONE = NoCompressionCodec.name
}
