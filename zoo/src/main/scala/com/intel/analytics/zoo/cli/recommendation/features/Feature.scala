package com.intel.analytics.zoo.cli.recommendation.features

import org.apache.spark.sql.DataFrame

object Feature {
  val listOfFeatures = List(
    classOf[com.intel.analytics.zoo.cli.recommendation.features.Continuous],
    classOf[com.intel.analytics.zoo.cli.recommendation.features.Feature],
    classOf[com.intel.analytics.zoo.cli.recommendation.features.CrossBucket],
    classOf[com.intel.analytics.zoo.cli.recommendation.features.Categorical],
    classOf[com.intel.analytics.zoo.cli.recommendation.features.BucketString],
    classOf[com.intel.analytics.zoo.cli.recommendation.features.BucketNumeric]
  )

}

trait Feature {

  val finalName: String

  val dimension: Int

  val embedOutDim: Option[Int] = None

  def addColumn(in: DataFrame): DataFrame = in

}
