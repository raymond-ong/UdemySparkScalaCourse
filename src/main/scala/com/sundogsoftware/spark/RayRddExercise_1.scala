package com.sundogsoftware.spark

import org.apache.spark._
import org.apache.log4j._

object RayRddExercise_1 {
  def parseLine(line: String): (Int, Float) = {
    //println("Line: " + line)
    val fields = line.split(",")
    (fields(0).toInt, fields(2).toFloat)
  }

  def main(args: Array[String]) {
    println("Start RayRddExercise_1")

    Logger.getLogger("org").setLevel(Level.ERROR) // Without this, there will be a bunch of warnings. So we set the log to Error level.

    val sc = new SparkContext("local[*]", "RayRddExercise_1")
    val lines = sc.textFile("data/customer-orders.csv") // it automatically splits it line by line

    // Convert to (stationID, entryType, temperature) tuples
    val parsedLines = lines.map(parseLine)
    val customersOrderTotal = parsedLines.reduceByKey((x, y) => {
      x + y
    })

    //customersOrderTotal.foreach(println)
    val sortedByTotal = customersOrderTotal.sortBy(_._2, false)
    //val sortedByTotal = customersOrderTotal.sortByKey()
    // NOTE: if you print this now, before calling collect, the order is incorrect. Need to call collect first.
    //sortedByTotal.foreach(println)

    val results = sortedByTotal.collect()
    results.foreach(println)

    println("Top 10...")
    val top10 = sortedByTotal.take(10)

    // calling take does not require collect() anymore
    top10.foreach(println)

    println("End RayRddExercise_1")

  }
}
