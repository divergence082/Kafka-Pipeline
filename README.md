# Kafka-Pipeline

Lightweight library for processing messages from one topic to another topic   

Tests:     
------
```
sbt test
```
  
Integration tests:  
-----------------
```
sbt it:test
```

Usage:
------
```
import com.divergence.kafka.pipeline
import org.apache.kafka.clients.consumer.ConsumerRecord


type InputKey = String
type InputValue = String
type OutputKey = String
type OutputValue = String
type Record = pipeline.Record[OutputKey, OutputValue]

def process(record: ConsumerRecord[InputKey, InputValue]): Future[Record] = 
  Future((record.key, record.value))

val consumer = new pipeline.Consumer[InputKey, InputValue](
  consumerProperties, new StringDeserializer, new StringDeserializer, List("consumer-topic"))
  
val producer = new pipeline.Producer[OutputKey, OutputValue](
  consumerProperties, new StringSerializer, new StringSerializer, "producer-topic")

val pipeline = new pipeline.Pipeline[InputKey, InputValue, OutputKey, OutputValue](
  consumer, process, producer, pipeline.handle)

val thread = new Thread(pipeline)
thread.start()
```
