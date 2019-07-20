package com.intel.analytics.zoo.cli.recommendation.features

import com.intel.analytics.zoo.models.recommendation.Utils
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{col, udf}

case class BucketString(name: String, bucketSize: Int, override val embedOutDim: Option[Int] = None) extends Feature {

  val finalName = s"${name}_bucketString"

  private def bucketUdf: UserDefinedFunction = udf(Utils.buckBuckets(bucketSize)(_: String))

  override def addColumn(in: DataFrame): DataFrame = in.withColumn(finalName,bucketUdf(col(name)))

  override val dimension: Int = bucketSize
}
