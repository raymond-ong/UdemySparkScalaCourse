package com.sundogsoftware.spark

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.log4j._
import org.apache.spark
import org.apache.spark.streaming.{Seconds, StreamingContext}

object RayKafka2 {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val streamingContext = new StreamingContext("local[*]", "KafkaExample", Seconds(1))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "192.168.56.124:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("quickstart-events")
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val lines = stream.map(record => {
      //println(record.key().toString, record.value().toString)
      (record.value().toString)
    })

    lines.print()

    streamingContext.start()
    streamingContext.awaitTermination()
  }

}
