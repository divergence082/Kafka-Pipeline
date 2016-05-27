package com.divergence.kafka.pipeline.properties

import org.apache.kafka.clients.producer.internals.DefaultPartitioner
import org.apache.kafka.clients.consumer.{RangeAssignor, OffsetResetStrategy}


object Defaults {

  val BUFFER_MEMORY = 32 * 1024 * 1024L
  val RETRIES = 0
  val ACKS = "1"
  val COMPRESSION_TYPE = CompressionType.NONE
  val BATCH_SIZE = 16384
  val LINGER_MS = 0L
  val CLIENT_ID = ""
  val SEND_BUFFER = 128 * 1024
  val RECEIVE_BUFFER = 32 * 1024
  val MAX_REQUEST_SIZE = 1 * 1024 * 1024
  val RECONNECT_BACKOFF_MS = 50L
  val METRIC_REPORTER_CLASSES = List("")
  val RETRY_BACKOFF_MS = 100L
  val MAX_BLOCK_MS = 60 * 1000
  val METADATA_MAX_AGE = 5 * 60 * 1000
  val METRICS_SAMPLE_WINDOW_MS = 30000
  val METRICS_NUM_SAMPLES = 2
  val MAX_IN_FLIGHT_REQ_PER_CONN = 5
  val CONNECTIONS_MAX_IDLE_MS = 9 * 60 * 1000
  val PARTITIONER_CLASS = classOf[DefaultPartitioner].getName

  val GROUP_ID = ""
  val SESSION_TIMEOUT_MS = 30000
  val HEARTBEAT_INTERVAL_MS = 3000
  val PARTITION_ASSIGN_STRATEGY = List(classOf[RangeAssignor].getName)
  val ENABLE_AUTO_COMMIT = true
  val AUTO_COMMIT_INTERVAL_MS = 5000
  val MAX_PARTITION_FETCH_BYTES = 1 * 1024 * 1024
  val FETCH_MIN_BYTES = 1
  val FETCH_MAX_WAIT_MS = 500
  val AUTO_OFFSET_RESET = OffsetResetStrategy.LATEST
  val CHECK_CRCS = true
  val REQUEST_TIMEOUT_MS = 40 * 1000
}
