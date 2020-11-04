package com.sundogsoftware.spark

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.log4j._
import org.apache.spark
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.SparkSession

// This is based on Frank Kane's Kafka Streaming tutorial,
// but updated to use newer syntax.
// Below it, will write the result to parquet file
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

    val topics = Array("quickstart-events", "raytopic1")
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val lines = stream.map(record => {
      //println(record.key().toString, record.value().toString)
      ((record.key().toString), record.value().toString)
    })

    //lines.print()
    lines.foreachRDD(rdd => {
      val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
      import spark.implicits._
      val rawDF = rdd.toDF("key","value")
      //rawDF.printSchema()
      /*
      val valueCol = rawDF.select("value")
      if (valueCol.count() > 0) {
        valueCol.show()
      }
      */
      val columnNames = Seq("key","value")
      val query = rawDF.select(columnNames.head, columnNames.tail: _*)
      if (query.count() > 0) {
        query.write.mode("append").parquet("parquet.DirectStream.Test")
      }
    })

    streamingContext.start()
    streamingContext.awaitTermination()
  }

}
