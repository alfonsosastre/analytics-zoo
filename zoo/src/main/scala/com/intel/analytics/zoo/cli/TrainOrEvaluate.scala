package com.intel.analytics.zoo.cli

import com.intel.analytics.zoo.cli.recommendation.WideAndDeep
import org.apache.spark.sql.SparkSession

object TrainOrEvaluate {

  def main(args: Array[String]): Unit = {

    val spark: SparkSession = SparkSession
      .builder
      .master("local[8]")
      .appName("WideAndDeep evaluator")
      .config("spark.executor.memory","12g")
      .config("spark.driver.memory","12g")
      .config("spark.sql.crossJoin.enabled", "true")
      .config(key = "spark.shuffle.reduceLocality.enabled",value = false)
      .config("spark.shuffle.blockTransferService","nio")
      .config("spark.scheduler.minRegisteredResourcesRatio",1.0)
      .config(key = "spark.speculation",value = false)
      .getOrCreate()

    val configPath = "/Users/alfonso.sastre/IdeaProjects/analytics-zoo/zoo/src/main/resources/census.json"

    val wideAndDeep = WideAndDeep(configPath)

    val trainPath = "/Users/alfonso.sastre/IdeaProjects/wide_n_deep/wideAndDeep/src/main/resources/data/census/adult_train.csv"
    val valPath =  "/Users/alfonso.sastre/IdeaProjects/wide_n_deep/wideAndDeep/src/main/resources/data/census/adult_test.csv"

    val trainDf = spark.read.format("csv").options(Map("header" -> "true","inferSchema"->"true")).load(trainPath)
    val valDf = spark.read.format("csv").options(Map("header" -> "true","inferSchema"->"true")).load(valPath)

    wideAndDeep.train(spark,trainDf,valDf)

  }

}
