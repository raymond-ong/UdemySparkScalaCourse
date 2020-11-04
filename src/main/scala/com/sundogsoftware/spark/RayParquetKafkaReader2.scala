package com.sundogsoftware.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._

object RayParquetKafkaReader2 {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark = SparkSession
      .builder
      .appName("RayParquetTest")
      .master("local[*]")
      .getOrCreate()


    import spark.implicits._
    val parquetFileDF = spark.read.parquet("./parquet.DirectStream.Test")

    // Parquet files can also be used to create a temporary view and then used in SQL statements
    parquetFileDF.createOrReplaceTempView("parquetFile")
    parquetFileDF.printSchema()
    val valuesDF = spark.sql("SELECT key, value FROM parquetFile")
    //valuesDF.map(attributes => "value: " + attributes(0)).show()
    valuesDF.foreach(record => {
      println(f"${record(0)}: ${record(1)}")
    })
  }
}
