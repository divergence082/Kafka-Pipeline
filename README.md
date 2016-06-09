# Kafka-Pipeline

Lightweight library for processing messages from one Kafka topic to another   
  
  
Sbt:
----
```
libraryDependencies += "space.divergence" % "kafka-pipeline_2.11" % "0.0.1"
```
or
```
libraryDependencies += "space.divergence" %% "kafka-pipeline" % "0.0.1"
```

Usage:
------
```
import space.divergence.kafka.pipeline
import org.apache.kafka.clients.consumer.ConsumerRecord


type InputKey = String
type InputValue = String
type OutputKey = String
type OutputValue = String
type Record = pipeline.Record[OutputKey, OutputValue]

def process(record: ConsumerRecord[InputKey, InputValue]): Future[Record] = 
  Future((record.key, record.value))

val consumer = new pipeline.Consumer[InputKey, InputValue](
  consumerProperties, new StringDeserializer, new StringDeserializer, 
  List("consumer-topic-0", "consumer-topic-1"))
  
val producer = new pipeline.Producer[OutputKey, OutputValue](
  consumerProperties, new StringSerializer, new StringSerializer, "producer-topic")

val pipeline = new pipeline.Pipeline[InputKey, InputValue, OutputKey, OutputValue](
  consumer, process, producer, pipeline.handle)

val thread = new Thread(pipeline)
thread.start()
```

Tests:
------------------
```
sbt "it:test-only space.divergence.kafka.pipeline.PipelineTest -- -Dintopic=in -Douttopic=out -Dincpp=src/test/resources/in-consumer.properties -Dinppp=src/test/resources/in-producer.properties -Doutcpp=src/test/resources/out-consumer.properties -Doutppp=src/test/resources/out-producer.properties -Dload=1000 -Dttp=1"
```
