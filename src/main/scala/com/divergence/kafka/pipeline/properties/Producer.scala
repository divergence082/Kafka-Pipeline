package com.divergence.kafka.pipeline.properties

import java.util.Properties
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.ProducerConfig


case class Producer(bootstrapServers: List[String],
                    keySerializerClass: Option[Class] = None,
                    valueSerializerClass: Option[Class] = None,
                    bufferMemory: Long = Defaults.BUFFER_MEMORY,
                    retries: Int = Defaults.RETRIES,
                    acks: String = Defaults.ACKS,
                    compressionType: CompressionType.Type = Defaults.COMPRESSION_TYPE,
                    batchSize: Int = Defaults.BATCH_SIZE,
                    lingerMs: Long = Defaults.LINGER_MS,
                    clientId: String = Defaults.CLIENT_ID,
                    sendBuffer: Int = Defaults.SEND_BUFFER,
                    receiveBuffer: Int = Defaults.RECEIVE_BUFFER,
                    maxRequestSize: Int = Defaults.MAX_REQUEST_SIZE,
                    reconnectBackoffMs: Long = Defaults.RECONNECT_BACKOFF_MS,
                    metricReporterClasses: List[String] = Defaults.METRIC_REPORTER_CLASSES,
                    retryBackoffMs: Int = Defaults.RETRY_BACKOFF_MS,
                    maxBlockMs: Long = Defaults.MAX_BLOCK_MS,
                    requestTimeoutMs: Int = Defaults.REQUEST_TIMEOUT_MS,
                    metadataMaxAge: Long = Defaults.METADATA_MAX_AGE,
                    metricsSampleWindowMs: Long = Defaults.METRICS_SAMPLE_WINDOW_MS,
                    metricsNumSamples: Int = Defaults.METRICS_NUM_SAMPLES,
                    connectionsMaxIdleMs: Long = Defaults.CONNECTIONS_MAX_IDLE_MS,
                    partitionerClass: String = Defaults.PARTITIONER_CLASS,
                    securityProtocol: String = CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL,
                    maxInFlightRequestsPerConnection: Int = Defaults.MAX_IN_FLIGHT_REQ_PER_CONN) {

  def properties: Properties = {
    val props = new Properties()

    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory)
    props.put(ProducerConfig.RETRIES_CONFIG, retries)
    props.put(ProducerConfig.ACKS_CONFIG, acks)
    props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType)
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize)
    props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId)
    props.put(ProducerConfig.SEND_BUFFER_CONFIG, sendBuffer)
    props.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, receiveBuffer)
    props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize)
    props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, reconnectBackoffMs)
    props.put(ProducerConfig.METRIC_REPORTER_CLASSES_CONFIG, metricReporterClasses)
    props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffMs)
    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs)
    props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs)
    props.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, metadataMaxAge)
    props.put(ProducerConfig.METRICS_SAMPLE_WINDOW_MS_CONFIG, metricsSampleWindowMs)
    props.put(ProducerConfig.METRICS_NUM_SAMPLES_CONFIG, metricsNumSamples)
    props.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, connectionsMaxIdleMs)
    props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, partitionerClass)
    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol)
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
      maxInFlightRequestsPerConnection)


    keySerializerClass match {
      case Some(ksc) => props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ksc)
      case None =>
    }

    valueSerializerClass match {
      case Some(vsc) => props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, vsc)
      case None =>
    }

    props
  }
}
