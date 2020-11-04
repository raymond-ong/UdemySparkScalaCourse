package com.sundogsoftware.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.storage.StorageLevel

import java.util.regex.Pattern
import java.util.regex.Matcher

import org.apache.spark.streaming.kafka010._
import kafka.serializer.StringDecoder

import org.apache.log4j._
// This is the example from Frank Kane's Streaming Course (Kafka Preview)
// The syntax is outdated, but we can still get some hints on how to process the stream
object RayKafkaTest {
  def apacheLogPattern():Pattern = {
    val ddd = "\\d{1,3}"
    val ip = s"($ddd\\.$ddd\\.$ddd\\.$ddd)?"
    val client = "(\\S+)"
    val user = "(\\S+)"
    val dateTime = "(\\[.+?\\])"
    val request = "\"(.*?)\""
    val status = "(\\d{3})"
    val bytes = "(\\S+)"
    val referer = "\"(.*?)\""
    val agent = "\"(.*?)\""
    val regex = s"$ip $client $user $dateTime $request $status $bytes $referer $agent"
    Pattern.compile(regex)
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    // Create the context with a 1 second batch size
    val ssc = new StreamingContext("local[*]", "KafkaExample", Seconds(1))
    // Construct a regular expression (regex) to extract fields from raw Apache log lines
    val pattern = apacheLogPattern()

    val kafkaParams = Map("metadata.broker.list" -> "localhost:9092")
    // List of topics you want to listen for from Kafka
    val topics = List("quickstart-events").toSet

    /*
    val lines = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topics).map(_._2)

    // Extract the request field from each log line
    val requests = lines.map(x => {val matcher:Matcher = pattern.matcher(x); if (matcher.matches()) matcher.group(5)})

    // Extract the URL from the request
    val urls = requests.map(x => {val arr = x.toString().split(" "); if (arr.size == 3) arr(1) else "[error]"})

    // Reduce by URL over a 5-minute window sliding every second
    val urlCounts = urls.map(x => (x, 1)).reduceByKeyAndWindow(_ + _, _ - _, Seconds(300), Seconds(1))

    // Sort and print the results
    val sortedResults = urlCounts.transform(rdd => rdd.sortBy(x => x._2, false))
    sortedResults.print()

    // Kick it off
    ssc.checkpoint("C:/checkpoint/")
    ssc.start()
    ssc.awaitTermination()
    */
  }

}
