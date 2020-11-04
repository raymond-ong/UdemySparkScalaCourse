package com.sundogsoftware.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._

// Followed the official Spark documentation to Stream Kafka data
object RayKafkaParquetTest {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark = SparkSession
      .builder
      .appName("RayParquetTest")
      .master("local[*]")
      .getOrCreate()


    import spark.implicits._
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "192.168.56.124:9092")
      .option("subscribe", "raytopic1")
      .load()

    /*
    // Use this to output to string properly
    df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]
      .writeStream
      .outputMode("append")
      .format("console")
      .start()
      .awaitTermination();
     */
    /*
    // Value and Key are sent as binary hex values if not CAST as String
    val query = df.writeStream
      .outputMode("append")
      .format("console")
      .start()

    query.awaitTermination();
*/

    // try writing to parquet
    df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]
      .writeStream
      .format("parquet")
      .option("path", "./kafka_parquet")
      .option("checkpointLocation", "./kafka_parquet_checkpoint")
      .start()
      .awaitTermination();

//    df.writeStream
//      .format("parquet")
//      .option("path", "./kafka_parquet")
//      .option("checkpointLocation", "./kafka_parquet_checkpoint")
//      .start()
//      .awaitTermination();


    // ====== code below are useless ======
    //df.printSchema()
    //val valCol = df.select("value")
    //valCol.show()
    val valuesSel = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]
    valuesSel.show()
    println("Count: " + valuesSel.count())

    //val vals = df.select("value")
    //df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
    //.as[(String, String)]
    //vals.write.parquet("parquet.rayTest")
    //val query1 = df.collect.foreach(println)

  }
}
