package com.divergence.kafka.pipeline.properties

import java.util.Properties
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.{OffsetResetStrategy, ConsumerConfig}


case class Consumer(bootstrapServers: List[String],
                    keyDeserializerClass: Option[Class] = None,
                    valueDeserializerClass: Option[Class] = None,
                    groupId: String = Defaults.GROUP_ID,
                    clientId: String = Defaults.CLIENT_ID,
                    sessionTimeoutMs: Int = Defaults.SESSION_TIMEOUT_MS,
                    heartbeatIntervalMs: Int = Defaults.HEARTBEAT_INTERVAL_MS,
                    partitionAssignmentStrategy: List[String] = Defaults.PARTITION_ASSIGN_STRATEGY,
                    metadataMaxAge: Int = Defaults.METADATA_MAX_AGE,
                    enableAutoCommit: Boolean = Defaults.ENABLE_AUTO_COMMIT,
                    autoCommitIntervalMs: Int = Defaults.AUTO_COMMIT_INTERVAL_MS,
                    maxPartitionFetchBytes: Int = Defaults.MAX_PARTITION_FETCH_BYTES,
                    sendBuffer: Int = Defaults.SEND_BUFFER,
                    receiveBuffer: Int = Defaults.RECEIVE_BUFFER,
                    fetchMinBytes: Int = Defaults.FETCH_MIN_BYTES,
                    fetchMaxWaitMs: Int = Defaults.FETCH_MAX_WAIT_MS,
                    reconnectBackoffMs: Long = Defaults.RECONNECT_BACKOFF_MS,
                    retryBackoffMs: Long = Defaults.RETRY_BACKOFF_MS,
                    autoOffsetReset: OffsetResetStrategy = Defaults.AUTO_OFFSET_RESET,
                    checkCrcs: Boolean = Defaults.CHECK_CRCS,
                    metricsSampleWindowMs: Long = Defaults.METRICS_SAMPLE_WINDOW_MS,
                    metricsNumSamples: Int = Defaults.METRICS_NUM_SAMPLES,
                    metricReporterClasses: List[String] = Defaults.METRIC_REPORTER_CLASSES,
                    requestTimeoutMs: Int = Defaults.REQUEST_TIMEOUT_MS,
                    connectionsMaxIdleMs: Long = Defaults.CONNECTIONS_MAX_IDLE_MS,
                    securityProtocol: String = CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL) {

  def properties: Properties = {
    val props = new Properties()

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs)
    props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatIntervalMs)
    props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, metadataMaxAge)
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit)
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMs)
    props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId)
    props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes)
    props.put(ConsumerConfig.SEND_BUFFER_CONFIG, sendBuffer)
    props.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, receiveBuffer)
    props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes)
    props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitMs)
    props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, reconnectBackoffMs)
    props.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffMs)
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset)
    props.put(ConsumerConfig.CHECK_CRCS_CONFIG, checkCrcs)
    props.put(ConsumerConfig.METRICS_SAMPLE_WINDOW_MS_CONFIG, metricsSampleWindowMs)
    props.put(ConsumerConfig.METRICS_NUM_SAMPLES_CONFIG, metricsNumSamples)
    props.put(ConsumerConfig.METRIC_REPORTER_CLASSES_CONFIG, metricReporterClasses)
    props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs)
    props.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, connectionsMaxIdleMs)
    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol)

    keyDeserializerClass match {
      case Some(kdc) => props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kdc)
      case None =>
    }

    valueDeserializerClass match {
      case Some(vdc) => props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, vdc)
      case None =>
    }

    props
  }
}
