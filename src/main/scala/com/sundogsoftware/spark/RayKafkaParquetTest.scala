package com.sundogsoftware.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._

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
    //val vals = df.select("value")
    //df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      //.as[(String, String)]
    //vals.write.parquet("parquet.rayTest")
    val query1 = df.collect.foreach(println)
    df.writeStream.format("console").start()

    df.printSchema()
  }
}
