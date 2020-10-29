package com.sundogsoftware.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
// $example on$
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.util.MLUtils

object RayBayes1 {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val sc = new SparkContext("local[*]", "RayBayes1")
    // $example on$
    // Load and parse the data file.
    val data = MLUtils.loadLibSVMFile(sc, "data/sample_libsvm_data.txt")
    println("data count: " + data.count())

    // Split data into training (60%) and test (40%).
    val Array(training, test) = data.randomSplit(Array(0.6, 0.4))

    val model = NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")

    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()

    // Save and load model
    model.save(sc, "target/tmp/myNaiveBayesModel")
    val sameModel = NaiveBayesModel.load(sc, "target/tmp/myNaiveBayesModel")
    // $example off$

    sc.stop()
  }
}
